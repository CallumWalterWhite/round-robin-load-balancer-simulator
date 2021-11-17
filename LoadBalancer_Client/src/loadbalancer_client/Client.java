package loadbalancer_client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import loadbalancer_common.MessageTypes.ClientMessageType;
import loadbalancer_common.MessageTypes.ConnectionType;
import loadbalancer_common.Request.RequestMessage;

public class Client 
{
    private int serverPort = 0;
    private String serverHost = "";
    private InetAddress serverAddress = null;
    private Socket serverSocket = null;
    private BufferedReader keyboard = null;
    private BufferedReader streamFromServer = null;
    private PrintWriter streamToServer = null;
    private ConnectionType connectionType = ConnectionType.Client;
    private UUID connectionId = null;

    public Client(String serverHost, int serverPort) {
        this.serverHost = serverHost;
        this.serverPort = serverPort;
        keyboard = new BufferedReader(new InputStreamReader(System.in));
    }
    
    public void resolveServer() throws UnknownHostException {
        serverAddress = InetAddress.getByName(serverHost);
    }

    public void connectToServer() throws IOException 
    {
        serverSocket = new Socket(serverAddress, serverPort);
        streamToServer = new PrintWriter(serverSocket.getOutputStream(), true);
        streamFromServer = new BufferedReader(new InputStreamReader(serverSocket.getInputStream()));
    }

    public void start() {
        try {
            Register();
            while (true) 
            {
                if (CommandMenu())
                {
                    break;
                }
            }
            
            System.out.println("Terminating session with server...");
            
            Deregister();
            
        } catch (IOException error) {
            System.out.println("There was an error while communicating with the server!");
        } finally 
        {    
            try {
                streamToServer.close();
                streamFromServer.close();
                serverSocket.close();
            } catch (IOException error) {
            }
        }
    }
    
    
    private Boolean CommandMenu() throws IOException
    {
        Boolean breakLoop = false;
        
        System.out.println();
        System.out.println("Commands - ");
        System.out.println("1. Get available nodes");
        System.out.println("2. Send job request");
        System.out.println("3. Get job history");
        System.out.println("4. Get node priorities");
        System.out.println("5. Quit");
        System.out.print("> ");
        String request = keyboard.readLine().trim();
        
        RequestMessage requestMessage = null;
        switch(request)
        {
            case "1":
                requestMessage = new RequestMessage(
                        ClientMessageType.GetNodes.name(),
                        "",
                        connectionType,
                        connectionId
                );   
                break;
            case "2":
                System.out.println();
                System.out.print("Job time duration >");
                String durationInput = keyboard.readLine().trim();
                try{
                    Integer.parseInt(durationInput);
                    requestMessage = new RequestMessage(
                            ClientMessageType.RunDuration.name(),
                            durationInput,
                            connectionType,
                            connectionId
                    ); 
                } catch (NumberFormatException e) {
                    System.out.print("Input has to be a numer");
                }
                break;
            case "3":
                requestMessage = new RequestMessage(
                            ClientMessageType.JobHistory.name(),
                            "",
                            connectionType,
                            connectionId
                    );
                break;
            case "5":
                breakLoop = true;
                break;
            default:
                System.out.println("Invalid input.");
                CommandMenu();
                break;
        }
        
        if (requestMessage != null)
        {
            System.out.println(requestMessage.RequestToString());
            streamToServer.println(requestMessage.RequestToString());
            
            String reply = streamFromServer.readLine();
            System.out.println(reply);
            if (streamFromServer.ready())
            {
                while(streamFromServer.ready())
                {
                    reply = streamFromServer.readLine();
                    System.out.println(reply);
                }
            }
        }
        
        return breakLoop;
    }
    
    private void Register()
    {
        RequestMessage requestMessage = new RequestMessage(
                ClientMessageType.Register.name(),
                "",
                connectionType,
                null
        );

        streamToServer.println(requestMessage.RequestToString());
        
        try {
            String reply = streamFromServer.readLine();
            
            connectionId = RequestMessage.ParseUUIDString(reply);
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void Deregister()
    {
        RequestMessage requestMessage = new RequestMessage(
                ClientMessageType.Deregister.name(),
                "",
                connectionType,
                connectionId
        );

        streamToServer.println(requestMessage.RequestToString());
        
        try {
            String reply = streamFromServer.readLine();
            
            connectionId = RequestMessage.ParseUUIDString(reply);
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
