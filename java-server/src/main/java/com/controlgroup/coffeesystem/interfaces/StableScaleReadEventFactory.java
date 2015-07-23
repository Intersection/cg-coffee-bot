package com.controlgroup.coffeesystem.interfaces;

import com.google.inject.assistedinject.Assisted;

/**
 * Created by timmattison on 12/29/14.
 */
public interface StableScaleReadEventFactory {
    /**
     * Creates a stable scale read event
     *
     * @param timestamp
     * @param grams
     * @return
     */
    public StableScaleReadEvent create(@Assisted(StableScaleReadEvent.TIMESTAMP) long timestamp, @Assisted(StableScaleReadEvent.GRAMS) int grams);
}
