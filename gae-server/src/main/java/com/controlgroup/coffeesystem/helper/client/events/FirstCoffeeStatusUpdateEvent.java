package com.controlgroup.coffeesystem.helper.client.events;

import com.controlgroup.coffeesystem.helper.client.CoffeeStatus;

/**
 * Created by timmattison on 1/7/15.
 */
public class FirstCoffeeStatusUpdateEvent extends AbstractCoffeeStatusEvent {
    public FirstCoffeeStatusUpdateEvent(CoffeeStatus coffeeStatus) {
        super(coffeeStatus);
    }
}
