package ru.kata.spring.boot_security.demo.controller;

import org.springframework.web.servlet.ModelAndView;
import ru.kata.spring.boot_security.demo.model.User;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/user")
public class UserController {

    @GetMapping
    public ModelAndView getUserPage(@AuthenticationPrincipal User user) {
        ModelAndView mav = new ModelAndView("user/user-page");
        mav.addObject("user", user);
        return mav;
    }
}