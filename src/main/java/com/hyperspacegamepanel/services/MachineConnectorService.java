package com.hyperspacegamepanel.services;

import java.util.concurrent.CompletableFuture;

import com.hyperspacegamepanel.models.machine.Machine;

public interface MachineConnectorService {

    CompletableFuture<Void> connect(Machine machine, String plainTextMachinePassword);
    CompletableFuture<Boolean> isConnected(Machine machine);

    CompletableFuture<Void> uploadFileToMachine(String localScriptFilePath);
    CompletableFuture<String> executeCommand(String command);
    CompletableFuture<Void> executeCommandWithoutOutput(String command);


    
}
