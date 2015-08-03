package com.controlgroup.coffeesystem.crypto;

import com.controlgroup.coffeesystem.configuration.PropertyFetcher;
import org.apache.commons.codec.binary.Hex;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.inject.Inject;
import java.security.SignatureException;

public class HmacSha1MessageSigner implements MessageSigner {
    public static final String SECURITY = "security";
    public static final String HMAC = "hmac";
    public static final String SECURITY_HMAC_DEFAULT = "CHANGEME";
    private static final String HMAC_SHA1_ALGORITHM = "HmacSHA1";
    private final PropertyFetcher propertyFetcher;

    @Inject
    public HmacSha1MessageSigner(PropertyFetcher propertyFetcher) {
        // TODO: This is cleaner if we just pass in the key
        this.propertyFetcher = propertyFetcher;
    }

    @Override
    public String calculateSignature(String data) throws java.security.SignatureException {
        try {
            // Get the key from the configuration
            String keyString = propertyFetcher.getValue(SECURITY, HMAC);

            if (keyString == null) {
                throw new IllegalArgumentException("Missing required configuration value (" + SECURITY + "." + HMAC + ") to generate message signature");
            }

            if (keyString.equals(SECURITY_HMAC_DEFAULT)) {
                throw new IllegalArgumentException("HMAC has not been changed from the default value of " + SECURITY_HMAC_DEFAULT + ", refusing to check message signatures");
            }

            SecretKeySpec signingKey = new SecretKeySpec(keyString.getBytes(), HMAC_SHA1_ALGORITHM);

            // Create a signing instance and initialize it with the key
            Mac mac = Mac.getInstance(HMAC_SHA1_ALGORITHM);
            mac.init(signingKey);

            // Compute the HMAC
            byte[] rawHmac = mac.doFinal(data.getBytes());

            // Convert to hex string
            return Hex.encodeHexString(rawHmac);
        } catch (Exception e) {
            throw new SignatureException("Failed to generate HMAC: " + e.getMessage());
        }
    }
}