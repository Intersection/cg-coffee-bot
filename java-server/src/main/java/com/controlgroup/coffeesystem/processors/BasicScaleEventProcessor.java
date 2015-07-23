package com.controlgroup.coffeesystem.processors;

import com.controlgroup.coffeesystem.events.HeartbeatEvent;
import com.controlgroup.coffeesystem.interfaces.ScaleEventProcessor;
import com.controlgroup.coffeesystem.interfaces.ScaleReadEvent;
import com.controlgroup.coffeesystem.interfaces.StableScaleReadEventFactory;
import com.google.common.eventbus.EventBus;
import com.google.inject.Inject;

import javax.inject.Named;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by timmattison on 12/29/14.
 */
public class BasicScaleEventProcessor implements ScaleEventProcessor {
    private final EventBus eventBus;
    private final StableScaleReadEventFactory stableScaleReadEventFactory;
    private List<ScaleReadEvent> scaleReadEvents = new ArrayList<ScaleReadEvent>();
    private long lastScaleReadEventTimestamp;

    /**
     * If we get no events for this many milliseconds just throw our buffer away
     */
    private final long maxThreshold;

    /**
     * The number of identical reads required
     */
    private final int identicalReadsRequired;

    @Inject
    public BasicScaleEventProcessor(EventBus eventBus,
                                    StableScaleReadEventFactory stableScaleReadEventFactory,
                                    @Named(ScaleEventProcessor.MAX_THRESHOLD) long maxThreshold,
                                    @Named(ScaleEventProcessor.IDENTICAL_READS_REQUIRED) int identicalReadsRequired) {
        this.eventBus = eventBus;
        this.stableScaleReadEventFactory = stableScaleReadEventFactory;
        this.maxThreshold = maxThreshold;
        this.identicalReadsRequired = identicalReadsRequired;
    }

    @Override
    public synchronized void scaleReadEvent(ScaleReadEvent scaleReadEvent) {
        // Track the time at which we saw our last event
        lastScaleReadEventTimestamp = scaleReadEvent.getTimestamp();

        // Add this event to our list of events
        scaleReadEvents.add(scaleReadEvent);

        // Do we have enough reads to get a stable value?
        if (scaleReadEvents.size() < identicalReadsRequired) {
            // No, just return
            return;
        }

        // Get the first event and extract the timestamp and grams from it
        ScaleReadEvent firstScaleReadEvent = scaleReadEvents.get(0);
        long timestamp = firstScaleReadEvent.getTimestamp();
        int grams = firstScaleReadEvent.getGrams();

        // Check to see if we have a stable read
        for (int loop = 1; loop < scaleReadEvents.size(); loop++) {
            // Is this read the same as the first?
            if (scaleReadEvents.get(loop).getGrams() != grams) {
                // No, quit early and clear out the list
                scaleReadEvents.clear();
                return;
            }
        }

        // All reads are the same.  Emit a stable read event.
        eventBus.post(stableScaleReadEventFactory.create(timestamp, grams));

        // Remove the first event
        scaleReadEvents.remove(0);
    }

    @Override
    public synchronized void heartbeatEvent(HeartbeatEvent heartbeatEvent) {
        // Get the current time
        long current = System.currentTimeMillis();

        // See how long it has been since our last event
        long last = current - lastScaleReadEventTimestamp;

        // Has it been too long?
        if (last > maxThreshold) {
            // Yes, throw away our old events
            scaleReadEvents.clear();
        }
    }

    /**
     * For testing only
     *
     * @return
     */
    protected List<ScaleReadEvent> getScaleReadEvents() {
        return scaleReadEvents;
    }
}
