package com.controlgroup.coffeesystem;

/**
 * Created by timmattison on 12/30/14.
 */
public class CoffeeStatus {
    public static final String lastBrewedName = "lastBrewed";
    public static final String cupsRemainingName = "cupsRemaining";
    public static final String carafePresentName = "carafePresent";

    /**
     * Last brewed time in epoch milliseconds
     */
    public long lastBrewed;

    /**
     * Number of cups remaining in the carafe
     */
    public int cupsRemaining;

    /**
     * Whether or not the carafe is present
     */
    public boolean carafePresent;

    @Override
    public String toString() {
        return "{\"" + lastBrewedName + "\":" + lastBrewed + "," +
                "\"" + cupsRemainingName + "\":" + cupsRemaining + "," +
                "\"" + carafePresentName + "\":" + carafePresent + "}";
    }
}
