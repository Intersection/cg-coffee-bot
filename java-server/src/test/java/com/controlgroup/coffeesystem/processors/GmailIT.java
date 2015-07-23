package com.controlgroup.coffeesystem.processors;

import com.controlgroup.coffeesystem.TestHelpers;
import com.controlgroup.coffeesystem.configuration.PropertyFetcher;
import com.controlgroup.coffeesystem.configuration.TypeSafePropertyFetcher;
import com.controlgroup.coffeesystem.generators.BasicEmailGenerator;
import org.junit.Before;

/**
 * Created by timmattison on 12/31/14.
 */
public class GmailIT {
    private GMailCoffeeBrewedEventProcessor gMailCoffeeBrewedEventProcessor;
    private PropertyFetcher propertyFetcher;

    @Before
    public void setup() {
        propertyFetcher = new TypeSafePropertyFetcher();
        BasicEmailGenerator emailGenerator = new BasicEmailGenerator(TestHelpers.getMockTimestampGenerator());
        gMailCoffeeBrewedEventProcessor = new GMailCoffeeBrewedEventProcessor(propertyFetcher, emailGenerator);
    }

    // TODO - Write some clever tests here
}
