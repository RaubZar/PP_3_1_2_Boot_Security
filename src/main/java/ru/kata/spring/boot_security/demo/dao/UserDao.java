package ru.kata.spring.boot_security.demo.dao;


import java.util.List;
import ru.kata.spring.boot_security.demo.model.User;

public interface UserDao {
    User findByUsername(String username);
    List<User> getAllUsers();
    void saveUser(User user);
    User getUserById(Long id);
    void updateUser(User user);
    void deleteUser(Long id);
}
