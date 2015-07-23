package com.controlgroup.coffeesystem.interfaces;

import com.controlgroup.coffeesystem.CoffeeStatus;
import com.controlgroup.coffeesystem.events.HeartbeatEvent;
import com.google.common.eventbus.Subscribe;

/**
 * Created by timmattison on 12/29/14.
 */
public interface CoffeeStatusProcessor {
    /**
     * Receives a CoffeeStatus object
     *
     * @param coffeeStatus
     */
    @Subscribe
    public void coffeeStatus(CoffeeStatus coffeeStatus);

    /**
     * Receives a heartbeat
     *
     * @param heartbeatEvent
     */
    @Subscribe
    public void heartbeatEvent(HeartbeatEvent heartbeatEvent) throws Exception;
}
