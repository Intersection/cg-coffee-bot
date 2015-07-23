package com.controlgroup.coffeesystem.configuration;

import com.typesafe.config.ConfigFactory;

/**
 * Created by timmattison on 11/3/14.
 */
public class TypeSafePropertyFetcher implements PropertyFetcher {
    @Override
    public String getValue(String header, String name) {
        return ConfigFactory.load().getString(header + "." + name);
    }
}
