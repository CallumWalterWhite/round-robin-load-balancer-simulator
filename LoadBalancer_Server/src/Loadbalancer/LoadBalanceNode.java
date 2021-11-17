
package Loadbalancer;

import java.util.UUID;

public class LoadBalanceNode 
{
    public LoadBalanceNode(UUID connectionId, int maxJobSize, int port)
    {
        MaxJobSize = maxJobSize;
        Port = port;
        ConnectionId = connectionId;
    }
    
    public int MaxJobSize;
    
    public int Port;

    public UUID ConnectionId;
}
