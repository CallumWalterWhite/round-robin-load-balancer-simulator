package loadbalancer_node.infrastructure;

import java.io.BufferedReader;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import loadbalancer_common.MessageTypes.ConnectionType;
import loadbalancer_common.MessageTypes.NodeMessageType;
import loadbalancer_common.NodeJob.NodeJob;
import loadbalancer_common.Request.RequestMessage;

public class NodeSender
{
    private int serverPort = 0;
    private String serverHost = "";
    private int receiverPort = 0;
    private InetAddress serverAddress = null;
    private Socket clientSocket = null;
    private BufferedReader keyboard = null;
    private BufferedReader streamFromServer = null;
    private PrintWriter streamToServer = null;
    private ConnectionType connectionType = ConnectionType.Node;

    public NodeSender(String serverHost, int receiverPort, int serverPort) 
    {
        super();
        this.serverHost = serverHost;
        this.receiverPort = receiverPort;
        this.serverPort = serverPort;
        keyboard = new BufferedReader(new InputStreamReader(System.in));
        try {
            resolveServer();
        } catch (UnknownHostException ex) {
            Logger.getLogger(NodeSender.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    
    public UUID Register(int maxJobSize)
    {
        UUID connectionId = null;
        
        try 
        {
            this.connectToServer();
            
            RequestMessage requestMessage = new RequestMessage(
                NodeMessageType.Register.name(),
                String.join(",", String.valueOf(this.receiverPort), String.valueOf(maxJobSize)),
                connectionType,
                null
            );

            streamToServer.println(requestMessage.RequestToString());
            
            String reply = streamFromServer.readLine();
            
            connectionId = RequestMessage.ParseUUIDString(reply);
            
        } catch (IOException error) {
            System.out.println("There was an error while communicating with the server!");
        } finally 
        {    
            try {
                streamToServer.close();
                streamFromServer.close();
                clientSocket.close();
            } catch (IOException error) {
            }
        }
        return connectionId;
    }
    
    public void Deregister(UUID connectionId)
    {
        try 
        {
            this.connectToServer();
            
            RequestMessage requestMessage = new RequestMessage(
                    NodeMessageType.Deregister.name(),
                    "",
                    connectionType,
                    connectionId
            );

            streamToServer.println(requestMessage.RequestToString());
            
        } catch (IOException error) {
            System.out.println("There was an error while communicating with the server!");
        } finally 
        {    
            try {
                streamToServer.close();
                streamFromServer.close();
                clientSocket.close();
            } catch (IOException error) {
            }
        }
    }
    
    public void JobComplete(UUID connectionId, NodeJob nodeJob)
    {
        try 
        {
            this.connectToServer();
            
            RequestMessage requestMessage = new RequestMessage(
                    NodeMessageType.CompleteJob.name(),
                    nodeJob.JobToString(),
                    connectionType,
                    connectionId
            );
            
            System.out.println(requestMessage.RequestToString());

            streamToServer.println(requestMessage.RequestToString());
            
        } catch (IOException error) {
            System.out.println("There was an error while communicating with the server!");
        } finally 
        {    
            try {
                streamToServer.close();
                streamFromServer.close();
                clientSocket.close();
            } catch (IOException error) {
            }
        }
    }
    
    private void resolveServer() throws UnknownHostException {
        serverAddress = InetAddress.getByName(serverHost);
    }

    private void connectToServer() throws IOException 
    {
        clientSocket = new Socket(serverAddress, serverPort);
        streamToServer = new PrintWriter(clientSocket.getOutputStream(), true);
        streamFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
    }
}
