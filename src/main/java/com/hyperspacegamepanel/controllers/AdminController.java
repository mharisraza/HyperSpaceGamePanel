package com.hyperspacegamepanel.controllers;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.hyperspacegamepanel.entities.User;
import com.hyperspacegamepanel.repositories.UserRepository;
import com.hyperspacegamepanel.services.UserService;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private HttpSession httpSession;

    @Autowired
    private UserService userService;

    /*
     * The @ModelAttribute annotation allows you to centralize data preparation
     * and reuse it across multiple request handling methods,
     * avoiding duplication and making the data easily accessible to all views.
     */

    @ModelAttribute("users")
    public List<User> getUsers() {
        return userRepo.findAll();
    }

    @ModelAttribute("admin")
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

    @GetMapping("/user")
    public String userDetails(@RequestParam(required = false) Integer id, Model m) {
        if(id == null) {
            httpSession.setAttribute("status", "CANT_FIND_USER_WITH_PROVIDED_ID");
            return "redirect:/admin/users/all";
        }
        Optional<User> user = this.userRepo.findById(id);
        if(!user.isPresent()) {
            httpSession.setAttribute("status", "CANT_FIND_USER_WITH_PROVIDED_ID");
            return "redirect:/admin/users/all";
        }

        m.addAttribute("user", user.get());
        m.addAttribute("title", user.get().getFullName() + " | HyperSpaceGamePanel");
        return "admin/user.html";
    }

    @GetMapping(params = "action")
    public String adminActions(@RequestParam(required = false) String action, @RequestParam(required = false) Integer userId, Model m) {
        if(action == null) {
            httpSession.setAttribute("status", "CANT_FIND_ACTIONS");
            return "redirect:/admin/dashboard";
        }
        switch(action) {
            case "ban":
           this.userService.suspendUser(this.userRepo.findById(userId).get());
           httpSession.setAttribute("status", "USER_BANNED_SUCCESSFULLY");
           return "redirect:/admin/user?id="+userId;

           case "unban":
            this.userService.unbanUser(this.userRepo.findById(userId).get());
            httpSession.setAttribute("status", "USER_UNBANNED_SUCCESSFULLY");
            return "redirect:/admin/user?id="+userId;
        }
        return "redirect:/admin/dashboard";
    }

}
