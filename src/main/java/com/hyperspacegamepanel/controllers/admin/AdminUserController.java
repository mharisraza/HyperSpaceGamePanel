package com.hyperspacegamepanel.controllers.admin;

import java.util.Optional;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.hyperspacegamepanel.entities.User;
import com.hyperspacegamepanel.repositories.UserRepository;

@Controller
@RequestMapping("/admin/user")
public class AdminUserController {

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private HttpSession httpSession;

    @GetMapping("")
    public String userDetails(@RequestParam(required = false) Integer id, Model m) {
        if (id == null) {
            httpSession.setAttribute("status", "CANT_FIND_USER_WITH_PROVIDED_ID");
            return "redirect:/admin/users/all";
        }
        Optional<User> user = this.userRepo.findById(id);
        if (!user.isPresent()) {
            httpSession.setAttribute("status", "CANT_FIND_USER_WITH_PROVIDED_ID");
            return "redirect:/admin/users/all";
        }

        m.addAttribute("user", user.get());
        m.addAttribute("title", user.get().getFullName() + " | HyperSpaceGamePanel");
        return "admin/user.html";
    }
    
}
