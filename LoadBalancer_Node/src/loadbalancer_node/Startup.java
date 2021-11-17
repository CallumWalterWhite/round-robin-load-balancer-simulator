package loadbalancer_node;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;
import loadbalancer_common.ConfigurationManager.ConfigurationManagerFactory;
import loadbalancer_common.ConfigurationManager.IConfigurationManager;
import loadbalancer_node.infrastructure.Node;
import loadbalancer_node.infrastructure.NodeSender;

public class Startup {

    public static void main(String[] args) 
    {
        if (args.length != 3) {
            System.out.println("Usage: Node <host name> <port on server> <max job sizes>");
            System.exit(0);
        }

        String serverAddress = args[0];
        int receiverPort = Integer.parseInt(args[1].trim());
        int maxJobSize = Integer.parseInt(args[2].trim());
        
        IConfigurationManager configurationManager = ConfigurationManagerFactory.Get("server", "src\\loadbalancer_node\\appconfig.xml");
        int serverPort = configurationManager.GetDefaultPort();

        NodeSender nodeSender = new NodeSender(serverAddress, receiverPort, serverPort);
        Node node = new Node(receiverPort, maxJobSize, nodeSender);
        node.start();
        
        System.out.println("Node is now running on port -> " + receiverPort);
        BufferedReader keyboard = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("> ");
        try {
            if ("quit".equals(keyboard.readLine().trim()))
            {
                node.Stop();
            }
        } catch (IOException ex) {
            Logger.getLogger(Startup.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
