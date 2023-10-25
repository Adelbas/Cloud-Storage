package ru.adel.server.repository;

import ru.adel.server.entity.User;

import java.util.Optional;

public interface UserRepository {
    Optional<User> getUserByUsername(String username);
}
