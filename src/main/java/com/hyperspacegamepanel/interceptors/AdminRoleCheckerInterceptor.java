package com.hyperspacegamepanel.interceptors;

import com.hyperspacegamepanel.models.user.User;
import com.hyperspacegamepanel.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.List;

@Service
public class AdminRoleCheckerInterceptor implements HandlerInterceptor {

    @Autowired
    private UserRepository userRepo;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // Check if the "redirection" attribute already exists in the session
        if (request.getSession().getAttribute("redirection") == null) {
            List<User> users = this.userRepo.findAllByRole("ROLE_ADMIN");

            System.out.println("Number of admin users: " + users.size());
            System.out.println("USERS IS EMPTY OR NOT: " + users.isEmpty());

            if (users.isEmpty()) {
                // Set the "redirection" attribute if there are no admin users
                request.getSession().setAttribute("redirection", "/addAdmin");
            }
        }

        return true;
    }


}
