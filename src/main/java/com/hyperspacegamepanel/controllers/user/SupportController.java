package com.hyperspacegamepanel.controllers.user;

import java.security.Principal;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.hyperspacegamepanel.controllers.main.HelperController;
import com.hyperspacegamepanel.exceptions.ResourceNotFound;
import com.hyperspacegamepanel.helper.Alert;
import com.hyperspacegamepanel.models.ticket.Ticket;
import com.hyperspacegamepanel.models.ticket.TicketReply;
import com.hyperspacegamepanel.models.user.User;
import com.hyperspacegamepanel.services.TicketService;

@Controller
@RequestMapping("/me/support")
public class SupportController extends HelperController {

    @Autowired
    private TicketService ticketService;

    @Autowired
    private HttpSession httpSession;

    // showing all the user's ticket.
    @GetMapping("/tickets")
    public String userTickets(Model m, Principal principal) throws InterruptedException, ExecutionException {
        User user = getLoggedInUser(principal);
        m.addAttribute("tickets", this.ticketService.getAllTicketsOfUser(user.getId()).join());
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

    @GetMapping("/ticket/view/{ticketId}")
    public String ticket(@PathVariable Integer ticketId, Principal principal, Model m, @RequestParam(required = false) String action, @RequestParam(required = false) Integer messageId) {
        if (ticketId == null) return "redirect:/me/support/tickets";

        Ticket ticket = null;
        List<TicketReply> ticketReplies = null;

        if(action != null) {

            switch(action) {
                
                case "closeTicket" -> {
                     try {
                        this.ticketService.closeTicket(ticketId);
                        httpSession.setAttribute("status", new Alert("Ticket closed successfully.", Alert.SUCCESS, Alert.SUCCESS_CLASS));
                        return "redirect:/me/support/ticket/view/"+ticketId;
                    } catch (Exception e) {
                        httpSession.setAttribute("status", new Alert("Sorry, we were unable to close the ticket at this moment, please try later", Alert.ERROR, Alert.ERROR_CLASS));
                        return "redirect:/me/support/ticket/view/"+ticketId;
                    }
                }

                case "uncloseTicket" -> {
                    try {
                      this.ticketService.uncloseTicket(ticketId, getLoggedInUser(principal).getId());
                      httpSession.setAttribute("status", new Alert("Ticket unclosed successfully.", Alert.SUCCESS, Alert.SUCCESS_CLASS));
                      return "redirect:/me/support/ticket/view/"+ticketId;
                    } catch (Exception e) {
                        if(e.getMessage().equals("UNABLE_TO_UNCLOSE_THE_TICKET[REASON:TICKET_CLOSED_BY_ADMIN]")) {
                            httpSession.setAttribute("status", new Alert("This ticket was closed by an admin, it cannot unclose by you.", Alert.ERROR, Alert.ERROR_CLASS));
                            return "redirect:/me/support/ticket/view/"+ticketId;
                        }
                        httpSession.setAttribute("status", new Alert("Sorry, we were unable to un-close the ticket at this moment, please try later.", Alert.ERROR, Alert.ERROR_CLASS));
                        return "redirect:/me/support/ticket/view/"+ticketId;
                    }
                }

                case "deleteMessage" -> {
                    try {
                           this.ticketService.deleteTicketReply(messageId, false);
                           httpSession.setAttribute("status", new Alert("Successfully delete the message reply.", Alert.SUCCESS, Alert.SUCCESS_CLASS));
                           return "redirect:/me/support/ticket/view/"+ticketId;
                    } catch (Exception e) {
                        if(e.getMessage().equals("CANNOT_DELETE_THE_REPLY_MESSAGE_BECAUSE_IT_IS_SEND_BY_ADMIN")) {
                            httpSession.setAttribute("status", new Alert("Cannot delete the reply, it was send by an admin.", Alert.ERROR, Alert.ERROR_CLASS));
                            return "redirect:/me/support/ticket/view/" + ticketId;
                        }
                        httpSession.setAttribute("status", new Alert("Unabled to delete the ticket reply.", Alert.ERROR, Alert.ERROR_CLASS));
                        return "redirect:/me/support/ticket/view/" + ticketId;
                    }
                }

                default -> {
                    httpSession.setAttribute("status", new Alert("Invalid request or action.", Alert.ERROR, Alert.ERROR_CLASS));
                    return "redirect:/me/support/ticket/view/"+ticketId;
                }
            }
        }

        try {
         ticket = this.ticketService.getTicketByUser(ticketId, getLoggedInUser(principal).getId()).join();
         ticketReplies = this.ticketService.getTicketRepliesByTicket(ticket.getId()).join();
        } catch (Exception e) {
             if(e instanceof ResourceNotFound && e.getMessage().contains("Ticket not found")) {
                httpSession.setAttribute("status", new Alert("Sorry, we were unable to found the ticket. it may doesn't exist or deleted.", Alert.ERROR, Alert.ERROR_CLASS));
                return "redirect:/me/support/tickets";
             }
        }
        m.addAttribute("ticketReplies", ticketReplies);
        m.addAttribute("ticket", ticket);
        m.addAttribute("title", ticket.getSubject() + " | HyperSpaceGamePanel");
        return "user/ticket";
    }

    // processing and handling the newly submitted ticket.
    @PostMapping("/submit-ticket")
    public String processTicket(@Valid @ModelAttribute Ticket ticket, BindingResult bindResult, Model m, @RequestParam Integer userId) {
        if (bindResult.hasErrors()) return "user/new_ticket";
            
        Integer createdTicketId = null;
        try {
            Ticket createdTicket = this.ticketService.createTicket(ticket, userId).join();
            createdTicketId = createdTicket.getId();
        } catch (Exception e) {
            httpSession.setAttribute("status", new Alert("Ahh sorry, cannot submitted the ticket, please try again.", Alert.ERROR, Alert.ERROR_CLASS));
            return "redirect:/me/support/tickets";
        }
        return "redirect:/me/support/ticket/view/" + createdTicketId;
    }

    // processing and handling reply message to the existing ticket.
    @PostMapping("/process-reply")
    public String processAdditionalMessage(@RequestParam Integer ticketId, @RequestParam String replyMessage, Principal principal) {

        // if ticket id is empty we're not allowing to proceed.
        if (ticketId == null) {
            httpSession.setAttribute("status", new Alert("Ahh sorry, cannot find the ticket to submit the reply, please try again.", Alert.ERROR, Alert.ERROR_CLASS));
            return "redirect:/me/support/tickets";
        }

        // returns if message is empty.
        if (replyMessage == null || replyMessage.isBlank()) {
            httpSession.setAttribute("status", new Alert("Message is required for reply to the submitted ticket.", Alert.ERROR, Alert.ERROR_CLASS));
            return "redirect:/me/support/ticket/view/" + ticketId;
        }

        // getting logged in user
        User user = getLoggedInUser(principal);

        try {
            Ticket ticket = this.ticketService.getTicketByUser(ticketId, user.getId()).join();

            TicketReply ticketReply = new TicketReply();
            ticketReply.setMessage(replyMessage);
            ticketReply.setSender(user);
            ticketReply.setTicket(ticket);

            ticketReply = this.ticketService.createTicketReply(ticketReply, ticket).join();

            // if success.
            httpSession.setAttribute("status", new Alert("Message added successfully.", Alert.SUCCESS, Alert.SUCCESS_CLASS));

        } catch (Exception e) {
            if (e instanceof ResourceNotFound && e.getMessage().contains("Ticket not found")) {
                httpSession.setAttribute("status", new Alert("Sorry, cannot submit the reply to the ticket, ticket doesn't exist or deleted.", Alert.ERROR, Alert.ERROR_CLASS));
                return "redirect:/me/support/ticket/view/" + ticketId;
            }
            httpSession.setAttribute("status", new Alert("Ahh sorry, cannot submitted the ticket, please try later.", Alert.ERROR, Alert.ERROR_CLASS));
        }

        return "redirect:/me/support/ticket/view/" + ticketId;
    }

}
