package com.mehmetsolak.springmfa.domain;

import com.mehmetsolak.springmfa.domain.shared.DomainException;

public record PasswordHash(String value) {

    public PasswordHash {
        if (value == null || value.isBlank()) {
            throw new DomainException("Password hash cannot be empty!");
        }
    }

    public static PasswordHash of(String value) {
        return new PasswordHash(value);
    }

    @Override
    public String toString() {
        return "********";
    }
}
