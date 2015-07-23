package com.controlgroup.coffeesystem.helper.client;

import com.controlgroup.coffeesystem.helper.client.events.*;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.web.bindery.event.shared.binder.EventBinder;
import com.google.web.bindery.event.shared.binder.EventHandler;

/**
 * Created by timmattison on 1/7/15.
 */
public class HelperRpc implements AsyncCallback<CoffeeStatus> {
    interface CoffeeStatusEventBinder extends EventBinder<HelperRpc> {
    }

    public final CoffeeStatusEventBinder coffeeStatusEventBinder = GWT.create(CoffeeStatusEventBinder.class);

    private final EventBus eventBus;
    private static final int REFRESH_INTERVAL = 3000;
    private CoffeeStatus lastCoffeeStatus;

    public HelperRpc(EventBus eventBus) {
        this.eventBus = eventBus;
        coffeeStatusEventBinder.bindEventHandlers(this, eventBus);
    }

    @Override
    public void onFailure(Throwable caught) {
        scheduleRpc();

        eventBus.fireEvent(new RpcFailedEvent(caught));
    }

    @Override
    public void onSuccess(CoffeeStatus currentCoffeeStatus) {
        scheduleRpc();

        if (lastCoffeeStatus == null) {
            eventBus.fireEvent(new FirstCoffeeStatusUpdateEvent(currentCoffeeStatus));
        } else if (carafeRemoved(lastCoffeeStatus, currentCoffeeStatus)) {
            eventBus.fireEvent(new CarafeRemovedEvent(System.currentTimeMillis()));
        } else if (freshCarafePlaced(lastCoffeeStatus, currentCoffeeStatus)) {
            eventBus.fireEvent(new FreshCarafePlacedEvent(currentCoffeeStatus));
        } else if (oldCarafePlaced(lastCoffeeStatus, currentCoffeeStatus)) {
            eventBus.fireEvent(new OldCarafePlacedEvent(currentCoffeeStatus));
        } else if (coffeePumped(lastCoffeeStatus, currentCoffeeStatus)) {
            eventBus.fireEvent(new CoffeePumpedEvent(currentCoffeeStatus));
        }

        // Always send a generic coffee status update event for systems that do their own processing
        eventBus.fireEvent(new CoffeeStatusUpdateEvent(currentCoffeeStatus));

        lastCoffeeStatus = currentCoffeeStatus;
    }

    private boolean coffeePumped(CoffeeStatus lastCoffeeStatus, CoffeeStatus currentCoffeeStatus) {
        return ((lastCoffeeStatus.carafePresent == true) && (currentCoffeeStatus.carafePresent == true) &&
                (lastCoffeeStatus.cupsRemaining > currentCoffeeStatus.cupsRemaining));
    }

    private boolean oldCarafePlaced(CoffeeStatus lastCoffeeStatus, CoffeeStatus currentCoffeeStatus) {
        return carafePlaced(lastCoffeeStatus, currentCoffeeStatus) && !freshCarafePlaced(lastCoffeeStatus, currentCoffeeStatus);
    }

    private boolean freshCarafePlaced(CoffeeStatus lastCoffeeStatus, CoffeeStatus currentCoffeeStatus) {
        return carafePlaced(lastCoffeeStatus, currentCoffeeStatus) && (currentCoffeeStatus.lastBrewed > 0);
    }

    private boolean carafePlaced(CoffeeStatus lastCoffeeStatus, CoffeeStatus currentCoffeeStatus) {
        return ((lastCoffeeStatus.carafePresent == false) && (currentCoffeeStatus.carafePresent == true));
    }

    private boolean carafeRemoved(CoffeeStatus lastCoffeeStatus, CoffeeStatus currentCoffeeStatus) {
        return ((lastCoffeeStatus.carafePresent == true) && (currentCoffeeStatus.carafePresent == false));
    }

    public void scheduleRpc() {
        scheduleRpc(false);
    }

    public void scheduleRpc(boolean first) {
        Timer timer = new Timer() {
            @Override
            public void run() {
                try {
                    HelperService.App.getInstance().getCoffeeStatus(HelperRpc.this);
                } catch (DataNotFoundException e) {
                    // No data found, just print the stack trace
                    e.printStackTrace();
                }
            }
        };

        if (first) {
            timer.run();
        } else {
            timer.schedule(getRefreshInterval());
        }
    }

    private static int getRefreshInterval() {
        return REFRESH_INTERVAL;
    }


    public static native boolean isUpdateViewPresent() /*-{
        if ($wnd.updateView) {
            return true;
        }

        return false;
    }-*/;

    public static native boolean isServerErrorPresent() /*-{
        if ($wnd.serverError) {
            return true;
        }

        return false;
    }-*/;

    public static native void gwtUpdateView(double lastBrewed, double cupsRemaining, boolean carafePresent) /*-{
        $wnd.updateView(lastBrewed, cupsRemaining, carafePresent);
    }-*/;

    private static void serverError(Throwable throwable) {
        if (throwable != null) {
            innerServerError(throwable.getMessage());
        }
    }

    private static native void innerServerError(String message) /*-{
        $wnd.serverError(message);
    }-*/;

    @EventHandler
    void onCoffeeStatusUpdateEvent(CoffeeStatusUpdateEvent coffeeStatusUpdateEvent) {
        // Is there an update view function?
        if (!isUpdateViewPresent()) {
            // No, we are not handling this for the application
            return;
        }

        // Update the client
        CoffeeStatus coffeeStatus = coffeeStatusUpdateEvent.getCoffeeStatus();
        gwtUpdateView(coffeeStatus.lastBrewed, coffeeStatus.cupsRemaining, coffeeStatus.carafePresent);
    }

    @EventHandler
    void onRpcFailed(RpcFailedEvent rpcFailedEvent) {
        // Is there a server error function?
        if (!isServerErrorPresent()) {
            // No, we are not handling this for the application
            return;
        }

        // Update the client
        serverError(rpcFailedEvent.getCaught());
    }
}
