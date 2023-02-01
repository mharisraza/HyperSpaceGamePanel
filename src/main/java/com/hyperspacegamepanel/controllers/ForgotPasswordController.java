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
import com.hyperspacegamepanel.services.UserService;

  @Controller
  public class ForgotPasswordController {

      @Autowired
      private UserRepository userRepo;

      @Autowired
      private UserService userService;

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
          boolean messageSent = false;
          this.token = generateToken();
          this.tokenTime = System.currentTimeMillis(); 

          String tokenURL = request.getRequestURL().toString().replace("forgotPassword", "resetPassword") + "?token=" + token;

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
          props.put("mail.smtp.starttls.enable", "true");
          props.put("mail.smtp.host", "smtp.gmail.com");
          props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props, new Authenticator() {
          @Override
              protected PasswordAuthentication getPasswordAuthentication() {
                  return new PasswordAuthentication(Constants.EMAIL_FROM, Constants.EMAIL_APP_PASSWORD);
              }
        });

        String htmlText = String.format("<h1 style='font-size: 18px;'>Hello, %s!</h1> \nYour request for resetting password has been received! \n Please use the following link to reset your password: %s \nThis code will be expired in 30 minutes, \nif you didn't initiate this request please ignore this mail. \nBest Regards, \nHyperSpace - GamePanel", user.getFullName(), tokenURL);

        MimeMessage msg = new MimeMessage(session);
        msg.setFrom(new InternetAddress(email));
        msg.addRecipient(Message.RecipientType.TO, new InternetAddress(email));
        msg.setSubject("Reset Password Request.");
        msg.setContent(htmlText, "text/html");
        Transport.send(msg);
        messageSent = true;

        if(messageSent) {
          httpSession.setAttribute("status", "TOKEN_SENT_SUCCESSFULLY");
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
          boolean passwordUpdated = false;
          UserDto user = this.mapper.map(this.user, UserDto.class);
          user.setPassword(password);
          userService.updateUser(user, user.getId());
          passwordUpdated = true;
          this.token = "expired";

          if(passwordUpdated) {
            httpSession.setAttribute("status", "PASSWORD_UPDATED");
            return "redirect:/login?";
          }

          httpSession.setAttribute("status", "SOMETHING_WENT_WRONG");
            return "redirect:/login";
        }
      
  }
