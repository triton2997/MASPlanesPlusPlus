package es.csic.iiia.planes.custom;

import es.csic.iiia.planes.Task;
import es.csic.iiia.planes.messaging.AbstractMessage;

public class OpenAuctionMessage extends AbstractMessage {

    private Task task;

    public OpenAuctionMessage(Task t) {
        this.task = t;
    }

    public Task getTask() {
        return task;
    }

}
