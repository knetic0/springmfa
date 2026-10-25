package com.mehmetsolak.springmfa.application.features.login;

import com.mehmetsolak.springmfa.application.core.Command;

public record LoginCommand(String email, String password) implements Command<LoginCommandResponse> {
}
