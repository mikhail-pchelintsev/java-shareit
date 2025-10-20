package ru.practicum.shareit.user;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.regex.Pattern;

@Service
public class UserService {

    private final InMemoryUserRepository userRepository;

    public UserService(InMemoryUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public boolean userExists(Long userId) {
        return userRepository.existsById(userId);
    }

    public User addUser(User user) {
        if (userRepository.findByEmail(user.getEmail()) != null) {
            throw new IllegalArgumentException("Email already exists");
        }

        if (!isValidEmail(user.getEmail())) {
            throw new IllegalArgumentException("Invalid email format");
        }

        return userRepository.save(user);
    }

    public User updateUser(Long userId, User user) {
        User existingUser = userRepository.findById(userId);
        if (existingUser == null) {
            throw new IllegalArgumentException("User not found");
        }

        if (user.getName() != null) {
            existingUser.setName(user.getName());
        }

        if (user.getEmail() != null) {
            if (userRepository.findByEmail(user.getEmail()) != null) {
                throw new IllegalArgumentException("Email already exists");
            }
            if (!isValidEmail(user.getEmail())) {
                throw new IllegalArgumentException("Invalid email format");
            }
            existingUser.setEmail(user.getEmail());
        }

        return existingUser;
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public User getUser(Long userId) {
        return userRepository.findById(userId);
    }

    public void deleteUser(Long userId) {
        userRepository.delete(userId);
    }

    private boolean isValidEmail(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$";
        Pattern pattern = Pattern.compile(emailRegex);
        return pattern.matcher(email).matches();
    }
}
