package com.controlgroup.coffeesystem.interfaces;

/**
 * Created by timmattison on 12/29/14.
 */
public interface HeartbeatGenerator {
    /**
     * The header used in the configuration file for heartbeat options
     */
    public static final String NAME = "heartbeat";

    /**
     * The name of the interval field in the heartbeat configuration options
     */
    public static final String INTERVAL = "interval";

    /**
     * Start emitting heartbeat events at an interval
     */
    public void start();

    /**
     * Stop emitting heartbeat events
     */
    public void stop();
}
