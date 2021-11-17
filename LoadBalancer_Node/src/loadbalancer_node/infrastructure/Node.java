package loadbalancer_node.infrastructure;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.io.IOException;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import loadbalancer_common.NodeJob.NodeJob;

public class Node extends Thread {

    private NodeSender nodeSender;
    private int listeningPort = 0;
    private int maxJobSize = 0;
    private UUID connectionId;
    byte[] buffer = null;
    DatagramPacket packet = null;
    DatagramSocket socket = null;
    private Boolean stop = false;

    public Node(int port,
                int maxJobSize,
                NodeSender nodeSender) 
    {
        super();
        this.listeningPort = port;
        this.maxJobSize = maxJobSize;
        this.nodeSender = nodeSender;
    }
    
    @Override
    public void run() 
    {
        try {
            connectionId = nodeSender.Register(this.maxJobSize);
            socket = new DatagramSocket(listeningPort);
            socket.setSoTimeout(0);
            while (true) 
            {    
                if (stop)
                {
                    break;
                }
                
                try 
                {
                    receiveJob();
                    
                } catch (IOException ex) {
                    Logger.getLogger(Node.class.getName()).log(Level.SEVERE, null, ex);
                }
                
                yield();
            }
        } catch (SocketException socketError) {
            socketError.printStackTrace();
        } finally {
            nodeSender.Deregister(connectionId);
            socket.close();
        }
    }

    private void receiveJob() throws IOException 
    {
        buffer = new byte[1024];
        packet = new DatagramPacket(buffer, buffer.length);
        socket.receive(packet);
        String dataString = new String(packet.getData());
        System.out.println("Job has been recieved - " + dataString);
        NodeJob nodeJob = NodeJob.Resolve(dataString);
        
        NodeCommand command = new NodeCommand(connectionId, nodeSender, nodeJob);
        command.start();
    }
    
    public void Stop() throws IOException 
    {
        this.stop = true;
    }
}
