package com.controlgroup.coffeesystem.events;

import com.controlgroup.coffeesystem.interfaces.ScaleReadEvent;
import com.controlgroup.coffeesystem.interfaces.StableScaleReadEvent;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

/**
 * Created by timmattison on 12/29/14.
 */
public class BasicStableScaleReadEvent implements StableScaleReadEvent {
    public final long timestamp;
    public final int grams;

    @Inject
    public BasicStableScaleReadEvent(@Assisted(ScaleReadEvent.TIMESTAMP) long timestamp, @Assisted(ScaleReadEvent.GRAMS) int grams) {
        this.timestamp = timestamp;
        this.grams = grams;
    }

    @Override
    public long getTimestamp() {
        return timestamp;
    }

    @Override
    public int getGrams() {
        return grams;
    }
}
