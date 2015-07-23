package com.controlgroup.coffeesystem;

import com.controlgroup.coffeesystem.configuration.TypeSafePropertyFetcher;
import com.controlgroup.coffeesystem.exceptions.ScaleNotFoundException;
import com.controlgroup.coffeesystem.interfaces.*;
import com.controlgroup.coffeesystem.processors.*;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.jcabi.manifests.Manifests;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.usb.UsbException;

/**
 * Created by timmattison on 12/29/14.
 */
public class CoffeeSystem {
    private static final Logger logger = LoggerFactory.getLogger(CoffeeSystem.class);
    private static boolean debug = false;

    public static final String X_GIT_BRANCH = "X-Git-Branch";
    public static final String UNKNOWN_BRANCH = "UNKNOWN BRANCH";

    public static void main(String[] args) throws UsbException, ScaleNotFoundException {
        showVersionInformation();

        Injector injector = Guice.createInjector(new CoffeeSystemModule());

        UsbScale usbScale = null;
        HeartbeatGenerator heartbeatGenerator = null;

        try {
            usbScale = injector.getInstance(UsbScale.class);
            usbScale.start();

            heartbeatGenerator = injector.getInstance(HeartbeatGenerator.class);
            heartbeatGenerator.start();

            // Process the raw events into stable events
            ScaleEventProcessor scaleEventProcessor = injector.getInstance(ScaleEventProcessor.class);

            // Process the stable events into coffee status objects
            StableScaleEventProcessor stableScaleEventProcessor = injector.getInstance(StableScaleEventProcessor.class);

            // Send the coffee status objects to Google App Engine
            CoffeeStatusProcessor googleAppEngineCoffeeStatusProcessor = injector.getInstance(GoogleAppEngineCoffeeStatusProcessor.class);

            bindTwitterCoffeeStatusProcessorIfNecessary(injector);
            bindGMailCoffeeBrewedEventProcessorIfNecessary(injector);
            bindSlackCoffeeBrewedEventProcessorIfNecessary(injector);

            if (debug) {
                ScaleEventLogger scaleEventLogger = injector.getInstance(ScaleEventLogger.class);
                StableScaleEventLogger stableScaleEventLogger = injector.getInstance(StableScaleEventLogger.class);
                HeartbeatEventLogger heartbeatEventLogger = injector.getInstance(HeartbeatEventLogger.class);
            }

            // Just loop forever
            while (true) {
                try {
                    if (!usbScale.isRunning()) {
                        // Try to restart it
                        System.err.println("Scale is not running, attempting to restart it");
                        Thread.sleep(5000);
                        usbScale = injector.getInstance(UsbScale.class);
                        usbScale.start();
                    }

                    // TODO - Check to make sure the different objects are still running here
                    // TODO - Create an interface that they all implement so checking them can be done in a list
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            if (usbScale != null) {
                usbScale.stop();
            }

            if (heartbeatGenerator != null) {
                heartbeatGenerator.stop();
            }
        }
    }

    private static void showVersionInformation() {
        String versionInformation = "Running in standalone debug mode";

        String gitCommit = CoffeeSystem.class.getPackage().getImplementationVersion();
        String gitBranch = UNKNOWN_BRANCH;

        if (Manifests.exists(X_GIT_BRANCH)) {
            gitBranch = Manifests.read(X_GIT_BRANCH);
        }

        if (gitCommit != null) {
            versionInformation = "Running " + gitCommit + ", " + gitBranch;
        }

        System.out.println(versionInformation);
        System.err.println(versionInformation);
    }

    private static void bindSlackCoffeeBrewedEventProcessorIfNecessary(Injector injector) {
        // Does the webhook URL exist?
        if (configurationValueExists(SlackCoffeeBrewedEventProcessor.HEADER, SlackCoffeeBrewedEventProcessor.WEBHOOK_URL)) {
            // Yes, use the Slack processor
            injector.getInstance(SlackCoffeeBrewedEventProcessor.class);
            logger.info("Ready to send messages to Slack");
        }
    }

    private static void bindGMailCoffeeBrewedEventProcessorIfNecessary(Injector injector) {
        // Does the application specific password exist?
        if (configurationValueExists(GMailCoffeeBrewedEventProcessor.HEADER, GMailCoffeeBrewedEventProcessor.PASSWORD_NAME)) {
            // Yes, use the GMail processor
            injector.getInstance(GMailCoffeeBrewedEventProcessor.class);
            logger.info("Ready to send messages to GMail");
        }
    }

    private static void bindTwitterCoffeeStatusProcessorIfNecessary(Injector injector) {
        // Do all the OAuth values exist in the configuration files or in the environment?
        if (configurationValueExists(TwitterCoffeeStatusProcessor.HEADER, TwitterCoffeeStatusProcessor.OAUTH_CONSUMER_KEY) &&
                configurationValueExists(TwitterCoffeeStatusProcessor.HEADER, TwitterCoffeeStatusProcessor.OAUTH_CONSUMER_SECRET) &&
                configurationValueExists(TwitterCoffeeStatusProcessor.HEADER, TwitterCoffeeStatusProcessor.OAUTH_ACCESS_TOKEN) &&
                configurationValueExists(TwitterCoffeeStatusProcessor.HEADER, TwitterCoffeeStatusProcessor.OAUTH_ACCESS_TOKEN_SECRET)) {
            // Yes, use the Twitter processor
            injector.getInstance(TwitterCoffeeStatusProcessor.class);
            logger.info("Ready to send messages to Twitter");
        }
    }

    private static boolean configurationValueExists(String header, String name) {
        TypeSafePropertyFetcher typeSafePropertyFetcher = new TypeSafePropertyFetcher();

        return typeSafePropertyFetcher.getValue(header, name) != null;
    }
}
