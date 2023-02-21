package com.hyperspacegamepanel.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hyperspacegamepanel.entities.Machine;

public interface MachineRepository extends JpaRepository<Machine, Integer> {
    
}
