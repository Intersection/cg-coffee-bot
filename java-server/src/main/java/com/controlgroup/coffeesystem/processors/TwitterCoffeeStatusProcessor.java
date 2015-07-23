package com.controlgroup.coffeesystem.processors;

import com.controlgroup.coffeesystem.CoffeeStatus;
import com.controlgroup.coffeesystem.configuration.PropertyFetcher;
import com.controlgroup.coffeesystem.configuration.TypeSafePropertyFetcher;
import com.controlgroup.coffeesystem.generators.interfaces.TweetGenerator;
import com.google.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

/**
 * Created by timmattison on 12/29/14.
 */
public class TwitterCoffeeStatusProcessor extends AbstractCoffeeStatusProcessor {
    private final PropertyFetcher propertyFetcher;
    private final TweetGenerator tweetGenerator;
    private final Logger logger = LoggerFactory.getLogger(AbstractCoffeeStatusProcessor.class);
    public static final String HEADER = "Twitter";
    public static final String OAUTH_CONSUMER_KEY = "oauth_consumer_key";
    public static final String OAUTH_CONSUMER_SECRET = "oauth_consumer_secret";
    public static final String OAUTH_ACCESS_TOKEN = "oauth_access_token";
    public static final String OAUTH_ACCESS_TOKEN_SECRET = "oauth_access_token_secret";

    @Inject
    public TwitterCoffeeStatusProcessor(PropertyFetcher propertyFetcher, TweetGenerator tweetGenerator) {
        this.propertyFetcher = propertyFetcher;
        this.tweetGenerator = tweetGenerator;
    }

    @Override
    protected synchronized void handle(CoffeeStatus coffeeStatus) throws Exception {
        try {
            updateStatus(tweetGenerator.generate(coffeeStatus));
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    private void updateStatus(String statusString) throws TwitterException {
        // NOTE: Configuration builder is final and cannot be mocked by Mockito
        ConfigurationBuilder cb = new ConfigurationBuilder();
        cb.setOAuthConsumerKey(propertyFetcher.getValue(HEADER, OAUTH_CONSUMER_KEY))
                .setOAuthConsumerSecret(propertyFetcher.getValue(HEADER, OAUTH_CONSUMER_SECRET))
                .setOAuthAccessToken(propertyFetcher.getValue(HEADER, OAUTH_ACCESS_TOKEN))
                .setOAuthAccessTokenSecret(propertyFetcher.getValue(HEADER, OAUTH_ACCESS_TOKEN_SECRET));

        // NOTE: TwitterFactory is final and cannot be mocked by Mockito
        TwitterFactory tf = new TwitterFactory(cb.build());

        try {
            Twitter twitter = tf.getInstance();

            Status status = twitter.updateStatus(statusString);
            logger.info("Successfully updated the status to [" + status.getText() + "].");
        } catch (TwitterException e) {
            if ((e.getStatusCode() == 403) && (e.getMessage().contains("Status is a duplicate"))) {
                logger.info("Status is a duplicate, not resending it [" + statusString + "]");
                return;
            }

            // Throw all other exceptions
            throw e;
        }
    }
}
