package Server;

import Server.Infrastructure.Server;
import loadbalancer_common.ConfigurationManager.ConfigurationManagerFactory;
import loadbalancer_common.ConfigurationManager.IConfigurationManager;

public class Startup {

    public static void main(String[] args) 
    {
        IConfigurationManager configurationManager = ConfigurationManagerFactory.Get("server", "src\\Server\\appconfig.xml");
        int port = configurationManager.GetDefaultPort();
        int queueSize = configurationManager.GetConfigIntValue("queueSize");
        
        Server server = new Server(port, queueSize);
        server.start();
    }
    
}
