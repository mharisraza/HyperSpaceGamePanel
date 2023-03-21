package com.hyperspacegamepanel.services;

import com.hyperspacegamepanel.models.machine.Machine;
import com.hyperspacegamepanel.models.server.Server;
import com.hyperspacegamepanel.models.user.User;

public interface ServerService {

    Server createServer(Server server, Machine machine, User owner) throws Exception;
    Server updateServer(Server server);

    void startServer(Server server);
    void stopServer(Server server);
    
}
