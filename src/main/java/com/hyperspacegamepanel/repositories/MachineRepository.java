package com.hyperspacegamepanel.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import com.hyperspacegamepanel.models.machine.Machine;

@Transactional
public interface MachineRepository extends JpaRepository<Machine, Integer> {
    
}
