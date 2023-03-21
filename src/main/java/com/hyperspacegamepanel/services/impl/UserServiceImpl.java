package com.hyperspacegamepanel.services.impl;


import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.hyperspacegamepanel.exceptions.ResourceNotFound;
import com.hyperspacegamepanel.models.user.UpdateUserForm;
import com.hyperspacegamepanel.models.user.User;
import com.hyperspacegamepanel.repositories.UserRepository;
import com.hyperspacegamepanel.services.MailService;
import com.hyperspacegamepanel.services.TokenService;
import com.hyperspacegamepanel.services.UserService;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private MailService mailService;

    @Autowired
    private TokenService tokenService;


    @Override
    @Async
    public CompletableFuture<User> createUser(User user) {
        if (isUserExists(user)) {
            throw new RuntimeException("USER_ALREADY_EXISTS");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setEnabled(true);
        user.setVerified(false);
        user.setRole(User.ROLE_USER);

        this.userRepo.save(user);
        try {
            this.mailService.sendAccountConfirmationMail(user.getEmail(), user);
        } catch (Exception e) {
            throw new RuntimeException("UNABLE_TO_SEND_CONFIRMATION_MAIL");
        }
        return CompletableFuture.completedFuture(user);
    }

    @Override
    @Async
    public CompletableFuture<User> updateUser(UpdateUserForm updateUser, Integer userId) {
        User existingUser = this.userRepo.findById(userId).orElseThrow(() -> new ResourceNotFound("User", "ID" + userId));
        if (updateUser.getFullName() != null) existingUser.setFullName(updateUser.getFullName());
        if (updateUser.getUsername() != null) existingUser.setUsername(updateUser.getUsername());
        if (updateUser.getPassword() != null) existingUser.setPassword(passwordEncoder.encode(updateUser.getPassword()));
        return CompletableFuture.completedFuture(existingUser);
    }

    @Override
    @Async
    public CompletableFuture<Void> deleteUser(Integer userId) {
        User user = this.userRepo.findById(userId).orElseThrow(() -> new ResourceNotFound("User", "ID:" + userId));
        this.userRepo.delete(user);
        return CompletableFuture.completedFuture(null);
    }

    @Override
    @Async
    public CompletableFuture<User> getUser(Integer userId) {
        return CompletableFuture.completedFuture(this.userRepo.findById(userId).orElseThrow(() -> new ResourceNotFound("User", "ID" + userId)));
    }

    @Override
    @Async
    public CompletableFuture<Void> banUser(User user) {
        user.setEnabled(false);
        this.userRepo.save(user);
        return CompletableFuture.completedFuture(null);
    }

    @Override
    @Async
    public CompletableFuture<Void> unbanUser(User user) {
        user.setEnabled(true);
        this.userRepo.save(user);
        return CompletableFuture.completedFuture(null);
    }

    @Override
    @Async
    public CompletableFuture<Void> verifyUserAccount(String tokenValue) {
        try {
            User user = this.tokenService.validateToken(tokenValue).get();
            user.setVerified(true);
            this.userRepo.save(user);

            // now forcefully expire the token.
            this.tokenService.forceExpireToken(tokenValue);

        } catch (Exception e) {
            e.printStackTrace();    
              if(e.getMessage() == "TOKEN_IS_EXPIRED" || e.getMessage() == "TOKEN_IS_INVALID_OR_DOESNT_EXIST") {
                throw new RuntimeException("TOKEN_IS_EXPIRED_OR_INVALID");
              }
        }
        return CompletableFuture.completedFuture(null);
    }

    @Override
    @Async
    public CompletableFuture<User> createAdminUser(User user) {
        user.setRole(User.ROLE_ADMIN);
        user.setVerified(true);
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        this.userRepo.save(user);
        return CompletableFuture.completedFuture(user);
    }

    @Override
    @Async
    public CompletableFuture<Boolean> isAdminsExists() {
        return CompletableFuture.completedFuture(!this.userRepo.findAll().isEmpty());
    }

    private boolean isUserExists(User user) {
        return this.userRepo.existsByUsernameOrEmail(user.getUsername(), user.getEmail());
    }

    @Override
    public CompletableFuture<Void> sendVerificationMail(String userEmail) {
        User user = this.userRepo.findByEmail(userEmail).orElseThrow(()-> new ResourceNotFound("User", "Email"+userEmail));
        if(user.isVerified()) {
            throw new RuntimeException("USER_ALREADY_VERIFIED");
        }
        try {
            this.mailService.sendAccountConfirmationMail(userEmail, user);
        } catch (Exception e) {
            throw new RuntimeException("CANNOT_SEND_THE_MAIL");
        }
        return CompletableFuture.completedFuture(null);
    }

    @Override
    @Async
    @Scheduled(fixedDelay = 60000) // once every minute.
    public CompletableFuture<Void> removeNonVerifiedUsers() {
        List<User> users = this.userRepo.findAll();

        if(users == null) {
            return CompletableFuture.completedFuture(null);
        }

        // gets calendar instance and minus 24 hours which means that we are checking if user is created 24 hours ago or not.
        // if user created 24 hours ago and didn't verify in that time we should remove that user now to avoid unnecessary space.
        Calendar cutOff = Calendar.getInstance();
        cutOff.add(Calendar.HOUR_OF_DAY, -24);

        users.forEach((user) -> {
            if(!user.isVerified() || user.getRegisteredDate().before(cutOff.getTime())) {
                this.userRepo.delete(user);
            }
        });
         return CompletableFuture.completedFuture(null);
    }

}
