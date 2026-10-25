package com.mehmetsolak.springmfa.application.features.verifyMfa;

import com.mehmetsolak.springmfa.application.core.Command;

import java.util.UUID;

public record VerifyMfaCommand(UUID userId, String code) implements Command<VerifyMfaCommandResponse> {
}
