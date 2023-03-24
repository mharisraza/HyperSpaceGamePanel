package com.hyperspacegamepanel.controllers.admin;

import java.security.Principal;
import java.util.concurrent.ExecutionException;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.hyperspacegamepanel.controllers.main.HelperController;
import com.hyperspacegamepanel.helper.Alert;
import com.hyperspacegamepanel.services.UserService;

@Controller
@RequestMapping("/admin")
public class AdminController extends HelperController {


    @Autowired
    private HttpSession httpSession;

    @Autowired
    private UserService userService;

    @GetMapping
    public String adminActions(@RequestParam(required = false) String action, @RequestParam(required = false) Integer userId, Model m) throws InterruptedException, ExecutionException {
        if(action != null) {
            switch (action) {
                case "ban":
                    this.userService.banUser(this.userService.getUser(userId).get());
                    httpSession.setAttribute("status", new Alert("User banned successfully.", Alert.SUCCESS, Alert.SUCCESS_CLASS));
                    return "redirect:/admin/user/view/" + userId;
    
                case "unban":
                    this.userService.unbanUser(this.userService.getUser(userId).get());
                    httpSession.setAttribute("status", new Alert("User unbanned successfully.", Alert.SUCCESS, Alert.SUCCESS_CLASS));
                    return "redirect:/admin/user/view/" + userId;
            }
        }
        return "redirect:/admin/dashboard";
    }

    @GetMapping("/dashboard")
    public String home(Model m) {
        m.addAttribute("title", "Admin Panel | HyperSpaceGamePanel");
        return "admin/index.html";
    }

    @GetMapping("/users")
    public String userPage(Model m) {
        m.addAttribute("title", "Users | HyperSpaceGamePanel");
        return "admin/user_module/users.html";
    }

    @GetMapping("/tickets")
    public String allTickets(Model m) {
        m.addAttribute("title", "Tickets | HyperSpaceGamePanel");
        return "admin/ticket_module/tickets.html";
    }

    @GetMapping("/machines")
    public String allMachines(Model m) {
        m.addAttribute("title", "Machines | HyperSpaceGamePanel");
        return "admin/machine_module/machines.html";
    }

    @GetMapping("/servers")
    public String allServers(Model m) {
        m.addAttribute("title", "Servers | HyperSpaceGamePanel");
        return "admin/server_module/servers.html";
    }

}
