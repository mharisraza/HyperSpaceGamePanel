package com.hyperspacegamepanel.models.machine;

import lombok.Data;

import jakarta.persistence.*;

@Entity(name = "machine_details")
@Data
public class MachineDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String location;
    private String totalStorage;
    private String cpuProcessor;
    private String totalRam;
    private String hostname;
    private Integer totalCPUs;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "machine_id")
    private Machine machine;
    
    
}
