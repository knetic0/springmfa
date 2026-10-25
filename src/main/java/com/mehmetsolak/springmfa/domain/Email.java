package com.mehmetsolak.springmfa.domain;

import com.mehmetsolak.springmfa.domain.shared.DomainException;

import java.util.Locale;
import java.util.regex.Pattern;

public record Email(String value) {

    private static final Pattern PATTERN =
            Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    private static final int MAX_LENGTH = 255;

    public Email {
        if (value == null || value.isBlank()) {
            throw new DomainException("Email cannot be empty!");
        }
        value = value.trim().toLowerCase(Locale.ROOT);
        if (value.length() > MAX_LENGTH) {
            throw new DomainException("Email must be at most " + MAX_LENGTH + " characters!");
        }
        if (!PATTERN.matcher(value).matches()) {
            throw new DomainException("Invalid email: " + value);
        }
    }

    public static Email of(String value) {
        return new Email(value);
    }

    @Override
    public String toString() {
        return value;
    }
}
