package com.hyperspacegamepanel.utils;

import com.hyperspacegamepanel.models.user.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.security.Principal;

public class GlobalUtils {

    // get currently Logged in user with SecurityContext.
    public static User getLoggedInUser() {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        Authentication authentication = securityContext.getAuthentication();

        if (authentication != null && authentication.isAuthenticated()) {
            // The user is authenticated, and you can access user details here
            Object principal = authentication.getPrincipal();

            if (principal instanceof User) {
                return (User) principal;
            }
        }
        throw new RuntimeException("COULDNT_GET_LOGGED_IN_USER");
    }
}
