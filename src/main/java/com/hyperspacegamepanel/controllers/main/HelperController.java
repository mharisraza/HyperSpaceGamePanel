package com.hyperspacegamepanel.controllers.main;

import java.security.Principal;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.hyperspacegamepanel.helper.Helper;
import com.hyperspacegamepanel.models.machine.Machine;
import com.hyperspacegamepanel.models.server.Server;
import com.hyperspacegamepanel.models.ticket.Ticket;
import com.hyperspacegamepanel.models.user.User;
import com.hyperspacegamepanel.repositories.MachineRepository;
import com.hyperspacegamepanel.repositories.ServerRepository;
import com.hyperspacegamepanel.repositories.TicketRepository;
import com.hyperspacegamepanel.repositories.UserRepository;
import com.hyperspacegamepanel.services.ServerService;

public class HelperController {

    // all the method that are needed
    // in every controller should be here to for redundancy and maintability.

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private MachineRepository machineRepo;

    @Autowired
    private TicketRepository ticketRepo;

    @Autowired
    private ServerRepository serverRepo;

    @Autowired
    private ServerService serverService;

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

     @ModelAttribute("unread_tickets")
     public List<Ticket> getUnReadTickets() {
        return this.ticketRepo.getUnReadTickets();
     }
 
     @ModelAttribute("currentLoggedInUser")
     public User getLoggedInUser(Principal principal) {
        if(principal == null) return null;
         return userRepo.getByEmail(principal.getName());
     }

     @ModelAttribute("servers")
     public List<Server> getServers() {
        return serverRepo.findAll();
     }

     @ModelAttribute("helper")
     public Helper provideHelper() {
        return new Helper();
     }

     @ModelAttribute("userServers")
    public List<Server> userServers(Principal principal) throws InterruptedException, ExecutionException {
        if(principal != null) return serverService.getUserServers(getLoggedInUser(principal).getId()).join();
        return null;
    }


}
