package com.hyperspacegamepanel.controllers.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.hyperspacegamepanel.controllers.main.DataCenteralizedController;
import com.hyperspacegamepanel.services.ServerService;

@Controller
@RequestMapping(value = {"/me/servers", "/me/server"})
public class UserServersController extends DataCenteralizedController {

    @Autowired
    private ServerService serverService;
    

    @GetMapping
    public String servers(Model m) {
        m.addAttribute("title", "Your Servers | HyperSpaceGamePanel");
       return "user/servers";
    }
}
