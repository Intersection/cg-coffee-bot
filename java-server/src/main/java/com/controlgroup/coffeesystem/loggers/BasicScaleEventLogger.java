package com.controlgroup.coffeesystem.loggers;

import com.controlgroup.coffeesystem.interfaces.ScaleEventLogger;
import com.controlgroup.coffeesystem.interfaces.ScaleReadEvent;
import com.google.common.eventbus.Subscribe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by timmattison on 12/29/14.
 */
public class BasicScaleEventLogger implements ScaleEventLogger {
    private final Logger logger = LoggerFactory.getLogger(BasicScaleEventLogger.class);

    @Subscribe
    public void scaleReadEvent(ScaleReadEvent scaleReadEvent) {
        logger.info("RAW " + scaleReadEvent.getTimestamp() + " " + scaleReadEvent.getGrams() + " grams");
    }
}
