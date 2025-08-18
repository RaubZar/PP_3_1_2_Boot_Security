package ru.kata.spring.boot_security.demo.service;

import ru.kata.spring.boot_security.demo.model.Role;
import java.util.List;
import java.util.Optional;

public interface RoleService {
    Optional<Role> findByName(String name);
    void save(Role role);
    List<Role> findAll();
    void delete(Role role);
    Optional<Role> findById(Long id);
}