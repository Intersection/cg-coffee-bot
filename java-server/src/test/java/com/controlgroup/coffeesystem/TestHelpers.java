package com.controlgroup.coffeesystem;

import com.controlgroup.coffeesystem.generators.interfaces.TimestampGenerator;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by timmattison on 1/13/15.
 */
public class TestHelpers {
    public static final String FAKE_TIMESTAMP = "fakeTimestamp";

    public static TimestampGenerator getMockTimestampGenerator() {
        TimestampGenerator mockTimestampGenerator = mock(TimestampGenerator.class);
        when(mockTimestampGenerator.generateTimestamp()).thenReturn(FAKE_TIMESTAMP);
        return mockTimestampGenerator;
    }
}
