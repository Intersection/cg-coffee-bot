package com.controlgroup.coffeesystem.zelda.client.handlers;

import com.controlgroup.coffeesystem.helper.client.Utils;
import com.controlgroup.coffeesystem.zelda.client.events.*;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.Window;
import com.google.web.bindery.event.shared.binder.EventBinder;
import com.google.web.bindery.event.shared.binder.EventHandler;

/**
 * Created by timmattison on 1/26/15.
 */
public class KonamiCodeHandler {
    interface KonamiCodeHandlerEventBinder extends EventBinder<KonamiCodeHandler> {
    }

    public final KonamiCodeHandlerEventBinder konamiCodeHandleEventBinder = GWT.create(KonamiCodeHandlerEventBinder.class);
    public static final EventBus eventBus = Utils.EVENT_BUS;

    public static final String expectedCode = "UP UP DOWN DOWN LEFT RIGHT LEFT RIGHT B A";
    public static String code = "";
    public static String separator = "";

    public KonamiCodeHandler() {
        konamiCodeHandleEventBinder.bindEventHandlers(this, eventBus);
    }

    @EventHandler
    void onUpButtonPressedEvent(UpButtonPressedEvent upButtonPressedEvent) {
        code = code + separator + "UP";
        checkCode();
    }

    @EventHandler
    void onDownButtonPressedEvent(DownButtonPressedEvent downButtonPressedEvent) {
        code = code + separator + "DOWN";
        checkCode();
    }

    @EventHandler
    void onLeftButtonPressedEvent(LeftButtonPressedEvent leftButtonPressedEvent) {
        code = code + separator + "LEFT";
        checkCode();
    }

    @EventHandler
    void onRightButtonPressedEvent(RightButtonPressedEvent rightButtonPressedEvent) {
        code = code + separator + "RIGHT";
        checkCode();
    }

    @EventHandler
    void onBButtonPressedEvent(BButtonPressedEvent bButtonPressedEvent) {
        code = code + separator + "B";
        checkCode();
    }

    @EventHandler
    void onAButtonPressedEvent(AButtonPressedEvent aButtonPressedEvent) {
        code = code + separator + "A";
        checkCode();
    }

    private void checkCode() {
        if (code.length() > 0) {
            separator = " ";
        }

        if (expectedCode.equals(code)) {
            Window.alert("KONAMI'd!");
            code = "";
        } else if (!expectedCode.startsWith(code)) {
            code = "";
        }

        if (code.length() == 0) {
            separator = "";
        }
    }
}
