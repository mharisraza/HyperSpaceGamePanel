package com.hyperspacegamepanel.models.ticket;

import com.hyperspacegamepanel.models.user.User;
import com.hyperspacegamepanel.utils.Constants;
import lombok.*;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import java.util.Date;

@Entity(name = "ticket_replies")
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class TicketReply {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Size(max = Constants.TICKET_MESSAGE_SIZE)
    private String message;

    private Date repliedDate = new Date();

    @ManyToOne
    @JoinColumn(name = "ticket_id")
    private Ticket ticket;

    @ManyToOne
    private User sender;
    
}
