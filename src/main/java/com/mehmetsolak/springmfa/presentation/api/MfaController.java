package com.mehmetsolak.springmfa.presentation.api;

import com.mehmetsolak.springmfa.application.core.CommandHandler;
import com.mehmetsolak.springmfa.application.features.enableMfa.EnableMfaCommand;
import com.mehmetsolak.springmfa.application.features.enableMfa.EnableMfaCommandResponse;
import com.mehmetsolak.springmfa.application.features.setupMfa.SetupMfaCommand;
import com.mehmetsolak.springmfa.application.features.setupMfa.SetupMfaCommandResponse;
import com.mehmetsolak.springmfa.presentation.api.dtos.request.EnableMfaRequest;
import com.mehmetsolak.springmfa.presentation.security.Authenticated;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/mfa")
public class MfaController {

    private final CommandHandler<SetupMfaCommand, SetupMfaCommandResponse> setupMfaCommandHandler;
    private final CommandHandler<EnableMfaCommand, EnableMfaCommandResponse> enableMfaCommandHandler;

    public MfaController(
            CommandHandler<SetupMfaCommand, SetupMfaCommandResponse> setupMfaCommandHandler,
            CommandHandler<EnableMfaCommand, EnableMfaCommandResponse> enableMfaCommandHandler){
        this.setupMfaCommandHandler = setupMfaCommandHandler;
        this.enableMfaCommandHandler = enableMfaCommandHandler;
    }

    @PostMapping("/setup")
    @Authenticated
    public ResponseEntity<?> setup(Authentication auth) {
        UUID userId = UUID.fromString(auth.getName());
        SetupMfaCommand command = new SetupMfaCommand(userId);
        SetupMfaCommandResponse response = setupMfaCommandHandler.execute(command);
        return ResponseEntity.ok()
                .cacheControl(CacheControl.noStore())
                .body(response.provisioningUri());
    }

    @PostMapping("/enable")
    @Authenticated
    public ResponseEntity<?> enable(Authentication auth, @RequestBody EnableMfaRequest request) {
        UUID userId = UUID.fromString(auth.getName());
        EnableMfaCommand command = new EnableMfaCommand(userId, request.code());
        EnableMfaCommandResponse response = enableMfaCommandHandler.execute(command);
        return ResponseEntity.ok().build();
    }
}
