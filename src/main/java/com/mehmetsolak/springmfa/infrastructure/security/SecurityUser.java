package com.mehmetsolak.springmfa.infrastructure.security;

import com.mehmetsolak.springmfa.domain.User;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public record SecurityUser(User user) implements UserDetails {

    @Override
    @NullMarked
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()));
    }

    @Override
    public @Nullable String getPassword() {
        return user.getPasswordHash().value();
    }

    @Override
    @NullMarked
    public String getUsername() {
        return user.getId().toString();
    }

    @Override
    public boolean isEnabled() {
        return user.isActive();
    }
}
