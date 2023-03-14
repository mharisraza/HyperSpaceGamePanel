package com.hyperspacegamepanel.config.restcontrollers;

import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.github.koraktor.steamcondenser.steam.servers.GoldSrcServer;
import com.hyperspacegamepanel.entities.Machine;
import com.hyperspacegamepanel.entities.Server;
import com.hyperspacegamepanel.helper.ServerInfo;
import com.hyperspacegamepanel.helper.VPSConnector;
import com.hyperspacegamepanel.repositories.MachineRepository;
import com.hyperspacegamepanel.repositories.ServerRepository;
import com.hyperspacegamepanel.services.VPSService;
import com.hyperspacegamepanel.services.impl.VPSServiceImpl;

@RestController
public class RestAPIController {

    @Autowired
    private ServerRepository serverRepo;

    @Autowired
    private MachineRepository machineRepo;

    @Autowired
    private VPSConnector connector;

    // fetching vps uptime
    @GetMapping("/getVpsUptime")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getUptime(@RequestParam Integer machineId) {

        if(machineId == null) {
            return ResponseEntity.badRequest().body("Machine id is shouldn't be empty");
        }

        Optional<Machine> machine = this.machineRepo.findById(machineId);

        if(!machine.isPresent()) {
            return ResponseEntity.badRequest().body("Machine id shouldn't be manipulated");
        }

        VPSService vpsService = new VPSServiceImpl(this.connector, machine.get());

         String uptime = vpsService.getMachineUptime();
         return ResponseEntity.ok(uptime);
    }

    // querying server for real-time server data.
    @GetMapping("/serverInfo")
    @PreAuthorize("hasRole('ADMIN')")
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
