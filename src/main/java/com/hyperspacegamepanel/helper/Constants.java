package com.hyperspacegamepanel.helper;

import java.util.HashMap;
import java.util.Map;

public class Constants {

    // mail sender setting
    public static final String EMAIL_FROM = "wannaheadshot5@gmail.com"; // the mail you want to send from
    public static final String EMAIL_APP_PASSWORD = "vrseytghkzdvmleu"; // app password (gmail)

    // tickets setting
    public static final int TICKET_MESSAGE_SIZE = 1000; // change to your needs.
    public static final String TICKET_MAXIMUM_MESSAGE_ERROR = "Maximum 1000 characters are allowed";

    // Secret key for password encoder 
    public static final String PASSWORD_ENCODER_SECRET_KEY = "AcCeLwPMx02xISEwwEu0A6cyhRsBvW";

    // path to local scripts files (should be correct otherwise most of the module will not work such as creating game server.)
    public static final Map<String, String> SCRIPTS_FILES = new HashMap<>();

    // key will be script file name or whatever we want to be, value will be the location of the local scripts file
    static {
        SCRIPTS_FILES.put("VPS_INFO_SCRIPT", "./scripts/getvpsinfo.sh");
        SCRIPTS_FILES.put("CREATE_GAME_SERVER_SCRIPT", "./scripts/create_server.sh");
    }

    // assigning game ids (statically)
    public static final Map<String, Integer> GAME_IDS = new HashMap<>();

    static {
        GAME_IDS.put("cs", 1);
        GAME_IDS.put("csgo", 2);
        GAME_IDS.put("samp", 3);
        GAME_IDS.put("mc", 4);
    }
    
}
