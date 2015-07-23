package com.controlgroup.coffeesystem.processors;

import com.controlgroup.coffeesystem.configuration.PropertyFetcher;
import com.controlgroup.coffeesystem.events.CoffeeBrewedEvent;
import com.controlgroup.coffeesystem.generators.interfaces.EmailGenerator;
import com.controlgroup.coffeesystem.interfaces.HttpClientFactory;
import com.google.common.eventbus.Subscribe;
import com.google.gson.Gson;
import com.google.inject.Inject;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.mail.MessagingException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by timmattison on 12/29/14.
 */
public class SlackCoffeeBrewedEventProcessor {
    public static final String HEADER = "Slack";
    public static final String WEBHOOK_URL = "webhookUrl";
    private final Logger logger = LoggerFactory.getLogger(SlackCoffeeBrewedEventProcessor.class);
    private final PropertyFetcher propertyFetcher;
    private final EmailGenerator emailGenerator;
    private final HttpClientFactory httpClientFactory;

    @Inject
    protected SlackCoffeeBrewedEventProcessor(PropertyFetcher propertyFetcher, EmailGenerator emailGenerator, HttpClientFactory httpClientFactory) {
        this.propertyFetcher = propertyFetcher;
        this.emailGenerator = emailGenerator;
        this.httpClientFactory = httpClientFactory;
    }

    @Subscribe
    public synchronized void coffeeBrewedEvent(CoffeeBrewedEvent coffeeBrewedEvent) throws MessagingException, IOException {
        String webhookUrl = propertyFetcher.getValue(HEADER, WEBHOOK_URL);

        HttpClient httpClient = httpClientFactory.create();
        HttpPost httpPost = new HttpPost(webhookUrl);

        String text = emailGenerator.generateBody(coffeeBrewedEvent);
        Map<String, String> data = new HashMap<>();

        data.put("text", text);
        Gson gson = new Gson();
        String json = gson.toJson(data);

        httpPost.setEntity(new ByteArrayEntity(json.getBytes()));
        httpClient.execute(httpPost);

        logger.info("Message sent to Slack");
    }
}
