package ru.kata.spring.boot_security.demo.configs;

import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import ru.kata.spring.boot_security.demo.dao.RoleDao;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.service.UserService;

import java.util.HashSet;
import java.util.Set;

@Component
@Order(1)
public class DataInitializer implements CommandLineRunner {
    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);

    private final UserService userService;
    private final RoleDao roleDao;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public DataInitializer(UserService userService,
                           RoleDao roleDao,
                           PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.roleDao = roleDao;
        this.passwordEncoder = passwordEncoder;
        logger.info("DataInitializer dependencies initialized");
    }

    @Override
    @Transactional
    public void run(String... args) {
        try {
            logger.info("=== Начало инициализации данных ===");

            waitForDependencies();

            clearExistingData();

            initializeRoles();

            initializeUsers();

            logger.info("=== Инициализация данных успешно завершена ===");
        } catch (Exception e) {
            logger.error("Ошибка инициализации данных", e);
            throw new RuntimeException("Ошибка инициализации данных", e);
        }
    }

    private void waitForDependencies() throws InterruptedException {
        int attempts = 0;
        while ((userService == null || roleDao == null || passwordEncoder == null) && attempts < 10) {
            Thread.sleep(100);
            attempts++;
            logger.debug("Ожидание инициализации зависимостей... Попытка {}", attempts);
        }
        if (attempts >= 10) {
            throw new IllegalStateException("Не удалось инициализировать зависимости");
        }
    }

    private void clearExistingData() {
        logger.info("Очистка существующих данных...");
        try {
            userService.getAllUsers().forEach(user -> {
                logger.debug("Удаление пользователя: {}", user.getUsername());
                userService.deleteUser(user.getId());
            });

            roleDao.findAll().forEach(role -> {
                logger.debug("Удаление роли: {}", role.getName());
                roleDao.delete(role);
            });
        } catch (Exception e) {
            logger.warn("Ошибка при очистке данных (возможно таблицы не существуют): {}", e.getMessage());
        }
    }

    private void initializeRoles() {
        logger.info("Инициализация ролей...");
        createRoleIfNotExists("ROLE_ADMIN");
        createRoleIfNotExists("ROLE_USER");
    }

    private void createRoleIfNotExists(String roleName) {
        if (roleDao.findByName(roleName).isEmpty()) {
            Role role = new Role(roleName);
            roleDao.save(role);
            logger.info("Создана роль: {}", roleName);
        } else {
            logger.info("Роль уже существует: {}", roleName);
        }
    }

    private void initializeUsers() {
        logger.info("Инициализация пользователей...");
        createAdminUser();
        createRegularUser();
    }

    private void createAdminUser() {
        String username = "admin";
        if (userService.findByUsername(username).isPresent()) {
            logger.info("Администратор уже существует");
            return;
        }

        User admin = new User();
        admin.setUsername(username);
        admin.setPassword(passwordEncoder.encode("admin"));
        admin.setName("Admin");
        admin.setEmail("admin@example.com");
        admin.setAge(30);

        Set<Role> roles = new HashSet<>();
        roles.add(getOrCreateRole("ROLE_ADMIN"));
        roles.add(getOrCreateRole("ROLE_USER"));
        admin.setRoles(roles);

        userService.saveUser(admin);
        logger.info("Создан администратор: {}", username);
    }

    private void createRegularUser() {
        String username = "user";
        if (userService.findByUsername(username).isPresent()) {
            logger.info("Пользователь уже существует");
            return;
        }

        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode("user"));
        user.setName("User");
        user.setEmail("user@example.com");
        user.setAge(25);
        user.setRoles(Set.of(getOrCreateRole("ROLE_USER")));

        userService.saveUser(user);
        logger.info("Создан пользователь: {}", username);
    }

    private Role getOrCreateRole(String roleName) {
        return roleDao.findByName(roleName)
                .orElseGet(() -> {
                    Role newRole = new Role(roleName);
                    roleDao.save(newRole);
                    logger.info("Создана новая роль: {}", roleName);
                    return newRole;
                });
    }
}