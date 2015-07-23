package com.controlgroup.coffeesystem.loggers;

import com.controlgroup.coffeesystem.events.HeartbeatEvent;
import com.controlgroup.coffeesystem.interfaces.HeartbeatEventLogger;
import com.google.common.eventbus.Subscribe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by timmattison on 12/29/14.
 */
public class BasicHeartbeatEventLogger implements HeartbeatEventLogger {
    public static final String LOG_MESSAGE = "GOT HEARTBEAT!";
    private final Logger logger = LoggerFactory.getLogger(BasicHeartbeatEventLogger.class);

    @Subscribe
    public void heartbeatEvent(HeartbeatEvent heartbeatEvent) {
        logger.info(LOG_MESSAGE);
    }
}
