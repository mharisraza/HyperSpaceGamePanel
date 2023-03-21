package com.hyperspacegamepanel.services;

import java.util.Map;

import com.hyperspacegamepanel.models.server.Server;
import com.hyperspacegamepanel.models.user.User;

public interface VPSService {
    
    // get vps info
    Map<String, String> getMachineInfo();
    String getMachineUptime();

    // perfom actions on the machine
    void restartMachine();
    void updateHostname(String hostname);

    void configureMachine();

    String createGameServer(User serverOwner, Server server);

    



}
