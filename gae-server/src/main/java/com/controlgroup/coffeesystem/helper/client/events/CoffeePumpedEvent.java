package com.controlgroup.coffeesystem.helper.client.events;

import com.controlgroup.coffeesystem.helper.client.CoffeeStatus;

/**
 * Created by timmattison on 1/7/15.
 */
public class CoffeePumpedEvent extends AbstractCoffeeStatusEvent {
    public CoffeePumpedEvent(CoffeeStatus coffeeStatus) {
        super(coffeeStatus);
    }
}
