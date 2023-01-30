package com.hyperspacegamepanel.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {

    @GetMapping(value = {"/", "/home"})
    public String home(Model m) {
        m.addAttribute("title", "Home | HyperSpaceGamePanel");
        return "index.html";
    }

    @GetMapping("/register")
    public String registerPage(Model m) {
       m.addAttribute("title", "Register | HyperSpaceGamePanel");
       return "register.html";
    }
    
}
