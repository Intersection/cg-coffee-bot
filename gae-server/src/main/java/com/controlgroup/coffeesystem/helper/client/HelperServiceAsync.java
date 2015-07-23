package com.controlgroup.coffeesystem.helper.client;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface HelperServiceAsync {
    void getCoffeeStatus(AsyncCallback<CoffeeStatus> async) throws DataNotFoundException;
}
