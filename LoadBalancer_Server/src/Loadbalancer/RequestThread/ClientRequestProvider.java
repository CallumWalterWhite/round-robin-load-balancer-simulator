package Loadbalancer.RequestThread;

import Loadbalancer.LoadBalanceController;
import Loadbalancer.Registry.IRegistry;
import java.net.Socket;
import loadbalancer_common.Exceptions.InvalidMessageTypeException;
import loadbalancer_common.MessageTypes.ClientMessageType;
import loadbalancer_common.Request.RequestMessage;

public class ClientRequestProvider implements IRequestThreadProvider
{  
    private LoadBalanceController _loadBalanceController;
    
    private IRegistry _registry;
    
    public ClientRequestProvider(
            LoadBalanceController loadBalanceController,
            IRegistry registry)
    {
        _loadBalanceController = loadBalanceController;
        _registry = registry;
    }
    
    public ClientRequestHandlerThread GetRequestThread(Socket clientSocket, RequestMessage requestMessage) throws InvalidMessageTypeException
    {
        ClientMessageType clientMessageType = GetClientMessageType(requestMessage.GetMessageType());
        
        ClientRequestHandlerThread clientRequestThread = new ClientRequestHandlerThread(_loadBalanceController, clientMessageType, clientSocket, requestMessage, _registry);
        
        return clientRequestThread;
    }
    
    private static ClientMessageType GetClientMessageType(String messageType) throws InvalidMessageTypeException
    {
        for (ClientMessageType cmt : ClientMessageType.values()) 
        {
            if (cmt.name().equals(messageType)) 
            {
                return cmt;
            }
        }
        
        throw new InvalidMessageTypeException();
    }    
}
