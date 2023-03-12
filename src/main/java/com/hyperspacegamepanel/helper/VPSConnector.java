package com.hyperspacegamepanel.helper;

import java.io.File;
import java.io.InputStream;
import java.util.Scanner;

import org.springframework.stereotype.Service;

import com.hyperspacegamepanel.entities.Machine;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpATTRS;
import com.jcraft.jsch.SftpException;

@Service
public class VPSConnector {
    
    private Session session;
    private ChannelSftp sftpChannel;

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
        scanner.close();
        
       } catch (Exception e) {
        e.printStackTrace();
       }

        return result;
    }

    // execute comand without output
    public void executeCommandWithoutOutput(String commad) {
        try {

        ChannelExec channel = (ChannelExec) session.openChannel("exec");
        channel.setCommand(commad);
        channel.connect();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // scriptFilePath: exact path to the actual script file otherwise it can lead to runtime exception.
    public void uploadFile(String localScriptFilePath) {
        try {

            File scriptFile = new File(localScriptFilePath);

            if(!scriptFile.exists()) {
                 throw new RuntimeException("SCRIPT_FILE_DOESNT_EXISTS");
            }

            // our scripts should be under /scripts folder in every machine to understand
            String remoteScriptFilePath = String.format("/%s/%s", Constants.REMOTE_SCRIPTS_FOLDER, scriptFile.getName());

            Channel channel = session.openChannel("sftp");
            channel.connect();
            this.sftpChannel = (ChannelSftp) channel;

            // returning if file already exist in vps
            if(isFileExist(remoteScriptFilePath)) {
                  return;
            }

            try {
                sftpChannel.cd("/");
                sftpChannel.cd(Constants.REMOTE_SCRIPTS_FOLDER);
            } catch(SftpException e) {
                if(e.id == ChannelSftp.SSH_FX_NO_SUCH_FILE) {
                    sftpChannel.cd("/");
                    sftpChannel.mkdir(Constants.REMOTE_SCRIPTS_FOLDER);
                }    
            }

            // firstly we're providing our localScriptFilePath which is in our machine, and other is to upload to the remote machine
            sftpChannel.put(localScriptFilePath, remoteScriptFilePath);

        } catch (Exception e) {
           e.printStackTrace();
        }
    }

    // returns true if file exist, false if doesn't
    public boolean isFileExist(String remoteFilePath) {
        try {
            SftpATTRS attrs = this.sftpChannel.stat(remoteFilePath);
            return attrs != null;
        } catch (Exception e) {
            return false;
        }
    }
}
