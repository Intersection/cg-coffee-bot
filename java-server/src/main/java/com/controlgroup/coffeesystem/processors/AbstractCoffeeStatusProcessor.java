package com.controlgroup.coffeesystem.processors;

import com.controlgroup.coffeesystem.CoffeeStatus;
import com.controlgroup.coffeesystem.events.HeartbeatEvent;
import com.controlgroup.coffeesystem.interfaces.CoffeeStatusProcessor;
import com.google.common.eventbus.Subscribe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by timmattison on 12/29/14.
 */
public abstract class AbstractCoffeeStatusProcessor implements CoffeeStatusProcessor {
    private final Logger logger = LoggerFactory.getLogger(AbstractCoffeeStatusProcessor.class);
    protected List<CoffeeStatus> coffeeStatusList = new ArrayList<>();

    @Override
    @Subscribe
    public final synchronized void coffeeStatus(CoffeeStatus coffeeStatus) {
        coffeeStatusList.add(coffeeStatus);
    }

    @Override
    @Subscribe
    public final synchronized void heartbeatEvent(HeartbeatEvent heartbeatEvent) throws Exception {
        // Are there any statuses to post?
        if (coffeeStatusList.size() == 0) {
            // No, just return
            return;
        }

        // Get the last status (don't worry about posting old ones)
        CoffeeStatus coffeeStatus = coffeeStatusList.get(coffeeStatusList.size() - 1);

        // Post it to Google App Engine
        handle(coffeeStatus);

        // Success, clear out the status list
        coffeeStatusList.clear();
    }

    protected abstract void handle(CoffeeStatus coffeeStatus) throws Exception;
}
