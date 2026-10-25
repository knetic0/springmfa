package com.mehmetsolak.springmfa.application.features.setupMfa;

import com.mehmetsolak.springmfa.application.core.Command;

import java.util.UUID;

public record SetupMfaCommand(UUID userId) implements Command<SetupMfaCommandResponse> {
}
