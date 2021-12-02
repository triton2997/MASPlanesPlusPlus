package es.csic.iiia.planes.custom;

import es.csic.iiia.planes.Task;
import es.csic.iiia.planes.messaging.AbstractMessage;

public class ReallocateMessage extends AbstractMessage {

    private Task task;

    public ReallocateMessage(Task t) {
        this.task = t;
    }

    public Task getTask() {
        return task;
    }

}
