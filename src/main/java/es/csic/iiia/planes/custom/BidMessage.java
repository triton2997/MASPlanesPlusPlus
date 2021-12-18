package es.csic.iiia.planes.custom;

import es.csic.iiia.planes.Task;
import es.csic.iiia.planes.AbstractPlane;
import es.csic.iiia.planes.messaging.AbstractMessage;

import java.lang.Math;

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

    public int compareTo(BidMessage m1) {
        if (m1.getCost() == this.cost) {
            CustomPlane me = (CustomPlane)this.getSender();
            CustomPlane compared = (CustomPlane)m1.getSender();
            return me.getId() - compared.getId();
        }
        
        return (int)Math.round(this.cost - m1.getCost());
    }

}