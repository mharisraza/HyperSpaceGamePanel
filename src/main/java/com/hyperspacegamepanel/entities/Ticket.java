package com.hyperspacegamepanel.entities;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.hyperspacegamepanel.helper.Constants;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity(name = "tickets")
@Getter
@Setter
@ToString
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NotBlank(message = "Subject is required.")
    private String subject;

    @NotNull(message = "Please choose one server atleast.")
    private Integer serverId;

    @NotBlank(message = "Priority is required.")
    private String priority;

    @NotBlank(message = "Message is required.")
    @Size(max = Constants.TICKET_MESSAGE_SIZE, message = Constants.TICKET_MAXIMUM_MESSAGE_ERROR)
    private String message;

    private Date submittedDate;

    private boolean isClosed = false;

    private String closedBy;

    private String status = "Pending Admin Response";

    private boolean isRead = false;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "ticket")
    private List<TicketReply> replies = new ArrayList<>();
    
}
