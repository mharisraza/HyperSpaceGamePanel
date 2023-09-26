package com.hyperspacegamepanel.services.impl;

import com.hyperspacegamepanel.models.machine.Machine;
import com.hyperspacegamepanel.services.MachineConnectorService;
import com.hyperspacegamepanel.utils.Constants;
import com.jcraft.jsch.*;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.InputStream;
import java.util.Scanner;
import java.util.concurrent.CompletableFuture;

@Service
public class MachineConnectorServiceImpl implements MachineConnectorService {

    // keep it in threadLocal to avoid overridability of session if multiple request made at the same time.
    private ThreadLocal<Session> session = new ThreadLocal<>();
    private ChannelSftp sftpChannel;

    @Override
    @Async
    public CompletableFuture<Void> connect(Machine machine, String plainTextMachinePassword) {
        try {

           JSch jsch = new JSch();
           Session session = jsch.getSession(machine.getUsername(), machine.getIpAddress(), machine.getPort());

           session = jsch.getSession(machine.getUsername(), machine.getIpAddress(), machine.getPort());
           session.setPassword(plainTextMachinePassword);
           session.setConfig("StrictHostKeyChecking", "no");
           session.connect();
           setSession(session);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return CompletableFuture.completedFuture(null);
    }

    @Override
    @Async
    public CompletableFuture<String> executeCommand(String command) {
        String result = null;
        try {
            ChannelExec channel = (ChannelExec) getSession().openChannel("exec");
            channel.setCommand(command);
            channel.setInputStream(null);
            channel.setErrStream(System.err);
            InputStream inputStream = channel.getInputStream();
            channel.connect();

            Scanner scanner = new Scanner(inputStream).useDelimiter("\\A");
            result = scanner.hasNext() ? scanner.next() : null;
            scanner.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return CompletableFuture.completedFuture(result);
    }

    @Override
    @Async
    public CompletableFuture<Void> executeCommandWithoutOutput(String command) {
        try {
            ChannelExec channel = (ChannelExec) getSession().openChannel("exec");
            channel.setCommand(command);
            channel.connect();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return CompletableFuture.completedFuture(null);
    }

    @Override
    @Async
    public CompletableFuture<Boolean> isConnected(Machine machine) {
        if(getSession() != null && getSession().isConnected() && getSession().getHost().equals(machine.getIpAddress())) {
            return CompletableFuture.completedFuture(true);
        }
        return CompletableFuture.completedFuture(false);
    }

    @Override
    @Async
    public CompletableFuture<Void> uploadFileToMachine(String localScriptFilePath) {
        try {

            File scriptFile = new File(localScriptFilePath);

            if(!scriptFile.exists()) {
                 throw new RuntimeException("SCRIPT_FILE_DOESNT_EXISTS");
            }

            // our scripts should be under /scripts folder in every machine to understand
            String remoteScriptFilePath = String.format("/%s/%s", Constants.REMOTE_SCRIPTS_FOLDER, scriptFile.getName());

            Channel channel = getSession().openChannel("sftp");
            channel.connect();
            this.sftpChannel = (ChannelSftp) channel;

            // returning if file already exist in vps
            if(isFileExist(remoteScriptFilePath).get()) {
                  return CompletableFuture.completedFuture(null);
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
        return null;
    }

     // returns true if file exist, false if doesn't
     private CompletableFuture<Boolean> isFileExist(String remoteFilePath) {
        try {
            SftpATTRS attrs = this.sftpChannel.stat(remoteFilePath);
            return CompletableFuture.completedFuture(attrs != null);
        } catch (Exception e) {
            return CompletableFuture.completedFuture(false);
        }
    }

     private void setSession(Session session) {
        this.session.set(session);
    }

    private Session getSession() {
        return this.session.get();
    }
    
}
