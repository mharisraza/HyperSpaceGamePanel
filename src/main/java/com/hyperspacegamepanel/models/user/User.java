package com.hyperspacegamepanel.models.user;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

import com.hyperspacegamepanel.models.server.Server;
import com.hyperspacegamepanel.models.ticket.Ticket;
import com.hyperspacegamepanel.models.ticket.TicketReply;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "users")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "full_name", nullable = false)
    @NotBlank(message = "Full Name is required")
    private String fullName;

    @Column(name = "email", unique = true, nullable = false)
    @NotBlank(message = "Email is required")
    @Email
    private String email;

    @Column(name = "username", unique = true, nullable = false)
    @NotBlank(message = "Username is required.")
    private String username;

    @Column(name = "password", nullable = false)
    @NotBlank(message = "Password is required.")
    private String password;

    @Transient
    private String confirmPassword;

    @Column(name = "role", nullable = false)
    private String role;

    @Column(name = "user_suspension_status", nullable = false)
    private boolean isEnabled = true;

    @Column(name = "user_verify", nullable = false)
    private boolean isVerified = false;

    @Column(name = "registered_date", nullable = false)
    private Date registeredDate = new Date();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Ticket> tickets;

    @OneToMany(mappedBy = "sender", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TicketReply> ticketReplies = new ArrayList<>();

    @OneToMany(mappedBy = "owner", orphanRemoval = true)
    private List<Server> servers;

    public static final String ROLE_USER = "ROLE_USER";
    public static final String ROLE_ADMIN = "ROLE_ADMIN";

    
}
