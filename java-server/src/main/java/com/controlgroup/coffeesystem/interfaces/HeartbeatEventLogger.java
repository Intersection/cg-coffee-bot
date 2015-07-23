package com.controlgroup.coffeesystem.interfaces;

import com.controlgroup.coffeesystem.events.HeartbeatEvent;
import com.google.common.eventbus.Subscribe;

/**
 * Created by timmattison on 12/29/14.
 */
public interface HeartbeatEventLogger {
    /**
     * Logs a heartbeat event
     *
     * @param heartbeatEvent
     */
    @Subscribe
    public void heartbeatEvent(HeartbeatEvent heartbeatEvent);
}
