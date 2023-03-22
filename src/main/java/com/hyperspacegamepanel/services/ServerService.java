package com.hyperspacegamepanel.services;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import com.hyperspacegamepanel.models.machine.Machine;
import com.hyperspacegamepanel.models.server.Server;
import com.hyperspacegamepanel.models.server.ServerUpdateForm;
import com.hyperspacegamepanel.models.user.User;

public interface ServerService {

    CompletableFuture<Server> createServer(Server server, Machine machine, User owner) throws Exception;
    CompletableFuture<Server> updateServer(ServerUpdateForm updateServer, int serverId) throws InterruptedException, ExecutionException, IllegalArgumentException, IllegalAccessException;
    CompletableFuture<Server> getServer(int serverId);
    CompletableFuture<Void> deleteServer(int serverId);

    CompletableFuture<List<Server>> getUserServers(int userId) throws InterruptedException, ExecutionException;

    CompletableFuture<Void> startServer(Server server);
    CompletableFuture<Void> stopServer(Server server);
    CompletableFuture<Void> restartServer(Server server);


    
}
