package com.hyperspacegamepanel.services;

import java.util.Map;

public interface VPSService {
    
    // get vps info
    Map<String, String> getMachineInfo();

    // perfom actions on the machine
    void restartMachine();
    void updateHostname(String hostname);

    



}
