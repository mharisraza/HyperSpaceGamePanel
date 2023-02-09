package com.hyperspacegamepanel.controllers;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import com.hyperspacegamepanel.entities.User;
import com.hyperspacegamepanel.repositories.UserRepository;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private UserRepository userRepo;

    /*
     * The @ModelAttribute annotation allows you to centralize data preparation
     * and reuse it across multiple request handling methods,
     * avoiding duplication and making the data easily accessible to all views.
     */

    @ModelAttribute("users")
    public List<User> getUsers() {
        return userRepo.findAll();
    }

    @ModelAttribute("user")
    public User getUser(Principal principal) {
        return userRepo.getByEmail(principal.getName());
    }



    @GetMapping("/dashboard")
    public String home(Model m, Principal principal) {
        m.addAttribute("title", "Admin Panel | HyperSpaceGamePanel");
        return "admin/index.html";
    }

    @GetMapping("/users/all")
    public String userPage(Model m) {
        m.addAttribute("title", "Users | HyperSpaceGamePanel");
        return "admin/users.html";
    }

}
