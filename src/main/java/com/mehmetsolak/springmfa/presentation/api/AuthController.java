package com.mehmetsolak.springmfa.presentation.api;

import com.mehmetsolak.springmfa.application.core.CommandHandler;
import com.mehmetsolak.springmfa.application.features.login.LoginCommand;
import com.mehmetsolak.springmfa.application.features.login.LoginCommandResponse;
import com.mehmetsolak.springmfa.application.features.register.RegisterCommand;
import com.mehmetsolak.springmfa.application.features.register.RegisterCommandResponse;
import com.mehmetsolak.springmfa.application.features.verifyMfa.VerifyMfaCommand;
import com.mehmetsolak.springmfa.application.features.verifyMfa.VerifyMfaCommandResponse;
import com.mehmetsolak.springmfa.presentation.api.dtos.request.LoginRequest;
import com.mehmetsolak.springmfa.presentation.api.dtos.request.RegisterRequest;
import com.mehmetsolak.springmfa.presentation.api.dtos.request.VerifyMfaRequest;
import com.mehmetsolak.springmfa.presentation.api.dtos.response.LoginResponse;
import com.mehmetsolak.springmfa.presentation.api.dtos.response.RegisterResponse;
import com.mehmetsolak.springmfa.presentation.security.Authenticated;
import com.mehmetsolak.springmfa.presentation.security.SessionAuthenticator;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final CommandHandler<RegisterCommand, RegisterCommandResponse> registerCommandHandler;
    private final CommandHandler<LoginCommand, LoginCommandResponse> loginCommandHandler;
    private final CommandHandler<VerifyMfaCommand, VerifyMfaCommandResponse> verifyMfaCommandHandler;

    private final SessionAuthenticator sessionAuthenticator;

    public AuthController(
            CommandHandler<RegisterCommand, RegisterCommandResponse> registerCommandHandler,
            CommandHandler<LoginCommand, LoginCommandResponse> loginCommandHandler,
            CommandHandler<VerifyMfaCommand, VerifyMfaCommandResponse> verifyMfaCommandHandler,
            SessionAuthenticator sessionAuthenticator) {
        this.registerCommandHandler = registerCommandHandler;
        this.loginCommandHandler = loginCommandHandler;
        this.verifyMfaCommandHandler = verifyMfaCommandHandler;
        this.sessionAuthenticator = sessionAuthenticator;
    }

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(@RequestBody RegisterRequest request) {
        RegisterCommand command = new RegisterCommand(request.email(), request.password());
        RegisterCommandResponse commandResponse = registerCommandHandler.execute(command);
        RegisterResponse response = new RegisterResponse();
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @RequestBody LoginRequest request,
            HttpServletRequest httpRequest,
            HttpServletResponse httpResponse) {

        LoginCommand command = new LoginCommand(request.email(), request.password());
        LoginCommandResponse response = loginCommandHandler.execute(command);

        if(response.mfaRequired()) {
            sessionAuthenticator.startMfaChallenge(response.userId(), httpRequest);
            return ResponseEntity.ok(new LoginResponse((true)));
        }

        sessionAuthenticator.signIn(response.userId(), response.role().name(), httpRequest, httpResponse);
        return ResponseEntity.ok(new LoginResponse(false));
    }

    @PostMapping("/login/mfa")
    @Authenticated
    public ResponseEntity<Void> verifyMfa(
            @RequestBody VerifyMfaRequest request,
            HttpServletRequest httpRequest,
            HttpServletResponse httpResponse) {

        Optional<UUID> userId = sessionAuthenticator.consumeMfaAttempt(httpRequest);
        if(userId.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        VerifyMfaCommand command = new VerifyMfaCommand(userId.get(), request.code());
        VerifyMfaCommandResponse response = verifyMfaCommandHandler.execute(command);

        sessionAuthenticator.signIn(response.userId(), response.role().name(), httpRequest, httpResponse);
        return ResponseEntity.ok().build();
    }
}
