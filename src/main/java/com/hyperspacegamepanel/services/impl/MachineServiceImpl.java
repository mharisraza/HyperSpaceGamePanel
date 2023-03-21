package com.hyperspacegamepanel.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hyperspacegamepanel.helper.PasswordEncoder;
import com.hyperspacegamepanel.models.machine.Machine;
import com.hyperspacegamepanel.repositories.MachineRepository;
import com.hyperspacegamepanel.services.MachineService;

@Service
public class MachineServiceImpl implements MachineService {

    @Autowired
    private MachineRepository machineRepo;

    @Override
    public Machine createMachine(Machine machine) {
        machine.setPassword(PasswordEncoder.encrypt(machine.getPassword()));
      return this.machineRepo.save(machine);
    }
    
}
