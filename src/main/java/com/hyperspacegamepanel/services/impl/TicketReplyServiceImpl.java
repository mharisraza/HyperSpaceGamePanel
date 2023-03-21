package com.hyperspacegamepanel.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hyperspacegamepanel.models.ticket.TicketReply;
import com.hyperspacegamepanel.repositories.TicketReplyRepository;
import com.hyperspacegamepanel.services.TicketReplyService;

@Service
public class TicketReplyServiceImpl implements TicketReplyService {

    @Autowired
    private TicketReplyRepository ticketReplyRepository;

    @Override
    public TicketReply createTicketReply(TicketReply ticketReply) {
        this.ticketReplyRepository.save(ticketReply);
        return ticketReply;
    }
    
}
