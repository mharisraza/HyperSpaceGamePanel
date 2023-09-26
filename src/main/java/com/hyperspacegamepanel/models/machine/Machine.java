package com.hyperspacegamepanel.models.machine;

import com.hyperspacegamepanel.models.server.Server;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

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
    @Column(name = "machine_ip_address", unique = true, nullable = false)
    private String ipAddress;

    @NotNull
    @Column(name = "machine_connection_port", nullable = false)
    private Integer port;

    @NotBlank
    @Column(nullable = false)
    private String username;

    @NotBlank
    @Column(nullable = false)
    private String password;

    @OneToOne(mappedBy = "machine", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private MachineDetails machineInfo;

    @OneToMany(mappedBy = "machine", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Server> servers;

    
}
