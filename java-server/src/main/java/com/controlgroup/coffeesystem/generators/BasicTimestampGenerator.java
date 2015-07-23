package com.controlgroup.coffeesystem.generators;

import com.controlgroup.coffeesystem.generators.interfaces.TimestampGenerator;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by timmattison on 1/12/15.
 */
public class BasicTimestampGenerator implements TimestampGenerator {
    public static final String DATE_FORMAT_STRING = "hh:mm a";
    public static final String DEFAULT_TIMEZONE = "America/New_York";

    @Override
    public String generateTimestamp() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DATE_FORMAT_STRING);
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone(DEFAULT_TIMEZONE));

        return simpleDateFormat.format(new Date()).toString();
    }
}
