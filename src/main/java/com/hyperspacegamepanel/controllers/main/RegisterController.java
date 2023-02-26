package com.hyperspacegamepanel.controllers.main;

import java.util.List;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.hyperspacegamepanel.dtos.UserDto;
import com.hyperspacegamepanel.entities.User;
import com.hyperspacegamepanel.exceptions.UserAlreadyExistException;
import com.hyperspacegamepanel.repositories.UserRepository;
import com.hyperspacegamepanel.services.UserService;

@Controller
public class RegisterController {

    @Autowired
    private UserService userService;

     @Autowired
    private UserRepository userRepo;

    // process registration here
    @PostMapping("/create-account")
    public String processRegistration(@Valid @ModelAttribute("user") UserDto user, BindingResult bindingResult,
            Model m, HttpSession httpSession) {

       try {

        if (!user.getConfirmPassword().equals(user.getPassword())) {
            bindingResult.rejectValue("confirmPassword", "error.user", "Password and confirm password do not match.");
        }
        if (bindingResult.hasErrors()) {
            m.addAttribute("user", user);
            return "register";
        }

        if(this.userRepo.existsByEmail(user.getEmail())) {
            throw new UserAlreadyExistException(user.getEmail());
        }
        

         user.setRole("ROLE_NORMAL");
         this.userService.createUser(user);
 
            httpSession.setAttribute("status", "USER_REGISTERED_SUCCESSFULLY");

       } catch(Exception e) {
        httpSession.setAttribute("status", "SOMETHING_WENT_WRONG");
       }

        return "redirect:/register";
    }

    // process admin registration here
    @PostMapping("/addAdmin")
    public String processAdminRegistration(@Valid @ModelAttribute("user") UserDto user, BindingResult bindingResult,
            Model m, HttpSession httpSession) {

       try {

        List<User> users = this.userRepo.findAllByRole("ROLE_ADMIN");

        if(!users.isEmpty()) {
            return "redirect:/register";
        }

        if (!user.getConfirmPassword().equals(user.getPassword())) {
            bindingResult.rejectValue("confirmPassword", "error.user", "Password and confirm password do not match.");
        }
        if (bindingResult.hasErrors()) {
            m.addAttribute("user", user);
            return "add_admin.html";
        }
        
         user.setRole("ROLE_ADMIN");
         this.userService.createUser(user);
 
            httpSession.setAttribute("status", "USER_REGISTERED_SUCCESSFULLY");

       } catch(Exception e) {
        httpSession.setAttribute("status", "SOMETHING_WENT_WRONG");
       }

        return "redirect:/register";
    }



}
