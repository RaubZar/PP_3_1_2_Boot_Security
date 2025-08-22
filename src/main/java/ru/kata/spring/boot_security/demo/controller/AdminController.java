package ru.kata.spring.boot_security.demo.controller;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.service.RoleService;
import ru.kata.spring.boot_security.demo.service.UserService;


@Controller
@RequestMapping("/admin")
public class AdminController {

    private final UserService userService;
    private final RoleService roleService;

    public AdminController(UserService userService, RoleService roleService) {
        this.userService = userService;
        this.roleService = roleService;
    }

    @GetMapping
    public String getAllUsers(Model model) {
        model.addAttribute("users", userService.getAllUsers());
        return "admin/admin-page";
    }

    @PostMapping("/add")
    public String addUser(@RequestParam String username,
                          @RequestParam String password,
                          @RequestParam String name,
                          @RequestParam String email,
                          @RequestParam(required = false) Integer age,
                          @RequestParam List<String> roles) {

        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        user.setName(name);
        user.setEmail(email);
        user.setAge(age);

        Set<Role> roleSet = new HashSet<>();
        for (String roleName : roles) {
            Role role = roleService.findByName(roleName)
                    .orElseThrow(() -> new IllegalArgumentException("Role not found: " + roleName));
            roleSet.add(role);
        }

        user.setRoles(roleSet);
        userService.saveUser(user);
        return "redirect:/admin";
    }

    @PostMapping("/edit")
    public String updateUser(@RequestParam Long id,
                             @RequestParam String username,
                             @RequestParam(required = false) String password,
                             @RequestParam String name,
                             @RequestParam String email,
                             @RequestParam(required = false) Integer age,
                             @RequestParam List<String> roles) {

        User user = userService.getUserById(id).orElseThrow();
        user.setUsername(username);
        if (password != null && !password.isEmpty()) {
            user.setPassword(password);
        }
        user.setName(name);
        user.setEmail(email);
        user.setAge(age);

        Set<Role> roleSet = roles.stream()
                .map(roleName -> roleService.findByName(roleName))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toSet());

        user.setRoles(roleSet);
        userService.updateUser(user);
        return "redirect:/admin";
    }

    @PostMapping("/delete")
    public String deleteUser(@RequestParam Long id) {
        userService.deleteUser(id);
        return "redirect:/admin";
    }

    @ModelAttribute("roleUtils")
    public Object roleUtils() {
        return new Object() {
            public String getRolesAsString(Set<Role> roles) {
                return roles.stream()
                        .map(Role::getName)
                        .collect(Collectors.joining(","));
            }
        };
    }
}