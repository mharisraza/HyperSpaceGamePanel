package com.hyperspacegamepanel.services.impl;

import java.util.Date;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hyperspacegamepanel.helper.VPSConnector;
import com.hyperspacegamepanel.models.machine.Machine;
import com.hyperspacegamepanel.models.server.Server;
import com.hyperspacegamepanel.models.user.User;
import com.hyperspacegamepanel.repositories.ServerRepository;
import com.hyperspacegamepanel.services.ServerService;
import com.hyperspacegamepanel.services.VPSService;

@Service
public class ServerServiceImpl implements ServerService {

    @Autowired
    private ServerRepository serverRepo;

    @Autowired
    private VPSConnector connector;

    @Override
    public Server createServer(Server server, Machine machine, User owner) throws Exception {

        Date currentDate = new Date();
        server.setCreatedDate(currentDate);
        server.setMachine(machine);
        server.setIpAddress(machine.getIpAddress()+":"+server.getPort());
        server.setOwner(owner);

        Date expirationDate = server.getExpirationDate();

        if(expirationDate.before(currentDate) || expirationDate.equals(currentDate)) {
            throw new Exception("EXPIRATON_DATE_IS_WRONG");
        }

        server = this.serverRepo.save(server);

        String response = null;

        try {
            VPSService vpsService = new VPSServiceImpl(connector, machine);
            response = vpsService.createGameServer(owner, server);                    
        } catch(Exception e) {
            e.printStackTrace();
        }

        if(response != "GAME_SERVER_CREATED_SUCCESSFULLY") {
            this.serverRepo.delete(server);
            return null;
        }

        return server;
    }

    @Override
    public Server updateServer(Server server) {
        return null;
    }
    
    @Override
    public void startServer(Server server) {
        try {
           VPSServiceImpl vpsService = new VPSServiceImpl(connector, server.getMachine());
           vpsService.connectIfNotConnected();
        } catch (Exception e) {
            e.printStackTrace();
        }
       if(server.getGameType().equals("cs")) {
        connector.executeCommandWithoutOutput(String.format("screen -dmS %s sh -c 'cd /home/game-servers/%s/%s/game_files && chmod +x hlds_run && chmod +x hlds_linux && ./hlds_run -game cstrike +ip %s +port %s +maxplayers %s +map de_dust2' > /dev/null 2>&1", server.getGameType()+"_"+server.getId(), server.getGameType(), server.getId(), server.getMachine().getIpAddress(), server.getPort(), server.getSlots()));
       }
    }

    @Override
    public void stopServer(Server server) {
        try {
            VPSServiceImpl vpsService = new VPSServiceImpl(connector, server.getMachine());
            vpsService.connectIfNotConnected();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(server.getGameType().equals("cs")) {
            connector.executeCommandWithoutOutput(String.format("screen -S cs_%d -X quit", server.getId()));
        }
    }

    
}
