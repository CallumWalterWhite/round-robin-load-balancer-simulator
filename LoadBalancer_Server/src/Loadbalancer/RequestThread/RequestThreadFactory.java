package Loadbalancer.RequestThread;

import Loadbalancer.LoadBalanceController;
import Loadbalancer.Registry.IRegistry;
import loadbalancer_common.Exceptions.InvalidMessageTypeException;
import loadbalancer_common.MessageTypes.ConnectionType;

public class RequestThreadFactory 
{
    public static IRequestThreadProvider GetRequestThreadFactory(
            LoadBalanceController loadBalanceController, 
            String connectionMessageType,
            IRegistry registry) throws InvalidMessageTypeException
    {
        ConnectionType connectionType = GetConnectionType(connectionMessageType); 
        return GetRequestThreadFactory(loadBalanceController, connectionType, registry);
    }
    
    public static IRequestThreadProvider GetRequestThreadFactory(
            LoadBalanceController loadBalanceController, 
            ConnectionType connectionType,
            IRegistry registry) throws InvalidMessageTypeException
    {
        switch(connectionType)
        {
            case Client:
                return new ClientRequestProvider(loadBalanceController, registry);
            case Node:
                return new NodeRequestProvider(loadBalanceController, registry);
        }
        return null;
    }
        
    private static ConnectionType GetConnectionType(String connectionMessageType) throws InvalidMessageTypeException
    {
        for (ConnectionType cmt : ConnectionType.values()) 
        {
            if (cmt.name().equals(connectionMessageType)) 
            {
                return cmt;
            }
        }
        
        throw new InvalidMessageTypeException();
    }
}
