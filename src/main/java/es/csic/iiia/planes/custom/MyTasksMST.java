package es.csic.iiia.planes.custom;
import es.csic.iiia.planes.Task;

import java.util.HashSet;
import java.util.HashMap;

public class MyTasksMST {

    private HashSet<MSTNode> myTasks;
    private CustomPlane myAgent;
    private MSTNode agentNode;

    public MyTasksMST(CustomPlane plane) {
        this.myTasks = new HashSet<MSTNode>();
        this.myAgent = plane;
        agentNode = new MSTNode(plane);
        myTasks.add(agentNode);
    }

    public void addTask(Task t){
        for(MSTNode n: myTasks) {
            if(n.getTask() != null && n.getTask() == t)
                return;
        }
        MSTNode node = new MSTNode(t);
        myTasks.add(node);
    }

    public double calculateTaskAddCost(Task t){
        // calculate current MST
        double curr_mst = 0;
        if (myTasks.size() > 1) {
            curr_mst = calculateMST();
        }

        // add task node to vertex list
        MSTNode node = new MSTNode(t);
        this.myTasks.add(node);
        
        // calculate MST
        double new_mst = calculateMST();
        
        // remove task from vertex list
        myTasks.remove(node);
        
        // calculate MST
        return new_mst - curr_mst;
    }
    public MSTNode getMinTask(HashMap<MSTNode, Boolean> mstSet, HashMap<MSTNode, Double> key){
        double min_cost = Double.MAX_VALUE;
        MSTNode min_t = null;
        for(MSTNode t: myTasks){
            if(key.get(t) < min_cost && mstSet.get(t) == false){
                min_cost = key.get(t);
                min_t = t;
            }
        }
        return min_t;
    }
    public double calculateMST() {
        // System.out.printf("There are currently %d nodes\n", myTasks.size());
        // Array to store constructed MST
        HashMap<MSTNode, MSTNode> parent = new HashMap();
        
        // Key values used to pick minimum weight edge in cut
        HashMap<MSTNode, Double> key = new HashMap();
        
        // To represent set of vertices included in MST
        HashMap<MSTNode, Boolean> mstSet = new HashMap();

        // Task first_vertex = null;
        // int min_cost = INT_MAX
        double total_cost = 0;
        // Initialize all keys as INFINITE
        for (MSTNode t: myTasks) {
            parent.put(t, null);
            key.put(t, Double.MAX_VALUE);
            mstSet.put(t, false);
            // if(!first_vertex || myAgent.getLocation().distance(t.getLocation()) < min_cost){
            //     first_vertex = t;
            //     min_cost = myAgent.getLocation().distance(t.getLocation());
            // }
        }
        // Always include first 1st vertex in MST.
        // Make key 0 so that this vertex is picked as first vertex.
        key.put(this.agentNode, 0.0);
        parent.put(this.agentNode, null); // First node is always root of MST

        // The MST will have V vertices
        for (int count = 0; count < myTasks.size(); count++){
            // Pick the minimum key vertex from the
            // set of vertices not yet included in MST
            MSTNode t = getMinTask(mstSet, key);
            // System.out.printf("min task - Type - %s, id: %d\n", t.getType(), t.getId());
            // Add the picked vertex to the MST Set
            mstSet.put(t, true);
            total_cost += key.get(t);
    
            // Update key value and parent index of
            // the adjacent vertices of the picked vertex.
            // Consider only those vertices which are not
            // yet included in MST
            for (MSTNode t_oth: myTasks){
                // graph[u][v] is non zero only for adjacent vertices of m
                // mstSet[v] is false for vertices not yet included in MST
                // Update the key only if graph[u][v] is smaller than key[v]
                if (mstSet.get(t_oth) == false && t.getDistance(t_oth) < key.get(t_oth)){
                    parent.put(t_oth, t);
                    key.put(t_oth, t.getDistance(t_oth));
                }
            }
        }

        return total_cost;
    }

}