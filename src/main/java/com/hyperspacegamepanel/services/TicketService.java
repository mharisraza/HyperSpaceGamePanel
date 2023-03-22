package com.hyperspacegamepanel.services;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import com.hyperspacegamepanel.models.ticket.Ticket;

public interface TicketService {

    CompletableFuture<Ticket> createTicket(Ticket ticket);
    CompletableFuture<Ticket> getTicket(int ticketId);
    CompletableFuture<Void> deleteTicket(int ticketId);

    CompletableFuture<List<Ticket>> getAllTickets();
    CompletableFuture<List<Ticket>> getAllTicketsOfUser(int userId) throws InterruptedException, ExecutionException;
    CompletableFuture<List<Ticket>> getUnreadTickets();
    
    
}
