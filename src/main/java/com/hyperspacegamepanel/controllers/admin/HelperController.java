package com.hyperspacegamepanel.controllers.admin;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.hyperspacegamepanel.helper.Helper;

import com.hyperspacegamepanel.entities.Machine;
import com.hyperspacegamepanel.entities.Ticket;
import com.hyperspacegamepanel.entities.User;
import com.hyperspacegamepanel.repositories.MachineRepository;
import com.hyperspacegamepanel.repositories.TicketRepository;
import com.hyperspacegamepanel.repositories.UserRepository;

public class HelperController {

    // all the method that are needed
    // in every controller should be here to for redundancy and maintability.

    @Autowired
    private UserRepository userRepo;

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

     @ModelAttribute("helper")
     public Helper provideHelper() {
        return new Helper();
     }
}
