package com.controlgroup.coffeesystem.processors;

import com.controlgroup.coffeesystem.TestHelpers;
import com.controlgroup.coffeesystem.configuration.TypeSafePropertyFetcher;
import com.controlgroup.coffeesystem.generators.BasicTweetGenerator;
import org.junit.Before;

/**
 * Created by timmattison on 12/31/14.
 */
public class TwitterIT {
    private TwitterCoffeeStatusProcessor twitterCoffeeStatusProcessor;
    private TypeSafePropertyFetcher typeSafePropertyFetcher;

    @Before
    public void setup() {
        typeSafePropertyFetcher = new TypeSafePropertyFetcher();
        BasicTweetGenerator tweetGenerator = new BasicTweetGenerator(TestHelpers.getMockTimestampGenerator());
        twitterCoffeeStatusProcessor = new TwitterCoffeeStatusProcessor(typeSafePropertyFetcher, tweetGenerator);
    }

    // TODO - Write some clever tests here
}
