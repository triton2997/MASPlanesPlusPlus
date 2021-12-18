package es.csic.iiia.planes.custom;

import es.csic.iiia.planes.DefaultPlane;
import es.csic.iiia.planes.Location;
import es.csic.iiia.planes.behaviors.neighbors.NeighborTracking;

public class CustomPlane extends DefaultPlane {

    public CustomPlane(Location location) {
        super(location);
        // addBehavior(new NeighborTracking(this));
        addBehavior(new PrimAllocationAuctionsBehavior(this));
    }
}