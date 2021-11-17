package Server.Infrastructure;

import Loadbalancer.LoadBalanceController;
import Loadbalancer.Registry.ClientRegistry;
import Loadbalancer.Registry.NodeRegistry;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Server extends Thread 
{
    private int _listeningPort;
    private ServerSocket _serverSocket;
    private int _connectionQueueSize;
    private LoadBalanceController _loadBalanceController;
    private ClientRegistry _clientRegistry;
    private NodeRegistry _nodeRegistry;
    
    public Server(int listeningPort, int connectionQueueSize) 
    {
        super();
        this._listeningPort = listeningPort;
        this._connectionQueueSize = connectionQueueSize;
        this._loadBalanceController = CreateLoadBalanceController();
        this._clientRegistry = new ClientRegistry();
        this._nodeRegistry = new NodeRegistry();
    }
    
    @Override
    public void run() 
    {
        try 
        {
            _serverSocket = new ServerSocket(_listeningPort, _connectionQueueSize);
            System.out.println("Created server socket: " + _serverSocket.toString());
            while (true) 
            {
                Socket socket = _serverSocket.accept();
                
                CommunicationThread communicationThread = new CommunicationThread(
                        socket,
                        _loadBalanceController,
                        _clientRegistry,
                        _nodeRegistry
                );
                
                try {
                    communicationThread.initialiseCommunication();
                    communicationThread.start();
                } catch (IOException error) 
                {
                    System.out.println("Server: Unable to initialise client thread!");
                }
            }
        } catch (Exception error) 
        {
            Logger.getLogger(CommunicationThread.class.getName()).log(Level.SEVERE, null, error);
        } finally {
            try {
                _serverSocket.close();
            } catch (IOException error) {
            }
        }
    }
    
    public void close() 
    {
        try {
            _serverSocket.close();
        } catch (IOException error) {
        }
        this.stop();
    }
    
    private LoadBalanceController CreateLoadBalanceController()
    {
        return new LoadBalanceController();
    }
}
