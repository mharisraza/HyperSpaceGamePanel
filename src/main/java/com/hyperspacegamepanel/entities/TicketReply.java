package com.hyperspacegamepanel.entities;

import java.util.Date;
import java.util.List;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import com.hyperspacegamepanel.helper.Constants;

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

    @ManyToOne
    private Ticket ticket;

    @ManyToOne
    private User sender;
    
}
