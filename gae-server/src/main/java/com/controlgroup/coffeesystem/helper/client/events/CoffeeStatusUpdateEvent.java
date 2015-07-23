package com.controlgroup.coffeesystem.helper.client.events;

import com.controlgroup.coffeesystem.helper.client.CoffeeStatus;

/**
 * Created by timmattison on 1/20/15.
 */
public class CoffeeStatusUpdateEvent extends AbstractCoffeeStatusEvent {
    public CoffeeStatusUpdateEvent(CoffeeStatus coffeeStatus) {
        super(coffeeStatus);
    }
}
