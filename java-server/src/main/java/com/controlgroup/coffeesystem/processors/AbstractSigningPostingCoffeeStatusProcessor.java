package com.controlgroup.coffeesystem.processors;

import com.controlgroup.coffeesystem.CoffeeStatus;
import com.controlgroup.coffeesystem.crypto.MessageSigner;
import com.controlgroup.coffeesystem.interfaces.HttpClientFactory;
import com.google.inject.Inject;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.security.SignatureException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by timmattison on 12/29/14.
 */
public abstract class AbstractSigningPostingCoffeeStatusProcessor extends AbstractPostingCoffeeStatusProcessor {
    public static final String SIGNATURE = "signature";
    private final Logger logger = LoggerFactory.getLogger(AbstractSigningPostingCoffeeStatusProcessor.class);
    private final MessageSigner messageSigner;

    @Inject
    protected AbstractSigningPostingCoffeeStatusProcessor(HttpClientFactory httpClientFactory, MessageSigner messageSigner) {
        super(httpClientFactory);
        this.messageSigner = messageSigner;
    }

    @Override
    protected void addOptionalFields(List<NameValuePair> urlParameters, CoffeeStatus coffeeStatus) throws SignatureException {
        String signature = messageSigner.calculateSignature(coffeeStatus.toString());

        if (signature != null) {
            urlParameters.add(new BasicNameValuePair(SIGNATURE, signature));
        }
    }
}
