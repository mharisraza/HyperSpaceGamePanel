package com.hyperspacegamepanel.services;

import com.hyperspacegamepanel.models.user.User;

import java.util.concurrent.CompletableFuture;

public interface MailService {

    CompletableFuture<Void> sendMail(String to, String subject, String body) throws Exception;

    CompletableFuture<Void> sendAccountConfirmationMail(String to, User user) throws Exception;

    CompletableFuture<Void> sendResetPasswordMail(String to, User user) throws Exception;

    CompletableFuture<Void> sendAccountCreatedSuccessMail(String to, User user) throws Exception;


}
