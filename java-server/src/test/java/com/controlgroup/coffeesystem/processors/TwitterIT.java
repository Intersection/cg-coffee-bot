package com.controlgroup.coffeesystem.processors;

import com.controlgroup.coffeesystem.TestHelpers;
import com.controlgroup.coffeesystem.configuration.PropertyFetcher;
import com.controlgroup.coffeesystem.configuration.TypeSafePropertyFetcher;
import com.controlgroup.coffeesystem.generators.BasicTweetGenerator;
import org.junit.Before;

/**
 * Created by timmattison on 12/31/14.
 */
public class TwitterIT {
    private TwitterCoffeeStatusProcessor twitterCoffeeStatusProcessor;
    private PropertyFetcher propertyFetcher;

    @Before
    public void setup() {
        propertyFetcher = new TypeSafePropertyFetcher();
        BasicTweetGenerator tweetGenerator = new BasicTweetGenerator(TestHelpers.getMockTimestampGenerator());
        twitterCoffeeStatusProcessor = new TwitterCoffeeStatusProcessor(propertyFetcher, tweetGenerator);
    }

    // TODO - Write some clever tests here
}
