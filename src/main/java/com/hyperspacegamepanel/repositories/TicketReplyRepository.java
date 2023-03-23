package com.hyperspacegamepanel.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hyperspacegamepanel.models.ticket.Ticket;
import com.hyperspacegamepanel.models.ticket.TicketReply;

public interface TicketReplyRepository extends JpaRepository<TicketReply, Integer> {

    List<TicketReply> findAllByTicket(Ticket ticket);
    
}
