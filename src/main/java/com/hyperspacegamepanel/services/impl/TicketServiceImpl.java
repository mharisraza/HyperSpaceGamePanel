package com.hyperspacegamepanel.services.impl;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.hyperspacegamepanel.exceptions.ResourceNotFound;
import com.hyperspacegamepanel.models.ticket.Ticket;
import com.hyperspacegamepanel.models.user.User;
import com.hyperspacegamepanel.repositories.TicketRepository;
import com.hyperspacegamepanel.services.TicketService;
import com.hyperspacegamepanel.services.UserService;

@Service
public class TicketServiceImpl implements TicketService {

    @Autowired
    private TicketRepository ticketRepo;

    @Autowired
    private UserService userService;

    @Override
    @Async
    public CompletableFuture<Ticket> createTicket(Ticket ticket) {
        try {
             ticket = this.ticketRepo.save(ticket);
        } catch (Exception e) {
            throw new RuntimeException("UNABLE_TO_CREATE_THE_TICKET");
        }
        return CompletableFuture.completedFuture(ticket);
    }

    @Override
    @Async
    public CompletableFuture<Ticket> getTicket(int ticketId) {
        return CompletableFuture.completedFuture( this.ticketRepo.findById(ticketId).orElseThrow(()-> new ResourceNotFound("Ticket", "ID"+ticketId)));
    }

    @Override
    @Async
    public CompletableFuture<Void> deleteTicket(int ticketId) {
        this.ticketRepo.findById(ticketId).ifPresent((ticket) -> this.ticketRepo.delete(ticket));
     return CompletableFuture.completedFuture(null);
    }

    @Override
    @Async
    public CompletableFuture<List<Ticket>> getAllTickets() {
        return CompletableFuture.completedFuture(this.ticketRepo.findAll());
    }

    @Override
    @Async
    public CompletableFuture<List<Ticket>> getAllTicketsOfUser(int userId) throws InterruptedException, ExecutionException {
        User user = this.userService.getUser(userId).get();
        return CompletableFuture.completedFuture(this.ticketRepo.findAllByUser(user));
    }

    @Override
    @Async
    public CompletableFuture<List<Ticket>> getUnreadTickets() {
        return CompletableFuture.completedFuture(this.ticketRepo.getUnReadTickets());
    }

    
    
}
