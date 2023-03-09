package com.hyperspacegamepanel.entities;

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.Range;
import org.springframework.format.annotation.DateTimeFormat;

import lombok.Data;
import lombok.ToString;

@Entity(name = "servers")
@Data
@ToString
public class Server {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull
    @Range(min = 2000, max = 65535, message = "Minimum 200 and Maximum 65535 is allowed")
    private Integer port;
    @NotNull
    @Range(min = 10, max = 32, message = "Minimum 10 and Maximum 32 is allowed")
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
    private Date createdDate;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Column(nullable = false)
    private Date expirationDate;

    @ManyToOne
    private User owner;

    @ManyToOne
    @JoinColumn(name = "machine_id")
    private Machine machine;

    
}
