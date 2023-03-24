package com.hyperspacegamepanel.services.impl;

import java.lang.reflect.Field;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.hyperspacegamepanel.controllers.restcontrollers.RestAPIController;
import com.hyperspacegamepanel.exceptions.ResourceNotFound;
import com.hyperspacegamepanel.gameservers.CounterStrike;
import com.hyperspacegamepanel.models.machine.Machine;
import com.hyperspacegamepanel.models.server.Server;
import com.hyperspacegamepanel.models.server.ServerUpdateForm;
import com.hyperspacegamepanel.models.user.User;
import com.hyperspacegamepanel.repositories.ServerRepository;
import com.hyperspacegamepanel.services.MachineService;
import com.hyperspacegamepanel.services.ServerService;
import com.hyperspacegamepanel.services.UserService;

@Service
public class ServerServiceImpl implements ServerService {

    @Autowired
    private ServerRepository serverRepo;

    @Autowired
    private UserService userService;

    @Autowired
    private MachineService machineService;

    @Override
    @Async
    public CompletableFuture<Server> createServer(Server server, Machine machine, User owner) throws Exception {
        server.setMachine(machine);
        server.setIpAddress(machine.getIpAddress()+":"+server.getPort());
        server.setOwner(owner);

        Date expirationDate = server.getExpirationDate();
        if(expirationDate.before(server.getCreatedDate()) || expirationDate.equals(server.getCreatedDate())) {
            throw new RuntimeException("EXPIRATION_DATE_IS_SELECTED_WRONG");
        }

        server = this.serverRepo.save(server);
        String serverCreatedResponse = "NOT_CREATED";

        try {
          serverCreatedResponse = this.machineService.createGameServer(machine.getId(), owner, server).get();
          if(!serverCreatedResponse.equals("GAME_SERVER_CREATED_SUCCESSFULLY")) {
             this.serverRepo.delete(server);
          }
        } catch (Exception e) {
            throw new RuntimeException("CANNOT_CREATE_GAME_SERVER");
        }
        return CompletableFuture.completedFuture(server);
    }


    @Override
    @Async
    public CompletableFuture<Server> updateServer(ServerUpdateForm updateServer, int serverId) throws InterruptedException, ExecutionException, IllegalArgumentException, IllegalAccessException {
         Server server = this.getServer(serverId).get();
        if(updateServer != null) {

            Field[] serverUpdatedFields = updateServer.getClass().getDeclaredFields();
            Field[] serverFields = server.getClass().getDeclaredFields();

            for(int i = 0; i < serverUpdatedFields.length; i++) {
                Field updatedField = serverUpdatedFields[i];
                for(int j = 0; j < serverFields.length; j++) {
                    Field serverField = serverFields[j];
                    if(serverField.getName().equals(updatedField.getName())) {
                        updatedField.setAccessible(true);
                        serverField.setAccessible(true);
                        serverField.set(server, updatedField.get(updateServer));
                        break;
                    }
                }
            }
        }
        return CompletableFuture.completedFuture(server);
    }

    @Override
    @Async
    public CompletableFuture<Server> getServer(int serverId) {
        return CompletableFuture.completedFuture(this.serverRepo.findById(serverId).orElseThrow(()-> new ResourceNotFound("Server", "ID"+serverId)));
    }

    @Override
    @Async
    public CompletableFuture<Void> deleteServer(int serverId) {
        this.serverRepo.findById(serverId).ifPresent((server) -> this.serverRepo.delete(server));
        return CompletableFuture.completedFuture(null);
    }

    @Override
    @Async
    public CompletableFuture<List<Server>> getUserServers(int userId) throws InterruptedException, ExecutionException {
        User user = this.userService.getUser(userId).get();
        return CompletableFuture.completedFuture(this.serverRepo.findAllByOwner(user));
    }

    @Override
    @Async
    public CompletableFuture<Void> startServer(Server server) {
        try {
            int ping = RestAPIController.getServerPing(server);
            if(ping > 1) {
                throw new RuntimeException("SERVER_IS_ALREADY_RUNNING");
            }
            this.machineService.connectToMachine(server.getMachine());
            if(server.getGameType().equals("cs")) {
                 CounterStrike.startServer(server);
            }
        } catch (Exception e) {
            throw new RuntimeException("UNABLE_TO_START_THE_SERVER");
        }
     return CompletableFuture.completedFuture(null);
    }

    @Override
    @Async
    public CompletableFuture<Void> stopServer(Server server) {
        try {
            int ping = RestAPIController.getServerPing(server);
            if(ping == 0 || ping < 1) {
                   throw new RuntimeException("SERVER_IS_ALREADY_STOPPED");
            }
            this.machineService.connectToMachine(server.getMachine());
            if(server.getGameType().equals("cs")) {
                CounterStrike.stopServer(server);
            }
        } catch (Exception e) {
            throw new RuntimeException("UNABLE_TO_STOP_THE_SERVER");
        }
        return CompletableFuture.completedFuture(null);
    }

    @Override
    @Async
    public CompletableFuture<Void> restartServer(Server server) {
        try {
            int ping = RestAPIController.getServerPing(server);
            if(ping == 0 || ping < 1) {
                throw new RuntimeException("SERVER_IS_STOPPED");
            }
            this.machineService.connectToMachine(server.getMachine());
            if(server.getGameType().equals("cs")) {
                CounterStrike.restartServer(server);
            }
        } catch (Exception e) {
            throw new RuntimeException("UNABLE_TO_RESTART_THE_SERVER");
        }
        return CompletableFuture.completedFuture(null);
    }

}
