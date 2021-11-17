package Loadbalancer.RequestThread;

import Loadbalancer.LoadBalanceController;
import Loadbalancer.Registry.IRegistry;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import loadbalancer_common.MessageTypes.NodeMessageType;
import loadbalancer_common.NodeJob.NodeJob;
import loadbalancer_common.Request.RequestMessage;

public class NodeRequestHandlerThread extends RequestHandlerThread
{
    private Socket _nodeSocket;
    private LoadBalanceController _loadBalanceController;
    private PrintWriter _nodeOutput;
    private RequestMessage _requestMessage;
    private IRegistry _registry;
    private NodeMessageType _nodeMessageType;
    
    public NodeRequestHandlerThread(
            LoadBalanceController loadBalanceController,
            NodeMessageType nodeMessageType,
            Socket clientSocket,
            RequestMessage requestMessage,
            IRegistry registry)
    {
        super();
        _nodeSocket = clientSocket;
        _nodeMessageType = nodeMessageType;
        _loadBalanceController = loadBalanceController;
        _requestMessage = requestMessage;
        _registry = registry;
        
        try 
        {
            initialiseCommunication();
        } catch (IOException ex) 
        {
            Logger.getLogger(ClientRequestHandlerThread.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void initialiseCommunication() throws IOException 
    {
        _nodeOutput = new PrintWriter(_nodeSocket.getOutputStream(), true);
    }
    
    @Override
    public void run()
    {
        switch(_nodeMessageType)
        {
            case Register:
                Register();
                break;
            case Deregister:
                Deregister();
                break;
            case CompleteJob:
                CompleteJob();
                break;
        }
    }
    
    private void Register()
    {
        UUID connectionId = _registry.Register();
        String messageBody = _requestMessage.GetMessageBody();
        String[] nodeParams = messageBody.split(",");
        int port = Integer.parseInt(nodeParams[0]);
        System.out.println(nodeParams[1]);
        int maxJobSize = Integer.parseInt(nodeParams[1]);
        _loadBalanceController.AddNode(connectionId, maxJobSize, port);
        _nodeOutput.println(connectionId);
    }
    
    private void Deregister()
    {
        UUID connectionId = _requestMessage.GetConnectionId();
        
        if (_registry.Exist(connectionId))
        {
            _loadBalanceController.RemoveNode(connectionId);
            _registry.Remove(connectionId);
        }
        
        _nodeOutput.println("Node been removed.");
    }
    
    private void CompleteJob()
    {
        UUID connectionId = _requestMessage.GetConnectionId();
        
        if (_registry.Exist(connectionId))
        {
            String messageBody = _requestMessage.GetMessageBody();
            NodeJob nodeJob = NodeJob.Resolve(messageBody);
            _loadBalanceController.CompleteLoadBalanceNodeJob(connectionId, nodeJob.GetJobId(), nodeJob.GetResult());
        }
        
        _nodeOutput.println("Job completed.");
    }
}
