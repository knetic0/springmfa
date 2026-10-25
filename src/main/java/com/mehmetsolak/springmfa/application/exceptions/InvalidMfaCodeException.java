package com.mehmetsolak.springmfa.application.exceptions;

public class InvalidMfaCodeException extends RuntimeException {
    public InvalidMfaCodeException() {
        super("Invalid MFA code");
    }
}
