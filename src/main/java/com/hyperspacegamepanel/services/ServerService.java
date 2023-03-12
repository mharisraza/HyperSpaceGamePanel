package com.hyperspacegamepanel.services;

import com.hyperspacegamepanel.entities.Machine;
import com.hyperspacegamepanel.entities.Server;
import com.hyperspacegamepanel.entities.User;

public interface ServerService {

    Server createServer(Server server, Machine machine, User owner) throws Exception;
    Server updateServer(Server server);

    void startServer(Server server);
    void stopServer(Server server);
    
}
