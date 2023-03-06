package com.hyperspacegamepanel.helper;

import java.net.InetAddress;
import java.util.Random;

public class Helper {

    private static final Random random = new Random();

    // here are those methods that use globally without any dependencies whether via thymeleaf or server-side.

    public boolean isMachineOnline(String ipAddress) {
        boolean isReachable = false;
        try {
            InetAddress address = InetAddress.getByName(ipAddress);
            isReachable = address.isReachable(5000); // Time out in milliseconds (5 seconds here)

        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return isReachable;
    }

    public static String randomPasswordGenerator() {

        String allCharacters = "abcdefghijklmnopqrstuvwxyz,ABCDEFGHIJKLMNOPQRSTUVWXYZ,0123456789,!@#$%^&*()_-+={}[]\\|:;\'<>,.?/";
        StringBuilder passwordBuilder = new StringBuilder();

        for(int i = 0; i < 20; i++) {
            int randomIndex = random.nextInt(allCharacters.length());
            passwordBuilder.append(allCharacters.charAt(randomIndex));
        }

        return passwordBuilder.toString();
    }

    public static String randomUsernameGenerator() {
        String characters =  "0123456790ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
        StringBuilder usernamBuilder = new StringBuilder();
        for(int i = 0; i < 9; i++) {
            usernamBuilder.append(characters.charAt(random.nextInt(characters.length())));
        }
        return usernamBuilder.toString();
    }
    
}
