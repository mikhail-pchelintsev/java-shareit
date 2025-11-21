package ru.practicum.shareit.user;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@Transactional
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User addUser(User user) {
        if (user.getEmail().isBlank() || !user.getEmail().contains("@")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email required");
        }
        User byEmail = userRepository.findByEmail(user.getEmail());
        if (byEmail != null) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already exists");
        }
        return userRepository.save(user);
    }

    public User getUser(Long id) {
        return userRepository.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public User updateUser(Long userId, User patch) {
        User existing = getUser(userId);
        if (patch.getName() != null) existing.setName(patch.getName());
        if (patch.getEmail() != null) {
            User byEmail = userRepository.findByEmail(patch.getEmail());
            if (byEmail != null && !byEmail.getId().equals(userId)) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already exists");
            }
            existing.setEmail(patch.getEmail());
        }
        return userRepository.save(existing);
    }

    public void deleteUser(Long userId) {
        userRepository.deleteById(userId);
    }
}