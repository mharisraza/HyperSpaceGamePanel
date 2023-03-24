package com.hyperspacegamepanel.services;

import java.util.concurrent.CompletableFuture;

import com.hyperspacegamepanel.models.user.UpdateUserForm;
import com.hyperspacegamepanel.models.user.User;

public interface UserService {

    CompletableFuture<User> createUser(User user);
    CompletableFuture<User> updateUser(UpdateUserForm updateUser, Integer userId);
    CompletableFuture<Void> deleteUser(Integer userId);
    CompletableFuture<User> getUser(Integer userId);

    CompletableFuture<User> getUserByEmail(String email);

    CompletableFuture<Void> banUser(User user);
    CompletableFuture<Void> unbanUser(User user);

    CompletableFuture<Void> verifyUserAccount(String tokenValue);

    CompletableFuture<User> createAdminUser(User user);
    CompletableFuture<Boolean> isAdminsExists();

    // mails: mails to send to user.
    CompletableFuture<Void> sendVerificationMail(String userEmail);

    CompletableFuture<Void> removeNonVerifiedUsers();
    
}
