package com.controlgroup.coffeesystem.generators.interfaces;

import com.controlgroup.coffeesystem.events.CoffeeBrewedEvent;

/**
 * Created by timmattison on 1/9/15.
 */
public interface EmailGenerator {
    public String generateBody(CoffeeBrewedEvent coffeeBrewedEvent);

    public String generateSubject(CoffeeBrewedEvent coffeeBrewedEvent);
}
