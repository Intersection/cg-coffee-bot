package com.controlgroup.coffeesystem.crypto;

import com.controlgroup.coffeesystem.gae.Constants;
import com.google.appengine.repackaged.org.apache.commons.codec.binary.Hex;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.SignatureException;

public class HmacSha1MessageSigner implements MessageSigner {
    private static final String HMAC_SHA1_ALGORITHM = "HmacSHA1";
    public static final String SECURITY = "security";
    public static final String HMAC = "hmac";
    private final String key;

    public HmacSha1MessageSigner(String key) {
        this.key = key;
    }

    public String calculateSignature(String data) throws SignatureException {
        try {
            if (key == null) {
                throw new IllegalArgumentException("Missing required configuration value (" + SECURITY + "." + HMAC + ") to generate message signature");
            }

            if (key.equals(Constants.securityHmacDefault)) {
                throw new IllegalArgumentException("HMAC has not been changed from the default value of " + Constants.securityHmacDefault + ", refusing to check message signatures");
            }

            SecretKeySpec signingKey = new SecretKeySpec(key.getBytes(), HMAC_SHA1_ALGORITHM);

            // get an hmac_sha1 Mac instance and initialize with the signing key
            Mac mac = Mac.getInstance(HMAC_SHA1_ALGORITHM);
            mac.init(signingKey);

            // compute the hmac on input data bytes
            byte[] rawHmac = mac.doFinal(data.getBytes());

            // Convert to hex string
            return Hex.encodeHexString(rawHmac);
        } catch (Exception e) {
            throw new SignatureException("Failed to generate HMAC : " + e.getMessage());
        }
    }
}