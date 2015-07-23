package com.controlgroup.coffeesystem.processors;

import com.controlgroup.coffeesystem.CoffeeStatus;
import com.controlgroup.coffeesystem.events.CoffeeBrewedEvent;
import com.controlgroup.coffeesystem.interfaces.StableScaleEventProcessor;
import com.controlgroup.coffeesystem.interfaces.StableScaleReadEvent;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Named;
import java.io.IOException;

/**
 * Created by timmattison on 12/29/14.
 */
public class BasicStableScaleEventProcessor implements StableScaleEventProcessor {
    private final Logger logger = LoggerFactory.getLogger(BasicStableScaleEventProcessor.class);
    private final EventBus eventBus;
    private final int emptyCarafeInGrams;
    private final int fullCarafeInGrams;
    private final int gramsPerCup;
    private final int toleranceInGrams;
    private final int cupToleranceForFull;

    private StableScaleReadEvent lastStableScaleReadEvent;
    public static final long UNKNOWN = -1;
    private long lastBrewed = UNKNOWN;

    @Inject
    public BasicStableScaleEventProcessor(EventBus eventBus,
                                          @Named(StableScaleEventProcessor.EMPTY_CARAFE_IN_GRAMS) int emptyCarafeInGrams,
                                          @Named(StableScaleEventProcessor.FULL_CARAFE_IN_GRAMS) int fullCarafeInGrams,
                                          @Named(StableScaleEventProcessor.GRAMS_PER_CUP) int gramsPerCup,
                                          @Named(StableScaleEventProcessor.TOLERANCE_IN_GRAMS) int toleranceInGrams,
                                          @Named(StableScaleEventProcessor.CUP_TOLERANCE_FOR_FULL) int cupToleranceForFull) {
        this.eventBus = eventBus;
        this.emptyCarafeInGrams = emptyCarafeInGrams;
        this.fullCarafeInGrams = fullCarafeInGrams;
        this.gramsPerCup = gramsPerCup;
        this.toleranceInGrams = toleranceInGrams;
        this.cupToleranceForFull = cupToleranceForFull;
    }

    @Override
    @Subscribe
    public synchronized void stableScaleReadEvent(StableScaleReadEvent stableScaleReadEvent) {
        logger.debug("Event received");

        try {
            // Do we have an event already?
            if (lastStableScaleReadEvent == null) {
                // No, this is our first event
                logFirstEvent();
                checkIfCoffeeBrewed(stableScaleReadEvent);
                return;
            }

            // Has our reading changed?
            if (!hasChanged(stableScaleReadEvent)) {
                // No, it has not changed
                noChange(stableScaleReadEvent);
                return;
            }

            // Was the carafe empty the last time we checked?
            if (previouslyHadNoCarafe()) {
                // Yes, it was empty but now it isn't.  Check to see if it was brewed.
                checkIfCoffeeBrewed(stableScaleReadEvent);
                return;
            }

            // Is the carafe gone?
            if (!carafeIsPresent(stableScaleReadEvent)) {
                // Yes, someone just removed it
                carafeJustRemoved(stableScaleReadEvent);
                return;
            }

            // Did someone just take coffee from it?
            if (lastStableScaleReadEvent.getGrams() > stableScaleReadEvent.getGrams()) {
                // Yes, someone just pumped some coffee
                coffeePumped(stableScaleReadEvent);
                return;
            }

            // If we got here it looks like someone added coffee to the pot without removing it first, weird.
            logger.error("Did someone just pour their coffee back in the pot?");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // Update our last stable scale read event
            lastStableScaleReadEvent = stableScaleReadEvent;
        }
    }

    private boolean carafeIsPresent(StableScaleReadEvent stableScaleReadEvent) {
        return stableScaleReadEvent.getGrams() >= (emptyCarafeInGrams - toleranceInGrams);
    }

    private boolean previouslyHadNoCarafe() {
        return !carafeIsPresent(lastStableScaleReadEvent);
    }

    private void logFirstEvent() {
        logger.debug("First event!");
    }

    private boolean isCarafeFull(StableScaleReadEvent stableScaleReadEvent) {
        // Is the carafe within a few cups of full?  If so, consider it a new brew.
        return withinTolerance(stableScaleReadEvent, fullCarafeInGrams, cupToleranceForFull);
    }

    private boolean hasChanged(StableScaleReadEvent stableScaleReadEvent) {
        // Has the weight changed beyond a certain threshold so that it is significant?
        return !(Math.abs(lastStableScaleReadEvent.getGrams() - stableScaleReadEvent.getGrams()) < toleranceInGrams);
    }

    private boolean withinTolerance(StableScaleReadEvent stableScaleReadEvent, int weight, int numberOfCups) {
        // Is the weight within a specific number of cups?
        return (Math.abs(weight - stableScaleReadEvent.getGrams()) < (gramsPerCup * numberOfCups));
    }

    private void noChange(StableScaleReadEvent stableScaleReadEvent) {
        // No change, do nothing
        logger.debug("No change");
    }

    private void coffeePumped(StableScaleReadEvent stableScaleReadEvent) throws IOException {
        logger.info("Coffee pumped");

        // Build the status object and post it to the event bus
        getAndPostCoffeeStatus(stableScaleReadEvent);
    }

    private void carafeJustRemoved(StableScaleReadEvent stableScaleReadEvent) throws IOException {
        logger.info("Carafe removed");

        // Build the status object and post it to the event bus
        getAndPostCoffeeStatus(stableScaleReadEvent);
    }

    private CoffeeStatus getCoffeeStatus(StableScaleReadEvent stableScaleReadEvent) {
        // Assume the scale is empty
        int weightOnScale = 0;

        // Is the weight more than an empty carafe?
        if (stableScaleReadEvent.getGrams() > emptyCarafeInGrams) {
            // Yes, then we'll calculate the weight of the coffee
            weightOnScale = stableScaleReadEvent.getGrams() - emptyCarafeInGrams;
        }

        // Calculate the approximate number of cups (truncating)
        int numberOfCups = weightOnScale / gramsPerCup;

        // Create a new status object
        CoffeeStatus coffeeStatus = new CoffeeStatus();

        // Was there anything on the scale?
        if (weightOnScale == 0) {
            // No, the carafe isn't there
            noCarafe(coffeeStatus);
        } else {
            // Yes, the carafe is there.  Populate all of the values.
            carafePresent(numberOfCups, coffeeStatus);
        }

        return coffeeStatus;
    }

    private void carafePresent(int numberOfCups, CoffeeStatus coffeeStatus) {
        coffeeStatus.carafePresent = true;
        coffeeStatus.cupsRemaining = numberOfCups;
        coffeeStatus.lastBrewed = lastBrewed;
    }

    private void noCarafe(CoffeeStatus coffeeStatus) {
        coffeeStatus.carafePresent = false;
        coffeeStatus.cupsRemaining = 0;
        coffeeStatus.lastBrewed = UNKNOWN;
    }

    private void getAndPostCoffeeStatus(StableScaleReadEvent stableScaleReadEvent) throws IOException {
        // Get the current coffee status
        CoffeeStatus coffeeStatus = getCoffeeStatus(stableScaleReadEvent);

        // Post it to the event bus
        postCoffeeStatus(coffeeStatus);
    }

    private void checkIfCoffeeBrewed(StableScaleReadEvent stableScaleReadEvent) throws IOException {
        // Is this within a few cups of full?
        if (isCarafeFull(stableScaleReadEvent)) {
            // Yes, update the last brewed time
            updateLastBrewedTime();
            logCoffeeBrewed();
            sendCoffeeBrewedEvent();
        } else {
            // No, indicate that we don't know when the coffee was brewed
            setLastBrewedTimeToUnknown();

            if (carafeIsPresent(stableScaleReadEvent)) {
                logPartialCarafe();
            }
        }

        // Build the status object and post it to the event bus
        getAndPostCoffeeStatus(stableScaleReadEvent);
    }

    private void sendCoffeeBrewedEvent() {
        CoffeeBrewedEvent coffeeBrewedEvent = new CoffeeBrewedEvent();
        coffeeBrewedEvent.timestamp = System.currentTimeMillis();

        eventBus.post(coffeeBrewedEvent);
    }

    private void logPartialCarafe() {
        logger.info("Partially full carafe placed on scale");
    }

    private void logCoffeeBrewed() {
        logger.info("Coffee brewed");
    }

    private void setLastBrewedTimeToUnknown() {
        lastBrewed = UNKNOWN;
    }

    private void updateLastBrewedTime() {
        lastBrewed = System.currentTimeMillis();
    }

    /**
     * For testing only
     *
     * @return
     */
    protected StableScaleReadEvent getLastStableScaleReadEvent() {
        return lastStableScaleReadEvent;
    }

    /**
     * For testing only
     *
     * @return
     */
    protected long getLastBrewed() {
        return lastBrewed;
    }

    private void postCoffeeStatus(CoffeeStatus coffeeStatus) throws IOException {
        eventBus.post(coffeeStatus);
    }
}
