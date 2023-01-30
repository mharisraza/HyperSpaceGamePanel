package com.hyperspacegamepanel.controllers;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.hyperspacegamepanel.dtos.UserDto;
import com.hyperspacegamepanel.services.UserService;

@Controller
public class RegisterController {

    @Autowired
    private UserService userService;

    // process registration here
    @PostMapping("/create-account")
    public String processRegistration(@Valid @ModelAttribute("user") UserDto user, BindingResult bindingResult,
            Model m) {
        boolean userRegistered = false;
        if (!user.getConfirmPassword().equals(user.getPassword())) {
            bindingResult.rejectValue("confirmPassword", "error.user", "Password and confirm password do not match.");
        }
        if (bindingResult.hasErrors()) {
            System.out.println("errors does contain");
            m.addAttribute("user", user);
            return "register";
        }

        this.userService.createUser(user);
        userRegistered = true;
        if (userRegistered) {
            m.addAttribute("status", "USER_REGISTERED_SUCCESSFULLY");
        }
        return "redirect:/register";
    }

}
