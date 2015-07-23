package com.controlgroup.coffeesystem.crypto;

/**
 * Created by timmattison on 1/8/15.
 */
public interface MessageSigner {
    String calculateSignature(String data) throws java.security.SignatureException;
}
