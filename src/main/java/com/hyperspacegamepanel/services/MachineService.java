package com.hyperspacegamepanel.services;

import com.hyperspacegamepanel.models.machine.Machine;
import com.hyperspacegamepanel.models.machine.MachineUpdateForm;
import com.hyperspacegamepanel.models.server.Server;
import com.hyperspacegamepanel.models.user.User;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public interface MachineService {

    CompletableFuture<Machine> createMachine(Machine machine) throws InterruptedException, ExecutionException;
    CompletableFuture<Machine> updateMachine(MachineUpdateForm machine, int machineId);
    CompletableFuture<Machine> getMachine(int machineId);
    CompletableFuture<Void> deleteMachine(int machineId);

    // the below methods perfom action on the machine remotely by executing shell commands
    CompletableFuture<Void> connectToMachine(Machine machine) throws InterruptedException, ExecutionException;
    CompletableFuture<Void> restartMachine(int machineId) throws InterruptedException, ExecutionException;
    CompletableFuture<Void> updateMachineHostname(String hostname, int machineId) throws InterruptedException, ExecutionException;
    CompletableFuture<Void> configureMachine(int machineId) throws InterruptedException, ExecutionException;
    

    CompletableFuture<Map<String, String>> getMachineInfo(int machineId) throws InterruptedException, ExecutionException;
    CompletableFuture<String> getMachineUptime(int machineId) throws Exception;

    // below methods are for game-servers related tasks.
    CompletableFuture<String> createGameServer(int machineId, User owner, Server server) throws InterruptedException, ExecutionException;
    
}
