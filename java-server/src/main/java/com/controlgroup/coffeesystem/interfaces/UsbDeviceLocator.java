package com.controlgroup.coffeesystem.interfaces;

import javax.usb.UsbDevice;
import javax.usb.UsbException;
import javax.usb.UsbHub;

/**
 * Created by timmattison on 12/29/14.
 */
public interface UsbDeviceLocator {
    /**
     * Finds a USB device on a specific hub
     *
     * @param usbHub
     * @param vendorId
     * @param productId
     * @return
     */
    public UsbDevice findUsbDevice(UsbHub usbHub, short vendorId, short productId);

    /**
     * Finds a USB device on the root hub
     *
     * @param vendorId
     * @param productId
     * @return
     * @throws UsbException
     */
    public UsbDevice findUsbDevice(short vendorId, short productId) throws UsbException;
}
