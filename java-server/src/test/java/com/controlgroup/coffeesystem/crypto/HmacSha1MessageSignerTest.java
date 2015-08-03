package com.controlgroup.coffeesystem.crypto;

import com.controlgroup.coffeesystem.CoffeeStatus;
import com.controlgroup.coffeesystem.configuration.PropertyFetcher;
import com.controlgroup.coffeesystem.configuration.TypeSafePropertyFetcher;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;

import java.security.SignatureException;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by timmattison on 7/21/15.
 */
public class HmacSha1MessageSignerTest {
    public static final String KEY = "KEY";
    private static final String data1 = "This is a test";
    private static final String hashedData1 = "edd11ad8f60ba32e5116ff439a429a6482150be6";
    private HmacSha1MessageSigner messageSigner;

    @Before
    public void setup() {
        PropertyFetcher mockPropertyFetcher = mock(TypeSafePropertyFetcher.class);
        when(mockPropertyFetcher.getValue(Matchers.anyString(), Matchers.anyString())).thenReturn(KEY);
        messageSigner = new HmacSha1MessageSigner(mockPropertyFetcher);
    }

    @Test
    public void shouldReturnCorrectHmac() throws SignatureException {
        Assert.assertThat(messageSigner.calculateSignature(data1), is(hashedData1));
    }

    @Test
    public void shouldReturnCorrectHmacForCoffeeStatusObject() throws SignatureException {
        CoffeeStatus coffeeStatus = new CoffeeStatus();
        coffeeStatus.lastBrewed = 0;
        coffeeStatus.cupsRemaining = 3;
        coffeeStatus.carafePresent = false;

        String data = coffeeStatus.toString();
        String hmac = messageSigner.calculateSignature(data);

        Assert.assertThat(hmac, is("59eb266f831a3c2191f46a2c834db0ffdec463aa"));
    }
}
