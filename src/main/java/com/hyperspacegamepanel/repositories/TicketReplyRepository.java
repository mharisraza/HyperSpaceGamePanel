package com.hyperspacegamepanel.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hyperspacegamepanel.models.ticket.TicketReply;

public interface TicketReplyRepository extends JpaRepository<TicketReply, Integer> {
    
}
