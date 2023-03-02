package com.hyperspacegamepanel.services.impl;


import java.util.HashMap;
import java.util.Map;


import org.springframework.cache.annotation.Cacheable;

import com.hyperspacegamepanel.entities.Machine;
import com.hyperspacegamepanel.helper.Constants;
import com.hyperspacegamepanel.helper.PasswordEncoder;
import com.hyperspacegamepanel.helper.VPSConnector;
import com.hyperspacegamepanel.services.VPSService;

public class VPSServiceImpl implements VPSService {

    private final VPSConnector connector;
    private final Machine machine;


    public VPSServiceImpl(VPSConnector connector, Machine machine) {
        this.connector = connector;
        this.machine = machine;

        // decrypting password.
        this.machine.setPassword(PasswordEncoder.decrypt(this.machine.getPassword()));
        

    // By calling the connectIfNotConnected() method here, we avoid the need to call it
    // manually in each method. This helps to reduce redundancy and improve code maintainability.
    connectIfNotConnected();
    }

    @Override
    public void restartMachine() {
        connector.executeCommand("reboot");
    }

    @Override
    public void updateHostname(String hostname) {
        connector.executeCommand("hostnamectl set-hostname "+hostname);
    }

    @Override
    @Cacheable(value = "machineInfoCache", key = "machineInfo")
    public Map<String, String> getMachineInfo() {
        connector.uploadFile(Constants.SCRIPTS_FILES.get("VPS_INFO_SCRIPT"));
        String output = connector.executeCommand("bash /scripts/getvpsinfo.sh");
        Map<String, String> machineInfo = new HashMap<>();
        String[] lines = output.split("\n");
        for(String line : lines) {
            String[] parts = line.split("=");
            if(parts.length == 2) {
                machineInfo.put(parts[0], parts[1].trim());
            }
        }
        return machineInfo;
    }
    
    private void connectIfNotConnected() {
        try {
            if(!this.connector.isConnected()) {
                connector.connect(machine);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


  
}
