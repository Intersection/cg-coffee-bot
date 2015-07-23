package com.controlgroup.coffeesystem.interfaces;

import com.controlgroup.coffeesystem.events.HeartbeatEvent;
import com.google.common.eventbus.Subscribe;

/**
 * Created by timmattison on 12/29/14.
 */
public interface ScaleEventProcessor {
    /**
     * The header used the in the configuration file for scale event processor options
     */
    public static final String NAME = "scaleEventProcessor";

    /**
     * The name of the max threshold field in the scale event processor options
     */
    public static final String MAX_THRESHOLD = "maxThreshold";

    /**
     * The name of the identical fields required field in the scale event processor options
     */
    public static final String IDENTICAL_READS_REQUIRED = "identicalReadsRequired";

    /**
     * Handles a scale read event
     *
     * @param scaleReadEvent
     */
    @Subscribe
    public void scaleReadEvent(ScaleReadEvent scaleReadEvent);

    /**
     * Handles a heartbeat event
     *
     * @param heartbeatEvent
     */
    @Subscribe
    public void heartbeatEvent(HeartbeatEvent heartbeatEvent);
}
