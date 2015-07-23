package com.controlgroup.coffeesystem.interfaces;

import com.controlgroup.coffeesystem.events.HeartbeatEvent;

/**
 * Created by timmattison on 12/29/14.
 */
public interface HeartbeatEventFactory {
    /**
     * Creates a heartbeat
     *
     * @return
     */
    public HeartbeatEvent create();
}
