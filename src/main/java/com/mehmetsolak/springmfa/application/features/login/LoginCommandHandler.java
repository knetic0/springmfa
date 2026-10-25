package com.mehmetsolak.springmfa.application.features.login;

import com.mehmetsolak.springmfa.application.core.CommandHandler;
import com.mehmetsolak.springmfa.application.ports.AuthenticationPort;
import com.mehmetsolak.springmfa.domain.User;
import org.springframework.stereotype.Component;

@Component
public class LoginCommandHandler implements CommandHandler<LoginCommand, LoginCommandResponse> {

    private final AuthenticationPort authenticationPort;

    public LoginCommandHandler(AuthenticationPort authenticationPort) {
        this.authenticationPort = authenticationPort;
    }

    @Override
    public LoginCommandResponse execute(LoginCommand command) {
        User user = authenticationPort.authenticate(command.email(), command.password());
        return new LoginCommandResponse(user.getId().value(), user.getRole(), user.requiresMfa());
    }
}
