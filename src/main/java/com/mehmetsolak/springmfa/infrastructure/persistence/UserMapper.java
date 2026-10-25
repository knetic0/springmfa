package com.mehmetsolak.springmfa.infrastructure.persistence;

import com.mehmetsolak.springmfa.domain.Email;
import com.mehmetsolak.springmfa.domain.PasswordHash;
import com.mehmetsolak.springmfa.domain.User;
import com.mehmetsolak.springmfa.domain.UserId;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public User toDomain(JpaUserEntity entity) {
        UserId userId = UserId.of(entity.getId());
        Email email = Email.of(entity.getEmail());
        PasswordHash passwordHash = PasswordHash.of(entity.getPasswordHash());
        return User.reconstitute(
                userId,
                email,
                passwordHash,
                entity.getRole(),
                entity.isActive(),
                entity.isMfaEnabled(),
                entity.getMfaSecret()
        );
    }

    public JpaUserEntity toEntity(User user) {
        return new JpaUserEntity(
                user.getId().value(),
                user.getEmail().value(),
                user.getPasswordHash().value(),
                user.getRole(),
                user.isActive(),
                user.isMfaEnabled(),
                user.getMfaSecret());
    }
}
