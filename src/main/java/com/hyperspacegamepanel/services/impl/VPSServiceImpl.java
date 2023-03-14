package com.hyperspacegamepanel.services.impl;


import java.util.HashMap;
import java.util.Map;


import org.springframework.cache.annotation.Cacheable;

import com.hyperspacegamepanel.entities.Machine;
import com.hyperspacegamepanel.entities.Server;
import com.hyperspacegamepanel.entities.User;
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
    @Cacheable(value = "machineInfo", key = "machineInfo")
    public Map<String, String> getMachineInfo() {
        connector.uploadFile(Constants.SCRIPTS_FILES.get("VPS_INFO_SCRIPT"));
        String output = connector.executeCommand("cd / && bash " +Constants.SCRIPTS_FILES.get("VPS_INFO_SCRIPT"));
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

    @Override
    public String getMachineUptime() {
        return connector.executeCommand("awk '{d=int($1/86400);h=int($1%86400/3600);m=int(($1%3600)/60); printf \"%d days %d hours %d minutes\\n\", d, h, m}' /proc/uptime");
    }
    
    public void connectIfNotConnected() {
        try {
            if(!this.connector.isConnected()) {
                connector.connect(machine);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public String createGameServer(User serverOwner, Server server) {
        connector.uploadFile(Constants.SCRIPTS_FILES.get("CREATE_GAME_SERVER_SCRIPT"));
        String response = connector.executeCommand(String.format("cd / && bash %s '%s' '%s' '%s' '%s'", Constants.SCRIPTS_FILES.get("CREATE_GAME_SERVER_SCRIPT"), server.getFtpUsername(), server.getFtpPassword(), server.getGameType(), server.getId()));
        return response.contains("GAME_SERVER_CREATED_SUCCESSFULLY") ? "GAME_SERVER_CREATED_SUCCESSFULLY" : "GAME_SERVER_CREATED_FAILED";
    }

    @Override
    public void configureMachine() {
        connector.uploadFile(Constants.SCRIPTS_FILES.get("MACHINE_CONFIGURE_SCRIPT"));
        connector.executeCommand("cd / && bash "+Constants.SCRIPTS_FILES.get("MACHINE_CONFIGURE_SCRIPT"));
    }
  
}
