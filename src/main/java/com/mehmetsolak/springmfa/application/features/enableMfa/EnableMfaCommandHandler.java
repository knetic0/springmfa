package com.mehmetsolak.springmfa.application.features.enableMfa;

import com.mehmetsolak.springmfa.application.core.CommandHandler;
import com.mehmetsolak.springmfa.application.exceptions.InvalidMfaCodeException;
import com.mehmetsolak.springmfa.application.ports.TotpPort;
import com.mehmetsolak.springmfa.domain.User;
import com.mehmetsolak.springmfa.domain.UserRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class EnableMfaCommandHandler implements CommandHandler<EnableMfaCommand, EnableMfaCommandResponse> {

    private final UserRepository userRepository;
    private final TotpPort totp;

    public EnableMfaCommandHandler(UserRepository userRepository, TotpPort totp) {
        this.userRepository = userRepository;
        this.totp = totp;
    }

    @Override
    @Transactional
    public EnableMfaCommandResponse execute(EnableMfaCommand command) {
        User user = userRepository.findById(command.userId())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        String pending = user.getMfaSecret();
        if (pending == null || !totp.verify(pending, command.code())) {
            throw new InvalidMfaCodeException();
        }

        user.confirmMfa();
        userRepository.save(user);
        return new EnableMfaCommandResponse(true);
    }
}
