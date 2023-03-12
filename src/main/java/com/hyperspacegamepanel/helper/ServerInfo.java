package com.hyperspacegamepanel.helper;

import lombok.Data;

@Data
public class ServerInfo {

    private String mapName = "Map not available, server is offline";
    private String players = "0";
    private String maxPlayers = "Cannot get players, server is offline.";
    boolean isServerOnline = false;
    
}
