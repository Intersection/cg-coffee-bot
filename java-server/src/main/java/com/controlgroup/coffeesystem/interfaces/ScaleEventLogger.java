package com.controlgroup.coffeesystem.interfaces;

import com.google.common.eventbus.Subscribe;

/**
 * Created by timmattison on 12/29/14.
 */
public interface ScaleEventLogger {
    /**
     * Logs a raw scale read event (not guaranteed to be a stable read)
     *
     * @param scaleReadEvent
     */
    @Subscribe
    public void scaleReadEvent(ScaleReadEvent scaleReadEvent);
}
