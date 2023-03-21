package com.hyperspacegamepanel.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hyperspacegamepanel.models.ticket.Ticket;
import com.hyperspacegamepanel.repositories.TicketRepository;
import com.hyperspacegamepanel.services.TicketService;

@Service
public class TicketServiceImpl implements TicketService {

    @Autowired
    private TicketRepository ticketRepo;

    @Override
    public Ticket createTicket(Ticket ticket) {
        this.ticketRepo.save(ticket);
        return ticket;
    }
    
}
