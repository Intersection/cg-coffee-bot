package com.controlgroup.coffeesystem.interfaces;

import com.google.common.eventbus.Subscribe;

/**
 * Created by timmattison on 12/29/14.
 */
public interface StableScaleEventProcessor {
    /**
     * The header used in the configuration file for stable scale event processor options
     */
    public static final String NAME = "stableScaleEventProcessor";

    /**
     * The name of the empty carafe weight in grams field in the stable scale event processor configuration options
     */
    public static final String EMPTY_CARAFE_IN_GRAMS = "emptyCarafeInGrams";

    /**
     * The name of the full carafe weight in grams field in the stable scale event processor configuration options
     */
    public static final String FULL_CARAFE_IN_GRAMS = "fullCarafeInGrams";

    /**
     * The name of the tolerance in grams field in the stable scale event processor configuration options
     */
    public static final String TOLERANCE_IN_GRAMS = "toleranceInGrams";

    /**
     * The name of the grams per cup field in the stable scale event processor configuration options
     */
    public static final String GRAMS_PER_CUP = "gramsPerCup";

    /**
     * The name of the cup tolerance for full field (number of cups +/- the expected number at which we'll consider the
     * carafe full) in the stable scale event processor configuration options
     */
    public static final String CUP_TOLERANCE_FOR_FULL = "cupToleranceForFull";

    /**
     * Handles a stable scale read event
     *
     * @param stableScaleReadEvent
     */
    @Subscribe
    public void stableScaleReadEvent(StableScaleReadEvent stableScaleReadEvent);
}
