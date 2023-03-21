package com.hyperspacegamepanel.controllers.user;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import com.hyperspacegamepanel.models.user.User;
import com.hyperspacegamepanel.repositories.UserRepository;

@Controller
@RequestMapping("/me")
public class UserController {

    @Autowired
    private UserRepository userRepo;

     /*
     * The @ModelAttribute annotation allows you to centralize data preparation
     * and reuse it across multiple request handling methods,
     * avoiding duplication and making the data easily accessible to all views.
     */

    @ModelAttribute("user")
    public User getLoggedInUser(Principal principal) {
        return this.userRepo.findByEmail(principal.getName()).get();
    }

    @GetMapping(value = {"/", "/dashboard"})
    public String home(Model m) {
        m.addAttribute("title", "Home | HyperSpaceGamePanel");
         return "user/index.html";
    }
    
}
