package com.controlgroup.coffeesystem.interfaces;

import com.google.common.eventbus.Subscribe;

/**
 * Created by timmattison on 12/29/14.
 */
public interface StableScaleEventLogger {
    /**
     * Logs a stable scale read event
     *
     * @param stableScaleReadEvent
     */
    @Subscribe
    public void stableScaleReadEvent(StableScaleReadEvent stableScaleReadEvent);
}
