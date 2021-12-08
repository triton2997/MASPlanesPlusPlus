package es.csic.iiia.planes.custom;
import es.csic.iiia.planes.Task;

import java.util.HashSet;

public class MyTasksMST {

    private HashSet<Task> myTasks;
    private CustomPlane myAgent;

    public MyTasksMST(CustomPlane plane) {
        myTasks = new HashSet<Task>
        myAgent = 
    }

    public void addTask(Task t){
        myTasks.add(t);
    }

    public int calculateTaskAddCost(Task t){
        // calculate current MST
        int curr_mst = calculateMST();

        // add task to vertex list
        myTasks.add(t);
        
        // calculate MST
        int new_mst = calculateMST();
        
        // remove task from vertex list
        myTasks.remove(t);
        
        // calculate MST
        return new_mst - curr_mst;
    }
    public Task getMinTask(HashMap<Task, Boolean> mstSet, HashMap<Task, int> key){
        int min_cost = INT_MAX;
        Task min_t;
        for(Task t: myTasks){
            if(key.get(t) < min_cost && mstSet.get(t)){
                min_cost = key.get(t);
                min_t = t;
            }
        }
        return min_t;
    }
    public int calculateMST(){
        // Array to store constructed MST
        HashMap<Task, Task> parent = new HashMap();
        
        // Key values used to pick minimum weight edge in cut
        HashMap<Task, int> key = new HashMap();
        
        // To represent set of vertices included in MST
        HashMap<Task, Boolean> mstSet = new HashMap();

        Task first_vertex = null;
        int min_cost = INT_MAX
        int total_cost = 0;
        // Initialize all keys as INFINITE
        for (Task t: myTasks){
            parent.put(t, null);
            key.put(t, INT_MAX);
            mstSet.put(t, false);
            if(!first_vertex || myAgent.getLocation().distance(t.getLocation()) < min_cost){
                first_vertex = t;
                min_cost = myAgent.getLocation().distance(t.getLocation());
            }
        }
        // Always include first 1st vertex in MST.
        // Make key 0 so that this vertex is picked as first vertex.
        key.put(t, min_cost);
        // parent.put(t, null) = -1; // First node is always root of MST

        // The MST will have V vertices
        for (int count = 0; count < myTasks.size(); count++){
            // Pick the minimum key vertex from the
            // set of vertices not yet included in MST
            Task t = getMinTask(mstSet, key);

            // Add the picked vertex to the MST Set
            mstSet.put(t, true);
            total_cost += key.get(t);
    
            // Update key value and parent index of
            // the adjacent vertices of the picked vertex.
            // Consider only those vertices which are not
            // yet included in MST
            for (Task t_oth: myTasks){
                // graph[u][v] is non zero only for adjacent vertices of m
                // mstSet[v] is false for vertices not yet included in MST
                // Update the key only if graph[u][v] is smaller than key[v]
                if (mstSet.get(t_oth) == false && t.getLocation().distance(t_oth.getLocation()) < key.get(t_oth)){
                    parent.get(t_oth) = t;
                    key.put(t_oth, t.getLocation().distance(t_oth.getLocation()));
                }
            }
        }

        return 0;
    }

}