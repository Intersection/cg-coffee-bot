package com.controlgroup.coffeesystem.helper.client.events;

import com.google.web.bindery.event.shared.binder.GenericEvent;

/**
 * Created by timmattison on 1/7/15.
 */
public class CarafeRemovedEvent extends GenericEvent {
    private final long timestamp;

    public CarafeRemovedEvent(long timestamp) {
        this.timestamp = timestamp;
    }

    public long getTimestamp() {
        return timestamp;
    }
}
