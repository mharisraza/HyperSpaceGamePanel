package com.hyperspacegamepanel.services;

public interface VPSService {
    
    String getTotalRam();
    String getTotalStorage();
    String getCPUProcessor();
    String getUptime();
    String getTotalCPUs();
    String getHostName();
    String getLocation();

    // perfom actions on the machine
    void restartMachine();
    void updateHostname(String hostname);
    
    boolean isMachineOnline();


}
