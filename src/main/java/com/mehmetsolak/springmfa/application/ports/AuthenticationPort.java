package com.mehmetsolak.springmfa.application.ports;

import com.mehmetsolak.springmfa.domain.User;

public interface AuthenticationPort {
    User authenticate(String email, String password);
}
