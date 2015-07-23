package com.controlgroup.coffeesystem.iframe.client;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * Created by timmattison on 2/3/15.
 */
public class CssAnimation {
    public static native void registerCssCallback(Element elem, AsyncCallback<Void> callback) /*-{
        var eventListener = function () {
            $entry(@CssAnimation::cssCallback(Lcom/google/gwt/user/client/rpc/AsyncCallback;)(callback));
            elem.removeEventListener("webkitAnimationEnd", eventListener);
        };

        elem.addEventListener("webkitAnimationEnd", eventListener, false);
    }-*/;

    protected static void cssCallback(AsyncCallback<Void> callback) {
        callback.onSuccess(null);
    }
}
