package es.csic.iiia.planes.custom;

import es.csic.iiia.planes.Task;
import es.csic.iiia.planes.messaging.AbstractMessage;

public class BidMessage extends AbstractMessage implements Comparable<BidMessage> {

    private double cost;
    private Task task;

    public BidMessage(Task t, double cost) {
        this.task = t;
        this.cost = cost;
    }

    public double getCost() {
        return cost;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }

    public Task getTask() {
        return this.task;
    }

    public Boolean compareTo(BidMessage m1) {
        if (m1.getCost() == this.cost)
            return this.getSender().getId() - m1.getSender().getId();
        
        return this.cost - m1.getCost();
    }

}