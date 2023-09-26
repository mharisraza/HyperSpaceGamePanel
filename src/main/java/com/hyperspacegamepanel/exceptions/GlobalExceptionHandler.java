package com.hyperspacegamepanel.exceptions;

import com.hyperspacegamepanel.utils.Alert;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import jakarta.servlet.http.HttpSession;


@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(value = UserAlreadyExistException.class)
    public String handleUserAlreadyExistException(UserAlreadyExistException ex, HttpSession httpSession) {
        httpSession.setAttribute("status", new Alert("An user already exists with same username, please try another username", Alert.ERROR, Alert.ERROR_CLASS));
        return "redirect:/register";
    }
}
