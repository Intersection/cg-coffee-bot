package com.controlgroup.coffeesystem.crypto;

import java.security.SignatureException;

public class NullMessageSigner implements MessageSigner {
    @Override
    public String calculateSignature(String data) throws SignatureException {
        return null;
    }
}