package com.controlgroup.coffeesystem.processors;

import com.controlgroup.coffeesystem.events.BasicScaleReadEvent;
import com.controlgroup.coffeesystem.events.HeartbeatEvent;
import com.controlgroup.coffeesystem.interfaces.ScaleReadEvent;
import com.controlgroup.coffeesystem.interfaces.StableScaleReadEvent;
import com.controlgroup.coffeesystem.interfaces.StableScaleReadEventFactory;
import com.google.common.eventbus.EventBus;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.*;

/**
 * Created by timmattison on 12/29/14.
 */
public class BasicScaleEventProcessorTest {
    public static final long FAKE_TIMESTAMP = 1000000000L;
    private static final int FAKE_GRAMS = 500;
    private BasicScaleEventProcessor basicScaleEventProcessor;
    private EventBus mockEventBus;
    private StableScaleReadEventFactory mockStableScaleReadEventFactory;
    private StableScaleReadEvent mockStableScaleReadEvent;
    private static final long maxThreshold = 1000;
    private static final int identicalReadsRequired = 4;

    @Before
    public void setup() {
        mockEventBus = mock(EventBus.class);
        mockStableScaleReadEventFactory = mock(StableScaleReadEventFactory.class);
        mockStableScaleReadEvent = mock(StableScaleReadEvent.class);

        when(mockStableScaleReadEventFactory.create(anyLong(), anyInt())).thenReturn(mockStableScaleReadEvent);

        basicScaleEventProcessor = new BasicScaleEventProcessor(mockEventBus, mockStableScaleReadEventFactory, maxThreshold, identicalReadsRequired);
    }

    @Test
    public void shouldEmitStableScaleReadEventAfterEnoughIdenticalReads() {
        long timestamp = FAKE_TIMESTAMP;
        int grams = FAKE_GRAMS;

        triggerStableRead(timestamp, grams);

        // Make sure we get the right timestamp and weight
        verify(mockStableScaleReadEventFactory, times(1)).create(FAKE_TIMESTAMP, FAKE_GRAMS);

        ArgumentCaptor<StableScaleReadEvent> stableScaleReadEventArgumentCaptor = ArgumentCaptor.forClass(StableScaleReadEvent.class);

        verify(mockEventBus, times(1)).post(stableScaleReadEventArgumentCaptor.capture());

        // Make sure the eventbus is called with the mock created by our factory
        Assert.assertThat(stableScaleReadEventArgumentCaptor.getValue(), is(mockStableScaleReadEvent));
    }

    @Test
    public void shouldNotEmitStableScaleReadEventWithUnstableRead() {
        long timestamp = FAKE_TIMESTAMP;
        int grams = FAKE_GRAMS;

        doUnstableRead(timestamp, grams);

        // Make sure we don't create any stable reads or emit any events
        verify(mockStableScaleReadEventFactory, times(0)).create(anyLong(), anyInt());
        verify(mockEventBus, times(0)).post(anyObject());
    }

    @Test
    public void shouldKeepCurrentEvents() {
        doOneRead(System.currentTimeMillis() + maxThreshold - 1);

        Assert.assertThat(basicScaleEventProcessor.getScaleReadEvents().size(), is(1));

        HeartbeatEvent heartbeatEvent = new HeartbeatEvent();
        basicScaleEventProcessor.heartbeatEvent(heartbeatEvent);

        Assert.assertThat(basicScaleEventProcessor.getScaleReadEvents().size(), is(1));
    }

    @Test
    public void shouldThrowAwayOldEvents() {
        doOneRead(FAKE_TIMESTAMP);

        Assert.assertThat(basicScaleEventProcessor.getScaleReadEvents().size(), is(1));

        HeartbeatEvent heartbeatEvent = new HeartbeatEvent();
        basicScaleEventProcessor.heartbeatEvent(heartbeatEvent);

        Assert.assertThat(basicScaleEventProcessor.getScaleReadEvents().size(), is(0));
    }

    private void triggerStableRead(long timestamp, int grams) {
        ScaleReadEvent scaleReadEvent = createScaleReadEvent(timestamp, grams);

        for (int loop = 0; loop < identicalReadsRequired; loop++) {
            basicScaleEventProcessor.scaleReadEvent(scaleReadEvent);
            scaleReadEvent = createScaleReadEvent(timestamp + loop + 1, grams);
        }
    }

    private void doOneRead(long timestamp) {
        basicScaleEventProcessor.scaleReadEvent(createScaleReadEvent(timestamp, FAKE_GRAMS));
    }

    private void doUnstableRead(long timestamp, int grams) {
        ScaleReadEvent scaleReadEvent = createScaleReadEvent(timestamp, grams - 1);

        for (int loop = 0; loop < identicalReadsRequired; loop++) {
            basicScaleEventProcessor.scaleReadEvent(scaleReadEvent);
            scaleReadEvent = createScaleReadEvent(timestamp + loop + 1, grams);
        }
    }

    private BasicScaleReadEvent createScaleReadEvent(long timestamp, int grams) {
        return new BasicScaleReadEvent(timestamp, grams);
    }
}
