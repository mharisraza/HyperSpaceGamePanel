package com.hyperspacegamepanel.models.ticket;

import com.hyperspacegamepanel.models.user.User;
import com.hyperspacegamepanel.utils.Constants;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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

    @NotNull(message = "Please choose one server at least.")
    private Integer serverId;

    @NotBlank(message = "Priority is required.")
    private String priority;

    @NotBlank(message = "Message is required.")
    @Size(max = Constants.TICKET_MESSAGE_SIZE, message = Constants.TICKET_MAXIMUM_MESSAGE_ERROR)
    private String message;

    private Date submittedDate = new Date();

    private boolean isClosed = false;

    private String closedBy;

    private String status = "Pending Admin Response";

    private boolean isRead = false;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "ticket", cascade = CascadeType.ALL)
    private List<TicketReply> replies = new ArrayList<>();
    
}
