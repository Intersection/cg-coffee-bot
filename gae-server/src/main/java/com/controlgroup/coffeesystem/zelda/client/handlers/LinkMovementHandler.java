package com.controlgroup.coffeesystem.zelda.client.handlers;

import com.controlgroup.coffeesystem.helper.client.Utils;
import com.controlgroup.coffeesystem.zelda.client.*;
import com.controlgroup.coffeesystem.zelda.client.events.*;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.web.bindery.event.shared.binder.EventBinder;
import com.google.web.bindery.event.shared.binder.EventHandler;

/**
 * Created by timmattison on 1/26/15.
 */
public class LinkMovementHandler {
    private static final int MOVEMENT_INCREMENT = 8;
    private static final int Y_MOVEMENT_INCREMENT = MOVEMENT_INCREMENT;
    private static final int X_MOVEMENT_INCREMENT = MOVEMENT_INCREMENT;
    private static XY linkStartLocation;

    private final CollisionDetection collisionDetection = new SimpleCollisionDetection();

    interface LinkMovementHandlerEventBinder extends EventBinder<LinkMovementHandler> {
    }

    public final LinkMovementHandlerEventBinder linkMovementHandlerEventBinder = GWT.create(LinkMovementHandlerEventBinder.class);
    public static final EventBus eventBus = Utils.EVENT_BUS;

    public LinkMovementHandler() {
        linkMovementHandlerEventBinder.bindEventHandlers(this, eventBus);
    }

    public static XY getLinkStartLocation() {
        if (linkStartLocation == null) {
            int y = 0;

            for (int[] rowData : Drawing.caveWalls) {
                int x = 0;

                for (int columnData : rowData) {
                    if (columnData == 2) {
                        linkStartLocation = new XY(x, y);
                        return linkStartLocation;
                    }

                    x++;
                }

                y++;
            }

            linkStartLocation = new XY(-1, -1);
        }

        return linkStartLocation;
    }

    @EventHandler
    void onInitializeEvent(InitializeEvent initializeEvent) {
        XY linkLocation = getLinkStartLocation();

        int nextX = linkLocation.getX() * Drawing.gridElementWidth;
        int nextY = (linkLocation.getY() * Drawing.gridElementHeight) + Drawing.HUD_VERTICAL_SIZE;

//        XY nextXY = new XY(nextX, nextY);

        updateLinkPosition(nextX, nextY);
    }

    private void calculateAndBoundsCheck(XY nextXY) {
        calculateLinkSourceData();

        if (boundsCheckLink(nextXY)) {
            updateLinkPosition(nextXY.getX(), nextXY.getY());
        }
    }

    @EventHandler
    void onUpButtonPressedEvent(UpButtonPressedEvent upButtonPressedEvent) {
        int nextY = GlobalState.linkDestinationY - Y_MOVEMENT_INCREMENT;
        int nextX = GlobalState.linkDestinationX;

        updateWalkingState();

        GlobalState.linkState = LinkState.LINK_WALKING_UP;

        XY nextXY = new XY(nextX, nextY);

        calculateAndBoundsCheck(nextXY);
    }

    private void updateWalkingState() {
        GlobalState.walkingState += 1;
    }

    @EventHandler
    void onDownButtonPressedEvent(DownButtonPressedEvent downButtonPressedEvent) {
        int nextY = GlobalState.linkDestinationY + Y_MOVEMENT_INCREMENT;
        int nextX = GlobalState.linkDestinationX;

        updateWalkingState();

        GlobalState.linkState = LinkState.LINK_WALKING_DOWN;

        XY nextXY = new XY(nextX, nextY);

        calculateAndBoundsCheck(nextXY);
    }

    @EventHandler
    void onLeftButtonPressedEvent(LeftButtonPressedEvent leftButtonPressedEvent) {
        int nextX = GlobalState.linkDestinationX - X_MOVEMENT_INCREMENT;
        int nextY = GlobalState.linkDestinationY;

        updateWalkingState();

        GlobalState.linkState = LinkState.LINK_WALKING_LEFT;

        XY nextXY = new XY(nextX, nextY);

        calculateAndBoundsCheck(nextXY);
    }

    @EventHandler
    void onRightButtonPressedEvent(RightButtonPressedEvent rightButtonPressedEvent) {
        int nextX = GlobalState.linkDestinationX + X_MOVEMENT_INCREMENT;
        int nextY = GlobalState.linkDestinationY;
        GlobalState.linkState = LinkState.LINK_WALKING_RIGHT;

        updateWalkingState();

        XY nextXY = new XY(nextX, nextY);

        calculateAndBoundsCheck(nextXY);
    }

    private boolean boundsCheckLink(XY nextXY) {
        int x = nextXY.getX();
        int y = nextXY.getY();

        // Find what cells we're now overlapping
        XY[] overlapping = collisionDetection.overlapping(Drawing.gridElementWidth, Drawing.gridElementHeight, x, y - Drawing.HUD_VERTICAL_SIZE, Drawing.linkWidth + Drawing.linkPadding, Drawing.linkHeight + Drawing.linkPadding);

//        logOverlapping(overlapping);

        // Find out of any of these cells are walls (value 1)
        for (XY xy : overlapping) {
            int xIndex = xy.getX();
            int yIndex = xy.getY();

            if (xIndex < 0) {
                GWT.log("Invalid X");
                return false;
            }

            if (yIndex < 0) {
//                GWT.log("Invalid Y");
                return false;
            }

//            GWT.log(String.valueOf(Drawing.caveWalls.length));

            if (Drawing.caveWalls.length <= yIndex) {
                GWT.log("Out of bounds in Y axis");
                return false;
            }

//            GWT.log("Good 1 " + yIndex + ", " + Drawing.caveWalls.length);
            if (Drawing.caveWalls[yIndex].length <= xIndex) {
                GWT.log("Out of bounds in X axis");
                return false;
            }

//            GWT.log("Good 2");
            int tileData = Drawing.caveWalls[yIndex][xIndex];

            if ((tileData & 0x1) == 0x1) {
                // This is a wall, return early
//                GWT.log("Hit wall at " + xIndex + "," + yIndex);
                return false;
            }
//            GWT.log("Good 3");
        }

        // Didn't hit a wall, move Link
//        GWT.log("didn't hit wall!");

        return true;
    }

    private void logOverlapping(XY[] overlapping) {
        GWT.log("----------------------------------------------");

        for (XY xy : overlapping) {
            GWT.log(xy.getX() + ", " + xy.getY());
        }
    }

    private void updateLinkPosition(int x, int y) {
        GlobalState.linkDestinationX = x;
        GlobalState.linkDestinationY = y;
    }

    private void calculateLinkSourceData() {
        boolean evenFrame = ((GlobalState.walkingState % 2) == 0);

        switch (GlobalState.linkState) {
            case LINK_WALKING_DOWN:
                if (evenFrame) {
                    GlobalState.linkImageElement = Graphics.linkDown1ImageElement;
                } else {
                    GlobalState.linkImageElement = Graphics.linkDown2ImageElement;
                }
                break;
            case LINK_WALKING_LEFT:
                if (evenFrame) {
                    GlobalState.linkImageElement = Graphics.linkLeft1ImageElement;
                } else {
                    GlobalState.linkImageElement = Graphics.linkLeft2ImageElement;
                }
                break;
            case LINK_WALKING_UP:
                if (evenFrame) {
                    GlobalState.linkImageElement = Graphics.linkUp1ImageElement;
                } else {
                    GlobalState.linkImageElement = Graphics.linkUp2ImageElement;
                }
                break;
            case LINK_WALKING_RIGHT:
                if (evenFrame) {
                    GlobalState.linkImageElement = Graphics.linkRight1ImageElement;
                } else {
                    GlobalState.linkImageElement = Graphics.linkRight2ImageElement;
                }
                break;
        }
    }

    @EventHandler
    void onBButtonPressedEvent(BButtonPressedEvent bButtonPressedEvent) {
    }

    @EventHandler
    void onAButtonPressedEvent(AButtonPressedEvent aButtonPressedEvent) {
    }
}
