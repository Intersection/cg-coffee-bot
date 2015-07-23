package com.controlgroup.coffeesystem.generators;

import com.controlgroup.coffeesystem.CoffeeStatus;
import com.controlgroup.coffeesystem.TestHelpers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.core.IsNot.not;

/**
 * Created by timmattison on 1/9/15.
 */
public class BasicTweetGeneratorTest {
    private BasicTweetGenerator basicTweetGenerator;

    @Before
    public void setup() {
        basicTweetGenerator = new BasicTweetGenerator(TestHelpers.getMockTimestampGenerator());
    }

    @Test
    public void shouldDistinguishEmptyFromNoCarafe() {
        String noCarafe = basicTweetGenerator.generate(noCarafe());
        String emptyCarafe = basicTweetGenerator.generate(emptyCarafe());

        Assert.assertThat(noCarafe, not(emptyCarafe));
    }

    @Test
    public void shouldUnderstandCupPlurality() {
        String oneCup = basicTweetGenerator.generate(fakeCarafe(1));
        String twoCups = basicTweetGenerator.generate(fakeCarafe(2));

        Assert.assertThat(oneCup, not(twoCups));
    }

    @Test
    public void shouldDistinguishBetweenFreshAndOld() {
        CoffeeStatus coffeeStatus = new CoffeeStatus();
        coffeeStatus.lastBrewed = Long.MAX_VALUE;
        coffeeStatus.carafePresent = true;
        coffeeStatus.cupsRemaining = 10;

        String justBrewed = basicTweetGenerator.generate(coffeeStatus);

        coffeeStatus.lastBrewed = 1;

        String notJustBrewed = basicTweetGenerator.generate(coffeeStatus);

        Assert.assertThat(notJustBrewed, not(justBrewed));
    }

    @Test
    public void shouldDistinguishBetweenKnownAndUnknownBrewTimes() {
        CoffeeStatus coffeeStatus = new CoffeeStatus();
        coffeeStatus.lastBrewed = Long.MAX_VALUE;
        coffeeStatus.carafePresent = true;
        coffeeStatus.cupsRemaining = 10;

        String knownBrewTime = basicTweetGenerator.generate(coffeeStatus);

        coffeeStatus.lastBrewed = 0;

        String unknownBrewTime = basicTweetGenerator.generate(coffeeStatus);

        Assert.assertThat(knownBrewTime, not(unknownBrewTime));
    }

    private CoffeeStatus fakeCarafe(int cups) {
        CoffeeStatus coffeeStatus = new CoffeeStatus();
        coffeeStatus.carafePresent = true;
        coffeeStatus.cupsRemaining = cups;
        return coffeeStatus;
    }

    private CoffeeStatus noCarafe() {
        CoffeeStatus coffeeStatus = new CoffeeStatus();
        coffeeStatus.carafePresent = false;
        return coffeeStatus;
    }

    private CoffeeStatus emptyCarafe() {
        CoffeeStatus coffeeStatus = new CoffeeStatus();
        coffeeStatus.carafePresent = true;
        coffeeStatus.cupsRemaining = 0;
        return coffeeStatus;
    }
}
