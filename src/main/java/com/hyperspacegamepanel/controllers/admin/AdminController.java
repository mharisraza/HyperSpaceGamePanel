package com.hyperspacegamepanel.controllers.admin;

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

import com.hyperspacegamepanel.entities.Machine;
import com.hyperspacegamepanel.entities.Ticket;
import com.hyperspacegamepanel.entities.User;
import com.hyperspacegamepanel.helper.Helper;
import com.hyperspacegamepanel.repositories.MachineRepository;
import com.hyperspacegamepanel.repositories.TicketRepository;
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

    @Autowired
    private MachineRepository machineRepo;

    @Autowired
    private TicketRepository ticketRepo;

    /*
     * The @ModelAttribute annotation allows you to centralize data preparation
     * and reuse it across multiple request handling methods,
     * avoiding duplication and making the data easily accessible to all views.
     */

    @ModelAttribute("users")
    public List<User> getUsers() {
        return userRepo.findAll();
    }

    @ModelAttribute("machines")
    public List<Machine> getMachines() {
        return machineRepo.findAll();
    }

    @ModelAttribute("tickets")
    public List<Ticket> getTickets() {
        return this.ticketRepo.findAll();
    }

    @ModelAttribute("admin")
    public User getLoggedInUser(Principal principal) {
        return userRepo.getByEmail(principal.getName());
    }

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
        m.addAttribute("helper", new Helper());
        return "admin/machines.html";
    }

}
