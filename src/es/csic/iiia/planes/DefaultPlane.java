/*
 * Software License Agreement (BSD License)
 *
 * Copyright 2012 Marc Pujol <mpujol@iiia.csic.es>.
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

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Marc Pujol <mpujol@iiia.csic.es>
 */
public class DefaultPlane extends AbstractPlane {
    
    public DefaultPlane(Location location) {
        super(location);
    }

    @Override
    protected void taskCompleted(Task t) {
        setNextTask(getNearestTask());
    }
    
    @Override
    protected void taskAdded(Task t) {
        setNextTask(getNearestTask());
    }
    
    @Override
    public List<Location> getPlannedLocations() {
        List<Location> plannedLocations = new ArrayList<Location>();
        // Add all the tasks to a list
        ArrayList<Task> candidateTasks = new ArrayList<Task>(getTasks());
        Location nextLocation = getLocation();
        while (!candidateTasks.isEmpty()) {
            Task next = getNearest(nextLocation, candidateTasks);
            candidateTasks.remove(next);
            nextLocation = next.getLocation();
            plannedLocations.add(nextLocation);
        }
        
        return plannedLocations;
    }
    
    /**
     * Retrieve the nearest task among the ones owned by this plane
     * 
     * @return nearest task
     */
    private Task getNearestTask() {
        return getNearest(getLocation(), getTasks());
    }
    
    /**
     * Retrieve the task from the candidates list that is nearest to the given
     * position
     * 
     * @param position
     * @param candidates
     * @return 
     */
    private static Task getNearest(Location position, List<Task> candidates) {
        double max = Double.MAX_VALUE;
        Task result = null;
        for (Task candidate : candidates) {
            double d = position.distance(candidate.getLocation());
            if (d < max) {
                max = d;
                result = candidate;
            }
        }
        
        return result;
    }
    
}