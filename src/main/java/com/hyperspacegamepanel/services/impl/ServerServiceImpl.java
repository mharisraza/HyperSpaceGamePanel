package com.hyperspacegamepanel.services.impl;

import java.util.Date;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hyperspacegamepanel.entities.Machine;
import com.hyperspacegamepanel.entities.Server;
import com.hyperspacegamepanel.entities.User;
import com.hyperspacegamepanel.helper.VPSConnector;
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
    
}
