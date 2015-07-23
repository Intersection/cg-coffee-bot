package com.controlgroup.coffeesystem.processors;

/**
 * Created by timmattison on 12/29/14.
 */
public class GoogleAppEngineCoffeeStatusProcessorTest extends AbstractPostingCoffeeStatusProcessorTest {
    @Override
    public AbstractPostingCoffeeStatusProcessor getCoffeeStatusProcessor() {
        setHttpResponseCode(204);
        return new GoogleAppEngineCoffeeStatusProcessor(mockTypeSafePropertyFetcher, mockHttpClientFactory, mockMessageSigner);
    }
}
