package com.mehmetsolak.springmfa.application.features.setupMfa;

public record SetupMfaCommandResponse(String secret, String provisioningUri) {
}
