package com.controlgroup.coffeesystem.crypto;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.security.SignatureException;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.core.IsNull.nullValue;

/**
 * Created by timmattison on 7/21/15.
 */
public class NullMessageSignerTest {
    private MessageSigner messageSigner;

    @Before
    public void setup() {
        messageSigner = new NullMessageSigner();
    }

    @Test
    public void shouldReturnNull() throws SignatureException {
        Assert.assertThat(messageSigner.calculateSignature(null), is(nullValue()));
    }
}
