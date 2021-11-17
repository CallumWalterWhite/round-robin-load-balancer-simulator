package loadbalancer_common.Request;

import java.io.Serializable;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import loadbalancer_common.Exceptions.InvalidMessageTypeException;
import loadbalancer_common.MessageTypes.ConnectionType;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class RequestMessage implements Serializable
{
    private final String _messageType;
    private final String _messageBody;
    private final ConnectionType _connectionType; 
    private final UUID _connectionId;
    
    public RequestMessage(
            String messageType,
            String messageBody,
            ConnectionType connectionType,
            UUID connectionId)
    {
        _messageType = messageType;
        _messageBody = messageBody;
        _connectionType = connectionType;
        _connectionId = connectionId;
    }
    
    public String RequestToString()
    {
        JSONObject json = new JSONObject();
        json.put("MessageType", _messageType);
        json.put("MessageBody", _messageBody);
        json.put("ConnectionType", _connectionType.name());
        json.put("ConnectionId", _connectionId == null ? "" : _connectionId.toString());
        
        return json.toString();
    }
    
    public String GetMessageType()
    {
        return _messageType;
    }
    
    public String GetMessageBody()
    {
        return _messageBody;
    }
    
    public ConnectionType GetConnectionType()
    {
        return _connectionType;
    }
    
    public UUID GetConnectionId()
    {
        return _connectionId;
    }
    
    public byte[] toByte() {
        return this.RequestToString().getBytes();
    }
    
    public static RequestMessage Resolve(String jsonString)
    {
        JSONParser parser = new JSONParser();
        if (jsonString != null){
            try {
                System.out.println(jsonString);
                JSONObject json = (JSONObject)parser.parse(jsonString.trim());
                String messageType = (String) json.get("MessageType");
                String messageBody = (String) json.get("MessageBody");
                UUID connectionId = ParseUUIDString((String) json.get("ConnectionId"));
                String connectionTypeString = (String) json.get("ConnectionType");
                ConnectionType connectionType = GetConnectionType(connectionTypeString);
                return new RequestMessage(messageType, messageBody, connectionType, connectionId);
            } catch (ParseException | InvalidMessageTypeException ex) 
            {
                Logger.getLogger(RequestMessage.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return null;
    }
    
    private static ConnectionType GetConnectionType(String connectionMessageType) throws InvalidMessageTypeException
    {
        for (ConnectionType cmt : ConnectionType.values()) 
        {
            if (cmt.name().equals(connectionMessageType)) 
            {
                return cmt;
            }
        }
        
        throw new InvalidMessageTypeException();
    }
    
    public static UUID ParseUUIDString(String uuidString)
    {
        return uuidString.length() > 0 ? UUID.fromString(uuidString) : null;
    }
}
