package com.hyperspacegamepanel.services;

import javax.mail.MessagingException;

public interface MailService {

     // email address - mail to send to.
    void sendMail(String emailAddress, String subject, String message) throws MessagingException;
    
}
