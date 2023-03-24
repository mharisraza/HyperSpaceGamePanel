package com.hyperspacegamepanel.services.impl;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.hyperspacegamepanel.exceptions.ResourceNotFound;
import com.hyperspacegamepanel.models.ticket.Ticket;
import com.hyperspacegamepanel.models.ticket.TicketReply;
import com.hyperspacegamepanel.models.user.User;
import com.hyperspacegamepanel.repositories.TicketReplyRepository;
import com.hyperspacegamepanel.repositories.TicketRepository;
import com.hyperspacegamepanel.services.TicketService;
import com.hyperspacegamepanel.services.UserService;

@Service
public class TicketServiceImpl implements TicketService {

    @Autowired
    private TicketRepository ticketRepo;

    @Autowired
    private TicketReplyRepository ticketReplyRepo;

    @Autowired
    private UserService userService;

    @Override
    @Async
    public CompletableFuture<Ticket> createTicket(Ticket ticket, int userId) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                User user = this.userService.getUser(userId).get();
                ticket.setUser(user);
                return this.ticketRepo.save(ticket);
            } catch (Exception e) {
                throw new RuntimeException("UNABLE_TO_CREATE_THE_TICKET");
            }
        });
    }

    @Override
    @Async
    public CompletableFuture<Ticket> updateTicket(Ticket ticket, int ticketId) {
        this.getTicket(ticketId);
        try {
            this.ticketRepo.save(ticket);
        } catch (Exception e) {
            throw new RuntimeException("UNABLE_TO_UPDATE_THE_TICKET");
        }
        return CompletableFuture.completedFuture(ticket);
    }

    @Override
    @Async
    public CompletableFuture<Ticket> getTicket(int ticketId) {
        return CompletableFuture.supplyAsync(() -> this.ticketRepo.findById(ticketId)
                .orElseThrow(() -> new ResourceNotFound("Ticket", "ID" + ticketId)));
    }

    @Override
    @Async
    public CompletableFuture<Void> deleteTicket(int ticketId) {
        this.ticketRepo.findById(ticketId).ifPresent((ticket) -> this.ticketRepo.delete(ticket));
        return CompletableFuture.completedFuture(null);
    }

    @Override
    public CompletableFuture<Ticket> getTicketByUser(int ticketId, int userId)
            throws InterruptedException, ExecutionException {
        User user = this.userService.getUser(userId).get();
        return CompletableFuture.completedFuture(this.ticketRepo.findByIdAndUser(ticketId, user)
                .orElseThrow(() -> new ResourceNotFound("Ticket", "ID" + ticketId)));
    }

    @Override
    @Async
    public CompletableFuture<List<Ticket>> getAllTickets() {
        return CompletableFuture.completedFuture(this.ticketRepo.findAll());
    }

    @Override
    @Async
    public CompletableFuture<List<Ticket>> getAllTicketsOfUser(int userId)
            throws InterruptedException, ExecutionException {
        User user = this.userService.getUser(userId).get();
        return CompletableFuture.completedFuture(this.ticketRepo.findAllByUser(user));
    }

    @Override
    @Async
    public CompletableFuture<List<Ticket>> getUnreadTickets() {
        return CompletableFuture.completedFuture(this.ticketRepo.getUnReadTickets());
    }

    @Override
    @Async
    public CompletableFuture<TicketReply> geTicketReply(int ticketReplyId) {
        return CompletableFuture.completedFuture(this.ticketReplyRepo.findById(ticketReplyId)
                .orElseThrow(() -> new ResourceNotFound("TicketReply", "ID" + ticketReplyId)));
    }

    @Override
    @Async
    public CompletableFuture<TicketReply> createTicketReply(TicketReply ticketReply, Ticket ticket) {
        try {
            ticketReply = this.ticketReplyRepo.save(ticketReply);
        } catch (Exception e) {
            throw new RuntimeException("CANNOT_CREATE_THE_TICKET_REPLY");
        }
        return CompletableFuture.completedFuture(ticketReply);
    }

    @Override
    @Async
    public CompletableFuture<Void> deleteTicketReply(int ticketReplyId, boolean isAdmin) throws InterruptedException, ExecutionException {
        TicketReply ticketReply = this.geTicketReply(ticketReplyId).join();
        if(ticketReply.getSender().getRole().equals(User.ROLE_ADMIN)) {
            // checking the user is deleting the reply is admin or not, we'll just simply pass the true or false value from the controllers.
               if(!isAdmin) {
                    throw new RuntimeException("CANNOT_DELETE_THE_REPLY_MESSAGE_BECAUSE_IT_IS_SEND_BY_ADMIN");
               }
        }
        return CompletableFuture.completedFuture(null);
    }

    @Override
    @Async
    public CompletableFuture<List<TicketReply>> getTicketRepliesByTicket(int ticketId)
            throws InterruptedException, ExecutionException {
        Ticket ticket = this.getTicket(ticketId).get();
        return CompletableFuture.completedFuture(this.ticketReplyRepo.findAllByTicket(ticket));
    }

    @Override
    @Async
    public CompletableFuture<Void> closeTicket(int ticketId) throws InterruptedException, ExecutionException {
        try {
            Ticket ticket = this.getTicket(ticketId).get();
            ticket.setClosed(true);
            ticket.setStatus("Closed");

            if (ticket.getUser().getRole().equals(User.ROLE_ADMIN)) {
                ticket.setClosedBy("ADMIN");
            } else {
                ticket.setClosedBy("USER");
            }

            this.ticketRepo.save(ticket);
            return CompletableFuture.completedFuture(null);

        } catch (Exception e) {
            throw new RuntimeException("UNABLE_TO_CLOSE_THE_TICKET");
        }
    }

    @Override
    @Async
    public CompletableFuture<Void> uncloseTicket(int ticketId, int userId)
            throws InterruptedException, ExecutionException {

        Ticket ticket = this.getTicket(ticketId).get();
        User user = this.userService.getUser(userId).get();

        if (ticket.getClosedBy().equals("ADMIN") && !user.getRole().equals(User.ROLE_ADMIN)) {
            throw new RuntimeException("UNABLE_TO_UNCLOSE_THE_TICKET[REASON:TICKET_CLOSED_BY_ADMIN]");
        }

        ticket.setClosed(false);
        ticket.setStatus("Ticket recently got unclosed.");

        try {
            this.ticketRepo.save(ticket);
        } catch (Exception e) {
            throw new RuntimeException("UNABLE_TO_UNCLOSE_THE_TICKET");
        }
        return CompletableFuture.completedFuture(null);
    }

    @Override
    @Async
    public CompletableFuture<Void> markAsRead(Ticket ticket) {
       ticket.setRead(true);
       try {
        this.ticketRepo.save(ticket);
       } catch (Exception e) {
        throw new RuntimeException("UNABLE_TO_MARK_AS_READ");
       }
       return CompletableFuture.completedFuture(null);
    }
}
