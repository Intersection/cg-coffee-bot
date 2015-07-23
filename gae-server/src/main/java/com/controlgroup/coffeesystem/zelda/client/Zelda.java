package com.controlgroup.coffeesystem.zelda.client;

import com.controlgroup.coffeesystem.helper.client.CoffeeStatus;
import com.controlgroup.coffeesystem.helper.client.Utils;
import com.controlgroup.coffeesystem.helper.client.events.*;
import com.controlgroup.coffeesystem.zelda.client.events.*;
import com.controlgroup.coffeesystem.zelda.client.handlers.KonamiCodeHandler;
import com.controlgroup.coffeesystem.zelda.client.handlers.LinkMovementHandler;
import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.CssColor;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.web.bindery.event.shared.binder.EventBinder;
import com.google.web.bindery.event.shared.binder.EventHandler;

/**
 * Created by timmattison on 1/6/15.
 */
public class Zelda implements EntryPoint {
    private KeyUpHandler keyUpHandler;
    private KeyDownHandler keyDownHandler;
    private MouseDownHandler mouseDownHandler;
    private MouseUpHandler mouseUpHandler;
    private MouseOutHandler mouseOutHandler;
    private TouchStartHandler touchStartHandler;
    private TouchMoveHandler touchMoveHandler;
    private TouchEndHandler touchEndHandler;
    private TouchCancelHandler touchCancelHandler;

    interface CoffeeStatusEventBinder extends EventBinder<Zelda> {
    }

    public static final String NO_CANVAS_SUPPORT = "Your browser does not have canvas support.  Give up the BlackBerry rdp!";
    public static final String NO_CANVAS_DIV = "Canvas DIV is NULL, looks like the HTML file has not been set up correctly";
    public static final int UNKNOWN = -1;
    public static final int NO_CARAFE = -1;
    public static final int LOADING = -2;
    public static final int MILLISECONDS_PER_FRAME = 100;
    private static final long VERY_OLD = 9999999;
    public final CoffeeStatusEventBinder coffeeStatusEventBinder = GWT.create(CoffeeStatusEventBinder.class);
    public static final EventBus eventBus = Utils.EVENT_BUS;

    private int canvasWidth;
    private int canvasHeight;
    private static final int minimumWidth = 100;
    private static final int minimumHeight = 100;
    private int coffeeCupCount = LOADING;
    private long coffeeAgeInMinutes = UNKNOWN;
    private boolean staticBackgroundSetup = false;

    private final CssColor redrawColor = CssColor.make(0, 0, 0);

    private final int border = 20;

    private final int zeldaBackgroundWidth = Graphics.zeldaBackground.getWidth();
    private final int zeldaBackgroundHeight = Graphics.zeldaBackground.getHeight();
    private final double backgroundAspectRatio = (double) zeldaBackgroundWidth / (double) zeldaBackgroundHeight;

    private Canvas canvas;
    private Canvas backbuffer;
    private Canvas staticBackground;
    private Context2d canvasContext;
    private Context2d backbufferContext;
    private Context2d staticBackgroundContext;

    LinkMovementHandler linkMovementHandler = new LinkMovementHandler();
    KonamiCodeHandler konamiCodeHandler = new KonamiCodeHandler();

    public void onModuleLoad() {
        coffeeStatusEventBinder.bindEventHandlers(this, eventBus);

        Window.addResizeHandler(new ResizeHandler() {
            @Override
            public void onResize(ResizeEvent event) {
                int previousCanvasWidth = canvasWidth;
                int previousCanvasHeight = canvasHeight;

                recalculateCanvasSize();

                // Have the dimensions of the canvas changed?
                if ((previousCanvasWidth != canvasWidth) || (previousCanvasHeight != canvasHeight)) {
                    // Yes, reconfigure the canvases
                    configureCanvases();
                }
            }
        });

        recalculateCanvasSize();

        RootPanel canvasDiv = RootPanel.get("canvasDiv");

        createKeyboardHandlers();
        createMouseHandlers();
        createTouchHandlers();

        wireKeyboardHandlers();
        wireMouseAndTouchHandlers();

        if (canvasDiv == null) {
            Window.alert(NO_CANVAS_DIV);
            return;
        }

        // Quit if we are unable to create the canvases
        if (!createCanvases()) {
            Window.alert(NO_CANVAS_SUPPORT);
            return;
        }

        // Configure the canvases
        configureCanvases();

        if ((canvasContext = getContext2d(canvas)) == null) return;
        if ((backbufferContext = getContext2d(backbuffer)) == null) return;
        if ((staticBackgroundContext = getContext2d(staticBackground)) == null) return;

        canvasDiv.add(canvas);

        Timer timer = new Timer() {
            @Override
            public void run() {
                GlobalState.frameCounter++;

                drawFrame();

                // TODO - Check if mouse is still "down" and keep moving!
                if (GlobalState.mouseState == MouseState.MOUSE_PRESSED_DOWN) {
                    eventBus.fireEvent(new DownButtonPressedEvent());
                } else if (GlobalState.mouseState == MouseState.MOUSE_PRESSED_UP) {
                    eventBus.fireEvent(new UpButtonPressedEvent());
                } else if (GlobalState.mouseState == MouseState.MOUSE_PRESSED_RIGHT) {
                    eventBus.fireEvent(new RightButtonPressedEvent());
                } else if (GlobalState.mouseState == MouseState.MOUSE_PRESSED_LEFT) {
                    eventBus.fireEvent(new LeftButtonPressedEvent());
                }
            }
        };

        timer.scheduleRepeating(MILLISECONDS_PER_FRAME);

        eventBus.fireEvent(new InitializeEvent());
    }

    private void wireMouseAndTouchHandlers() {
        // Start moving
        RootPanel.get().addDomHandler(mouseDownHandler, MouseDownEvent.getType());
        RootPanel.get().addDomHandler(touchStartHandler, TouchStartEvent.getType());
        RootPanel.get().addDomHandler(touchMoveHandler, TouchMoveEvent.getType());

        // Stop/cancel moving
        RootPanel.get().addDomHandler(mouseUpHandler, MouseUpEvent.getType());
        RootPanel.get().addDomHandler(mouseOutHandler, MouseOutEvent.getType());
        RootPanel.get().addDomHandler(touchEndHandler, TouchEndEvent.getType());
        RootPanel.get().addDomHandler(touchCancelHandler, TouchCancelEvent.getType());
    }

    private void wireKeyboardHandlers() {
        RootPanel.get().addDomHandler(keyDownHandler, KeyDownEvent.getType());
        RootPanel.get().addDomHandler(keyUpHandler, KeyUpEvent.getType());
    }

    private void createTouchHandlers() {
        touchStartHandler = new TouchStartHandler() {
            @Override
            public void onTouchStart(TouchStartEvent event) {
                int x = event.getTargetTouches().get(0).getClientX();
                int y = event.getTargetTouches().get(0).getClientY();

                convertPositionToMouseState(x, y);
            }
        };

        touchMoveHandler = new TouchMoveHandler() {
            @Override
            public void onTouchMove(TouchMoveEvent event) {
                int x = event.getTargetTouches().get(0).getClientX();
                int y = event.getTargetTouches().get(0).getClientY();

                convertPositionToMouseState(x, y);
            }
        };

        touchEndHandler = new TouchEndHandler() {
            @Override
            public void onTouchEnd(TouchEndEvent event) {
                GlobalState.mouseState = MouseState.MOUSE_UP;
            }
        };

        touchCancelHandler = new TouchCancelHandler() {
            @Override
            public void onTouchCancel(TouchCancelEvent event) {
                GlobalState.mouseState = MouseState.MOUSE_UP;
            }
        };
    }

    private void createMouseHandlers() {
        mouseDownHandler = new MouseDownHandler() {
            @Override
            public void onMouseDown(MouseDownEvent event) {
                int x = event.getClientX();
                int y = event.getClientY();

                convertPositionToMouseState(x, y);
            }
        };

        mouseUpHandler = new MouseUpHandler() {
            @Override
            public void onMouseUp(MouseUpEvent event) {
                GlobalState.mouseState = MouseState.MOUSE_UP;
            }
        };

        mouseOutHandler = new MouseOutHandler() {
            @Override
            public void onMouseOut(MouseOutEvent event) {
                GlobalState.mouseState = MouseState.MOUSE_UP;
            }
        };
    }

    private void createKeyboardHandlers() {
        keyDownHandler = new KeyDownHandler() {
            @Override
            public void onKeyDown(KeyDownEvent event) {
                if (event.isUpArrow()) {
                    eventBus.fireEvent(new UpButtonPressedEvent());
                } else if (event.isDownArrow()) {
                    eventBus.fireEvent(new DownButtonPressedEvent());
                } else if (event.isLeftArrow()) {
                    eventBus.fireEvent(new LeftButtonPressedEvent());
                } else if (event.isRightArrow()) {
                    eventBus.fireEvent(new RightButtonPressedEvent());
                } else if (event.getNativeKeyCode() == 'B') {
                    eventBus.fireEvent(new BButtonPressedEvent());
                } else if (event.getNativeKeyCode() == 'A') {
                    eventBus.fireEvent(new AButtonPressedEvent());
                }
            }
        };

        keyUpHandler = new KeyUpHandler() {
            @Override
            public void onKeyUp(KeyUpEvent event) {
            }
        };
    }

    private void convertPositionToMouseState(int x, int y) {
        // TODO - This would make more sense with regions.  As it stands now it is simple but lame.
        if (y > (canvasHeight - (canvasHeight / 4))) {
            GlobalState.mouseState = MouseState.MOUSE_PRESSED_DOWN;
        } else if (y < (canvasHeight / 4)) {
            GlobalState.mouseState = MouseState.MOUSE_PRESSED_UP;
        } else if (x > (canvasWidth - (canvasWidth / 4))) {
            GlobalState.mouseState = MouseState.MOUSE_PRESSED_RIGHT;
        } else if (x < (canvasWidth / 4)) {
            GlobalState.mouseState = MouseState.MOUSE_PRESSED_LEFT;
        }
    }

    private boolean createCanvases() {
        if ((canvas = Canvas.createIfSupported()) == null) return false;
        if ((backbuffer = Canvas.createIfSupported()) == null) return false;
        if ((staticBackground = Canvas.createIfSupported()) == null) return false;

        return true;
    }

    private void configureCanvases() {
        // We need to set up the static background again on a resize
        staticBackgroundSetup = false;

        setCanvasWidthAndHeight(canvas);
        setCanvasWidthAndHeight(backbuffer, zeldaBackgroundWidth, zeldaBackgroundHeight);
        setCanvasWidthAndHeight(staticBackground, zeldaBackgroundWidth, zeldaBackgroundHeight);
    }

    private void recalculateCanvasSize() {
        // Find the biggest canvas we can create with the correct aspect ratio
        int clientWidth = Window.getClientWidth();
        int clientHeight = Window.getClientHeight();
        double windowAspectRatio = (double) clientWidth / (double) clientHeight;

        if (backgroundAspectRatio == windowAspectRatio) {
            // The aspect ratio is equal.  Make the canvas the size of the client and don't worry about it.
            canvasWidth = clientWidth;
            canvasHeight = clientHeight;
        } else if (backgroundAspectRatio > windowAspectRatio) {
            // The background is wider than the window
            canvasWidth = clientWidth;
            canvasHeight = (int) (clientWidth / backgroundAspectRatio);
        } else {
            // The background is taller than the window
            canvasHeight = clientHeight;
            canvasWidth = (int) (clientHeight * backgroundAspectRatio);
        }

        canvasHeight -= border;
        canvasWidth -= border;

        if (canvasHeight < 0) {
            canvasHeight = (int) (backgroundAspectRatio / minimumWidth);
        }

        if (canvasWidth < 0) {
            canvasWidth = (int) (minimumHeight * backgroundAspectRatio);
        }
    }

    private Context2d getContext2d(Canvas canvas) {
        Context2d canvasContext = canvas.getContext2d();

        if (canvasContext == null) {
            Window.alert(NO_CANVAS_SUPPORT);
            return null;
        }

        return canvasContext;
    }

    private void setCanvasWidthAndHeight(Canvas canvas) {
        setCanvasWidthAndHeight(canvas, canvasWidth, canvasHeight);
    }

    private void setCanvasWidthAndHeight(Canvas canvas, int width, int height) {
        canvas.setWidth(width + "px");
        canvas.setCoordinateSpaceWidth(width);
        canvas.setHeight(height + "px");
        canvas.setCoordinateSpaceHeight(height);
    }

    private void drawFrame() {
        // Static setup
        staticSetup();

        if (coffeeCupCount == LOADING) {
            Drawing.drawLifeMeter(GlobalState.frameCounter, backbufferContext, false);
            Drawing.drawString(backbufferContext, "CHECKING HYRULE'S", 165, 288, 4);
            Drawing.drawString(backbufferContext, "COFFEE SUPPLY...", 180, 310, 4);
        } else if (coffeeCupCount == NO_CARAFE) {
            Drawing.drawLifeMeter(GlobalState.frameCounter, backbufferContext, false);
        } else {
            // Dynamic stuff
            if (coffeeCupCount != 0) {
                Drawing.drawLifeMeter(GlobalState.frameCounter, backbufferContext, true);
                Drawing.drawCoffeeCups(backbufferContext, coffeeCupCount, coffeeAgeInMinutes);
                Drawing.drawOldGuy(redrawColor, backbufferContext);
                Drawing.drawString(backbufferContext, "IT'S DANGEROUS TO GO", 130, 288, 4);
                Drawing.drawString(backbufferContext, "ALONE! TAKE THIS.", 186, 310, 4);
            } else {
                Drawing.drawLifeMeter(GlobalState.frameCounter, backbufferContext, false);
            }

            Drawing.drawFire(GlobalState.frameCounter, backbufferContext);
        }

        Drawing.drawLink(backbufferContext, GlobalState.linkImageElement, GlobalState.linkDestinationX, GlobalState.linkDestinationY);

        Drawing.copyBufferOverOtherBuffer(redrawColor, canvasContext, backbufferContext);
    }

    private void staticSetup() {
        if (!staticBackgroundSetup) {
            Drawing.clear(redrawColor, staticBackgroundContext);
            Drawing.drawBackground(staticBackgroundContext);
            Drawing.drawCaveWalls(staticBackgroundContext, 0, Drawing.HUD_VERTICAL_SIZE);

            staticBackgroundSetup = true;
        }

        Drawing.copyBufferOverOtherBuffer(redrawColor, backbufferContext, staticBackgroundContext);
    }

    @EventHandler
    void onCoffeeStatusUpdateEvent(CoffeeStatusUpdateEvent coffeeStatusUpdateEvent) {
        CoffeeStatus coffeeStatus = coffeeStatusUpdateEvent.getCoffeeStatus();

        if (coffeeStatus == null) {
            return;
        }

        if (!coffeeStatus.carafePresent) {
            coffeeCupCount = NO_CARAFE;
            return;
        }

        coffeeCupCount = (int) coffeeStatus.cupsRemaining;

        coffeeAgeInMinutes = VERY_OLD;

        if (coffeeStatus.lastBrewed != UNKNOWN) {
            coffeeAgeInMinutes = (System.currentTimeMillis() - coffeeStatus.lastBrewed) / 1000 / 60;
        }
    }

    @EventHandler
    void onCarafeRemovedEvent(CarafeRemovedEvent carafeRemovedEvent) {
        // TODO - Carafe was removed
    }

    @EventHandler
    void onCoffeePumped(CoffeePumpedEvent coffeePumpedEvent) {
        // TODO - Coffee was pumped
    }

    @EventHandler
    void onFirstCoffeeStatusUpdateEvent(FirstCoffeeStatusUpdateEvent firstCoffeeStatusUpdateEvent) {
        // TODO - First time we saw a coffee status update
    }

    @EventHandler
    void onFreshCarafePlaced(FreshCarafePlacedEvent freshCarafePlacedEvent) {
        // TODO - Fresh carafe was placed
    }

    @EventHandler
    void onOldCarafePlaced(OldCarafePlacedEvent oldCarafePlacedEvent) {
        // TODO - Old carafe was placed
    }
}
