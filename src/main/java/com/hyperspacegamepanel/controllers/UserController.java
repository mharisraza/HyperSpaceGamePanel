package com.hyperspacegamepanel.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/me")
public class UserController {

    @GetMapping(value = {"/", "/dashboard"})
    public String home(Model m) {
        m.addAttribute("title", "Home | HyperSpaceGamePanel");
         return "user/index.html";
    }
    
}
