package com.mehmetsolak.springmfa.presentation.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextHolderStrategy;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class SessionAuthenticator {

    private static final String MFA_USER_ID = "MFA_PENDING_USER_ID";
    private static final String MFA_EXPIRES_AT = "MFA_PENDING_EXPIRES_AT";
    private static final String MFA_ATTEMPTS = "MFA_PENDING_ATTEMPTS";
    private static final Duration MFA_TTL = Duration.ofMinutes(5);
    private static final int MFA_MAX_ATTEMPTS = 5;

    private final SecurityContextRepository securityContextRepository;
    private final SecurityContextHolderStrategy contextHolderStrategy = SecurityContextHolder.getContextHolderStrategy();

    public SessionAuthenticator(SecurityContextRepository securityContextRepository) {
        this.securityContextRepository = securityContextRepository;
    }

    public void signIn(UUID userId, String role, HttpServletRequest request, HttpServletResponse response) {
        clearMfaChallenge(request);
        request.getSession(true);
        request.changeSessionId();

        var authentication = UsernamePasswordAuthenticationToken.authenticated(
                userId.toString(), null, List.of(new SimpleGrantedAuthority("ROLE_" + role)));

        SecurityContext context = contextHolderStrategy.createEmptyContext();
        context.setAuthentication(authentication);
        contextHolderStrategy.setContext(context);
        securityContextRepository.saveContext(context, request, response);
    }

    public void startMfaChallenge(UUID userId, HttpServletRequest request) {
        HttpSession session = request.getSession(true);
        request.changeSessionId();
        session.setAttribute(MFA_USER_ID, userId);
        session.setAttribute(MFA_EXPIRES_AT, Instant.now().plus(MFA_TTL));
        session.setAttribute(MFA_ATTEMPTS, 0);
    }

    public Optional<UUID> consumeMfaAttempt(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return Optional.empty();
        }
        if (!(session.getAttribute(MFA_USER_ID) instanceof UUID userId)
                || !(session.getAttribute(MFA_EXPIRES_AT) instanceof Instant expiresAt)) {
            return Optional.empty();
        }

        int attempts = session.getAttribute(MFA_ATTEMPTS) instanceof Integer i ? i : 0;
        if (Instant.now().isAfter(expiresAt) || attempts >= MFA_MAX_ATTEMPTS) {
            clearMfaChallenge(request);
            return Optional.empty();
        }

        session.setAttribute(MFA_ATTEMPTS, attempts + 1);
        return Optional.of(userId);
    }

    private void clearMfaChallenge(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.removeAttribute(MFA_USER_ID);
            session.removeAttribute(MFA_EXPIRES_AT);
            session.removeAttribute(MFA_ATTEMPTS);
        }
    }
}
