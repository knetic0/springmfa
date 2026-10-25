package com.mehmetsolak.springmfa.domain.shared;

public class DomainException extends RuntimeException {
    public DomainException(String message) {
        super(message);
    }
}
