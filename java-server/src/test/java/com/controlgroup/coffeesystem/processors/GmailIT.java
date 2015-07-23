package com.controlgroup.coffeesystem.processors;

import com.controlgroup.coffeesystem.TestHelpers;
import com.controlgroup.coffeesystem.configuration.TypeSafePropertyFetcher;
import com.controlgroup.coffeesystem.generators.BasicEmailGenerator;
import org.junit.Before;

/**
 * Created by timmattison on 12/31/14.
 */
public class GmailIT {
    private GMailCoffeeBrewedEventProcessor gMailCoffeeBrewedEventProcessor;
    private TypeSafePropertyFetcher typeSafePropertyFetcher;

    @Before
    public void setup() {
        typeSafePropertyFetcher = new TypeSafePropertyFetcher();
        BasicEmailGenerator emailGenerator = new BasicEmailGenerator(TestHelpers.getMockTimestampGenerator());
        gMailCoffeeBrewedEventProcessor = new GMailCoffeeBrewedEventProcessor(typeSafePropertyFetcher, emailGenerator);
    }

    // TODO - Write some clever tests here
}
