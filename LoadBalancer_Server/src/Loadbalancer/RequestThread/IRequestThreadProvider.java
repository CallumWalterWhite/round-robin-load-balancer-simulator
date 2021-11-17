package Loadbalancer.RequestThread;

import java.net.Socket;
import loadbalancer_common.Exceptions.InvalidMessageTypeException;
import loadbalancer_common.Request.RequestMessage;

public interface IRequestThreadProvider 
{
    public RequestHandlerThread GetRequestThread(Socket socket, RequestMessage requestMessage) throws InvalidMessageTypeException;
}
