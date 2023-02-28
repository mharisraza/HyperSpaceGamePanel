package com.hyperspacegamepanel.helper;

import org.jasypt.util.text.BasicTextEncryptor;

public class PasswordEncoder {

    public static String encrypt(String password) {
        BasicTextEncryptor textEncryptor = new BasicTextEncryptor();
        textEncryptor.setPassword(Constants.PASSWORD_ENCODER_SECRET_KEY);
        String encryptedPassword = textEncryptor.encrypt(password);
        return "ENC-" + encryptedPassword + "-ENC";
    }

    public static String decrypt(String password) {

        if(!password.startsWith("ENC-") && !password.endsWith("-ENC")) {
            return password;
        }
        password = password.substring(4, password.length() - 4);
        BasicTextEncryptor textEncryptor = new BasicTextEncryptor();
        textEncryptor.setPassword(Constants.PASSWORD_ENCODER_SECRET_KEY);
        return textEncryptor.decrypt(password);
    }
    
}
