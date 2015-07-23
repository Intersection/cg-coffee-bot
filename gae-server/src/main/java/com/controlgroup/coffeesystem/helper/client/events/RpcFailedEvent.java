package com.controlgroup.coffeesystem.helper.client.events;

import com.google.web.bindery.event.shared.binder.GenericEvent;

/**
 * Created by timmattison on 1/20/15.
 */
public class RpcFailedEvent extends GenericEvent {
    private final Throwable caught;

    public RpcFailedEvent(Throwable caught) {
        this.caught = caught;
    }

    public Throwable getCaught() {
        return caught;
    }
}
