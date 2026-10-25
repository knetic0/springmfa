package com.mehmetsolak.springmfa.domain;

import com.mehmetsolak.springmfa.domain.shared.DomainException;

import java.util.Objects;

public class User {

    private final UserId id;
    private Email email;
    private PasswordHash passwordHash;
    private Role role;
    private boolean active;
    private boolean mfaEnabled;
    private String mfaSecret;

    private User(UserId id, Email email, PasswordHash passwordHash, Role role,
                 boolean active, boolean mfaEnabled, String mfaSecret) {
        this.id = Objects.requireNonNull(id, "ID cannot be empty!");
        this.email = Objects.requireNonNull(email, "Email cannot be empty");
        this.passwordHash = Objects.requireNonNull(passwordHash, "Password cannot be empty!");
        this.role = Objects.requireNonNull(role, "Role cannot be empty");
        this.active = active;
        this.mfaEnabled = mfaEnabled;
        this.mfaSecret = mfaSecret;

        if (mfaEnabled && (mfaSecret == null || mfaSecret.isBlank())) {
            throw new DomainException("The secret cannot be empty if MFA enabled");
        }
    }

    public static User register(Email email, PasswordHash passwordHash) {
        return new User(UserId.newId(), email, passwordHash, Role.USER, true, false, null);
    }

    public static User reconstitute(UserId id, Email email, PasswordHash passwordHash, Role role,
                                    boolean active, boolean mfaEnabled, String mfaSecret) {
        return new User(id, email, passwordHash, role, active, mfaEnabled, mfaSecret);
    }

    public void changeEmail(Email newEmail) {
        ensureActive();
        this.email = Objects.requireNonNull(newEmail, "Email cannot be empty");
    }

    public void changePassword(PasswordHash newPasswordHash) {
        ensureActive();
        this.passwordHash = Objects.requireNonNull(newPasswordHash, "Password cannot be empty");
    }

    public void changeRole(Role newRole) {
        this.role = Objects.requireNonNull(newRole, "Role cannot be empty");
    }

    public void activate() {
        this.active = true;
    }

    public void deactivate() {
        this.active = false;
    }

    public void startMfaSetup(String secret) {
        ensureActive();
        if (mfaEnabled) {
            throw new DomainException("MFA already active");
        }
        if (secret == null || secret.isBlank()) {
            throw new DomainException("MFA secret cannot be empty");
        }
        this.mfaSecret = secret;
    }

    public void confirmMfa() {
        ensureActive();
        if (mfaEnabled) {
            throw new DomainException("MFA already active");
        }
        if (mfaSecret == null) {
            throw new DomainException("MFA setup must be initialized");
        }
        this.mfaEnabled = true;
    }

    public void disableMfa() {
        if (!mfaEnabled && mfaSecret == null) {
            throw new DomainException("MFA already deactivated");
        }
        this.mfaEnabled = false;
        this.mfaSecret = null;
    }

    public boolean canLogin() {
        return active;
    }

    public boolean requiresMfa() {
        return mfaEnabled;
    }

    private void ensureActive() {
        if (!active) {
            throw new DomainException("Pasif kullanıcı üzerinde işlem yapılamaz");
        }
    }

    public UserId getId() { return id; }
    public Email getEmail() { return email; }
    public PasswordHash getPasswordHash() { return passwordHash; }
    public Role getRole() { return role; }
    public boolean isActive() { return active; }
    public boolean isMfaEnabled() { return mfaEnabled; }
    public String getMfaSecret() { return mfaSecret; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User other)) return false;
        return id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
