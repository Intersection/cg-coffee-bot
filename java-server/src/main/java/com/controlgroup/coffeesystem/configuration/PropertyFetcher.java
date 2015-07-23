package com.controlgroup.coffeesystem.configuration;

/**
 * Created by timmattison on 11/3/14.
 */
public interface PropertyFetcher {
    /**
     * Fetch a property's string value using a specific header and name
     *
     * @param header
     * @param name
     * @return
     */
    public String getValue(String header, String name);
}
