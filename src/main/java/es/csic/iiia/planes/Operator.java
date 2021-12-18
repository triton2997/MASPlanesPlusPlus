/*
 * Software License Agreement (BSD License)
 *
 * Copyright (c) 2012, IIIA-CSIC, Artificial Intelligence Research Institute
 * All rights reserved.
 *
 * Redistribution and use of this software in source and binary forms, with or
 * without modification, are permitted provided that the following conditions
 * are met:
 *
 *   Redistributions of source code must retain the above
 *   copyright notice, this list of conditions and the
 *   following disclaimer.
 *
 *   Redistributions in binary form must reproduce the above
 *   copyright notice, this list of conditions and the
 *   following disclaimer in the documentation and/or other
 *   materials provided with the distribution.
 *
 *   Neither the name of IIIA-CSIC, Artificial Intelligence Research Institute
 *   nor the names of its contributors may be used to
 *   endorse or promote products derived from this
 *   software without specific prior written permission of
 *   IIIA-CSIC, Artificial Intelligence Research Institute
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package es.csic.iiia.planes;

import es.csic.iiia.planes.definition.DTask;
import es.csic.iiia.planes.gui.Drawable;
import es.csic.iiia.planes.gui.graphics.OperatorGraphic;
import es.csic.iiia.planes.messaging.Message;
import es.csic.iiia.planes.operator_behavior.OperatorStrategy;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Operator that will be submitting tasks to the UAVs.
 *
 * @author Marc Pujol <mpujol at iiia.csic.es>
 */
public class Operator extends AbstractMessagingAgent implements Drawable {

    private static AtomicInteger idGenerator = new AtomicInteger();

    private int id = idGenerator.incrementAndGet();

    /**
     * List of the definitions of all the tasks that this operator will submit
     * during the simulation.
     */
    private List<DTask> tasks;

    /**
     * Index of the next task to be submitted.
     */
    private int nextTask = 0;

    /**
     * Time step at which the next task has to be submitted.
     */
    private long nextTaskTime;

    /**
     * The strategy that this operator will use to submit tasks.
     */
    private OperatorStrategy strategy;

    /**
     * Tasks that should have been submitted to some UAV, but were not because
     * no plane is range.
     */
    List<Task> delayedTasks = new ArrayList<Task>();

    /**
     * Creates a new operator that will submit the given list of tasks.
     *
     * @param tasks to be submitted by this operator.
     */
    public Operator(Location position, List<DTask> tasks) {
        super(position);
        this.tasks = tasks;
        Collections.sort(this.tasks, new TaskSorter());
        nextTaskTime = this.tasks.get(0).getTime();
    }

    @Override
    public void initialize() {}

    /**
     * Get the strategy used by this operator.
     *
     * @return stategy used by this operator.
     */
    public OperatorStrategy getStrategy() {
        return strategy;
    }

    /**
     * Set the strategy used by this operator.
     *
     * @param strategy used by this operator.
     */
    public void setStrategy(OperatorStrategy strategy) {
        this.strategy = strategy;
    }

    /**
     * Do nothing, because no step initialization is needed by the operator.
     */
    @Override
    public void preStep() {}

    private ArrayList<DTask> pendingTasks = new ArrayList<DTask>();

    private int pendingTaskCount = 0;

    /**
     * Single-step advance of this operator.
     *
     * If the simulation has reached a point where one task should be submitted,
     * the operator creates and submits it according to the specified submission
     * strategy.
     */
    @Override
    public void step() {
        while (nextTaskTime <= getWorld().getTime()) {
            // Task t = createTask(tasks.get(nextTask));
            pendingTasks.add(tasks.get(nextTask));

            tasks.set(nextTask, null);
            nextTask++;

            if (nextTask == tasks.size()) {
                nextTaskTime = Long.MAX_VALUE;
            } else {
                nextTaskTime = tasks.get(nextTask).getTime();
            }

            this.pendingTaskCount += 1;
        }

        if (isPlaneInRange() && !pendingTasks.isEmpty() && this.pendingTaskCount == 5) {
            for (DTask dt : pendingTasks) {
                Task t = createTask(dt);
                strategy.submitTask(getWorld(), this, t);
            }
            pendingTaskCount = 0;
            pendingTasks.clear();
        }
    }

    /**
     * Do nothing, because no step finalization is needed by the operator.
     */
    @Override
    public void postStep() {}

    /**
     * Create a simulation Task from the given Task definition.
     *
     * @param task definition.
     * @return actual simulation Task.
     */
    private Task createTask(DTask nt) {
        Location l = new Location(nt.getX(), nt.getY());
        Task t = getWorld().getFactory().buildTask(l);
        return t;
    }

    private static OperatorGraphic og = new OperatorGraphic();
    @Override
    public void draw(Graphics2D g) {
        int x = getLocation().getXInt();
        int y = getLocation().getYInt();

        Color previous = g.getColor();
        int dim = (int)(500 * getWorld().getSpace().getWidth() / 10000f);
        /*
        g.setColor(Color.BLUE);
        int dim = (int)(200 * getWorld().getSpace().getWidth() / 10000f);
        g.fillOval(x-dim/2, y-dim/2, dim, dim); */
        og.setDimension(new Dimension(dim,dim));
        og.paint(g, x-dim/2, y-dim/2);

        g.setColor(new Color(200,200,255,100));
        final int r = (int)getCommunicationRange();
        g.fillOval(x-r, y-r, r*2, r*2);

        g.setColor(previous);
    }

    private boolean isPlaneInRange() {
        final Location l = getLocation();

        for (Plane p : getWorld().getPlanes()) {
            if (l.getDistance(p.getLocation()) <= getCommunicationRange()) {
                return true;
            }
        }

        return false;
    }

    @Override
    public void send(Message message) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void receive(Message message) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int compareTo(Object t) {
        if (t == null) {
            return -1;
        }
        if (t instanceof Operator) {
            return id - ((Operator)t).id;
        }

        return 1;
    }

    /**
     * Comparator of DTasks that is used to sort the list of task definitions
     * by increasing submission time.
     */
    private class TaskSorter implements Comparator<DTask> {
        @Override
        public int compare(DTask t, DTask t1) {
            return Long.valueOf(t.getTime()).compareTo(t1.getTime());
        }
    }

}