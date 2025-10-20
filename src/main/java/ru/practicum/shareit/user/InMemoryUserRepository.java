package ru.practicum.shareit.user;

import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class InMemoryUserRepository {
    private final List<User> users = new ArrayList<>();

    public User save(User user) {
        user.setId((long) (users.size() + 1));
        users.add(user);
        return user;
    }

    public User findById(Long id) {
        return users.stream().filter(user -> user.getId().equals(id)).findFirst().orElse(null);
    }

    public User findByEmail(String email) {
        return users.stream().filter(user -> user.getEmail().equals(email)).findFirst().orElse(null);
    }

    public List<User> findAll() {
        return new ArrayList<>(users);
    }

    public void delete(Long id) {
        //User user = findById(id);
        //users.removeIf(user1 -> user1.getId().equals(id));
        //user.setDeleted(true);
        //users.add(user);
    }

    // ДОБАВЬТЕ ЭТОТ МЕТОД
    public boolean existsById(Long userId) {
        return users.stream().anyMatch(user -> user.getId().equals(userId));
    }
}
