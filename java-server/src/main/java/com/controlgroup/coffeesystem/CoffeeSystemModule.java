package com.controlgroup.coffeesystem;

import com.controlgroup.coffeesystem.configuration.PropertyFetcher;
import com.controlgroup.coffeesystem.configuration.TypeSafePropertyFetcher;
import com.controlgroup.coffeesystem.events.BasicScaleReadEvent;
import com.controlgroup.coffeesystem.events.BasicStableScaleReadEvent;
import com.controlgroup.coffeesystem.events.HeartbeatEvent;
import com.controlgroup.coffeesystem.generators.BasicEmailGenerator;
import com.controlgroup.coffeesystem.generators.BasicHeartbeatGenerator;
import com.controlgroup.coffeesystem.generators.BasicTimestampGenerator;
import com.controlgroup.coffeesystem.generators.BasicTweetGenerator;
import com.controlgroup.coffeesystem.generators.interfaces.EmailGenerator;
import com.controlgroup.coffeesystem.generators.interfaces.TimestampGenerator;
import com.controlgroup.coffeesystem.generators.interfaces.TweetGenerator;
import com.controlgroup.coffeesystem.http.BasicHttpClientFactory;
import com.controlgroup.coffeesystem.http.PermissiveSslHttpClientFactory;
import com.controlgroup.coffeesystem.interfaces.*;
import com.controlgroup.coffeesystem.loggers.BasicHeartbeatEventLogger;
import com.controlgroup.coffeesystem.loggers.BasicScaleEventLogger;
import com.controlgroup.coffeesystem.loggers.BasicStableScaleEventLogger;
import com.controlgroup.coffeesystem.processors.BasicScaleEventProcessor;
import com.controlgroup.coffeesystem.processors.BasicStableScaleEventProcessor;
import com.controlgroup.coffeesystem.usb.BasicUsbDeviceLocator;
import com.controlgroup.coffeesystem.usb.DymoUsbScale;
import com.google.common.eventbus.EventBus;
import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.google.inject.TypeLiteral;
import com.google.inject.assistedinject.FactoryModuleBuilder;
import com.google.inject.matcher.Matchers;
import com.google.inject.name.Names;
import com.google.inject.spi.InjectionListener;
import com.google.inject.spi.TypeEncounter;
import com.google.inject.spi.TypeListener;

/**
 * Created by timmattison on 12/29/14.
 */
public class CoffeeSystemModule extends AbstractModule {
    private final EventBus eventBus = new EventBus("Default EventBus");

    /**
     * Setting this to true indicates that you are using a self-signed SSL certificate that is not in your certificate
     * store.  This disables certificate verification in the entire application!  Only use this if you need it!
     */
    private final boolean permissiveSslRequired = false;

    @Override
    protected void configure() {
        // Use a Guava event bus
        bind(EventBus.class).toInstance(eventBus);

        // Find all @Subscribe annotations and bind them to the event bus
        bindListener(Matchers.any(), new TypeListener() {
            public <I> void hear(TypeLiteral<I> typeLiteral, TypeEncounter<I> typeEncounter) {
                typeEncounter.register(new InjectionListener<I>() {
                    public void afterInjection(I i) {
                        eventBus.register(i);
                    }
                });
            }
        });

        // The USB scale must be a singleton!
        bind(UsbScale.class).to(DymoUsbScale.class).in(Singleton.class);

        // Use all of the basic implementations
        bind(HeartbeatGenerator.class).to(BasicHeartbeatGenerator.class);
        bind(UsbDeviceLocator.class).to(BasicUsbDeviceLocator.class);
        bind(ScaleEventLogger.class).to(BasicScaleEventLogger.class);
        bind(StableScaleEventLogger.class).to(BasicStableScaleEventLogger.class);
        bind(HeartbeatEventLogger.class).to(BasicHeartbeatEventLogger.class);
        bind(ScaleEventProcessor.class).to(BasicScaleEventProcessor.class);
        bind(StableScaleEventProcessor.class).to(BasicStableScaleEventProcessor.class);

        if (permissiveSslRequired) {
            // Use a permissive SSL HttpClient factory so we can connect to a system using SSL and a self-signed cert
            bind(HttpClientFactory.class).to(PermissiveSslHttpClientFactory.class);
        } else {
            // Use a normal client factory
            bind(HttpClientFactory.class).to(BasicHttpClientFactory.class);
        }

        // Bind the constants from the configuration file.  They will be overridden by environment variables if specified.
        bindConstants();

        // Create an automatic factory to build scale read events
        install(new FactoryModuleBuilder().implement(ScaleReadEvent.class, BasicScaleReadEvent.class).build(ScaleReadEventFactory.class));

        // Create an automatic factory to build heartbeat events
        install(new FactoryModuleBuilder().implement(HeartbeatEvent.class, HeartbeatEvent.class).build(HeartbeatEventFactory.class));

        // Create an automatic factory to build stable scale read events
        install(new FactoryModuleBuilder().implement(StableScaleReadEvent.class, BasicStableScaleReadEvent.class).build(StableScaleReadEventFactory.class));

        // Use the basic e-mail generator
        bind(EmailGenerator.class).to(BasicEmailGenerator.class);

        // Use the basic tweet generator
        bind(TweetGenerator.class).to(BasicTweetGenerator.class);

        // Use the basic timestamp generator
        bind(TimestampGenerator.class).to(BasicTimestampGenerator.class);
    }

    private void bindConstants() {
        // Use the TypeSafe property fetcher
        PropertyFetcher propertyFetcher = new TypeSafePropertyFetcher();

        constant(propertyFetcher, HeartbeatGenerator.NAME, HeartbeatGenerator.INTERVAL);

        constant(propertyFetcher, ScaleEventProcessor.NAME, ScaleEventProcessor.MAX_THRESHOLD);
        constant(propertyFetcher, ScaleEventProcessor.NAME, ScaleEventProcessor.IDENTICAL_READS_REQUIRED);

        constant(propertyFetcher, StableScaleEventProcessor.NAME, StableScaleEventProcessor.EMPTY_CARAFE_IN_GRAMS);
        constant(propertyFetcher, StableScaleEventProcessor.NAME, StableScaleEventProcessor.FULL_CARAFE_IN_GRAMS);
        constant(propertyFetcher, StableScaleEventProcessor.NAME, StableScaleEventProcessor.TOLERANCE_IN_GRAMS);
        constant(propertyFetcher, StableScaleEventProcessor.NAME, StableScaleEventProcessor.GRAMS_PER_CUP);
        constant(propertyFetcher, StableScaleEventProcessor.NAME, StableScaleEventProcessor.CUP_TOLERANCE_FOR_FULL);
    }

    private void constant(PropertyFetcher propertyFetcher, String header, String name) {
        // Bind the constant in Guice using the header and name values and the data from the property fetcher
        bindConstant().annotatedWith(Names.named(name)).to(propertyFetcher.getValue(header, name));
    }
}
