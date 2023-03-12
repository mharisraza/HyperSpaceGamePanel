package com.hyperspacegamepanel.controllers.main;

import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.github.koraktor.steamcondenser.steam.servers.GoldSrcServer;
import com.hyperspacegamepanel.entities.Server;
import com.hyperspacegamepanel.helper.ServerInfo;
import com.hyperspacegamepanel.repositories.ServerRepository;

@RestController
public class GameServerInfoController {

    @Autowired
    private ServerRepository serverRepo;

    @GetMapping("/serverInfo")
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
       }

        return ResponseEntity.ok(serverInfo);
    }

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
