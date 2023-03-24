package com.hyperspacegamepanel.controllers.main;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutionException;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.hyperspacegamepanel.exceptions.ResourceNotFound;
import com.hyperspacegamepanel.helper.Alert;
import com.hyperspacegamepanel.models.user.User;
import com.hyperspacegamepanel.services.UserService;

@Controller
public class RegistrationController {

    @Autowired
    private UserService userService;

    @Autowired
    private HttpSession httpSession;

    // showing page for registration page
    @GetMapping("/register")
    public String registerPage(Model m) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (!(auth instanceof AnonymousAuthenticationToken)) return "redirect:/login"; // redirect to login page and login page will redirect it to the dashboard page based on the user.
        m.addAttribute("title", "Register | HyperSpaceGamePanel");
        m.addAttribute("user", new User());
        return "register.html";
    }

    // process registration here 
    @PostMapping("/create-account")
    public String processRegistration(@Valid @ModelAttribute("user") User user, BindingResult bindResult, Model model) {
          if(bindResult.hasErrors()) return "register";
          try {
            this.userService.createUser(user);
            httpSession.setAttribute("status", new Alert("Registered successfully, we've sent you an email to activate your account.", Alert.SUCCESS, Alert.SUCCESS_CLASS));
          } catch (Exception e) {
            if(e.getMessage() == "USER_ALREADY_EXISTS") {
                httpSession.setAttribute("status", new Alert("User already exists with this username or email.", Alert.ERROR, Alert.ERROR_CLASS));
                return "redirect:/register";
            }
            httpSession.setAttribute("status", new Alert("Sorry, cannot register at this moment, please try again.", Alert.ERROR, Alert.ERROR_CLASS));
            e.printStackTrace();
          }
        return "redirect:/register";
    }

    // showing page for verify account if account is non-verified after registration
    @GetMapping("/verifyAccount")
    public String verifyAccount(@RequestParam(name = "email", required = false) String userEmail, @RequestParam(name = "token", required = false) String tokenValue, Model m, HttpSession httpSession) {
        if (userEmail != null) {
            try {
                this.userService.sendVerificationMail(userEmail);
            } catch (Exception e) {
               if(e.getMessage() == "USER_ALREADY_VERIFIED") {
                httpSession.setAttribute("status", new Alert("User is already verified with this email address.", Alert.ERROR, Alert.ERROR_CLASS));
                return "redirect:/verifyAccount";
               }
               if(e.getMessage() == "CANNOT_SEND_THE_MAIL") {
                httpSession.setAttribute("status", new Alert("We were unable to send the mail to the provided email address, please provide valid email address.", Alert.ERROR, Alert.ERROR_CLASS));
                return "redirect:/verifyAccount";
               }
               if(e instanceof ResourceNotFound && e.getMessage().contains("User not found")) { 
                httpSession.setAttribute("status", new Alert("User not found with the provided email address, please try registered email address.", Alert.ERROR, Alert.ERROR_CLASS));
                return "redirect:/verifyAccount";
               }
            }
        }
        if(tokenValue != null) {
             try {
                this.userService.verifyUserAccount(tokenValue);
                httpSession.setAttribute("status", new Alert("User verified successfully.", Alert.SUCCESS, Alert.SUCCESS_CLASS));
                return "redirect:/login";
             } catch(Exception e) {
                 if(e.getMessage() == "TOKEN_IS_EXPIRED_OR_INVALID") {
                    httpSession.setAttribute("status", new Alert("The request you tried is expired or invalid", Alert.ERROR, Alert.ERROR_CLASS));
                 }
             }
        }
        m.addAttribute("title", "Verify Your Account | HyperSpaceGamePanel");
        return "verify_account";

    }

    // ..showing page for the registerable admin user if there is no any admin user
    // in the database.
    @GetMapping("/addAdmin")
    public String addAdmin(Model m, HttpSession httpSession) {
        if(this.userService.isAdminsExists().join()) return "redirect:/register";
        m.addAttribute("title", "Add Admin | HyperSpaceGamePanel");
        m.addAttribute("user", new User());
        return "add_admin.html";
    }

    // process admin registration here
    @PostMapping("/addAdmin")
    public String processAdminRegistration(@Valid @ModelAttribute("user") User user, BindingResult bindingResult, Model m, HttpSession httpSession) {
        if(this.userService.isAdminsExists().join()) return "redirect:/register";
        try {
            this.userService.createAdminUser(user);
        } catch (Exception e) {
            httpSession.setAttribute("status", new Alert("Ahh Sorry, something went wrong from our side.", Alert.ERROR, Alert.ERROR_CLASS));
        }
        return "redirect:/login";
    }

}
