package com.hyperspacegamepanel.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import com.hyperspacegamepanel.entities.MachineDetails;

@Transactional
public interface MachineDetailsRepository extends JpaRepository<MachineDetails, Integer> {
    
}
