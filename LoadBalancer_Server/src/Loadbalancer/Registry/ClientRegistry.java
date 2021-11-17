package Loadbalancer.Registry;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ClientRegistry implements IRegistry
{
    private List<UUID> clientIds;
    
    public ClientRegistry()
    {
        clientIds = new ArrayList<UUID>();
    }
    
    public UUID Register(){
        UUID newClientId = UUID.randomUUID();
        clientIds.add(newClientId);
        return newClientId;
    }
    
    public void Remove(UUID id)
    {
        clientIds.remove(
                clientIds.indexOf(id)
        );
    }

    public Boolean Exist(UUID id) {
        return clientIds.indexOf(id) > -1;
    }
}
