package com.hyperspacegamepanel.services.impl;

import java.net.InetAddress;

import com.hyperspacegamepanel.entities.Machine;
import com.hyperspacegamepanel.helper.VPSConnector;
import com.hyperspacegamepanel.services.VPSService;

public class VPSServiceImpl implements VPSService {

    private final VPSConnector connector;
    private final Machine machine;



    public VPSServiceImpl(VPSConnector connector, Machine machine) {
        this.connector = connector;
        this.machine = machine;
    }

    @Override
    public String getTotalRam() {
        connectIfNotConnected();
        return connector.executeCommand("free -k | awk 'NR==2{printf \"%dGB\\n\", $2/1024/1024}'");
    }

    @Override
    public String getTotalStorage() {
        connectIfNotConnected();
        return connector.executeCommand("df -h / | awk 'NR==2{printf \"%s/%s (%s)\", $3,$2,$5}'");
    }

    @Override
    public String getCPUProcessor() {
        connectIfNotConnected();
        return connector.executeCommand("cat /proc/cpuinfo | grep 'model name' | uniq | awk -F':' '{print $2}'");
    }

    @Override
    public String getUptime() {
        connectIfNotConnected();
        return connector.executeCommand("awk '{d=int($1/86400);h=int($1%86400/3600);m=int(($1%3600)/60); printf \"%d days %d hours %d minutes\\n\", d, h, m}' /proc/uptime");
    }

    @Override
    public String getTotalCPUs() {
        connectIfNotConnected();
        return connector.executeCommand("nproc");
    }

    @Override
    public String getHostName() {
        connectIfNotConnected();
        return connector.executeCommand("hostname");
    }

    @Override
    public String getLocation() {
        connectIfNotConnected();
        return connector.executeCommand("curl https://ipapi.co/country_name");
    }

    @Override
    public boolean isMachineOnline() {
        boolean isReachable = false;
        try {
            InetAddress address = InetAddress.getByName(machine.getIpAddress());
            isReachable = address.isReachable(5000); // Time out in milliseconds (5 seconds here)

        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return isReachable;
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
