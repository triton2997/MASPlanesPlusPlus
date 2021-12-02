package es.csic.iiia.planes.custom;

import es.csic.iiia.planes.Task;
import es.csic.iiia.planes.messaging.AbstractMessage;

public class BidMessage extends AbstractMessage {

    private double cost;
    private Task task;

    public BidMessage(Task t, double cost) {
        this.task = t;
        this.cost = cost;
    }

    public double getCost() {
        return cost;
    }

    public Task getTask() {
        return this.task;
    }

}