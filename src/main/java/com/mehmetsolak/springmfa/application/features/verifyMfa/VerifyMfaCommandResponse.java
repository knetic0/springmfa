package com.mehmetsolak.springmfa.application.features.verifyMfa;

import com.mehmetsolak.springmfa.domain.Role;

import java.util.UUID;

public record VerifyMfaCommandResponse(UUID userId, Role role) {
}
