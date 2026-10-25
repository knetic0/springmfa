package com.mehmetsolak.springmfa.application.features.register;

import com.mehmetsolak.springmfa.application.core.Command;

public record RegisterCommand(String email, String password)
        implements Command<RegisterCommandResponse> { }
