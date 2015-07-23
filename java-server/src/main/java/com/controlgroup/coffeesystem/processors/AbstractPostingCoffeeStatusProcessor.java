package com.controlgroup.coffeesystem.processors;

import com.controlgroup.coffeesystem.CoffeeStatus;
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
public abstract class AbstractPostingCoffeeStatusProcessor extends AbstractCoffeeStatusProcessor {
    private final Logger logger = LoggerFactory.getLogger(AbstractPostingCoffeeStatusProcessor.class);
    private final HttpClientFactory httpClientFactory;

    @Inject
    protected AbstractPostingCoffeeStatusProcessor(HttpClientFactory httpClientFactory) {
        super();
        this.httpClientFactory = httpClientFactory;
    }

    @Override
    protected void handle(CoffeeStatus coffeeStatus) throws IOException, SignatureException {
        logger.info("Posting coffee status to " + getName());

        // Create an HTTP client
        HttpClient client = httpClientFactory.create();
        HttpPost post = new HttpPost(getUpdateUrl());

        // Build up the parameters we need
        List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
        urlParameters.add(new BasicNameValuePair(getCarafePresentName(), Boolean.toString(coffeeStatus.carafePresent)));
        urlParameters.add(new BasicNameValuePair(getCupsRemainingName(), Integer.toString(coffeeStatus.cupsRemaining)));
        urlParameters.add(new BasicNameValuePair(getLastBrewedName(), Long.toString(coffeeStatus.lastBrewed)));

        addOptionalFields(urlParameters, coffeeStatus);

        // URL encode it
        post.setEntity(new UrlEncodedFormEntity(urlParameters));

        // Execute the POST
        HttpResponse response = client.execute(post);

        // Get the status code
        int statusCode = response.getStatusLine().getStatusCode();

        // Did we get what we expected?
        if (statusCode != getExpectedStatusCode()) {
            // No, throw an exception
            throw new UnsupportedOperationException("Invalid status code when POSTing to " + getName() + " [" + statusCode + "], expected " + getExpectedStatusCode());
        }

        // Coffee status successfully updated
        logger.info("Posted coffee status to " + getName() + ": " + response.getStatusLine().getStatusCode());
    }

    protected abstract void addOptionalFields(List<NameValuePair> urlParameters, CoffeeStatus coffeeStatus) throws SignatureException;

    protected String getCarafePresentName() {
        return CoffeeStatus.carafePresentName;
    }

    protected String getCupsRemainingName() {
        return CoffeeStatus.cupsRemainingName;
    }

    protected String getLastBrewedName() {
        return CoffeeStatus.lastBrewedName;
    }

    protected abstract int getExpectedStatusCode();

    protected abstract String getUpdateUrl();

    protected abstract String getName();

    /**
     * For testing only
     *
     * @return
     */
    protected List<CoffeeStatus> getCoffeeStatusList() {
        return coffeeStatusList;
    }
}
