  package com.hyperspacegamepanel.controllers.main;

  import javax.mail.MessagingException;
  import javax.mail.internet.AddressException;
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
import com.hyperspacegamepanel.helper.Alert;
import com.hyperspacegamepanel.helper.Token;
import com.hyperspacegamepanel.repositories.UserRepository;
import com.hyperspacegamepanel.services.MailService;
import com.hyperspacegamepanel.services.TokenService;
import com.hyperspacegamepanel.services.UserService;

  @Controller
  public class ForgotPasswordController extends HelperController {

      @Autowired
      private UserRepository userRepo;

      @Autowired
      private UserService userService;

      @Autowired
      private MailService mailService;

      @Autowired
      private ModelMapper mapper;

      @Autowired
      private TokenService tokenService;
      
      @GetMapping("/resetPassword")
      public String resetPasswordView(@RequestParam(name = "token", required = false) String tokenValue, HttpSession httpSession, Model m) {

            if(tokenValue == null) {
              return "redirect:/login?invalidRequest";
            }

            if(!this.tokenService.isTokenValid(tokenValue)) {
              httpSession.setAttribute("status", new Alert("Ahh Sorry, it was an invalid request", Alert.ERROR, Alert.ERROR_CLASS));
                return "redirect:/login?invalidRequest";
            }

            Token token = this.tokenService.getToken(tokenValue);

            httpSession.setAttribute("resetPasswordToken", token);

            m.addAttribute("title", "Reset Password | HyperSpaceGamePanel");
            return "resetPassword.html";
      }

      @PostMapping("/forgotPassword")
      public String forgotPassword(@RequestParam String email, Model m, HttpSession httpSession, HttpServletRequest request) throws AddressException, MessagingException {
          if(!this.userRepo.existsByEmail(email)) {
                  httpSession.setAttribute("status", new Alert("Sorry, user not exist with provided email address, please try with valid email address.", Alert.ERROR, Alert.ERROR_CLASS));
                  return "redirect:/login?forgotPassword=WrongEmailAddress";
          }

          User user = this.userRepo.getByEmail(email);
          httpSession.setAttribute("user", user);

          // send mail
          try {
            this.mailService.sendResetPasswordMail(email, user);
            httpSession.setAttribute("status", new Alert("We've sent you a mail with reset password link, please look in to your mailbox.", Alert.SUCCESS, Alert.SUCCESS_CLASS));
          } catch(Exception e) {
            e.printStackTrace();
            httpSession.setAttribute("status", new Alert("Ahh Sorry, we cannot send the mail, something went wrong from our side", Alert.ERROR, Alert.ERROR_CLASS));
          }
          return "redirect:/login?";
      }

        @PostMapping("/resetPassword")
        public String resetPasswordAndUpdate(@RequestParam String password, @RequestParam String confirmPassword, HttpSession httpSession) {

          Token token = (Token) httpSession.getAttribute("resetPasswordToken");
          User user = (User) httpSession.getAttribute("user");
          
          if(password.isBlank() || confirmPassword.isBlank() || !password.equals(confirmPassword)) {
            httpSession.setAttribute("status", new Alert("Confirm password do not matches password, please try again", Alert.ERROR, Alert.ERROR_CLASS));
            return "redirect:/resetPassword?token="+token;
          }

          UserDto userDto = this.mapper.map(user, UserDto.class);
          userDto.setPassword(password);
          userDto = userService.updateUser(userDto, user.getId());
          token.expire();

            httpSession.setAttribute("status", new Alert("Password update successfully.", Alert.SUCCESS, Alert.SUCCESS_CLASS));

            return "redirect:/login";
        }
      
  }
