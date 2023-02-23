package com.hyperspacegamepanel.controllers;

import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.hyperspacegamepanel.dtos.UserDto;
import com.hyperspacegamepanel.entities.User;
import com.hyperspacegamepanel.repositories.UserRepository;

@Controller
public class MainController {

    @Autowired
    private UserRepository userRepo;

    @GetMapping(value = {"/", "/home"})
    public String home(Model m) {
        m.addAttribute("title", "Home | HyperSpaceGamePanel");
        return "index.html";
    }

    @GetMapping("/register")
    public String registerPage(Model m) {
       m.addAttribute("title", "Register | HyperSpaceGamePanel");
       m.addAttribute("user", new UserDto());
       return "register.html";
    }

    @GetMapping("/login")
    public String loginPage(Model m, HttpSession httpSession) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if(!(auth instanceof AnonymousAuthenticationToken)) {
            User user = this.userRepo.getByEmail(auth.getName());
    
            if(user.getRole().equalsIgnoreCase("ROLE_NORMAL")) {
                return "redirect:/me/dashboard";
            }

            if(user.getRole().equalsIgnoreCase("ROLE_ADMIN")) {
                return "redirect:/admin/dashboard";
            }
        }


        m.addAttribute("title", "Login | HyperSpaceGamePanel");
        return "login.html";
    }

    @GetMapping("/addAdmin")
    public String addAdmin(Model m, HttpSession httpSession) {

        List<User> users = this.userRepo.findAllByRole("ROLE_ADMIN");

        if(!users.isEmpty()) {
            return "redirect:/register";
        }
        
        m.addAttribute("title", "Add Admin | HyperSpaceGamePanel");
        m.addAttribute("user", new UserDto());
       return "add_admin.html";
    }

    
}
