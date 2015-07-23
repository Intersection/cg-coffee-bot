package com.controlgroup.coffeesystem.processors;

import com.controlgroup.coffeesystem.CoffeeStatus;
import com.controlgroup.coffeesystem.events.BasicStableScaleReadEvent;
import com.controlgroup.coffeesystem.events.CoffeeBrewedEvent;
import com.controlgroup.coffeesystem.interfaces.StableScaleReadEvent;
import com.google.common.eventbus.EventBus;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.ArgumentCaptor;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.io.IOException;

import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;
import static org.mockito.Mockito.*;

/**
 * Created by timmattison on 12/29/14.
 */
public class BasicStableScaleEventProcessorTest {
    public static final long FAKE_TIMESTAMP = 1000000000L;
    private BasicStableScaleEventProcessor basicStableScaleEventProcessor;
    private EventBus mockEventBus;

    private static final int emptyCarafeInGrams = 1000;
    private static final int fullCarafeInGrams = 5000;
    private static final int gramsPerCup = 250;
    private static final int toleranceInGrams = 100;
    private static final int cupToleranceForFull = 2;
    private static final int cupsPerCarafe = (fullCarafeInGrams - emptyCarafeInGrams) / gramsPerCup;

    private int coffeeBrewedEventCount = 0;
    private int coffeeStatusCount = 0;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Before
    public void setup() {
        mockEventBus = mock(EventBus.class);

        basicStableScaleEventProcessor = new BasicStableScaleEventProcessor(mockEventBus, emptyCarafeInGrams, fullCarafeInGrams, gramsPerCup, toleranceInGrams, cupToleranceForFull);

        doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocationOnMock) throws Throwable {
                Object argument = invocationOnMock.getArguments()[0];

                if (argument instanceof CoffeeStatus) {
                    coffeeStatusCount++;
                } else if (argument instanceof CoffeeBrewedEvent) {
                    coffeeBrewedEventCount++;
                } else {
                    throw new UnsupportedOperationException("This kind of event was not expected");
                }

                return null;
            }
        }).when(mockEventBus).post(anyObject());
    }

    @Test
    public void shouldHaveUnknownLastBrewedTimeAtStartup() {
        Assert.assertThat(basicStableScaleEventProcessor.getLastBrewed(), is(BasicStableScaleEventProcessor.UNKNOWN));
    }

    @Test
    public void shouldSetLastBrewedTimeOnFirstEventWithFullCarafe() {
        StableScaleReadEvent stableScaleReadEvent = getFullCarafeEvent();

        Assert.assertThat(basicStableScaleEventProcessor.getLastBrewed(), is(BasicStableScaleEventProcessor.UNKNOWN));

        basicStableScaleEventProcessor.stableScaleReadEvent(stableScaleReadEvent);

        Assert.assertThat(basicStableScaleEventProcessor.getLastBrewed(), is(not(BasicStableScaleEventProcessor.UNKNOWN)));
    }

    @Test
    public void shouldSetLastBrewedTimeOnFirstEventWithPartiallyFullCarafe() {
        StableScaleReadEvent stableScaleReadEvent = new BasicStableScaleReadEvent(FAKE_TIMESTAMP, fullCarafeInGrams - (gramsPerCup * cupToleranceForFull) - 1);

        Assert.assertThat(basicStableScaleEventProcessor.getLastBrewed(), is(BasicStableScaleEventProcessor.UNKNOWN));

        basicStableScaleEventProcessor.stableScaleReadEvent(stableScaleReadEvent);

        Assert.assertThat(basicStableScaleEventProcessor.getLastBrewed(), is(BasicStableScaleEventProcessor.UNKNOWN));
    }

    @Test
    public void shouldPostOnFirstEvent() {
        StableScaleReadEvent stableScaleReadEvent = getFullCarafeEvent();

        Assert.assertThat(basicStableScaleEventProcessor.getLastBrewed(), is(BasicStableScaleEventProcessor.UNKNOWN));

        basicStableScaleEventProcessor.stableScaleReadEvent(stableScaleReadEvent);

        verifyExactlyOneCoffeeStatusPosted();
        verifyExactlyOneBrewedEventPosted();

        CoffeeStatus coffeeStatus = captureCoffeeStatusEvent();

        assertCarafeIsFull(coffeeStatus);
    }

    @Test
    public void shouldDoNothingWhenCarafeWeightChangesLessThanTheTolerance() {
        StableScaleReadEvent firstStableScaleReadEvent = getFullCarafeEvent();
        StableScaleReadEvent secondStableScaleReadEvent = new BasicStableScaleReadEvent(FAKE_TIMESTAMP, fullCarafeInGrams - toleranceInGrams + 1);

        basicStableScaleEventProcessor.stableScaleReadEvent(firstStableScaleReadEvent);
        basicStableScaleEventProcessor.stableScaleReadEvent(secondStableScaleReadEvent);

        verifyExactlyOneCoffeeStatusPosted();
    }

    private void verifyExactlyOneCoffeeStatusPosted() {
        Assert.assertThat(coffeeStatusCount, is(1));
    }

    private void verifyExactlyOneBrewedEventPosted() {
        Assert.assertThat(coffeeBrewedEventCount, is(1));
    }

    @Test
    public void shouldSetBrewedTimeToUnknownWithPartiallyFilledCarafe() {
        StableScaleReadEvent firstStableScaleReadEvent = getFullCarafeEvent();
        StableScaleReadEvent secondStableScaleReadEvent = new BasicStableScaleReadEvent(FAKE_TIMESTAMP, fullCarafeInGrams - toleranceInGrams + 1);

        basicStableScaleEventProcessor.stableScaleReadEvent(firstStableScaleReadEvent);
        basicStableScaleEventProcessor.stableScaleReadEvent(secondStableScaleReadEvent);

        verifyExactlyOneCoffeeStatusPosted();
    }

    @Test
    public void shouldSetLastBrewedTimeWithFullCarafeAfterNoCarafe() {
        StableScaleReadEvent noCarafeEvent = getNoCarafeEvent();
        StableScaleReadEvent fullCarafeEvent = getFullCarafeEvent();

        basicStableScaleEventProcessor.stableScaleReadEvent(noCarafeEvent);
        basicStableScaleEventProcessor.stableScaleReadEvent(fullCarafeEvent);

        Assert.assertThat(basicStableScaleEventProcessor.getLastBrewed(), is(not(BasicStableScaleEventProcessor.UNKNOWN)));
    }

    @Test
    public void shouldSetCarafeNotPresentAfterCarafeRemoved() {
        StableScaleReadEvent noCarafeEvent = getNoCarafeEvent();
        StableScaleReadEvent fullCarafeEvent = getFullCarafeEvent();

        basicStableScaleEventProcessor.stableScaleReadEvent(fullCarafeEvent);
        basicStableScaleEventProcessor.stableScaleReadEvent(noCarafeEvent);

        // Get the last event
        CoffeeStatus coffeeStatus = captureCoffeeStatusEvent();

        Assert.assertThat(coffeeStatus.carafePresent, is(false));
    }

    @Test
    public void shouldDecreaseCupCountWhenCoffeePumped() {
        StableScaleReadEvent fullCarafeEvent = getFullCarafeEvent();
        StableScaleReadEvent oneCupRemovedEvent = new BasicStableScaleReadEvent(FAKE_TIMESTAMP, fullCarafeInGrams - gramsPerCup);

        basicStableScaleEventProcessor.stableScaleReadEvent(fullCarafeEvent);
        CoffeeStatus firstCoffeeStatus = captureCoffeeStatusEvent();

        basicStableScaleEventProcessor.stableScaleReadEvent(oneCupRemovedEvent);
        CoffeeStatus secondCoffeeStatus = captureCoffeeStatusEvent();

        int differenceInCups = firstCoffeeStatus.cupsRemaining - secondCoffeeStatus.cupsRemaining;

        Assert.assertThat(differenceInCups, is(1));
        Assert.assertThat(secondCoffeeStatus.cupsRemaining, is(greaterThan(1)));
    }

    @Test
    public void shouldEatIOExceptions() {
        doThrow(IOException.class).when(mockEventBus).post(anyObject());
        StableScaleReadEvent fullCarafeEvent = getFullCarafeEvent();
        basicStableScaleEventProcessor.stableScaleReadEvent(fullCarafeEvent);
    }

    /**
     * This just gets us 100% coverage since there is no event for "someone poured their coffee back in the pot"
     */
    @Test
    public void iDidItAllForTheCoverage() {
        StableScaleReadEvent fullCarafeEvent = getFullCarafeEvent();
        StableScaleReadEvent moreThanFullCarafeEvent = new BasicStableScaleReadEvent(FAKE_TIMESTAMP, fullCarafeInGrams + toleranceInGrams);
        basicStableScaleEventProcessor.stableScaleReadEvent(fullCarafeEvent);
        basicStableScaleEventProcessor.stableScaleReadEvent(moreThanFullCarafeEvent);
        verifyExactlyOneCoffeeStatusPosted();
    }

    private BasicStableScaleReadEvent getNoCarafeEvent() {
        return new BasicStableScaleReadEvent(FAKE_TIMESTAMP, 0);
    }

    private void assertCarafeIsFull(CoffeeStatus coffeeStatus) {
        Assert.assertThat(coffeeStatus.carafePresent, is(true));
        Assert.assertThat(coffeeStatus.cupsRemaining, is(cupsPerCarafe));
        Assert.assertThat(coffeeStatus.lastBrewed, is(not(BasicStableScaleEventProcessor.UNKNOWN)));
    }

    private BasicStableScaleReadEvent getFullCarafeEvent() {
        return new BasicStableScaleReadEvent(FAKE_TIMESTAMP, fullCarafeInGrams);
    }

    private CoffeeStatus captureCoffeeStatusEvent() {
        ArgumentCaptor<CoffeeStatus> coffeeStatusArgumentCaptor = ArgumentCaptor.forClass(CoffeeStatus.class);

        verify(mockEventBus, atLeastOnce()).post(coffeeStatusArgumentCaptor.capture());

        return coffeeStatusArgumentCaptor.getValue();
    }
}
