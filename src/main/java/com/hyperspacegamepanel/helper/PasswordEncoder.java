package com.hyperspacegamepanel.helper;

import org.jasypt.util.text.BasicTextEncryptor;

public class PasswordEncoder {

    public static String encrypt(String password) {
        BasicTextEncryptor textEncryptor = new BasicTextEncryptor();
        textEncryptor.setPassword(Constants.PASSWORD_ENCODER_SECRET_KEY);
        return textEncryptor.encrypt("ENCRYPTED" + password);
    }

    public static String decrypt(String password) {
        BasicTextEncryptor textEncryptor = new BasicTextEncryptor();
        textEncryptor.setPassword(Constants.PASSWORD_ENCODER_SECRET_KEY);
        return textEncryptor.decrypt(password);
    }
    
}
