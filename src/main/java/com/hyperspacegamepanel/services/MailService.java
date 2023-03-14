package com.hyperspacegamepanel.services;

import javax.mail.MessagingException;

import com.hyperspacegamepanel.entities.User;

public interface MailService {
     
    /**
     * @param to = email address of the user we want to send mail on
     * @param subject = subject of the mail
     * @param message = message of the mail
     * @throws MessagingException
     */
    void sendMail(String to, String subject, String message) throws MessagingException;

    /**
     * @param to = email address of the user we want to send mail on
     * @param user = user
     * @throws MessagingException
     */
    void sendAccountConfirmationMail(String to, User user) throws MessagingException;
    void sendResetPasswordMail(String to, User user) throws MessagingException;

}
