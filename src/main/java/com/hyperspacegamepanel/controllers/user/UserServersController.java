package com.hyperspacegamepanel.controllers.user;

import com.hyperspacegamepanel.controllers.main.DataCentralizedController;
import com.hyperspacegamepanel.services.ServerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(value = {"/me/servers", "/me/server"})
public class UserServersController {

    @Autowired private ServerService serverService;

    @GetMapping
    public String servers(Model m) {
        m.addAttribute("title", "Your Servers | HyperSpaceGamePanel");
       return "user/servers";
    }
}
