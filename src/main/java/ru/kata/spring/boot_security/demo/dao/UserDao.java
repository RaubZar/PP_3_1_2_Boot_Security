package ru.kata.spring.boot_security.demo.dao;

import ru.kata.spring.boot_security.demo.model.User;
import java.util.List;
import java.util.Optional;

public interface UserDao {
    Optional<User> findByUsername(String username);
    List<User> getAllUsers();
    void saveUser(User user);
    Optional<User> getUserById(Long id);
    void updateUser(User user);
    void deleteUser(Long id);
    Optional<User> findByUsernameWithRoles(String username);
}