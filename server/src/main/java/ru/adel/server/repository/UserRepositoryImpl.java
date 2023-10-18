package ru.adel.server.repository;

import ru.adel.server.entity.User;

import java.util.Map;
import java.util.HashMap;
import java.util.Optional;

/**
 * Simple implementation of user repository with HashMap using usernames as keys and User objects as values.
 *
 * @see User
 */
public class UserRepositoryImpl implements UserRepository {

    private final Map<String, User> users = new HashMap<>();

    {
        User user1 = User.builder()
                .username("user1")
                .password("user1")
                .build();
        User user2 = User.builder()
                .username("user2")
                .password("user2")
                .build();

        users.put(user1.getUsername(), user1);
        users.put(user2.getUsername(), user2);
    }

    @Override
    public Optional<User> getUserByUsername(String username) {
        return Optional.ofNullable(users.get(username));
    }
}
