package com.controlgroup.coffeesystem.gae;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

public class Constants {
    public static final String ENTITY_KIND = "entityKind";
    public static final Key dataKey = KeyFactory.createKey(ENTITY_KIND, 1);
    public static final String JSON = "json";
    public static final int NO_CONTENT_204 = 204;

    public static final String lastBrewedParameter = "lastBrewed";
    public static final String cupsRemainingParameter = "cupsRemaining";
    public static final String carafePresentParameter = "carafePresent";
    public static final String signature = "signature";

    public static final String securityHmacKey = "security.hmac";
    public static final String securityHmacDefault = "CHANGEME";
}