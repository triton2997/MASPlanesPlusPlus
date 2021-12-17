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
    
    private boolean isAuctionOngoing;
    private int auctionTaskCount;

    public PrimAllocationAuctionsBehavior(CustomPlane agent) {
        super(agent);
        this.taskQueue = new PriorityQueue<BidMessage>();
        this.collectedBids = new PriorityQueue<BidMessage>();
        this.tasksWon = new ArrayList<Task>();
        this.mst = new MyTasksMST(this);
        this.auctionTaskCount = 0;
        this.isAuctionOngoing = false;
    }

    @Override
    public Class[] getDependencies() {
        return null;
    }

    @Override
    public void beforeMessages() {
        collectedBids.clear();
    }

    public void addBid(BidMessage bid) {
        this.collectedBids.add(bid);
    }

    public BidMessage getNextBid() {
        this.addBid(taskQueue.peek());
        return taskQueue.peek();
    }

    public BidMessage addTaskToQueue(Task t) {
        double task_add_cost = this.mst.calculateTaskAddCost(t);
        BidMessage msg = new BidMessage(t, task_add_cost, this);
        this.taskQueue.add(msg);

        return msg;
        //mst.addTask(t);
    }

    public void allocateTask(Task t) {
        getAgent().addTask(t);
    }

    public void removeTaskFromQueue(Task t) {
        // System.out.printf("Removing task %s from agent %s\n", t.getId(), this.id);
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
        if (plane.getTasks()) {
            this.isAuctionOngoing = true;
            for (Task t : plane.getTasks()) {
                OpenAuctionMessage msg = new OpenAuctionMessage(t);
                plane.send(msg);
                plane.removeTask(t);
                this.auctionTaskCount += 1;
            }
        }
    }

    public void on(OpenAuctionMessage auction) {
        CustomPlane plane = getAgent();
        Task t = auction.getTask();
        // On receiving OpenAuctionMessage, broadcast the bid to all robots
        // double cost = plane.getLocation().distance(t.getLocation());
        // BidMessage bid = new BidMessage(t, cost);
        // bid.setRecipient(auction.getSender());
        // plane.send(bid);
        this.isAuctionOngoing = true;
        this.auctionTaskCount += 1;
        BidMessage msg = this.addTaskToQueue(t);
        plane.send(msg);
    }

    public void on(BidMessage bid) {
        // Task t = bid.getTask();
        // Add bid to the list of collected bids
        // List<BidMessage> taskBids = collectedBids.get(t);
        // if (taskBids == null) {
        //     taskBids = new ArrayList<BidMessage>();
        //     collectedBids.put(t, taskBids);
        // }
        collectedBids.add(bid);
    }

    // public void on(ReallocateMessage msg) {
    //     getAgent().addTask(msg.getTask());
    // }

    @Override
    public void afterMessages() {
    // Open new auctions only once every four steps
        // if (getAgent().getWorld().getTime() % 4 == 0) {
        //     openAuctions();
        // }
        this.auctionTaskCount -= 1;
        
        if (this.auctionTaskCount <= 0){
            this.auctionTaskCount = 0;
            this.isAuctionOngoing = false;
            openAuctions();
        }
        // if (!isAuctionOngoing){
        //     isAuctionOngoing = true;
        //     openAuctions();
        // }
        // if collectedBids is empty, initiate auctions
        // if (collectedBids.isEmpty()){
        //     OpenAuctionMessage msg = tasks.poll();
        //     BidMessage bid = new BidMessage(msg.getTask(), cost);
        // }

        // Compute auction winners only if we have received bids in this step
        if (!collectedBids.isEmpty()) {
            computeAuctionWinners();
        }
    }

    public void computeAuctionWinners() {
        BidMessage winningBid = collectedBids.poll();
        CustomPlane winningAgent = winningBid.getAgent();
        // System.out.printf("Winner as calculated by %s is %s for task %s\n", this.id, winningAgent.getId(), winningBid.getTask().getId());
        // winningAgent.allocateTask(winningBid);
        // ReallocateMessage msg = new ReallocateMessage(winningBid.getTask());
        // msg.setRecipient(winningAgent);
        // plane.send(msg);
        if(winningAgent == getAgent()) {
            this.allocateTask(winningBid.getTask());
        }
        else {
            this.removeTaskFromQueue(winningBid.getTask());
        }
        // this.printCollectedBids();
        // for(BidMessage msg: collectedBids) {
        //     msg.getAgent().removeTask(winningBid.getTask());
        // }
        collectedBids.clear();
    }

    // private void reallocateTask(BidMessage winner) {
    //     CustomPlane plane = getAgent();

    //     // No need to reallocate when the task is already ours
    //     if (winner.getSender() == plane) {
    //         return;
    //     }

    //     // Remove the task from our list of pending tasks
    //     plane.removeTask(winner.getTask());

    //     // Send it to the auction's winner
    //     ReallocateMessage msg = new ReallocateMessage(winner.getTask());
    //     msg.setRecipient(winner.getSender());
    //     plane.send(msg);
    // }
}