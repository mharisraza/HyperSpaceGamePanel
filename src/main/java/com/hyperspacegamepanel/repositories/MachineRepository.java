package com.hyperspacegamepanel.repositories;

import com.hyperspacegamepanel.models.machine.Machine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Transactional
public interface MachineRepository extends JpaRepository<Machine, Integer> {

    Optional<Machine> findByIpAddress(String ipAddress);
    
}
