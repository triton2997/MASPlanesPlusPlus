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

import es.csic.iiia.planes.util.TimeTracker;
import es.csic.iiia.planes.custom.CustomPlane;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

/**
 *
 * @author Marc Pujol <mpujol@iiia.csic.es>
 */
class StatsCollector {

    private AbstractWorld world;
    private DescriptiveStatistics taskStats = new DescriptiveStatistics();
    private DescriptiveStatistics planeStats = new DescriptiveStatistics();
    private DescriptiveStatistics sentMessageStats = new DescriptiveStatistics();
    private DescriptiveStatistics receivedMessageStats = new DescriptiveStatistics();

    public StatsCollector(AbstractWorld w) {
        world = w;
    }

    public void collect(Task t) {
        final long time = world.getTime() - t.getSubmissionTime();
        taskStats.addValue(time);
    }

    public void collect(Plane p) {
        planeStats.addValue(p.getTotalDistance());
    }

    public void collect(MessagingAgent a) {
        int sentMessages = a.getSentMessages();
        int receivedMessages = a.getReceivedMessages();
        if (a instanceof CustomPlane) {
            sentMessageStats.addValue(a.getSentMessages());
            receivedMessageStats.addValue(a.getReceivedMessages());
        }
    }

    public void display() {
        // Final stats
        StringBuilder buf = new StringBuilder();

        buf.append("\nplane_min=").append((long)(planeStats.getMin()/1000)).append("\n")
           .append("plane_max=").append((long)(planeStats.getMax()/1000)).append("\n")
           .append("\n");
        
        buf.append("sent_messages_min=").append(sentMessageStats.getMin()).append("\n")
           .append("sent_messages_max=").append(sentMessageStats.getMax()).append("\n")
           .append("\n");
        
        buf.append("received_messages_min=").append(receivedMessageStats.getMin()).append("\n")
           .append("received_messages_max=").append(receivedMessageStats.getMax()).append("\n")
           .append("\n");

        System.out.println(buf);
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter("F:\\GitHub repos\\MASPlanesPlusPlus\\outputs\\output.log", true));
            bw.write(buf.toString());
            bw.close();
        }
        catch (IOException e) {}
    }

}