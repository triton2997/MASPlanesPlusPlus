package es.csic.iiia.planes.custom;

import es.csic.iiia.planes.Task;

public class MSTNode {

    private CustomPlane agent;
    private Task task;

    public MSTNode(Task t) {
        this.task = t;
        this.agent = null;
    }

    public MSTNode(CustomPlane a) {
        this.task = null;
        this.agent = a;
    }

    public CustomPlane getAgent() {
        return this.agent;
    }

    public Task getTask() {
        return this.task;
    }

    public int getId() {
        if(this.agent == null)
            return this.task.getId();
        return this.agent.getId();
    }

    public String getType() {
        if(this.agent == null)
            return "Task";
        return "Agent";
    }

    public double getDistance(MSTNode t) {
        if(this.agent == null)
            return this.task.getLocation().distance(t.getTask().getLocation());
        return this.agent.getLocation().distance(t.getTask().getLocation());
    }

}