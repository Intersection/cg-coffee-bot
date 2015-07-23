package com.controlgroup.coffeesystem.helper.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.SimpleEventBus;

/**
 * Created by timmattison on 1/6/15.
 */
public class Utils {
    public static EventBus EVENT_BUS = GWT.create(SimpleEventBus.class);
}
