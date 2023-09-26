package com.hyperspacegamepanel.controllers.main;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Controller
public class ErrorsController implements ErrorController {

    @GetMapping("/error")
    public String handleError(HttpServletRequest request, HttpServletResponse response, @ModelAttribute("error") String error, Model m) {

        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        String returnTo = "";

        if(status != null) {
            int statusCode = Integer.parseInt(status.toString());

            if(statusCode == 404) {
                m.addAttribute("title", "404 Not Found | HyperSpaceGamePanel");
                returnTo = "errors/404.html";
            } else if(statusCode == 403) {
                m.addAttribute("title", "403 Forbidden | HyperSpaceGamePanel");
                returnTo = "errors/403.html";
            } else {
                m.addAttribute("title", "Internal Server Error | HyperSpaceGamePanel");
                returnTo = "errors/500.html";
            }
        }
        
        return returnTo;

    }
    
}
