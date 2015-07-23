package com.controlgroup.coffeesystem.generators;

import com.controlgroup.coffeesystem.CoffeeStatus;
import com.controlgroup.coffeesystem.generators.interfaces.TimestampGenerator;
import com.controlgroup.coffeesystem.generators.interfaces.TweetGenerator;
import com.google.inject.Inject;

/**
 * Created by timmattison on 1/9/15.
 */
public class BasicTweetGenerator implements TweetGenerator {
    private final TimestampGenerator timestampGenerator;

    @Inject
    public BasicTweetGenerator(TimestampGenerator timestampGenerator) {
        this.timestampGenerator = timestampGenerator;
    }

    @Override
    public String generate(CoffeeStatus coffeeStatus) {
        // To avoid Twitter duplicate tweet detection
        String timestamp = timestampGenerator.generateTimestamp();
        String tweet = "[" + timestamp + "] " + getStatusString(coffeeStatus);

        return tweet;
    }

    private String getStatusString(CoffeeStatus coffeeStatus) {
        StringBuilder stringBuilder = new StringBuilder();

        if (coffeeStatus.carafePresent == false) {
            stringBuilder.append("Coffee pot removed");
            return stringBuilder.toString();
        }

        stringBuilder.append(coffeeStatus.cupsRemaining);
        stringBuilder.append(" cup");

        if (coffeeStatus.cupsRemaining != 1) {
            stringBuilder.append("s");
        }

        stringBuilder.append(" left");

        if (coffeeStatus.cupsRemaining == 0) {
            return stringBuilder.toString();
        }

        stringBuilder.append(", ");

        if (coffeeStatus.lastBrewed <= 0) {
            stringBuilder.append("I'm not quite sure when it was brewed");
            return stringBuilder.toString();
        }

        long ageInMilliseconds = (System.currentTimeMillis() - coffeeStatus.lastBrewed);
        long ageInSeconds = ageInMilliseconds / 1000;
        long ageInMinutes = ageInSeconds / 60;

        stringBuilder.append("it was ");

        if (ageInMinutes <= 1) {
            stringBuilder.append("just brewed");
        } else {
            stringBuilder.append("brewed ");
            stringBuilder.append(ageInMinutes);
            stringBuilder.append(" minutes ago");
        }

        return stringBuilder.toString();
    }
}
