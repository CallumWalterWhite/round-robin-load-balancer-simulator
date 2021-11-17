package Loadbalancer.RequestThread;

import Loadbalancer.LoadBalanceController;
import Loadbalancer.LoadBalanceNode;
import Loadbalancer.LoadBalanceNodeJobRequestThread;
import Loadbalancer.Registry.IRegistry;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import loadbalancer_common.MessageTypes.ClientMessageType;
import loadbalancer_common.NodeJob.NodeJob;
import loadbalancer_common.Request.RequestMessage;

public final class ClientRequestHandlerThread extends RequestHandlerThread
{
    private Socket _clientSocket;
    private ClientMessageType _clientMessageType;
    private LoadBalanceController _loadBalanceController;
    private PrintWriter _clientOutput;
    private RequestMessage _requestMessage;
    private IRegistry _registry;
    
    public ClientRequestHandlerThread(
            LoadBalanceController loadBalanceController,
            ClientMessageType clientMessageType,
            Socket clientSocket,
            RequestMessage requestMessage,
            IRegistry registry)
    {
        super();
        _clientSocket = clientSocket;
        _loadBalanceController = loadBalanceController;
        _requestMessage = requestMessage;
        _registry = registry;
        _clientMessageType = clientMessageType;
        
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
        _clientOutput = new PrintWriter(_clientSocket.getOutputStream(), true);
    }
    
    @Override
    public void run()
    {
        switch(_clientMessageType)
        {
            case JobHistory:
                JobHistory();
                break;
            case RunDuration:
                RunDuration();
                break;
            case Register:
                Register();
                break;
            case Deregister:
                Deregister();
                break;
            case GetNodes:
                GetNodes();
                break;
        }
    }
    
    private void GetNodes()
    {
        if (_registry.Exist(_requestMessage.GetConnectionId()))
        {
            List<LoadBalanceNode> nodes = _loadBalanceController.GetNodes();
            
            if (nodes.isEmpty())
            {
                _clientOutput.println("Node list is empty");
            }
            else
            {
                _clientOutput.println("Nodes - ");
                int i = 0;
                for (LoadBalanceNode node : nodes)
                {
                    i++;
                    _clientOutput.println("Node - " + i);
                    _clientOutput.println("Id: " + node.ConnectionId );
                    _clientOutput.println("Weight: " + node.MaxJobSize );
                    _clientOutput.println("Port: " + node.Port );
                }
            }
        }
    }
    
    private void JobHistory()
    {
        if (_registry.Exist(_requestMessage.GetConnectionId()))
        {
            List<NodeJob> nodeJobs =_loadBalanceController.GetJobs();
            if (nodeJobs.isEmpty())
            {
                _clientOutput.println("Job list is empty");
            }
            else
            {
                _clientOutput.println("Jobs - ");
                int i = 0;
                for (NodeJob job : nodeJobs)
                {
                    i++;
                    _clientOutput.println("Job - " + i + (job.Completed() ? " Completed" : "Running"));
                    _clientOutput.println("Id: " + job.GetJobId() );
                    _clientOutput.println("JobType: " + job.GetClientMessageType().name() );
                    _clientOutput.println("Duration: " + job.GetDuration() );
                    if (job.Completed())
                    {
                        _clientOutput.println("Result (completed): " + job.GetResult());   
                    }
                }
            }
        }
    }
    
    private void RunDuration()
    {
        if (_registry.Exist(_requestMessage.GetConnectionId()))
        {
            int duration = Integer.parseInt(_requestMessage.GetMessageBody());
            
            LoadBalanceNodeJobRequestThread loadBalanceNodeJob = _loadBalanceController.CreateLoadBalanceNodeJob(_clientMessageType, duration);
            
            loadBalanceNodeJob.start();
            
            _clientOutput.println(loadBalanceNodeJob.Job.GetJobId().toString());
        }
    }
    
    private void Register()
    {
        UUID id = _registry.Register();
        _clientOutput.println(id);
    }
    
    private void Deregister()
    {
        UUID connectionId = _requestMessage.GetConnectionId();
        
        if (_registry.Exist(connectionId))
        {
            _registry.Remove(connectionId);
        }
        
        _clientOutput.println("Client been removed.");
    }
}
