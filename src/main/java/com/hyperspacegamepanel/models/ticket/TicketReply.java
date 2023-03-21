package com.hyperspacegamepanel.models.ticket;

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.Size;

import com.hyperspacegamepanel.helper.Constants;
import com.hyperspacegamepanel.models.user.User;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

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

    private Date repliedDate;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "ticket_id")
    private Ticket ticket;

    @ManyToOne
    private User sender;
    
}
