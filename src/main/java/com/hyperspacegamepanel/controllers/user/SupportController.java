package com.hyperspacegamepanel.controllers.user;

import java.security.Principal;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.hyperspacegamepanel.entities.Ticket;
import com.hyperspacegamepanel.entities.TicketReply;
import com.hyperspacegamepanel.entities.User;
import com.hyperspacegamepanel.repositories.TicketReplyRepository;
import com.hyperspacegamepanel.repositories.TicketRepository;
import com.hyperspacegamepanel.repositories.UserRepository;
import com.hyperspacegamepanel.services.TicketService;

@Controller
@RequestMapping("/me/support")
public class SupportController extends UserController {

    @Autowired
    private TicketService ticketService;

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private TicketRepository ticketRepo;

    @Autowired
    private TicketReplyRepository ticketReplyRepo;

    @Autowired
    private HttpSession httpSession;

    // showing all the user's ticket.
    @GetMapping("/tickets")
    public String userTickets(Model m, Principal principal) {
        User user = getLoggedInUser(principal);
        m.addAttribute("tickets", user.getTickets());
        m.addAttribute("title", "Your Tickets | HyperSpaceGamePanel");
        return "user/tickets";
    }

    // showing page for where user can submit a new ticket.
    @GetMapping("/new")
    public String newTicket(Model m) {
        m.addAttribute("ticket", new Ticket());
        m.addAttribute("title", "New Ticket | HyperSpaceGamePanel");
        return "user/new_ticket";
    }

    // handling logic where user can delete additional ticket message or close the
    // ticket.
    @GetMapping("")
    public String ticket(@RequestParam(required = false) Integer ticketId, Principal principal, Model m,
            @RequestParam(required = false) String action, @RequestParam(required = false) Integer messageId) {
        if (ticketId == null) {
            httpSession.setAttribute("status", "CANT_FIND_TICKET");
            return "redirect:/me/support/tickets";
        }

        User user = getLoggedInUser(principal);
        Optional<Ticket> ticket = this.ticketRepo.findById(ticketId);

        if (!ticket.isPresent() || ticket.get().getUser().getId() != user.getId()) {
            httpSession.setAttribute("status", "CANT_FIND_TICKET");
            return "redirect:/me/support/tickets";
        }

        List<TicketReply> ticketReplies = this.ticketReplyRepo.findAll();

        if (action != null && action.equals("closeTicket")) {
            ticket.get().setClosed(true);
            ticket.get().setClosedBy("USER");
            ticket.get().setStatus("Closed");
            httpSession.setAttribute("status", "TICKET_CLOSED_SUCCESSFULLY");
            this.ticketRepo.save(ticket.get());
            return "redirect:/me/support?ticketId=" + ticketId;
        }

        if (action != null && action.equals("uncloseTicket")) {

            if(ticket.get().getClosedBy() == "ADMIN") {
                httpSession.setAttribute("status", "CANT_UNCLOSE_TICKET_CLOSED_BY_ADMIN");
                return "redirect:/me/support?ticketId="+ticketId;
            }

            ticket.get().setClosed(false);
            ticket.get().setStatus("Ticket is recently unclosed.");
            httpSession.setAttribute("status", "TICKET_UNCLOSED_SUCCESSFULLY");
            this.ticketRepo.save(ticket.get());
            return "redirect:/me/support?ticketId=" + ticketId;
        }

        if(action != null && action.equals("deleteMessage")) {
            if(messageId == null) {
                return "redirect:/me/support?ticketId=" + ticketId;
            }

         Optional<TicketReply> ticketReply = this.ticketReplyRepo.findById(messageId);

         if(!ticketReply.isPresent() || ticketReply.get().getSender().getId() != user.getId()  || ticketReply.get().getSender().getRole() == "ROLE_ADMIN") {
            httpSession.setAttribute("status", "TICKET_REPLY_DOES_NOT_EXIST_OR_NOT_ALLOW_TO_DELETE");
            return "redirect:/me/support?ticketId=" + ticketId;
         }
              
            this.ticketReplyRepo.delete(ticketReply.get());
            httpSession.setAttribute("status", "REPLY_MESSAGE_DELETED_SUCCESSFULLY");
            return "redirect:/me/support?ticketId=" + ticketId;
        }

        m.addAttribute("ticketReplies", ticketReplies);
        m.addAttribute("ticket", ticket.get());
        m.addAttribute("title", ticket.get().getSubject() + " | HyperSpaceGamePanel");
        return "user/ticket";
    }

    // processing and handling the newly submitted ticket.
    @PostMapping("/submit-ticket")
    public String processTicket(@Valid @ModelAttribute Ticket ticket, BindingResult bindResult, Model m,
            @RequestParam Integer userId) {

        if (bindResult.hasErrors()) {
            m.addAttribute("ticket", ticket);
            return "user/new_ticket";
        }

        User user = this.userRepo.findById(userId).get();
        ticket.setUser(user);
        ticket.setSubmittedDate(new Date());
        ticket = this.ticketService.createTicket(ticket);

        if (ticket != null) {
            httpSession.setAttribute("status", "TICKET_SUBMITTED_SUCCESSFULLY");
            return "redirect:/me/support?ticketId=" + ticket.getId();
        }

        httpSession.setAttribute("status", "SOMETHING_WENT_WRONG");
        return "user/new_ticket";
    }

    // processing and handling reply message to the existing ticket.
    @PostMapping("/process-reply")
    public String processAdditionalMessage(@RequestParam Integer ticketId, @RequestParam String replyMessage,
            Principal principal) {

        // if ticket id is empty we're not allowing to proceed.
        if (ticketId == null) {
            httpSession.setAttribute("status", "CANT_FIND_TICKET");
            return "redirect:/me/support/tickets";
        }

        // getting logged in user info and ticket with provided ticket id.
        User user = getLoggedInUser(principal);
        Optional<Ticket> ticket = this.ticketRepo.findById(ticketId);

        // may be ticketId were manipulated or ticket doesn't belong to the logged in
        // user's ticket.
        if (!ticket.isPresent() || ticket.get().getUser().getId() != user.getId()) {
            httpSession.setAttribute("status", "CANT_FIND_TICKET");
            return "redirect:/me/support/tickets";
        }

        // returns if message is empty.
        if (replyMessage == null || replyMessage.isBlank()) {
            httpSession.setAttribute("status", "REPLY_MESSAGE_IS_EMPTY");
            return "redirect:/me/support?ticketId=" + ticketId;
        }

        // returns if ticket is closed and code is manipulated.
        if (ticket.get().isClosed()) {
            httpSession.setAttribute("status", "CANT_ADD_MESSAGE_TICKET_IS_CLOSED");
            return "redirect:/me/support?ticketId=" + ticketId;
        }

        TicketReply ticketReply = new TicketReply();
        ticketReply.setRepliedDate(new Date());
        ticketReply.setTicket(ticket.get());
        ticketReply.setSender(user);
        ticketReply.setMessage(replyMessage);
        ticket.get().setStatus("Pending Admin Response");


        this.ticketReplyRepo.save(ticketReply);

        httpSession.setAttribute("status", "REPLY_MESSAGE_ADDED_SUCCESSFULLY");

        return "redirect:/me/support?ticketId=" + ticketId;
    }

}
