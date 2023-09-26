package com.hyperspacegamepanel.utils;

import java.util.HashMap;
import java.util.Map;

public class Constants {

    // mail sender setting
    public static final String EMAIL_FROM = ""; // the mail you want to send from
    public static final String EMAIL_APP_PASSWORD = ""; // app password (gmail)

    // tickets setting
    public static final int TICKET_MESSAGE_SIZE = 1000; // change to your needs.
    public static final String TICKET_MAXIMUM_MESSAGE_ERROR = "Maximum 1000 characters are allowed";

    // Secret key for password encoder 
    // it is recommended to change PASSWORD Encoder Secret Key
    public static final String PASSWORD_ENCODER_SECRET_KEY = "AcCeLwPMx02xISEwwEu0A6cyhRsBvW";

    // path to local scripts files (should be correct otherwise most of the module will not work such as creating game server.)
    public static final Map<String, String> SCRIPTS_FILES = new HashMap<>();

    // key will be script file name or whatever we want to be, value will be the location of the local scripts file
    // it is not recommended to change the key or value it can lead to exceptions, leave it them as default
    static {
        SCRIPTS_FILES.put("VPS_INFO_SCRIPT", "./scripts/getvpsinfo.sh");
        SCRIPTS_FILES.put("CREATE_GAME_SERVER_SCRIPT", "./scripts/create_server.sh");
        SCRIPTS_FILES.put("MACHINE_CONFIGURE_SCRIPT", "./scripts/configure_machine.sh");
    }

    // path to remote scripts files
    public static final Map<String, String> REMOTE_SCRIPTS_FILES = new HashMap<>();

    static {
        REMOTE_SCRIPTS_FILES.put("MACHINE_CONFIGURE_SCRIPT", "/scripts/configure_machine.sh");
    }

    // specify the directory you want to make in VPS (where our scripts will be placed. default: scripts)
    // it is recommended to leave it default to avoid ambiguity
    
    public static final String REMOTE_SCRIPTS_FOLDER = "scripts";

    // OUR SUPPORTED GAMES, key will be the game's short name and value will be the full name
    public static final Map<String, String> SUPPORTED_GAMES = new HashMap<>();

    static {
        SUPPORTED_GAMES.put("cs", "Counter Strike 1.6");
    }

    // OUR SUPPORTED GAMES ICONS links, key will be the game's short name and value will be the link to the icon
    public static final Map<String, String> SUPPORT_GAMES_ICONS = new HashMap<>();

    // if you want to change the location, make sure the image should be available there.
    static {
        SUPPORT_GAMES_ICONS.put("cs", "/images/icons/game-cs.png");
    }

    
    
}
