package com.hyperspacegamepanel.gameservers;

import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;

import com.hyperspacegamepanel.models.server.Server;
import com.hyperspacegamepanel.services.MachineConnectorService;

public class CounterStrike {

    @Autowired
    private static MachineConnectorService machineConnector;
    
    @Async
    public static CompletableFuture<Void> startServer(Server server) {
       try {
        String command = String.format("screen -dmS %s sh -c 'cd /home/game-servers/%s/%s/game_files && chmod +x hlds_run && chmod +x hlds_linux && ./hlds_run -game cstrike +ip %s +port %s +maxplayers %s +map de_dust2' > /dev/null 2>&1", server.getGameType()+"_"+server.getId(), server.getGameType(), server.getId(), server.getMachine().getIpAddress(), server.getPort(), server.getSlots());
        machineConnector.executeCommandWithoutOutput(command);
       } catch (Exception e) {
        throw new RuntimeException("UNABLE_TO_START_CS_SERVER");
       }
       return CompletableFuture.completedFuture(null);
    }


    @Async
    public static CompletableFuture<Void> stopServer(Server server) {
        try {
            String command = String.format("screen -S cs_%d -X quit", server.getId());
            machineConnector.executeCommandWithoutOutput(command);
        } catch (Exception e) {
            throw new RuntimeException("UNABLE_TO_STOP_CS_SERVER");
        }
        return CompletableFuture.completedFuture(null);
    }

    @Async
    public static CompletableFuture<Void> restartServer(Server server) {
        try {
        String command = String.format(""); // trying to find the restartable cs server command.
        machineConnector.executeCommandWithoutOutput(command);
        } catch (Exception e) {
            throw new RuntimeException("UNABLE_TO_RESTART_THE_CS_SERVER");
        }
        return CompletableFuture.completedFuture(null);
    }
}
