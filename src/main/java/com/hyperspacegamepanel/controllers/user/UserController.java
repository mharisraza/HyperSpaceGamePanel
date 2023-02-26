package com.hyperspacegamepanel.controllers.user;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import com.hyperspacegamepanel.entities.User;
import com.hyperspacegamepanel.repositories.UserRepository;

@Controller
@RequestMapping("/me")
public class UserController {

    @Autowired
    private UserRepository userRepo;

    @ModelAttribute("user")
    public User getUser(Principal principal) {
        return this.userRepo.findByEmail(principal.getName()).get();
    }

    @GetMapping(value = {"/", "/dashboard"})
    public String home(Model m) {
        m.addAttribute("title", "Home | HyperSpaceGamePanel");
         return "user/index.html";
    }
    
}
