package com.hyperspacegamepanel.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hyperspacegamepanel.entities.TicketReply;

public interface TicketReplyRepository extends JpaRepository<TicketReply, Integer> {
    
}
