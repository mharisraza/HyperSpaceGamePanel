package com.hyperspacegamepanel.exceptions;

import javax.servlet.http.HttpSession;


import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;


@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(value = UserAlreadyExistException.class)
    public String handleUserAlreadyExistException(UserAlreadyExistException ex, HttpSession httpSession) {
        httpSession.setAttribute("status", "USER_ALREADY_EXISTS_WITH_PROVIDED_EMAIL");
        return "redirect:/register";
    }
}
