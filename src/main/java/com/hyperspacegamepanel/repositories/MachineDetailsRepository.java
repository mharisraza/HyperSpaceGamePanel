package com.hyperspacegamepanel.repositories;

import com.hyperspacegamepanel.models.machine.MachineDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface MachineDetailsRepository extends JpaRepository<MachineDetails, Integer> {
    
}
