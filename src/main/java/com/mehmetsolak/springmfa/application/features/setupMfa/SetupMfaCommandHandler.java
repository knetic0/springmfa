package com.mehmetsolak.springmfa.application.features.setupMfa;

import com.mehmetsolak.springmfa.application.core.CommandHandler;
import com.mehmetsolak.springmfa.application.exceptions.UserNotFoundException;
import com.mehmetsolak.springmfa.application.ports.TotpPort;
import com.mehmetsolak.springmfa.domain.User;
import com.mehmetsolak.springmfa.domain.UserRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class SetupMfaCommandHandler implements CommandHandler<SetupMfaCommand, SetupMfaCommandResponse> {

    private final UserRepository userRepository;
    private final TotpPort totpPort;

    public SetupMfaCommandHandler(UserRepository userRepository, TotpPort totpPort) {
        this.userRepository = userRepository;
        this.totpPort = totpPort;
    }

    @Override
    @Transactional
    public SetupMfaCommandResponse execute(SetupMfaCommand command) {
        User user = userRepository.findById(command.userId())
                .orElseThrow(UserNotFoundException::new);

        String secret = totpPort.generateSecret();
        user.startMfaSetup(secret);
        userRepository.save(user);

        return new SetupMfaCommandResponse(secret, totpPort.qrDataUri(secret, user.getEmail().value()));
    }
}
