package com.hyperspacegamepanel.repositories;


import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.hyperspacegamepanel.models.ticket.Ticket;
import com.hyperspacegamepanel.models.user.User;


public interface TicketRepository extends JpaRepository<Ticket, Integer> {


    @Query(value = "select * from tickets where is_read and is_closed = TRUE", nativeQuery = true)
    List<Ticket> getUnReadTickets();

    List<Ticket> findAllByUser(User user);

}
