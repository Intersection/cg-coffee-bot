package com.controlgroup.coffeesystem.interfaces;

import com.controlgroup.coffeesystem.exceptions.ScaleNotFoundException;

import javax.usb.UsbException;

/**
 * Created by timmattison on 12/29/14.
 */
public interface UsbScale extends IsRunning {
    /**
     * Start emitting scale events
     *
     * @throws UsbException
     * @throws ScaleNotFoundException
     */
    public void start() throws UsbException, ScaleNotFoundException;

    /**
     * Stop emitting scale events
     */
    public void stop();
}
