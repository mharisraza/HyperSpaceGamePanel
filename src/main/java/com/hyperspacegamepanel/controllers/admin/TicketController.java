package com.hyperspacegamepanel.controllers.admin;

import com.hyperspacegamepanel.controllers.main.DataCentralizedController;
import com.hyperspacegamepanel.exceptions.ResourceNotFound;
import com.hyperspacegamepanel.models.ticket.Ticket;
import com.hyperspacegamepanel.models.ticket.TicketReply;
import com.hyperspacegamepanel.models.user.User;
import com.hyperspacegamepanel.services.TicketService;
import com.hyperspacegamepanel.utils.Alert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;
import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/admin/ticket")
public class TicketController extends DataCentralizedController {

    @Autowired
    private HttpSession httpSession;

    @Autowired
    private TicketService ticketService;

    @GetMapping("/view/{ticketId}")
    public String showTicket(@PathVariable(required = false) Integer ticketId, @RequestParam(required = false) String action, @RequestParam(required =  false) Integer messageId, Model m) {

        if(ticketId == null) {
            httpSession.setAttribute("status", new Alert("Sorry, cannot find the ticket, it may not exists or deleted.", Alert.ERROR, Alert.ERROR_CLASS));
            return "redirect:/admin/tickets";
        }

        if(action != null) {

            switch(action) {

                case "deleteMessage" -> {
                    try {
                       this.ticketService.deleteTicketReply(ticketId, true);
                       httpSession.setAttribute("status", new Alert("Message reply deleted successfully.", Alert.SUCCESS, Alert.SUCCESS_CLASS));
                       return "redirect:/admin/ticket/view/" + ticketId;
                    } catch (Exception e) {
                        httpSession.setAttribute("status", new Alert("Sorry, cannot delete the reply at this moment, please try later.", Alert.ERROR, Alert.ERROR_CLASS));
                        return "redirect:/admin/ticket/view/" + ticketId;
                    }
                }

                case "MarkAsRead" -> {
                    try {
                        this.ticketService.markAsRead(this.ticketService.getTicket(ticketId).join());
                    } catch (Exception e) {
                        httpSession.setAttribute("status", new Alert("Unable to mark the ticket as read, please try later.", Alert.ERROR, Alert.ERROR_CLASS));
                        return "redirect:/admin/ticket/view/"+ticketId;
                    }

                }

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
                     this.ticketService.uncloseTicket(ticketId, this.ticketService.getTicket(ticketId).join().getUser().getId());
                     httpSession.setAttribute("status", new Alert("Ticket unclosed successfully.", Alert.SUCCESS, Alert.SUCCESS_CLASS));
                     return "redirect:/me/support/ticket/view/"+ticketId;
                   } catch (Exception e) {
                       httpSession.setAttribute("status", new Alert("Sorry, we were unable to un-close the ticket at this moment, please try later.", Alert.ERROR, Alert.ERROR_CLASS));
                       return "redirect:/me/support/ticket/view/"+ticketId;
                   }
                }

                default -> {
                    httpSession.setAttribute("status", new Alert("Invalid request or action.", Alert.ERROR, Alert.ERROR_CLASS));
                    return "redirect:/admin/ticket/view/" + ticketId;
                }
            }
        }

        Ticket ticket = null;
        List<TicketReply> ticketReplies = null;

        try {
        ticket = this.ticketService.getTicket(ticketId).join();
        ticketReplies = this.ticketService.getTicketRepliesByTicket(ticket.getId()).join();
        } catch (Exception e) {
            httpSession.setAttribute("status", new Alert("Ticket not found", Alert.ERROR, Alert.ERROR_CLASS));
            return "redirect:/admin/tickets";
        }

        m.addAttribute("ticketReplies", ticketReplies);
        m.addAttribute("ticket", ticket);
        return "admin/ticket_module/ticket";
    }

    @PostMapping("/process-reply")
    public String processAdminReply(@RequestParam Integer ticketId, @RequestParam String replyMessage, Principal principal) {

        if(ticketId == null) {
            httpSession.setAttribute("status", new Alert("Sorry, cannot find the ticket, it may doesn't exist or deleted.", Alert.ERROR, Alert.ERROR_CLASS));
            return "redirect:/admin/tickets";
        }

        if(replyMessage == null || replyMessage.isBlank()) {
            httpSession.setAttribute("status", new Alert("Message is required.", Alert.ERROR, Alert.ERROR_CLASS));
            return "redirect:/admin/ticket/view/"+ticketId;
        }

        try {

            // get user whom submitted that ticket.
            User userWhomSubmittedTicket = this.ticketService.getTicket(ticketId).join().getUser();

            // get Loggedin Admin user to set the sender.
            User currentLoggedInAdmin = getLoggedInUser(principal);

            Ticket ticket = this.ticketService.getTicketByUser(ticketId, userWhomSubmittedTicket.getId()).join();

            TicketReply ticketReply = new TicketReply();
            ticketReply.setMessage(replyMessage);
            ticketReply.setSender(currentLoggedInAdmin);
            ticketReply.setTicket(ticket);

            ticket.setStatus("Admin Response Received.");

            ticketReply = this.ticketService.createTicketReply(ticketReply, ticket).join();
            this.ticketService.updateTicket(ticket, ticket.getId());

            // if success.
            httpSession.setAttribute("status", new Alert("Message added successfully.", Alert.SUCCESS, Alert.SUCCESS_CLASS));

        } catch (Exception e) {
            if (e instanceof ResourceNotFound && e.getMessage().contains("Ticket not found")) {
                httpSession.setAttribute("status", new Alert("Sorry, cannot submit the reply to the ticket, ticket doesn't exist or deleted.", Alert.ERROR, Alert.ERROR_CLASS));
                return "redirect:/me/support/ticket/view/" + ticketId;
            }
            httpSession.setAttribute("status", new Alert("Ahh sorry, cannot submitted the ticket, please try later.", Alert.ERROR, Alert.ERROR_CLASS));
        }

             return "redirect:/admin/ticket/view/"+ticketId;
    }
    
}
