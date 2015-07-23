package com.controlgroup.coffeesystem.helper.client.events;

import com.controlgroup.coffeesystem.helper.client.CoffeeStatus;
import com.google.web.bindery.event.shared.binder.GenericEvent;

/**
 * Created by timmattison on 1/7/15.
 */
public abstract class AbstractCoffeeStatusEvent extends GenericEvent {
    private final CoffeeStatus coffeeStatus;

    public AbstractCoffeeStatusEvent(CoffeeStatus coffeeStatus) {
        this.coffeeStatus = coffeeStatus;
    }

    public CoffeeStatus getCoffeeStatus() {
        return coffeeStatus;
    }
}
