package com.hyperspacegamepanel.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hyperspacegamepanel.entities.MachineDetails;

public interface MachineDetailsRepository extends JpaRepository<MachineDetails, Integer> {
    
}
