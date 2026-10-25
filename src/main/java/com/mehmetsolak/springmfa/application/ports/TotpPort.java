package com.mehmetsolak.springmfa.application.ports;

public interface TotpPort {
    String generateSecret();
    String qrDataUri(String secret, String accountName);
    boolean verify(String secret, String code);
}
