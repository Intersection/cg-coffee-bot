package com.controlgroup.coffeesystem.usb;

import com.controlgroup.coffeesystem.interfaces.UsbDeviceLocator;

import javax.usb.*;
import java.util.List;

/**
 * Created by timmattison on 12/29/14.
 */
public class BasicUsbDeviceLocator implements UsbDeviceLocator {
    @Override
    public UsbDevice findUsbDevice(short vendorId, short productId) throws UsbException {
        // Find the device on the root hub
        return findUsbDevice(UsbHostManager.getUsbServices().getRootUsbHub(), vendorId, productId);
    }

    @Override
    public UsbDevice findUsbDevice(UsbHub usbHub, short vendorId, short productId) {
        // Loop through all attached devices
        for (UsbDevice device : (List<UsbDevice>) usbHub.getAttachedUsbDevices()) {
            // Get the device descriptor
            UsbDeviceDescriptor desc = device.getUsbDeviceDescriptor();

            if ((desc.idVendor() == vendorId) && (desc.idProduct() == productId)) {
                // The vendor and product IDs match.  Return the device.
                return device;
            } else if (device.isUsbHub()) {
                // The device is a USB hub.  Recursively search it for the device.
                UsbDevice tempDevice = findUsbDevice((UsbHub) device, vendorId, productId);

                // Did we find the device?
                if (tempDevice != null) {
                    // Yes, return it
                    return tempDevice;
                }
            }
        }

        // No luck, just return NULL
        return null;
    }
}
