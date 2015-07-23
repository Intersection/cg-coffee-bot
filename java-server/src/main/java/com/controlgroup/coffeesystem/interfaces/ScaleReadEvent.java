package com.controlgroup.coffeesystem.interfaces;

/**
 * Created by timmattison on 12/29/14.
 */
public interface ScaleReadEvent {
    /**
     * The name of the timestamp field (for DI)
     */
    public static final String TIMESTAMP = "TIMESTAMP";

    /**
     * The name of the grams field (for DI)
     */
    public static final String GRAMS = "GRAMS";

    /**
     * Returns the timestamp (epoch milliseconds) at which a scale event occurred
     *
     * @return
     */
    public long getTimestamp();

    /**
     * Returns the number of grams the scale read at the indicated time
     *
     * @return
     */
    public int getGrams();
}
