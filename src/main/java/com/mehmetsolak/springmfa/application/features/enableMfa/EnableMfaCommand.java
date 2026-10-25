package com.mehmetsolak.springmfa.application.features.enableMfa;

import com.mehmetsolak.springmfa.application.core.Command;

import java.util.UUID;

public record EnableMfaCommand(UUID userId, String code) implements Command<EnableMfaCommandResponse> {
}