package Loadbalancer.Registry;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class NodeRegistry implements IRegistry
{
    private List<UUID> nodeIds;
    
    public NodeRegistry()
    {
        nodeIds = new ArrayList<UUID>();
    }
    
    public UUID Register(){
        UUID newNodeId = UUID.randomUUID();
        nodeIds.add(newNodeId);
        return newNodeId;
    }
    
    public void Remove(UUID id)
    {
        nodeIds.remove(
                nodeIds.indexOf(id)
        );
    }

    public Boolean Exist(UUID id) 
    {
        return nodeIds.indexOf(id) > -1;
    }
}
