package Loadbalancer.Registry;

import java.util.UUID;

public interface IRegistry 
{
    public UUID Register();
    
    public void Remove(UUID id);
    
    public Boolean Exist(UUID id);
}
