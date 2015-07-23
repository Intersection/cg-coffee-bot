package com.controlgroup.coffeesystem.zelda.client;

import com.google.gwt.dom.client.ImageElement;

/**
 * Created by timmattison on 1/26/15.
 */
public class GlobalState {
    public static int linkDestinationX = -1;
    public static int linkDestinationY = -1;

    public static int frameCounter = 0;
    public static int walkingState = 0;
    public static LinkState linkState = LinkState.LINK_WALKING_UP;
    public static MouseState mouseState = MouseState.MOUSE_UP;
    public static ImageElement linkImageElement = Graphics.linkUp1ImageElement;
}
