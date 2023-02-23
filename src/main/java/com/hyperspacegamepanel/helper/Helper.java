package com.hyperspacegamepanel.helper;

import java.net.InetAddress;

public class Helper {

    // here are those methods that use globally without any dependencies whether via thymeleaf or server-side.

    public boolean isMachineOnline(String ipAddress) {
        boolean isReachable = false;
        try {
            InetAddress address = InetAddress.getByName(ipAddress);
            isReachable = address.isReachable(5000); // Time out in milliseconds (5 seconds here)

        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return isReachable;
    }
    
}
