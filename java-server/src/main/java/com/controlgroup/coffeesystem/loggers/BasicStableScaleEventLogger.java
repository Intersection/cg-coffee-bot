package com.controlgroup.coffeesystem.loggers;

import com.controlgroup.coffeesystem.interfaces.StableScaleEventLogger;
import com.controlgroup.coffeesystem.interfaces.StableScaleReadEvent;
import com.google.common.eventbus.Subscribe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by timmattison on 12/29/14.
 */
public class BasicStableScaleEventLogger implements StableScaleEventLogger {
    private final Logger logger = LoggerFactory.getLogger(BasicStableScaleEventLogger.class);

    @Subscribe
    public void stableScaleReadEvent(StableScaleReadEvent stableScaleReadEvent) {
        logger.info("STABLE " + stableScaleReadEvent.getTimestamp() + " " + stableScaleReadEvent.getGrams() + " grams");
    }
}
