package com.mehmetsolak.springmfa.application.ports;

public interface PasswordHasherPort {
    String hash(String password);
}
