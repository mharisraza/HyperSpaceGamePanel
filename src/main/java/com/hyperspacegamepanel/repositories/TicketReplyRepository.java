package com.hyperspacegamepanel.repositories;

import com.hyperspacegamepanel.models.ticket.Ticket;
import com.hyperspacegamepanel.models.ticket.TicketReply;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TicketReplyRepository extends JpaRepository<TicketReply, Integer> {

    List<TicketReply> findAllByTicket(Ticket ticket);
    
}
