package com.mehmetsolak.springmfa.application.features.register;

import com.mehmetsolak.springmfa.application.core.CommandHandler;
import com.mehmetsolak.springmfa.application.ports.PasswordHasherPort;
import com.mehmetsolak.springmfa.domain.Email;
import com.mehmetsolak.springmfa.domain.PasswordHash;
import com.mehmetsolak.springmfa.domain.User;
import com.mehmetsolak.springmfa.domain.UserRepository;
import org.springframework.stereotype.Component;

@Component
public class RegisterCommandHandler implements CommandHandler<RegisterCommand, RegisterCommandResponse> {

    private final UserRepository userRepository;
    private final PasswordHasherPort passwordHasherPort;

    public RegisterCommandHandler(UserRepository userRepository, PasswordHasherPort passwordHasherPort) {
        this.userRepository = userRepository;
        this.passwordHasherPort = passwordHasherPort;
    }

    public RegisterCommandResponse execute(RegisterCommand command) {
        Email email = Email.of(command.email());

        if(userRepository.existsByEmail(email)) {
            throw new EmailAlreadyExists("Email already exists!");
        }

        String hashed = passwordHasherPort.hash(command.password());
        PasswordHash passwordHash = PasswordHash.of(hashed);
        User user = User.register(email, passwordHash);

        userRepository.save(user);

        return new RegisterCommandResponse();
    }
}
