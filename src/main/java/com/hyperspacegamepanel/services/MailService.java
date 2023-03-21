package com.hyperspacegamepanel.services;

import java.util.concurrent.CompletableFuture;

import com.hyperspacegamepanel.models.user.User;

public interface MailService {

    CompletableFuture<Void> sendMail(String to, String subject, String body) throws Exception;

    CompletableFuture<Void> sendAccountConfirmationMail(String to, User user) throws Exception;

    CompletableFuture<Void> sendResetPasswordMail(String to, User user) throws Exception;


}
