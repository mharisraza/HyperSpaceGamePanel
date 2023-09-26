package com.hyperspacegamepanel.controllers.main;

import com.hyperspacegamepanel.models.user.User;
import com.hyperspacegamepanel.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Objects;

@Controller
public class MainController {

    @Autowired
    private UserService userService;

    @GetMapping(value = {"/", "/home"})
    public String home(Model m) {
        m.addAttribute("title", "Home | HyperSpaceGamePanel");
        return "index.html";
    }

    @GetMapping("/login")
    public String loginPage(Model m) {
        
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if(!(auth instanceof AnonymousAuthenticationToken)) {
            User user = this.userService.getUserByEmail(auth.getName()).join();
    
            if(Objects.equals(user.getRole(), User.ROLE_USER)) {
                return "redirect:/me/dashboard";
            }

            if(Objects.equals(user.getRole(), User.ROLE_ADMIN)) {
                return "redirect:/admin/dashboard";
            }
        }

        m.addAttribute("title", "Login | HyperSpaceGamePanel");
        return "login.html";
    }

    @GetMapping("/plans")
    public String serverPlans(Model m) {
        m.addAttribute("title", "GameServers Plans | HyperSpaceGamePanel");
        return "server_plans.html";
    }

}
