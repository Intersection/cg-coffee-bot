package com.controlgroup.coffeesystem.crypto;

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
    private HmacSha1MessageSigner messageSigner;

    private static final String data1 = "This is a test";
    private static final String hashedData1 = "edd11ad8f60ba32e5116ff439a429a6482150be6";

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
}
