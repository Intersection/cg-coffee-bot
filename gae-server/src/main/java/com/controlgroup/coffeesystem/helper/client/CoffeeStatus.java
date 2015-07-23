package com.controlgroup.coffeesystem.helper.client;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * Created by timmattison on 1/2/15.
 */
public class CoffeeStatus implements IsSerializable {
    public long lastBrewed;
    public long cupsRemaining;
    public boolean carafePresent;
}
