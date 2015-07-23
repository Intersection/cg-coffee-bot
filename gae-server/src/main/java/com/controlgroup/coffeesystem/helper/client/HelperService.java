package com.controlgroup.coffeesystem.helper.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("HelperService")
public interface HelperService extends RemoteService {
    CoffeeStatus getCoffeeStatus() throws DataNotFoundException;

    /**
     * Utility/Convenience class.
     * Use HelperService.App.getInstance() to access static instance of HelperServiceAsync
     */
    public static class App {
        private static HelperServiceAsync ourInstance = GWT.create(HelperService.class);

        public static synchronized HelperServiceAsync getInstance() {
            return ourInstance;
        }
    }
}
