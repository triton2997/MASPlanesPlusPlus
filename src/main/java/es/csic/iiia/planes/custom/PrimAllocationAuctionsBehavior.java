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
    
    private PriorityQueue<BidMessage> collectedBids =
        new ArrayList<BidMessage>();
    private PriorityQueue<BidMessage> taskQueue = 
        new PriorityQueue<BidMessage>();
    
    private boolean isAuctionOngoing;

    public PrimAllocationAuctionsBehavior(CustomPlane agent) {
        super(agent);
        isAuctionOngoing = false;
    }

    @Override
    public Class[] getDependencies() {
        return null;
    }

    @Override
    public void beforeMessages() {
        collectedBids.clear();
    }

    private void openAuctions() {
        CustomPlane plane = getAgent();
        for (Task t : plane.getTasks()) {
            OpenAuctionMessage msg = new OpenAuctionMessage(t);
            plane.send(msg);
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
        tasks.add(auction);
    }

    public void on(BidMessage bid) {
        Task t = bid.getTask();

        // Add bid to the list of collected bids
        // List<BidMessage> taskBids = collectedBids.get(t);
        // if (taskBids == null) {
        //     taskBids = new ArrayList<BidMessage>();
        //     collectedBids.put(t, taskBids);
        // }

        collectedBids.add(bid);
    }

    public void on(ReallocateMessage msg) {
        getAgent().addTask(msg.getTask());
    }

    @Override
    public void afterMessages() {
    // Open new auctions only once every four steps
        // if (getAgent().getWorld().getTime() % 4 == 0) {
        //     openAuctions();
        // }
        if (!isAuctionOngoing){
            isAuctionOngoing = true;
            openAuctions();
        }
        // if collectedBids is empty, send bids
        if (collectedBids.isEmpty()){
            OpenAuctionMessage msg = tasks.poll();
            BidMessage bid = new BidMessage(msg.getTask(), cost);
        }
        // Compute auction winners only if we have received bids in this step
        if (!collectedBids.isEmpty()) {
            computeAuctionWinners();
        }
    }

    private void computeAuctionWinners() {
        // For each auction we opened
        for (Task t : collectedBids.keySet()) {
            // Determine the winner
            BidMessage winner = computeAuctionWinner(collectedBids.get(t));
            // Reallocate the task
            reallocateTask(winner);
        }
    }
    
    private BidMessage computeAuctionWinner(List<BidMessage> bids) {
        BidMessage winner = null;
        double minCost = Double.MAX_VALUE;
    
        for (BidMessage bid : bids) {
            if (bid.getCost() < minCost) {
                winner = bid;
                minCost = bid.getCost();
            }
        }
    
        return winner;
    }

    private void reallocateTask(BidMessage winner) {
        CustomPlane plane = getAgent();

        // No need to reallocate when the task is already ours
        if (winner.getSender() == plane) {
            return;
        }

        // Remove the task from our list of pending tasks
        plane.removeTask(winner.getTask());

        // Send it to the auction's winner
        ReallocateMessage msg = new ReallocateMessage(winner.getTask());
        msg.setRecipient(winner.getSender());
        plane.send(msg);
    }
}