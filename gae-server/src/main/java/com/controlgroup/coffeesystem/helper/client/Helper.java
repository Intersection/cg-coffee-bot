package com.controlgroup.coffeesystem.helper.client;

import com.google.gwt.core.client.EntryPoint;

/**
 * Entry point classes define <code>onModuleLoad()</code>
 */
public class Helper implements EntryPoint {
    private final HelperRpc helperRpc = new HelperRpc(Utils.EVENT_BUS);

    public void onModuleLoad() {
        helperRpc.scheduleRpc(true);
    }
}
