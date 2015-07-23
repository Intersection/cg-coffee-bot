package com.controlgroup.coffeesystem.processors;

import com.controlgroup.coffeesystem.configuration.PropertyFetcher;
import com.controlgroup.coffeesystem.crypto.MessageSigner;
import com.controlgroup.coffeesystem.interfaces.HttpClientFactory;
import com.google.inject.Inject;

/**
 * Created by timmattison on 12/29/14.
 */
public class GoogleAppEngineCoffeeStatusProcessor extends AbstractSigningPostingCoffeeStatusProcessor {
    public static final String GOOGLE_APP_ENGINE = "Google App Engine";
    public static final int EXPECTED_STATUS_CODE = 204;
    private final PropertyFetcher propertyFetcher;
    private final String HEADER = "GoogleAppEngine";
    private final String UPDATE_URL = "updateUrl";

    @Inject
    public GoogleAppEngineCoffeeStatusProcessor(PropertyFetcher propertyFetcher, HttpClientFactory httpClientFactory, MessageSigner messageSigner) {
        super(httpClientFactory, messageSigner);
        this.propertyFetcher = propertyFetcher;
    }

    @Override
    protected int getExpectedStatusCode() {
        return EXPECTED_STATUS_CODE;
    }

    @Override
    protected String getUpdateUrl() {
        return propertyFetcher.getValue(HEADER, UPDATE_URL);
    }

    @Override
    protected String getName() {
        return GOOGLE_APP_ENGINE;
    }
}
