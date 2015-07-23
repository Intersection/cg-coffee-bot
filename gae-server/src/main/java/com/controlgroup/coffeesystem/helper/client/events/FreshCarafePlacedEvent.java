package com.controlgroup.coffeesystem.helper.client.events;

import com.controlgroup.coffeesystem.helper.client.CoffeeStatus;

/**
 * Created by timmattison on 1/7/15.
 */
public class FreshCarafePlacedEvent extends AbstractCoffeeStatusEvent {
    public FreshCarafePlacedEvent(CoffeeStatus coffeeStatus) {
        super(coffeeStatus);
    }
}
