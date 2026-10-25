package com.mehmetsolak.springmfa.infrastructure.persistence;

import com.mehmetsolak.springmfa.domain.Email;
import com.mehmetsolak.springmfa.domain.User;
import com.mehmetsolak.springmfa.domain.UserRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public class UserRepositoryImpl implements UserRepository {

    private final SpringDataUserRepository repository;
    private final UserMapper mapper;

    public UserRepositoryImpl(SpringDataUserRepository repository, UserMapper mapper){
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public Optional<User> findByEmail(Email email) {
        return repository.findByEmail(email.value())
                .map(mapper::toDomain);
    }

    @Override
    public Optional<User> findById(UUID id) {
        return repository.findById(id)
                .map(mapper::toDomain);
    }

    @Override
    public User save(User user) {
        JpaUserEntity saved = repository.save(mapper.toEntity(user));
        return mapper.toDomain(saved);
    }

    @Override
    public boolean existsByEmail(Email email) {
        return repository.existsByEmail(email.value());
    }
}
