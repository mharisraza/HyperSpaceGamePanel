package com.hyperspacegamepanel.controllers.restcontrollers;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.github.koraktor.steamcondenser.steam.servers.GoldSrcServer;
import com.hyperspacegamepanel.helper.ServerInfo;
import com.hyperspacegamepanel.models.server.Server;
import com.hyperspacegamepanel.repositories.ServerRepository;
import com.hyperspacegamepanel.services.MachineService;


@RestController
public class RestAPIController {

    @Autowired
    private ServerRepository serverRepo;

    @Autowired
    private MachineService machineService;

    // fetching vps uptime
    @GetMapping("/getVpsUptime")
    @PreAuthorize("hasRole('ADMIN')") // implies that user that requesting this should be 'ADMIN' otherwise it'll not return the result.
    public ResponseEntity<?> getUptime(@RequestParam Integer machineId) throws InterruptedException, ExecutionException {

        if(machineId == null) {
            return ResponseEntity.badRequest().body("Machine id is shouldn't be empty");
        }

       try {
          String machineUptime = this.machineService.getMachineUptime(machineId).join();
          return ResponseEntity.ok(machineUptime);
       } catch (Exception e) {
            if(e.getMessage().contains("Machine not found")) {
                return ResponseEntity.badRequest().body("Machine not found");
            }
       }
       return ResponseEntity.internalServerError().body("Something went wrong.");
    }

    // querying server for real-time server data.
    @GetMapping("/serverInfo")
    @PreAuthorize("hasRole('ADMIN') || hasRole('USER')") // implies that user should login to request the server info.
    public ResponseEntity<?> getServerInfo(@RequestParam Integer serverId) {

        if(serverId == null) {
            return ResponseEntity.badRequest().body("Server id shouldn't be empty");
        }

       Optional<Server> server  = this.serverRepo.findById(serverId);

       if(!server.isPresent()) {
        return ResponseEntity.badRequest().body("Server id shouldn't be manipulated");
       }

       ServerInfo serverInfo = new ServerInfo();

       String ipAddress[] = server.get().getIpAddress().split(":");
       Integer port = Integer.parseInt(ipAddress[1]);

       try {

        GoldSrcServer srcServer = new GoldSrcServer(ipAddress[0], port);
        srcServer.initialize();

        Map<String, Object> srcServerInfo = srcServer.getServerInfo();

        serverInfo.setMapName((String)srcServerInfo.get("mapName"));
        serverInfo.setPlayers(Byte.toString((Byte)srcServerInfo.get("numberOfPlayers")));
        serverInfo.setMaxPlayers(Byte.toString((Byte)srcServerInfo.get("maxPlayers")));
        
        if(srcServer.getPing() > 1) {
            serverInfo.setServerOnline(true);
        }
       } catch (Exception e) {
        e.printStackTrace();
       }
        return ResponseEntity.ok(serverInfo);
    }

    // getting server ping
    public static int getServerPing(Server server) {
        int ping = 0;

        String ipAddress[] = server.getIpAddress().split(":");
        Integer port = Integer.parseInt(ipAddress[1]);

        try {
            GoldSrcServer srcServer = new GoldSrcServer(ipAddress[0], port);

            if(srcServer.getPing() > 0) {
                ping = srcServer.getPing();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ping;
    }



    
}
