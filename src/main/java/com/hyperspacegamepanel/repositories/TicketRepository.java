package com.hyperspacegamepanel.repositories;


import org.springframework.data.jpa.repository.JpaRepository;

import com.hyperspacegamepanel.entities.Ticket;


public interface TicketRepository extends JpaRepository<Ticket, Integer> {

}
