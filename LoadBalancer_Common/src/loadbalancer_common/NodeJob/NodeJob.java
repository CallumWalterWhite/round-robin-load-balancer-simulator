package loadbalancer_common.NodeJob;

import java.io.Serializable;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import loadbalancer_common.Exceptions.InvalidMessageTypeException;
import loadbalancer_common.MessageTypes.ClientMessageType;
import loadbalancer_common.Request.RequestMessage;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class NodeJob implements Serializable
{
    private final UUID _jobId;
    
    private final UUID _nodeConnectionId;
    
    private String _result;
    
    private final ClientMessageType _clientMessageType;
    
    private final int _duration;
    
    private Boolean _completed;
    
    public NodeJob(
            UUID jobId,
            UUID nodeConnectionId,
            ClientMessageType clientMessageType,
            int duration) 
    {
        _jobId = jobId;
        _nodeConnectionId = nodeConnectionId;
        _result = "";
        _clientMessageType = clientMessageType;
        _duration = duration;
        _completed = false;
    }
    
    public NodeJob(
            UUID jobId,
            UUID nodeConnectionId,
            String result,
            ClientMessageType clientMessageType,
            int duration,
            Boolean completed) 
    {
        _jobId = jobId;
        _nodeConnectionId = nodeConnectionId;
        _result = result;
        _clientMessageType = clientMessageType;
        _duration = duration;
        _completed = completed;
    }
    
    public void Complete() 
    {
        _completed = true;
    }
    
    public void SetResult(String result) 
    {
        _result = result;
    }
    
    public Boolean Completed() 
    {
        return _completed;
    }
    
    public UUID GetJobId() 
    {
        return _jobId;
    }
    
    public UUID GetNodeConnectionId() 
    {
        return _nodeConnectionId;
    }
    
    public String GetResult() 
    {
        return _result;
    }
    
    public int GetDuration() 
    {
        return _duration;
    }
    
    public ClientMessageType GetClientMessageType() 
    {
        return _clientMessageType;
    }
    
    public String JobToString()
    {
        JSONObject json = new JSONObject();
        json.put("Completed", String.valueOf(_completed));
        json.put("Result", _result);
        json.put("Duration", String.valueOf(_duration));
        json.put("ClientMessageType", _clientMessageType.name());
        json.put("JobId", _jobId == null ? "" : _jobId.toString());
        json.put("NodeConnectionId", _nodeConnectionId == null ? "" : _nodeConnectionId.toString());
        
        return json.toString();
    }
    
    public byte[] toByte() {
        return this.JobToString().getBytes();
    }
    
    public static NodeJob Resolve(String jsonString)
    {
        JSONParser parser = new JSONParser();
        try {
            JSONObject json = (JSONObject)parser.parse(jsonString.trim());
            Boolean completed = Boolean.parseBoolean((String) json.get("Completed"));
            int duration = Integer.valueOf((String) json.get("Duration"));
            String result = (String) json.get("Result");
            UUID jobId = ParseUUIDString((String) json.get("JobId"));
            UUID nodeConnectionId = ParseUUIDString((String) json.get("NodeConnectionId"));
            String clientMessageTypeString = (String) json.get("ClientMessageType");
            ClientMessageType clientMessageType = GetClientMessageType(clientMessageTypeString);
            return new NodeJob(jobId, nodeConnectionId, result, clientMessageType, duration, completed);
        } catch (ParseException | InvalidMessageTypeException ex) 
        {
            Logger.getLogger(RequestMessage.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    private static ClientMessageType GetClientMessageType(String clientMessageType) throws InvalidMessageTypeException
    {
        for (ClientMessageType cmt : ClientMessageType.values()) 
        {
            if (cmt.name().equals(clientMessageType)) 
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
