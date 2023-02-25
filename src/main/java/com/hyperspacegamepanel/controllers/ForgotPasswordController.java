  package com.hyperspacegamepanel.controllers;

  import java.security.SecureRandom;
  import java.util.Base64;
  import java.util.Properties;

  import javax.mail.Authenticator;
  import javax.mail.Message;
  import javax.mail.MessagingException;
  import javax.mail.PasswordAuthentication;
  import javax.mail.Session;
  import javax.mail.Transport;
  import javax.mail.internet.AddressException;
  import javax.mail.internet.InternetAddress;
  import javax.mail.internet.MimeMessage;
  import javax.servlet.http.HttpServletRequest;
  import javax.servlet.http.HttpSession;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
  import org.springframework.stereotype.Controller;
  import org.springframework.ui.Model;
  import org.springframework.web.bind.annotation.GetMapping;
  import org.springframework.web.bind.annotation.PostMapping;
  import org.springframework.web.bind.annotation.RequestParam;

import com.hyperspacegamepanel.dtos.UserDto;
import com.hyperspacegamepanel.entities.User;
import com.hyperspacegamepanel.helper.Constants;
import com.hyperspacegamepanel.repositories.UserRepository;
import com.hyperspacegamepanel.services.MailService;
import com.hyperspacegamepanel.services.UserService;

  @Controller
  public class ForgotPasswordController {

      @Autowired
      private UserRepository userRepo;

      @Autowired
      private UserService userService;

      @Autowired
      private MailService mailService;

      @Autowired
      private ModelMapper mapper;

      private SecureRandom secureRandom = new SecureRandom();
      private User user;
      private String token;
      private long tokenTime;


      @PostMapping("/forgotPassword")
      public String forgotPassword(@RequestParam String email, Model m, HttpSession httpSession, HttpServletRequest request) throws AddressException, MessagingException {
          if(!this.userRepo.existsByEmail(email)) {
                  httpSession.setAttribute("status", "USER_DOESNT_EXIST_WITH_PROVIDED_EMAIL");
                  return "redirect:/login?forgotPassword=WrongEmailAddress";
          }

          this.user = this.userRepo.getByEmail(email);
          this.token = generateToken();
          this.tokenTime = System.currentTimeMillis(); 

          String tokenURL = request.getRequestURL().toString().replace("forgotPassword", "resetPassword") + "?token=" + token;

          String message = String.format("<h1 style='font-size: 18px;'>Hello, %s!</h1> \nYour request for resetting password has been received! \n Please use the following link to reset your password: %s \nThis code will be expired in 30 minutes, \nif you didn't initiate this request please ignore this mail. \nBest Regards, \nHyperSpace - GamePanel", user.getFullName(), tokenURL);

          try {
            mailService.sendMail(email, "Password Reset Request", message);
            httpSession.setAttribute("status", "TOKEN_SENT_SUCCESSFULLY");
          } catch (Exception e) {
            httpSession.setAttribute("status", "SOMETHING_WENT_WRONG");
          }

          return "login.html";
      }

      private String generateToken() {
          byte[] tokenBytes = new byte[16];
          secureRandom.nextBytes(tokenBytes);
          return Base64.getUrlEncoder().encodeToString(tokenBytes);
        }

        private boolean isTokenValid(String token, long tokenTime) {
          if (token == null || token.isEmpty() || tokenTime == 0 || token.equals("expired")) {
            return false;
        }
        long currentTime = (System.currentTimeMillis() / 1000L);
        long timeDifference = currentTime - tokenTime;
        
        if (timeDifference > 1800) {
            return false;
        }
        return token.equals(this.token);
        }

        @GetMapping("/resetPassword")
        public String resetPasswordView(@RequestParam(required = false) String token, HttpSession httpSession) {
              if(!isTokenValid(token, this.tokenTime)) {
                httpSession.setAttribute("status", "INVALID_TOKEN");
                  return "redirect:/login?forgotPassword=invalidRequest";
              }
              return "resetPassword.html";
        }

        @PostMapping("/resetPassword")
        public String resetPasswordAndUpdate(@RequestParam String password, @RequestParam String confirmPassword, HttpSession httpSession) {
          if(password.isBlank() || confirmPassword.isBlank() || !password.equals(confirmPassword)) {
            httpSession.setAttribute("status", "PASSWORD_CONFPW_NOT_MATCH");
            return "redirect:/resetPassword?token="+this.token;
          }

          UserDto user = this.mapper.map(this.user, UserDto.class);
          user.setPassword(password);
          user = userService.updateUser(user, user.getId());
          this.token = "expired";

          if(user != null) {
            httpSession.setAttribute("status", "PASSWORD_UPDATED");
            return "redirect:/login?";
          }

          httpSession.setAttribute("status", "SOMETHING_WENT_WRONG");
            return "redirect:/login";
        }
      
  }
