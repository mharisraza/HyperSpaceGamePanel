package com.hyperspacegamepanel.services;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import com.hyperspacegamepanel.models.ticket.Ticket;
import com.hyperspacegamepanel.models.ticket.TicketReply;

public interface TicketService {

    CompletableFuture<Ticket> createTicket(Ticket ticket, int userId);
    CompletableFuture<Ticket> updateTicket(Ticket ticket, int ticketId);
    CompletableFuture<Ticket> getTicket(int ticketId);
    CompletableFuture<Void> deleteTicket(int ticketId);

    CompletableFuture<Void> closeTicket(int ticketId) throws InterruptedException, ExecutionException;
    CompletableFuture<Void> uncloseTicket(int ticketId, int userId) throws InterruptedException, ExecutionException;

    CompletableFuture<Void> markAsRead(Ticket ticket);

    CompletableFuture<TicketReply> geTicketReply(int ticketReplyId);

    CompletableFuture<TicketReply> createTicketReply(TicketReply ticketReply, Ticket ticket);
    CompletableFuture<Void> deleteTicketReply(int ticketReplyId, boolean isAdmin) throws InterruptedException, ExecutionException;

    CompletableFuture<List<TicketReply>> getTicketRepliesByTicket(int ticketId) throws InterruptedException, ExecutionException;

    
    CompletableFuture<Ticket> getTicketByUser(int ticketId, int userId) throws InterruptedException, ExecutionException;

    CompletableFuture<List<Ticket>> getAllTickets();
    CompletableFuture<List<Ticket>> getAllTicketsOfUser(int userId) throws InterruptedException, ExecutionException;
    CompletableFuture<List<Ticket>> getUnreadTickets();
    
    
}
