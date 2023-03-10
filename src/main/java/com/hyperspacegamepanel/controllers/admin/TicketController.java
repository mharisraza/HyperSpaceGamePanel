package com.hyperspacegamepanel.controllers.admin;

import java.security.Principal;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.hyperspacegamepanel.controllers.main.HelperController;
import com.hyperspacegamepanel.entities.Ticket;
import com.hyperspacegamepanel.entities.TicketReply;
import com.hyperspacegamepanel.entities.User;
import com.hyperspacegamepanel.repositories.TicketReplyRepository;
import com.hyperspacegamepanel.repositories.TicketRepository;
import com.hyperspacegamepanel.services.TicketReplyService;

@Controller
@RequestMapping("/admin/ticket")
public class TicketController extends HelperController {

    @Autowired
    private HttpSession httpSession;

    @Autowired
    private TicketRepository ticketRepo;

    @Autowired
    private TicketReplyService ticketReplyService;

    @Autowired
    private TicketReplyRepository ticketReplyRepo;



    @GetMapping("/view/{ticketId}")
    public String showTicket(@PathVariable(required = false) Integer ticketId, @RequestParam(required = false) String action, @RequestParam(required =  false) Integer messageId, Model m) {
        if(ticketId == null) {
            httpSession.setAttribute("status", "CANT_FIND_TICKET");
            return "redirect:/admin/tickets";
        }

        Optional<Ticket> ticket = this.ticketRepo.findById(ticketId);
        if(!ticket.isPresent()) {
            httpSession.setAttribute("status", "CANT_FIND_TICKET");
            return "redirect:/admin/tickets";
        }

        List<TicketReply> ticketReplies = this.ticketReplyRepo.findAll();

        // delete reply message
        if(action != null && action.equals("deleteMessage")) {
            if(messageId == null) {
                return "redirect:/admin/tickets";
            }

            Optional<TicketReply> ticketReply = this.ticketReplyRepo.findById(messageId);

            if(!ticketReply.isPresent()) {
                httpSession.setAttribute("status", "TICKET_REPLY_DOES_NOT_EXIST");
                return "redirect:/admin/ticket/view/" + ticketId;
            }

             this.ticketReplyRepo.deleteById(messageId);
             httpSession.setAttribute("status", "REPLY_MESSAGE_DELETED_SUCCESSFULLY");
            return "redirect:/admin/ticket/view/" + ticketId;

        }

        if(action != null && action.equals("MarkAsRead")) {
            ticket.get().setRead(true);
            this.ticketRepo.save(ticket.get());
            return "redirect:/admin/ticket/view/" + ticketId;
        }
        

        if(action != null && action.equals("closeTicket")) {
                ticket.get().setClosed(true);
                ticket.get().setClosedBy("ADMIN");
                ticket.get().setStatus("Closed by Admin");
                this.ticketRepo.save(ticket.get());

                httpSession.setAttribute("status", "TICKET_CLOSED_SUCCESSFULLY");
                return "redirect:/admin/ticket/view/" + ticketId;
        }

        if(action != null && action.equals("uncloseTicket")) {
            ticket.get().setClosed(false);
            this.ticketRepo.save(ticket.get());

            httpSession.setAttribute("status", "TICKET_UNCLOSED_SUCCSESFULLY");
            return "redirect:/admin/ticket/view/" + ticketId;
        }

        m.addAttribute("ticketReplies", ticketReplies);
        m.addAttribute("ticket", ticket.get());
        return "admin/ticket_module/ticket";
    }

    @PostMapping("/process-reply")
    public String processAdminReply(@RequestParam Integer ticketId, @RequestParam String replyMessage, Principal principal) {

        if(ticketId == null) {
            httpSession.setAttribute("status", "CANT_FIND_TICKET");
            return "redirect:/admin/tickets";
        }

        if(replyMessage == null || replyMessage.isBlank()) {
            httpSession.setAttribute("status", "REPLY[MESSAGE]_IS_EMPTY");
            return "redirect:/admin/ticket/view/"+ticketId;
        }

        Optional<Ticket> ticket = this.ticketRepo.findById(ticketId);
        if(!ticket.isPresent()) {
            httpSession.setAttribute("status", "CANT_FIND_TICKET");
            return "redirect:/admin/tickets";
        }

         User user = getLoggedInUser(principal);


         TicketReply ticketReply = new TicketReply();
         ticketReply.setRepliedDate(new Date());
         ticketReply.setTicket(ticket.get());
         ticketReply.setSender(user);
         ticketReply.setMessage(replyMessage);
         ticket.get().setStatus("Admin Response Received");

         this.ticketReplyService.createTicketReply(ticketReply);
         this.ticketRepo.save(ticket.get());

             return "redirect:/admin/ticket/view/"+ticketId;
    }
    
}
