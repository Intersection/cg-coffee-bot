package com.controlgroup.coffeesystem.processors;

import com.controlgroup.coffeesystem.TestHelpers;
import com.controlgroup.coffeesystem.configuration.PropertyFetcher;
import com.controlgroup.coffeesystem.configuration.TypeSafePropertyFetcher;
import com.controlgroup.coffeesystem.generators.BasicEmailGenerator;
import com.controlgroup.coffeesystem.http.BasicHttpClientFactory;
import org.junit.Before;

/**
 * Created by timmattison on 12/31/14.
 */
public class SlackIT {
    private SlackCoffeeBrewedEventProcessor slackCoffeeBrewedEventProcessor;
    private PropertyFetcher propertyFetcher;

    @Before
    public void setup() {
        propertyFetcher = new TypeSafePropertyFetcher();
        BasicEmailGenerator emailGenerator = new BasicEmailGenerator(TestHelpers.getMockTimestampGenerator());
        slackCoffeeBrewedEventProcessor = new SlackCoffeeBrewedEventProcessor(propertyFetcher, emailGenerator, new BasicHttpClientFactory());
    }

    // TODO - Write some clever tests here
}
