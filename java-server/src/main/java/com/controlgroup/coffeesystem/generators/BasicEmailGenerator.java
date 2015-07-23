package com.controlgroup.coffeesystem.generators;

import com.controlgroup.coffeesystem.events.CoffeeBrewedEvent;
import com.controlgroup.coffeesystem.generators.interfaces.EmailGenerator;
import com.controlgroup.coffeesystem.generators.interfaces.TimestampGenerator;
import com.google.inject.Inject;

/**
 * Created by timmattison on 1/9/15.
 */
public class BasicEmailGenerator implements EmailGenerator {
    private final TimestampGenerator timestampGenerator;

    @Inject
    public BasicEmailGenerator(TimestampGenerator timestampGenerator) {
        this.timestampGenerator = timestampGenerator;
    }

    @Override
    public String generateBody(CoffeeBrewedEvent coffeeBrewedEvent) {
        String emailBody = "A fresh pot of coffee was just brewed at " + timestampGenerator.generateTimestamp();

        return emailBody;
    }

    @Override
    public String generateSubject(CoffeeBrewedEvent coffeeBrewedEvent) {
        String subject = "[" + timestampGenerator.generateTimestamp() + "] Fresh coffee!";

        return subject;
    }
}
