package com.mehmetsolak.springmfa.application.features.login;

import com.mehmetsolak.springmfa.domain.Role;

import java.util.UUID;

public record LoginCommandResponse(UUID userId, Role role, boolean mfaRequired) {
}
