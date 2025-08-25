package ru.kata.spring.boot_security.demo.controller;


import org.springframework.stereotype.Controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import ru.kata.spring.boot_security.demo.dto.UserConverter;
import ru.kata.spring.boot_security.demo.dto.UserDto;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.service.UserService;


@Controller
@RequestMapping("/admin")
public class AdminController {

    private final UserService userService;
    private final UserConverter userConverter;

    public AdminController(UserService userService,  UserConverter userConverter) {
        this.userService = userService;
        this.userConverter = userConverter;

    }

    @GetMapping
    public ModelAndView getAllUsers() {
        ModelAndView mav = new ModelAndView("admin/admin-page");
        mav.addObject("users", userService.getAllUsers());
        return mav;
    }

    @PostMapping("/add")
    public ModelAndView addUser(@ModelAttribute UserDto userDto) {
        User user = userConverter.convertToEntity(userDto);
        userService.saveUser(user);
        return new ModelAndView("redirect:/admin");
    }

    @PostMapping("/edit")
    public ModelAndView updateUser(@ModelAttribute User user) {
        userService.updateUser(user);
        return new ModelAndView("redirect:/admin");
    }

    @PostMapping("/delete")
    public ModelAndView deleteUser(@RequestParam Long id) {
        userService.deleteUser(id);
        return new ModelAndView("redirect:/admin");
    }


}