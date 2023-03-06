package com.hyperspacegamepanel.controllers.admin;

import java.security.Principal;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.hyperspacegamepanel.controllers.main.HelperController;
import com.hyperspacegamepanel.repositories.MachineRepository;
import com.hyperspacegamepanel.repositories.TicketRepository;
import com.hyperspacegamepanel.repositories.UserRepository;
import com.hyperspacegamepanel.services.UserService;

@Controller
@RequestMapping("/admin")
public class AdminController extends HelperController {

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private HttpSession httpSession;

    @Autowired
    private UserService userService;

    @Autowired
    private MachineRepository machineRepo;

    @Autowired
    private TicketRepository ticketRepo;

    @GetMapping("/dashboard")
    public String home(Model m, Principal principal) {
        m.addAttribute("title", "Admin Panel | HyperSpaceGamePanel");
        return "admin/index.html";
    }

    @GetMapping("/users")
    public String userPage(Model m) {
        m.addAttribute("title", "Users | HyperSpaceGamePanel");
        return "admin/users.html";
    }

    @GetMapping(params = "action")
    public String adminActions(@RequestParam(required = false) String action,
            @RequestParam(required = false) Integer userId, Model m) {
        if (action == null) {
            httpSession.setAttribute("status", "CANT_FIND_ACTIONS");
            return "redirect:/admin/dashboard";
        }
        switch (action) {
            case "ban":
                this.userService.suspendUser(this.userRepo.findById(userId).get());
                httpSession.setAttribute("status", "USER_BANNED_SUCCESSFULLY");
                return "redirect:/admin/user?id=" + userId;

            case "unban":
                this.userService.unbanUser(this.userRepo.findById(userId).get());
                httpSession.setAttribute("status", "USER_UNBANNED_SUCCESSFULLY");
                return "redirect:/admin/user?id=" + userId;
        }
        return "redirect:/admin/dashboard";
    }

    @GetMapping("/tickets")
    public String allTickets(Model m) {
        m.addAttribute("title", "Tickets | HyperSpaceGamePanel");
        return "admin/tickets.html";
    }

    @GetMapping("/machines")
    public String allMachines(Model m) {
        m.addAttribute("title", "Machines | HyperSpaceGamePanel");
        return "admin/machines.html";
    }

    @GetMapping("/servers")
    public String allServers(Model m) {
        m.addAttribute("title", "Servers | HyperSpaceGamePanel");
        return "admin/servers.html";
    }

}
