package Loadbalancer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import loadbalancer_common.MessageTypes.ClientMessageType;
import loadbalancer_common.NodeJob.NodeJob;

public class LoadBalanceController 
{
    private List<LoadBalanceNode> _nodes;
    
    private LoadBalanceJobRepository _jobRepository; 
    
    public LoadBalanceController()
    {
        this._nodes = new ArrayList<LoadBalanceNode>();
        _jobRepository = new LoadBalanceJobRepository();
    }
    
    public List<LoadBalanceNode> GetNodes()
    {
        return _nodes;
    }
    
    public void AddNode(UUID connectionId, int maxJobSize, int Port)
    {
        LoadBalanceNode node = new LoadBalanceNode(connectionId, maxJobSize, Port);
        this._nodes.add(node);
    }
    
    public void RemoveNode(UUID connectionId)
    {
        LoadBalanceNode node = GetNode(connectionId);
        this._nodes.remove(node);
    }
    
    public LoadBalanceNode GetNode(UUID connectionId)
    {
        LoadBalanceNode node = null;
        
        for (LoadBalanceNode lbn : _nodes)
        {
            if (lbn.ConnectionId.equals(connectionId))
            {
                node = lbn;
                break;
            }
        }
        
        return node;
    }
    
    public List<NodeJob> GetJobs()
    {
        return _jobRepository.GetJobNodes();
    }
    
    public LoadBalanceNodeJobRequestThread CreateLoadBalanceNodeJob(
            ClientMessageType clientMessageType,
            int duration)
    {
        LoadBalanceNode node = GetRoundRobinLoadBalanceNode();
        NodeJob job =_jobRepository.CreateJob(node.ConnectionId, clientMessageType, duration);
        return new LoadBalanceNodeJobRequestThread(node, job);
    }
    
    public void CompleteLoadBalanceNodeJob(
            UUID connectionId,
            UUID jobId,
            String result
        )
    {
        _jobRepository.CompleteJob(jobId, result);
    }
    
    private LoadBalanceNode GetRoundRobinLoadBalanceNode() 
    {   
        HashMap<UUID, Float> nodeIdList = new HashMap<UUID, Float>();
        for (LoadBalanceNode node : _nodes)
        {
            float weight = GetAvailableNodeWeight(node);
            nodeIdList.put(node.ConnectionId, weight);
        }
        
        //Convert hashmap to list map
        List<Map.Entry<UUID, Float> > nodeWeightList =
               new LinkedList<Map.Entry<UUID, Float>>(nodeIdList.entrySet());
        
        //Sort list map by weight percentage
        Collections.sort(nodeWeightList, new Comparator<Map.Entry<UUID, Float> >() 
        {
            @Override
            public int compare(Map.Entry<UUID, Float> o1,
                               Map.Entry<UUID, Float> o2)
            {
                return ((o1.getValue()).compareTo(o2.getValue()));
            }
        });
        //Pick top node with most left over job space
        UUID id = nodeWeightList.get((nodeWeightList.size() - 1)).getKey();
        return GetNode(id);
    }
    
    private float GetAvailableNodeWeight(LoadBalanceNode node)
    {
        int maxJobSize = node.MaxJobSize;
        List<NodeJob> jobs = _jobRepository.GetJobsByNodeId(node.ConnectionId);
        float weight =  (float) 1.0 - (float) (jobs.size()) / (maxJobSize);
        System.out.println(weight);
        return weight;
    }
    
}
