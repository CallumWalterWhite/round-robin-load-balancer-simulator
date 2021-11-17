package loadbalancer_client;

import java.io.IOException;
import java.net.UnknownHostException;

public class Startup 
{
    public static void main(String[] args) 
    {
        if (args.length != 2) {
            System.out.println("Usage: Client <host name> <port on server>");
            System.exit(0);
        }

        String host = args[0];
        int destinationPort = Integer.parseInt(args[1]);

        Client client = new Client(host, destinationPort);

        try {
            client.resolveServer();
            client.connectToServer();
            client.start();
            
        } catch (UnknownHostException error) {
            System.out.println("Unable to resolve server host: " + host);
        } catch (IOException error ) {
            System.out.println("Unable to connect to the server!");
        }
    }
}
