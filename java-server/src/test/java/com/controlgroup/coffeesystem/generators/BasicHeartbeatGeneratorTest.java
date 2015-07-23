package com.controlgroup.coffeesystem.generators;

import com.controlgroup.coffeesystem.events.HeartbeatEvent;
import com.controlgroup.coffeesystem.interfaces.HeartbeatEventFactory;
import com.google.common.eventbus.EventBus;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.core.Is.is;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.*;

/**
 * Created by timmattison on 1/13/15.
 */
public class BasicHeartbeatGeneratorTest {
    private static final int interval = 1;
    private HeartbeatEventFactory mockHeartbeatEventFactory;
    private EventBus mockEventBus;
    private HeartbeatEvent heartbeatEvent;
    private BasicHeartbeatGenerator basicHeartbeatGenerator;
    private int heartbeatEventCount = 0;

    @Before
    public void setup() {
        mockHeartbeatEventFactory = mock(HeartbeatEventFactory.class);
        heartbeatEvent = new HeartbeatEvent();
        when(mockHeartbeatEventFactory.create()).thenReturn(heartbeatEvent);
        mockEventBus = mock(EventBus.class);
        basicHeartbeatGenerator = new BasicHeartbeatGenerator(interval, mockEventBus, mockHeartbeatEventFactory);

        doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocationOnMock) throws Throwable {
                Object argument = invocationOnMock.getArguments()[0];

                if (argument instanceof HeartbeatEvent) {
                    heartbeatEventCount++;
                } else {
                    throw new UnsupportedOperationException("This kind of event was not expected");
                }

                return null;
            }
        }).when(mockEventBus).post(anyObject());
    }

    @Test
    public void shouldPostAtLeastOneEvent() throws InterruptedException {
        Assert.assertThat(heartbeatEventCount, is(0));
        basicHeartbeatGenerator.start();

        Thread.sleep(interval * 10);

        basicHeartbeatGenerator.stop();

        Assert.assertThat(heartbeatEventCount, is(greaterThan(0)));
    }

    @Test
    public void shouldStopPostingEventsWhenStopped() throws InterruptedException {
        basicHeartbeatGenerator.start();

        Thread.sleep(interval * 10);

        basicHeartbeatGenerator.stop();

        Assert.assertThat(heartbeatEventCount, is(greaterThan(0)));

        Thread.sleep(interval * 10);

        int currentHeartbeatEventCount = heartbeatEventCount;

        Thread.sleep(interval * 10);

        Assert.assertThat(heartbeatEventCount, is(currentHeartbeatEventCount));
    }

    @Test
    public void shouldDoNothingWhenStartedTwice() {
        basicHeartbeatGenerator.start();
        basicHeartbeatGenerator.start();
        basicHeartbeatGenerator.stop();
    }
}
