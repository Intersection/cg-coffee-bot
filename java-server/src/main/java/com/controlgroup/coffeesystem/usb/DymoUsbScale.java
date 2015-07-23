package com.controlgroup.coffeesystem.usb;

import com.controlgroup.coffeesystem.exceptions.ScaleNotFoundException;
import com.controlgroup.coffeesystem.interfaces.ScaleReadEvent;
import com.controlgroup.coffeesystem.interfaces.ScaleReadEventFactory;
import com.controlgroup.coffeesystem.interfaces.UsbDeviceLocator;
import com.controlgroup.coffeesystem.interfaces.UsbScale;
import com.google.common.eventbus.EventBus;
import com.google.inject.Inject;
import org.apache.commons.lang3.SystemUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.usb.*;

/**
 * Created by timmattison on 12/29/14.
 */
public class DymoUsbScale implements UsbScale {
    private final Logger logger = LoggerFactory.getLogger(DymoUsbScale.class);
    private static final short VENDOR_ID = 0x0922;
    private static final short PRODUCT_ID = (short) 0x8004;
    private static final byte USB_INTERFACE_NUMBER = (byte) 0;
    private static final byte USB_ENDPOINT_NUMBER = (byte) 0x82;
    private static final short USB_HID_CLASS = 0x03;
    private static final int DATA_MODE_GRAMS = 2;
    private static final int DATA_MODE_OUNCES = 11;
    private static final int BUFFER_SIZE = 256;
    private static final int MINIMUM_DATA_LENGTH = 6;
    private static final int LOWER_BYTE = 4;
    private static final int UPPER_BYTE = 5;
    private static final int DATA_MODE_BYTE = 2;

    private final EventBus eventBus;
    private final UsbDeviceLocator usbDeviceLocator;
    private final ScaleReadEventFactory scaleReadEventFactory;
    private boolean running = false;

    @Inject
    public DymoUsbScale(EventBus eventBus, UsbDeviceLocator usbDeviceLocator, ScaleReadEventFactory scaleReadEventFactory) {
        this.eventBus = eventBus;
        this.usbDeviceLocator = usbDeviceLocator;
        this.scaleReadEventFactory = scaleReadEventFactory;
    }

    @Override
    public synchronized void start() throws UsbException, ScaleNotFoundException {
        // Are we running already?
        if (running) {
            // Yes, just return
            return;
        }

        // Locate our device
        UsbDevice device = usbDeviceLocator.findUsbDevice(VENDOR_ID, PRODUCT_ID);

        // Did we find the device?
        if (device == null) {
            // No, throw an exception
            throw new ScaleNotFoundException();
        }

        // Get the device's configuration and interface
        UsbConfiguration usbConfiguration = device.getActiveUsbConfiguration();
        final UsbInterface usbInterface = usbConfiguration.getUsbInterface(USB_INTERFACE_NUMBER);

        byte interfaceClass = usbInterface.getUsbInterfaceDescriptor().bInterfaceClass();

        // Is this USB HID on Mac OS?
        if ((interfaceClass == USB_HID_CLASS) && (SystemUtils.IS_OS_MAC_OSX)) {
            // Yes, due to OS limitations we can't do anything with this
            throw new UnsupportedOperationException("Cannot work with USB HID devices with libusb in Mac OS [See - http://www.libusb.org/ticket/89]");
        }

        // Get the endpoint and the pipe
        UsbEndpoint usbEndpoint = usbInterface.getUsbEndpoint(USB_ENDPOINT_NUMBER);
        final UsbPipe usbPipe = usbEndpoint.getUsbPipe();

        // Claim the interface
        usbInterface.claim(new UsbInterfacePolicy() {
            @Override
            public boolean forceClaim(UsbInterface usbInterface) {
                return true;
            }
        });

        // Open the pipe
        usbPipe.open();

        // Create the reading thread
        Thread readingThread = createReadingThread(usbInterface, usbPipe);

        // Indicate that we are running
        running = true;

        // Start the reading thread
        readingThread.start();
    }

    private Thread createReadingThread(final UsbInterface usbInterface, final UsbPipe usbPipe) {
        return new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while (running) {
                        // Get data from the pipe
                        byte[] data = new byte[BUFFER_SIZE];
                        usbPipe.syncSubmit(data);

                        // Process and publish the weight, if possible
                        processAndPublishWeight(data);
                    }
                } catch (UsbException e) {
                    e.printStackTrace();
                } finally {
                    // Indicate that we're no longer running in the event of an exception
                    running = false;

                    // Is the pipe not NULL?
                    if (usbPipe != null) {
                        try {
                            // Yes, close it if possible
                            usbPipe.close();
                        } catch (UsbException e) {
                            e.printStackTrace();
                        }
                    }

                    // Is the interface not NULL?
                    if (usbInterface != null) {
                        try {
                            // Yes, release it if possible
                            usbInterface.release();
                        } catch (UsbException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });
    }

    private void processAndPublishWeight(byte[] data) {
        // Is the data NULL?
        if (data == null) {
            // Yes, we cannot process NULL data
            throw new UnsupportedOperationException("Cannot process NULL data");
        }

        // Is the data long enough?
        if (data.length < MINIMUM_DATA_LENGTH) {
            // No, we cannot process it
            throw new UnsupportedOperationException("Not enough data");
        }

        // Get the upper and lower bytes
        int lowerByte = (data[LOWER_BYTE] & 0x0FF);
        int upperByte = (data[UPPER_BYTE] & 0x0FF);

        // Are they both zero?
        if ((lowerByte == 0) && (upperByte == 0)) {
            /**
             * Yes, just publish zero weight.  We do this because zero weight is always indicated in ounces even if
             * the scale is set to grams and we do not process ounces.
             */
            publishWeight(0);
            return;
        }

        // Did we get ounces?
        if (data[DATA_MODE_BYTE] == DATA_MODE_OUNCES) {
            // Yes, what is this... AMERICA?  Pffft, we don't do ounces.
            logger.error("We don't like ounces [" + lowerByte + ", " + upperByte + "], ignoring");
            return;
        }

        // Combine the upper and lower bytes
        int rawWeight = lowerByte + (upperByte * 256);

        // Publish the weight to the event bus
        publishWeight(rawWeight);
    }

    private void publishWeight(int weight) {
        // Create a scale read event with the current timestamp and weight
        ScaleReadEvent scaleReadEvent = scaleReadEventFactory.create(System.currentTimeMillis(), weight);

        // Post it to the event bus
        eventBus.post(scaleReadEvent);
    }

    @Override
    public synchronized void stop() {
        // Indicate that we are no longer running so our thread will exit
        running = false;
    }

    @Override
    public boolean isRunning() {
        return running;
    }
}
