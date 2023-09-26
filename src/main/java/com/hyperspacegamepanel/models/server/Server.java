package com.hyperspacegamepanel.models.server;

import com.hyperspacegamepanel.models.machine.Machine;
import com.hyperspacegamepanel.models.user.User;
import lombok.Data;
import lombok.ToString;
import org.hibernate.validator.constraints.Range;
import org.springframework.format.annotation.DateTimeFormat;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.Date;

@Entity(name = "servers")
@Data
@ToString
public class Server {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull
    @Range(min = 2000, max = 65535, message = "Minimum 2000 and Maximum 65535 is allowed")
    @Column(name = "server_port", unique = true, nullable = false)
    private Integer port;

    @NotNull
    @Range(min = 10, message = "Minimum 10 is allowed")
    private Integer slots;

    @NotBlank(message = "Server Name is required.")
    private String name;

    @NotBlank(message = "Server Game type is required.")
    private String gameType;

    @NotBlank(message = "FTP Username is required.")
    private String ftpUsername;


    @NotBlank(message = "FTP Password is required.")
    private String ftpPassword;

    @Column(nullable = false)
    private String ipAddress;

    @Column(nullable = false)
    private Date createdDate = new Date();

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Column(nullable = false)
    private Date expirationDate;

    @ManyToOne
    private User owner;

    @ManyToOne
    @JoinColumn(name = "machine_id")
    private Machine machine;
    
}
