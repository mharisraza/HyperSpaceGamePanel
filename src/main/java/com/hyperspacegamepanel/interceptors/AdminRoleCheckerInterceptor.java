package com.hyperspacegamepanel.interceptors;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.HandlerInterceptor;

import com.hyperspacegamepanel.entities.User;
import com.hyperspacegamepanel.repositories.UserRepository;

@Service
public class AdminRoleCheckerInterceptor implements HandlerInterceptor {

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private HttpSession httpSession;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {

            List<User> users = this.userRepo.findAllByRole("ROLE_ADMIN");
            if(users.isEmpty()) {
               httpSession.setAttribute("status", "THERE_IS_NO_ANY_ADMIN_USER");
            }
    
        return true;
    }

   
    
}
