package loadbalancer_common.ConfigurationManager;

public class ConfigurationManagerFactory 
{
    public static IConfigurationManager Get(String clientType, String configPath)
    {
        switch(clientType){
            case "server":
                return new ServerConfigurationManager(configPath);
            case "node":
                return new ServerConfigurationManager(configPath);
            case "client":
                return new ServerConfigurationManager(configPath);
            default:
                throw new UnsupportedOperationException("Not supported yet.");
        }
    }
    
    
}
