package com.controlgroup.coffeesystem.iframe.client;

import com.google.gwt.core.client.Duration;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.RootPanel;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by timmattison on 2/2/15.
 */
public class Iframe implements EntryPoint {
    public static final String WIDTH = "100%";
    public static final String HEIGHT = "100%";
    public static final String ABSOLUTE_PANEL_WIDTH = "100%";
    public static final String ABSOLUTE_PANEL_HEIGHT = "100%";
    public static final int PERIOD_MILLIS = 1000;
    private final AbsolutePanel absolutePanel = new AbsolutePanel();

    private String[] interfaces = new String[]{"coffee-mainframe/index.html", "coffee-rain/index.html", "Zelda.html"};

    private List<Frame> frameList = new ArrayList<Frame>();

    private int frontFrame = 0;
    private boolean transitioning = false;
    private Duration duration = null;
    private static final int millisecondsBetweenTransitions = 15000;
    private AsyncCallback<Void> oldFrameCallback = null;

    public void onModuleLoad() {
        // Loop through all the interfaces and add the to the frame list
        for (String currentInterface : interfaces) {
            Frame frame = createIframe(currentInterface);
            frameList.add(frame);
        }

        // Loop through the frame list and add the frames to the absolute panel
        int counter = 0;
        for (Frame frame : frameList) {
            absolutePanel.add(frame);
            if (counter == 0) {
                frame.setStyleName("in-foreground");
            } else if (counter == 1) {
                frame.setStyleName("in-background");
            } else {
                frame.setStyleName("in-far-background");
            }

            counter++;
        }

        // Set the width of the absolute panel
        absolutePanel.setWidth(ABSOLUTE_PANEL_WIDTH);
        absolutePanel.setHeight(ABSOLUTE_PANEL_HEIGHT);

        // Add the absolute panel to the root panel
        RootPanel.get().add(absolutePanel);

        // Put all frames in the same place
        for (Frame frame : frameList) {
            // All frames go at 0, 0 to start
            absolutePanel.setWidgetPosition(frame, 0, 0);
        }

        // Create a timer to handle the transition
        Timer timer = new Timer() {
            @Override
            public void run() {
                // Are we already transitioning?
                if (transitioning) {
                    // Yes, do nothing
                    return;
                }

                // Are we already tracking a duration?
                if (duration == null) {
                    // No, start tracking a duration
                    duration = new Duration();
                    return;
                }

                // Has it been long enough for us to start transitioning?
                if (duration.elapsedMillis() > millisecondsBetweenTransitions) {
                    // Set up the transition
                    setupTransition();

                    // Indicate that the transition has started
                    transitioning = true;

                    // Get rid of the duration object
                    duration = null;
                }
            }
        };

        // Run every once in a while
        timer.scheduleRepeating(PERIOD_MILLIS);
    }

    private void setupTransition() {
        // Get the current visible frame and the frame that will be visible after the transition
        final Frame oldVisibleFrame = frameList.get(frontFrame % frameList.size());
        final Frame newVisibleFrame = frameList.get((frontFrame + 1) % frameList.size());

        // Register callbacks for when each animation finishes

        oldFrameCallback = new AsyncCallback<Void>() {
            @Override
            public void onFailure(Throwable caught) {
            }

            @Override
            public void onSuccess(Void result) {
                // Do the cleanup work
                transitionFinished();

                oldFrameCallback = null;
            }
        };

        CssAnimation.registerCssCallback(oldVisibleFrame.getElement(), oldFrameCallback);

        // Start the transition of the old visible frame to the background and the new visible frame to the foreground
        oldVisibleFrame.setStyleName("go-to-background");
        newVisibleFrame.setStyleName("go-to-foreground");
    }

    private void transitionFinished() {
        if (!transitioning) {
            return;
        }

        // Indicate that we're no longer transitioning
        transitioning = false;

        // Move to the next frame
        GWT.log(new Date() + " --------------------------------------");
        GWT.log("Transition finished");
        frontFrame++;

//        logFrameList(frameList);

        // Set the styles of the frames so that no longer using one of the "go-to-" styles
        int first = frontFrame % frameList.size();
        frameList.get(first).setStyleName("in-foreground");
        GWT.log("first: " + frameList.get(first).getUrl());

        int second = (frontFrame + 1) % frameList.size();
        frameList.get(second).setStyleName("in-background");
        GWT.log("second: " + frameList.get(second).getUrl());

        int start = (frontFrame + 2);
        int end = (frontFrame + 2) + (frameList.size() - 2);

        for (int loop = start; loop < end; loop++) {
            int index = loop % frameList.size();
            GWT.log("index: " + index + " " + frameList.get(index).getUrl());
            frameList.get(index).setStyleName("in-far-background");
        }
    }

    private void logFrameList(List<Frame> frameList) {
        GWT.log("Frames: " + frameList.size());
        for (Frame frame : frameList) {
            GWT.log(frame.getUrl());
        }
    }

    /**
     * Creates a borderless and seamless frame relative to the root of the site using index.html
     *
     * @param url
     * @return
     */
    private Frame createIframe(String url) {
        Frame frame = new Frame("/" + url);
        frame.getElement().setAttribute("frameBorder", "0");
        frame.getElement().setAttribute("seamless", "seamless");
        frame.setWidth(WIDTH);
        frame.setHeight(HEIGHT);

        return frame;
    }
}
