/**
 * Copyright 2011 Lennart Koopmann <lennart@socketfeed.com>
 *
 * This file is part of Graylog2.
 *
 * Graylog2 is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Graylog2 is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Graylog2.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package org.graylog2.periodical;

import org.apache.log4j.Logger;
import org.graylog2.GraylogServer;
import org.graylog2.messagehandlers.common.MessageCounter;

/**
 * ServerValueWriterThread.java
 * <p/>
 * Periodically writes server values to MongoDB.
 *
 * @author Lennart Koopmann <lennart@socketfeed.com>
 */
public class ServerValueWriterThread implements Runnable {

    private static final Logger LOG = Logger.getLogger(ServerValueWriterThread.class);

    public static final int PERIOD = 5;
    public static final int INITIAL_DELAY = 0;

    private final GraylogServer graylogServer;

    public ServerValueWriterThread(GraylogServer graylogServer) {
        this.graylogServer = graylogServer;
    }

    /**
     * Start the thread. Runs forever.
     */
    @Override
    public void run() {
        try {
            // ohai, we are alive. \o/
            graylogServer.getServerValue().ping();

            // Current throughput.
            MessageCounter c = MessageCounter.getInstance();
            graylogServer.getServerValue().writeThroughput(c.getFiveSecondThroughput(), c.getHighestFiveSecondThroughput());
            c.resetFiveSecondThroughput(); // Reset five second throughput count.

            /*
             * Message queue size is written in BulkIndexerThread. More about the
             * reason for that can be found there.
             */
        } catch (Exception e) {
            LOG.warn("Error in ServerValue  WriterThread: " + e.getMessage(), e);
        }
    }
}
