package com.mehmetsolak.springmfa.infrastructure.security;

import com.mehmetsolak.springmfa.application.exceptions.InvalidCredentialsException;
import com.mehmetsolak.springmfa.application.ports.AuthenticationPort;
import com.mehmetsolak.springmfa.domain.User;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

@Component
public class CredentialsAuthenticator implements AuthenticationPort {

    private final AuthenticationManager authenticationManager;

    public CredentialsAuthenticator(AuthenticationManager authenticationManager){
        this.authenticationManager = authenticationManager;
    }

    @Override
    public User authenticate(String email, String password) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    UsernamePasswordAuthenticationToken.unauthenticated(email, password)
            );

            if(!(authentication.getPrincipal() instanceof SecurityUser securityUser)) {
                throw new InvalidCredentialsException();
            }

            return securityUser.user();
        } catch (AuthenticationException e) {
            throw new InvalidCredentialsException();
        }
    }
}
