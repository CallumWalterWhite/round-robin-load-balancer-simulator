package Loadbalancer;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import loadbalancer_common.MessageTypes.ClientMessageType;
import loadbalancer_common.NodeJob.NodeJob;

public class LoadBalanceJobRepository {
    
    private List<NodeJob> _nodeJobs; 
    
    public LoadBalanceJobRepository()
    {
        _nodeJobs = new ArrayList<NodeJob>();
    }
    
    public NodeJob CreateJob(
            UUID nodeId,
            ClientMessageType clientMessageType,
            int duration)
    {
        UUID jobId = UUID.randomUUID();
        NodeJob nodeJob = new NodeJob(jobId, nodeId, clientMessageType, duration);
        _nodeJobs.add(nodeJob);
        return nodeJob;
    }
    
    
    public void CompleteJob(
            UUID jobId,
            String result)
    {
        NodeJob nodeJob = GetJob(jobId);
        nodeJob.Complete();
        nodeJob.SetResult(result);
    }
    
    public NodeJob GetJob(UUID jobId)
    {
        NodeJob job = null;
        
        for (NodeJob nodeJob : _nodeJobs)
        {
            if (nodeJob.GetJobId().equals(jobId))
            {
                job = nodeJob;
                break;
            }
        }
        
        return job;
    }
    
    public List<NodeJob> GetJobsByNodeId(UUID nodeId)
    {
        List<NodeJob> jobs = new ArrayList<NodeJob>();
        
        for (NodeJob nodeJob : _nodeJobs)
        {
            if (nodeJob.GetNodeConnectionId().equals(nodeId))
            {
                jobs.add(nodeJob);
                break;
            }
        }
        
        return jobs;
    }
    
    public List<NodeJob> GetJobNodes()
    {
        return _nodeJobs;
    }
}
