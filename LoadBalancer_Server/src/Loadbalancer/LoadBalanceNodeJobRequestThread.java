
package Loadbalancer;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;
import loadbalancer_common.ConfigurationManager.ConfigurationManagerFactory;
import loadbalancer_common.ConfigurationManager.IConfigurationManager;
import loadbalancer_common.NodeJob.NodeJob;

public class LoadBalanceNodeJobRequestThread extends Thread
{
    private InetAddress addr;
    private byte[] buffer = null;
    private DatagramPacket packet = null;
    private DatagramSocket socket = null;
    
    public LoadBalanceNodeJobRequestThread(LoadBalanceNode node, NodeJob job)
    {
        super();
        try {
            addr = InetAddress.getLocalHost();
        } catch (UnknownHostException ex) {
            Logger.getLogger(LoadBalanceNodeJobRequestThread.class.getName()).log(Level.SEVERE, null, ex);
        }
        Node = node;
        Job = job;
    }
    
    public LoadBalanceNode Node;
    
    public NodeJob Job;
    
    @Override
    public void run() 
    {
        try {
            
            IConfigurationManager configurationManager = ConfigurationManagerFactory.Get("server", "src\\Server\\appconfig.xml");
            int port = configurationManager.GetDatagramPort();
            socket = new DatagramSocket(port);
            sendJob();
            
        } catch (SocketException socketError) {
            socketError.printStackTrace();
        } catch (IOException ioError) {
            ioError.printStackTrace();
        } finally {
            socket.close();
        }

    }
    
    private void sendJob() throws IOException {
        buffer = Job.toByte();
        packet = new DatagramPacket(buffer, buffer.length, addr, Node.Port);
        System.out.println("Sender: Ready to send on " + socket.getLocalAddress().toString() + " port: " + socket.getLocalPort());
        socket.send(packet);

    }
}
