package ru.kata.spring.boot_security.demo.service;

import org.springframework.security.core.userdetails.UserDetailsService;
import ru.kata.spring.boot_security.demo.model.User;
import java.util.List;
import java.util.Optional;

public interface UserService extends UserDetailsService {
    Optional<User> findByUsername(String username);
    List<User> getAllUsers();
    void saveUser(User user);
    Optional<User> getUserById(Long id);
    void updateUser(User user);
    void deleteUser(Long id);
    Optional<User> findByUsernameWithRoles(String username);
}