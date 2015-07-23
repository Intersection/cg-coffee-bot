package com.controlgroup.coffeesystem.generators;

import com.controlgroup.coffeesystem.interfaces.HeartbeatEventFactory;
import com.controlgroup.coffeesystem.interfaces.HeartbeatGenerator;
import com.google.common.eventbus.EventBus;
import com.google.inject.Inject;

import javax.inject.Named;

/**
 * Created by timmattison on 12/29/14.
 */
public class BasicHeartbeatGenerator implements HeartbeatGenerator {
    private final int interval;
    private final EventBus eventBus;
    private final HeartbeatEventFactory heartbeatEventFactory;
    private boolean running = false;

    @Inject
    public BasicHeartbeatGenerator(@Named(HeartbeatGenerator.INTERVAL) int interval, EventBus eventBus, HeartbeatEventFactory heartbeatEventFactory) {
        this.interval = interval;
        this.eventBus = eventBus;
        this.heartbeatEventFactory = heartbeatEventFactory;
    }

    @Override
    public synchronized void start() {
        // Are we already running?
        if (running) {
            // Yes, just return
            return;
        }

        // Create a heartbeat thread
        Thread heartbeatThread = createHeartbeatThread();

        // Track that we're running
        running = true;

        // Start the heartbeat thread
        heartbeatThread.start();
    }

    private Thread createHeartbeatThread() {
        return new Thread(new Runnable() {
            @Override
            public void run() {
                // Are we running?
                while (running) {
                    try {
                        // Yes, try to sleep
                        Thread.sleep(interval);
                    } catch (InterruptedException e) {
                        // Ignore exceptions
                        e.printStackTrace();
                    }

                    // Post a heartbeat event
                    eventBus.post(heartbeatEventFactory.create());
                }
            }
        });
    }

    @Override
    public synchronized void stop() {
        // Toggle our running state so our thread will exit
        running = false;
    }
}
