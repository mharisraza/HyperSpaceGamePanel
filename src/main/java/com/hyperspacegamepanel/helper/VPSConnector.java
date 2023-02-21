package com.hyperspacegamepanel.helper;

import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

import org.springframework.stereotype.Service;

import com.hyperspacegamepanel.entities.Machine;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

@Service
public class VPSConnector {
    
    private Session session;

    public void connect(Machine machine) {
        try {

        JSch jsch = new JSch();
        session = jsch.getSession(machine.getUsername(), machine.getIpAddress(), machine.getPort());
        session.setPassword(machine.getPassword());
        session.setConfig("StrictHostKeyChecking", "no");
        session.connect();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void disconnect() {
        if(session != null && session.isConnected()) {
            session.disconnect();
        }
    }

    public boolean isConnected() {
        if(session != null && session.isConnected()) {
            return true;
        }
        return false;
    }

    public String executeCommand(String command) {
       String result = "";
       try {
        ChannelExec channel = (ChannelExec) session.openChannel("exec");
        channel.setCommand(command);
        channel.setInputStream(null);
        channel.setErrStream(System.err);
        InputStream in = channel.getInputStream();
        channel.connect();

        Scanner scanner = new Scanner(in).useDelimiter("\\A");
        result = scanner.hasNext() ? scanner.next() : "";

       } catch (Exception e) {
        e.printStackTrace();
       }

        return result;
    }
    
    
}
