package com.controlgroup.coffeesystem.interfaces;

import com.google.inject.assistedinject.Assisted;

/**
 * Created by timmattison on 12/29/14.
 */
public interface ScaleReadEventFactory {
    /**
     * Creates a scale read event
     *
     * @param timestamp
     * @param grams
     * @return
     */
    public ScaleReadEvent create(@Assisted(ScaleReadEvent.TIMESTAMP) long timestamp, @Assisted(ScaleReadEvent.GRAMS) int grams);
}
