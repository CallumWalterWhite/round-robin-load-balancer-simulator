package Loadbalancer.RequestThread;

import Loadbalancer.LoadBalanceController;
import Loadbalancer.Registry.IRegistry;
import java.net.Socket;
import loadbalancer_common.Exceptions.InvalidMessageTypeException;
import loadbalancer_common.MessageTypes.NodeMessageType;
import loadbalancer_common.Request.RequestMessage;

public class NodeRequestProvider implements IRequestThreadProvider
{  
    private LoadBalanceController _loadBalanceController;
    
    private IRegistry _registry;
    
    public NodeRequestProvider(
            LoadBalanceController loadBalanceController,
            IRegistry registry)
    {
        _loadBalanceController = loadBalanceController;
        _registry = registry;
    }
    
    public NodeRequestHandlerThread GetRequestThread(Socket nodeSocket, RequestMessage requestMessage) throws InvalidMessageTypeException
    {
        NodeMessageType nodeMessageType = GetNodeMessageType(requestMessage.GetMessageType());
        
        NodeRequestHandlerThread nodeRequestThread = new NodeRequestHandlerThread(_loadBalanceController, nodeMessageType, nodeSocket, requestMessage, _registry);
        
        return nodeRequestThread;
    }
    
    private static NodeMessageType GetNodeMessageType(String messageType) throws InvalidMessageTypeException
    {
        
        for (NodeMessageType nmt : NodeMessageType.values()) 
        {
            if (nmt.name().equals(messageType)) 
            {
                return nmt;
            }
        }
        
        throw new InvalidMessageTypeException();
    }
    
}
