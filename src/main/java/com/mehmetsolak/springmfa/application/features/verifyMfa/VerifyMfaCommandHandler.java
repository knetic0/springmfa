package com.mehmetsolak.springmfa.application.features.verifyMfa;

import com.mehmetsolak.springmfa.application.core.CommandHandler;
import com.mehmetsolak.springmfa.application.exceptions.InvalidCredentialsException;
import com.mehmetsolak.springmfa.application.ports.TotpPort;
import com.mehmetsolak.springmfa.domain.User;
import com.mehmetsolak.springmfa.domain.UserRepository;
import org.springframework.stereotype.Component;

@Component
public class VerifyMfaCommandHandler implements CommandHandler<VerifyMfaCommand, VerifyMfaCommandResponse> {

    private final UserRepository userRepository;
    private final TotpPort totpPort;

    public VerifyMfaCommandHandler(UserRepository userRepository, TotpPort totpPort) {
        this.userRepository = userRepository;
        this.totpPort = totpPort;
    }

    @Override
    public VerifyMfaCommandResponse execute(VerifyMfaCommand command) {
        User user = userRepository.findById(command.userId())
                .filter(User::isActive)
                .filter(User::isMfaEnabled)
                .filter(u -> totpPort.verify(u.getMfaSecret(), command.code()))
                .orElseThrow(InvalidCredentialsException::new);

        return new VerifyMfaCommandResponse(user.getId().value(), user.getRole());
    }
}
