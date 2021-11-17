package Server.Infrastructure;

import Loadbalancer.LoadBalanceController;
import Loadbalancer.Registry.ClientRegistry;
import Loadbalancer.Registry.IRegistry;
import Loadbalancer.Registry.NodeRegistry;
import Loadbalancer.RequestThread.RequestHandlerThread;
import Loadbalancer.RequestThread.RequestThreadFactory;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import loadbalancer_common.Exceptions.InvalidConnectionTypeException;
import loadbalancer_common.Exceptions.InvalidMessageTypeException;
import loadbalancer_common.Exceptions.UnableResolveRequestException;
import loadbalancer_common.MessageTypes.ConnectionType;
import loadbalancer_common.Request.RequestMessage;
import Loadbalancer.RequestThread.IRequestThreadProvider;

public class CommunicationThread extends Thread 
{
    private Socket _socket = null;
    private PrintWriter _output = null;
    private BufferedReader _input = null;
    private LoadBalanceController _loadBalanceController;
    private ClientRegistry _clientRegistry;
    private NodeRegistry _nodeRegistry;
    
    public CommunicationThread(Socket clientSocket, 
            LoadBalanceController loadBalanceController,
            ClientRegistry clientRegistry,
            NodeRegistry nodeRegistry) 
    {
        super();
        this._socket = clientSocket;
        this._loadBalanceController = loadBalanceController;
        this._clientRegistry = clientRegistry;
        this._nodeRegistry = nodeRegistry;
    }
    
    public void initialiseCommunication() throws IOException 
    {
        _output = new PrintWriter(this._socket.getOutputStream(), true);
        _input = new BufferedReader(new InputStreamReader(_socket.getInputStream()));
    }
    
    @Override
    public void run() 
    {
        try {
            while (true) 
            {
                if (!_socket.isConnected())
                {
                    break;
                }
                
                RequestMessage requestMessage = GetRequestMessage();
                
                ConnectionType connectionType = requestMessage.GetConnectionType();
                
                IRequestThreadProvider requestFactory = GetRequestThreadFactory(connectionType);
                
                RequestHandlerThread requestThread = requestFactory.GetRequestThread(_socket, requestMessage);
                
                requestThread.start();
                
                yield();
            }
        } catch (InvalidMessageTypeException | IOException | InvalidConnectionTypeException ex) 
        {
            Logger.getLogger(CommunicationThread.class.getName()).log(Level.SEVERE, null, ex);
        } 
        catch (UnableResolveRequestException ex)
        {
            Logger.getLogger(CommunicationThread.class.getName()).log(Level.FINE, null, ex);
        } 
        finally 
        {
            try {
                if (_input != null) {
                    _input.close();
                }
            } catch (IOException error) {
            }
            if (_output != null) {
                _output.close();
            }
        }
    }
    
    private IRequestThreadProvider GetRequestThreadFactory(ConnectionType connectionType) throws InvalidMessageTypeException, InvalidConnectionTypeException
    {
        IRegistry registry = GetRegistry(connectionType);
        return RequestThreadFactory.GetRequestThreadFactory(_loadBalanceController, connectionType, registry);
    }
    
    private IRegistry GetRegistry(ConnectionType connectionType) throws InvalidConnectionTypeException
    {
        switch(connectionType)
        {
            case Client:
                return _clientRegistry;
            case Node:
                return _nodeRegistry;
            default:
                throw new InvalidConnectionTypeException();
        }
    }
    
    private RequestMessage GetRequestMessage() throws IOException, UnableResolveRequestException
    {
        String inputMessage = _input.readLine();
        
        if (inputMessage == null)
        {
            System.out.println("Connection closed");
            _input.close();
        }

        System.out.println("socket message: " + inputMessage);

        RequestMessage requestMessage = RequestMessage.Resolve(inputMessage);

        if (requestMessage == null)
        {
            throw new UnableResolveRequestException();
        }

        return requestMessage;  
    }
    
}
