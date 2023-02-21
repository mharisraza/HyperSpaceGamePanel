package com.hyperspacegamepanel.services;

public interface VPSService {
    
    String getTotalRam();
    String getTotalStorage();
    String getCPUProcessor();
    String getUptime();
    String getTotalCPUs();
    String getHostName();
    String getLocation();
    
    boolean isMachineOnline();


}
