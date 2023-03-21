package com.hyperspacegamepanel.models.machine;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.hyperspacegamepanel.models.server.Server;

import lombok.Data;



@Entity(name = "machines")
@Data
public class Machine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "machine_name")
    @NotBlank
    private String name;

    @NotBlank
    @Column(name = "machine_ip_address")
    private String ipAddress;

    @NotNull
    @Column(name = "machine_connection_port")
    private Integer port;

    @NotBlank
    private String username;

    @NotBlank
    private String password;

    @OneToOne(mappedBy = "machine", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private MachineDetails machineInfo;

    @OneToMany(mappedBy = "machine", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Server> servers;

    
}
