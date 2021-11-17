package loadbalancer_node.infrastructure;

import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import loadbalancer_common.MessageTypes.ClientMessageType;
import loadbalancer_common.NodeJob.NodeJob;

public class NodeCommand extends Thread
{
    private UUID _connectionId;
    
    private NodeSender _nodeSender;
    
    private NodeJob _nodeJob;
    
    public NodeCommand(UUID connectionId, NodeSender nodeSender, NodeJob nodeJob) 
    {
        _connectionId = connectionId;
        _nodeSender = nodeSender;
        _nodeJob = nodeJob;
    }
    
    
    @Override
    public void run() 
    {
        ClientMessageType clientMessageType = _nodeJob.GetClientMessageType();
        
        switch(clientMessageType){
            case RunDuration:
            {
                try {
                    RunDuration();
                } catch (InterruptedException ex) {
                    Logger.getLogger(NodeCommand.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
                break;

        }
        
        this.CompleteJob();
    }
    
    private void RunDuration() throws InterruptedException
    {
        Thread.sleep(_nodeJob.GetDuration());
        _nodeJob.SetResult("Job ran for " + _nodeJob.GetDuration());
    }
    
    private void CompleteJob()
    {
        _nodeJob.Complete();
        _nodeSender.JobComplete(_connectionId, _nodeJob);
    }
}
