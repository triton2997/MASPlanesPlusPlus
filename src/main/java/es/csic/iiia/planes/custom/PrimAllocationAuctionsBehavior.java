package es.csic.iiia.planes.custom;

import es.csic.iiia.planes.Task;
import es.csic.iiia.planes.behaviors.AbstractBehavior;
import es.csic.iiia.planes.behaviors.neighbors.NeighborTracking;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

public class PrimAllocationAuctionsBehavior extends AbstractBehavior<CustomPlane> {
    
    private PriorityQueue<BidMessage> collectedBids;
    private PriorityQueue<BidMessage> taskQueue;
    private ArrayList<Task> tasksWon;
    private MyTasksMST mst;
    private GilbertElliottModel GEModel;
    
    private boolean isAuctionOngoing;
    private int auctionTaskCount;

    // Change this parameter to use Bernoulli model with some probability value p
    private double p = 1;

    public PrimAllocationAuctionsBehavior(CustomPlane agent) {
        super(agent);
        this.taskQueue = new PriorityQueue<BidMessage>();
        this.collectedBids = new PriorityQueue<BidMessage>();
        this.tasksWon = new ArrayList<Task>();
        this.mst = new MyTasksMST(getAgent());
        this.auctionTaskCount = 0;
        this.isAuctionOngoing = false;
        
        // Update this line to use Gilbert-Elliott model with some p_gg and p_bb
        this.GEModel = new GilbertElliottModel(0.9,0.9);
    }

    @Override
    public Class[] getDependencies() {
        return null;
    }

    @Override
    public void beforeMessages() {
    }

    // public void addBid(BidMessage bid) {
    //     this.collectedBids.add(bid);
    // }

    public BidMessage sendNextBid() {
        // this.addBid(taskQueue.peek());
        CustomPlane plane = getAgent();
        plane.send(taskQueue.peek());
        return taskQueue.peek();
    }

    public BidMessage addTaskToQueue(Task t) {
        double task_add_cost = this.mst.calculateTaskAddCost(t);
        BidMessage msg = new BidMessage(t, task_add_cost);
        msg.setSender(getAgent());
        this.taskQueue.add(msg);

        return msg;
        //mst.addTask(t);
    }

    public void allocateTask(Task t) {
        getAgent().addTask(t);
        for(BidMessage msg: this.taskQueue) {
            msg.setCost(this.mst.calculateTaskAddCost(msg.getTask()));
        }
    }

    public void removeTaskFromQueue(Task t) {
        for(BidMessage msg: taskQueue){
            if(msg.getTask().getId() == t.getId()){
                this.taskQueue.remove(msg);
                break;
            }
        }
        // this.printTaskQueue();
    }
    
    private void openAuctions() {
        CustomPlane plane = getAgent();
        if (plane.getQueuedTasks().isEmpty() == false) {
            this.isAuctionOngoing = true;
            for (Task t : plane.getQueuedTasks()) {
                BidMessage msg = this.addTaskToQueue(t);
            }
            this.sendNextBid();

            // ArrayList<Task> tasksCopy = new ArrayList<Task>(plane.getQueuedTasks());
            // for(Task t: tasksCopy) {
            //     plane.removeTask(t);
            // }
            plane.getQueuedTasks().clear();
            // System.out.println("Task Queue at " + getAgent());
            // for(BidMessage msg: this.taskQueue) {
            //     System.out.println(msg.getTask() + ": " + msg.getCost());
            // }
            // System.out.println();
        }
    }

    public void on(BidMessage bid) {

        CustomPlane sender = (CustomPlane)bid.getSender();

        // Bernoulli model
        // Uncomment to use
        if(getAgent() == sender) {
            collectedBids.add(bid);
        }
        else if (BernoulliModel.bernoulli(p)) {
            getAgent().incrementReceivedMessages();
            collectedBids.add(bid);
        }
        else {
            bid.setCost(Double.MAX_VALUE);
            collectedBids.add(bid);
        }

        // Gilbert Elliott model
        // Comment this and uncomment the above if you want to use bernoulli model
        // if(getAgent() == sender) {
        //     collectedBids.add(bid);
        // }
        // else if (GEModel.isReceived()) {
        //     getAgent().incrementReceivedMessages();
        //     collectedBids.add(bid);
        // }
        // else {
        //     bid.setCost(Double.MAX_VALUE);
        //     collectedBids.add(bid);
        // }
    }

    @Override
    public void afterMessages() {
        if (this.isAuctionOngoing == false) {
            this.openAuctions();
        }

        // Compute auction winners only if we have received bids in this step
        if (!collectedBids.isEmpty()) {
            // System.out.println(this.getAgent() + ": Computing next winner");
            computeAuctionWinners();
        }
    }

    public void computeAuctionWinners() {
        BidMessage winningBid = collectedBids.poll();
        CustomPlane winningAgent = (CustomPlane)winningBid.getSender();
        if (winningAgent == this.getAgent()) {
            // System.out.println("Plane " + winningAgent.getId() + " won task " + winningBid.getTask().getId());
            this.removeTaskFromQueue(winningBid.getTask());
            this.allocateTask(winningBid.getTask());
        }
        else {
            // System.out.println(this.getAgent() + " removing task " + winningBid.getTask().getId());
            this.removeTaskFromQueue(winningBid.getTask());
        }
        collectedBids.clear();
        if (this.taskQueue.isEmpty()) {
            this.isAuctionOngoing = false;
            CustomPlane plane = getAgent();
            // System.out.println("Tasks allocated to " + plane);
            // for(Task t: plane.getTasks()) {
            //     System.out.println(t);
            // }
        }
        else {
            this.sendNextBid();
        }
    }
}