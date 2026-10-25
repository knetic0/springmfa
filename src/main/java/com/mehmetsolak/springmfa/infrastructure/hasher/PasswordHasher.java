package com.mehmetsolak.springmfa.infrastructure.hasher;

import com.mehmetsolak.springmfa.application.ports.PasswordHasherPort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class PasswordHasher implements PasswordHasherPort {

    private final PasswordEncoder passwordEncoder;

    public PasswordHasher(PasswordEncoder passwordEncoder){
        this.passwordEncoder = passwordEncoder;
    }

    public String hash(String password) {
        return passwordEncoder.encode(password);
    }
}
