package com.hyperspacegamepanel.controllers.main;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.hyperspacegamepanel.helper.Alert;
import com.hyperspacegamepanel.models.token.Token;
import com.hyperspacegamepanel.models.user.UpdateUserForm;
import com.hyperspacegamepanel.models.user.User;
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
  private TokenService tokenService;

  @GetMapping("/resetPassword")
  public String resetPasswordView(@RequestParam(name = "token", required = false) String tokenValue,
      HttpSession httpSession, Model m) {
    if (tokenValue == null)
      return "redirect:/login?e=invalidRequest";
    try {
      this.tokenService.validateToken(tokenValue);
    } catch (Exception e) {
      if (e.getMessage().equals("TOKEN_IS_EXPIRED")) {
        httpSession.setAttribute("status",
            new Alert("The request is expired or invalid, please make a new request.", Alert.ERROR, Alert.ERROR_CLASS));
        return "redirect:/login?e=invalidRequest";
      }
      httpSession.setAttribute("status",
          new Alert("Something went wrong, please try later.", Alert.ERROR, Alert.ERROR_CLASS));
      return "redirect:/login?e=internalError";
    }

    httpSession.setAttribute("resetPasswordToken", this.tokenService.getToken(tokenValue).join());
    m.addAttribute("updateUserForm", new UpdateUserForm());
    m.addAttribute("title", "Reset Password | HyperSpaceGamePanel");
    return "resetPassword.html";
  }

  @PostMapping("/resetPassword")
  public String resetPasswordAndUpdate(@ModelAttribute("updateUserForm") UpdateUserForm updatedUser, HttpSession httpSession) {

    // token may loss if application is restarted or stopped (not from database but session).
    // we'll find a solution in future if needed.
    Token token = (Token) httpSession.getAttribute("resetPasswordToken");

    if (updatedUser.getPassword().isBlank() || updatedUser.getConfirmPassword().isBlank() || !updatedUser.getPassword().equals(updatedUser.getConfirmPassword())) {
      httpSession.setAttribute("status", new Alert("Confirm password do not matches password, please try again", Alert.ERROR, Alert.ERROR_CLASS));
      return "redirect:/resetPassword?token=" + token.getTokenValue();
    }

    try {
      this.userService.updateUser(updatedUser, token.getUser().getId());
      httpSession.setAttribute("status", new Alert("Password updated successfully, you can login now.", Alert.SUCCESS, Alert.SUCCESS_CLASS));
      this.tokenService.forceExpireToken(token.getTokenValue());
      return "redirect:/login?s=passwordUpdated";
    } catch (Exception e) {
      httpSession.setAttribute("status", new Alert("Sorry something went wrong, please try later.", Alert.ERROR, Alert.ERROR_CLASS));
    }
    return "redirect:/login?e=failedtoUpdatePassword";
  }

  @PostMapping("/forgotPassword")
  public String forgotPassword(@RequestParam String email, Model m, HttpSession httpSession)
      throws AddressException, MessagingException {

    if (!this.userRepo.existsByEmail(email)) {
      httpSession.setAttribute("status", new Alert("Sorry, user not exist with provided email address, please try with valid email address.", Alert.ERROR, Alert.ERROR_CLASS));
      return "redirect:/login?e=forgotPassword=WrongEmailAddress";
    }

    User user = this.userService.getUserByEmail(email).join();

    try {
      this.mailService.sendResetPasswordMail(email, user);
      httpSession.setAttribute("status", new Alert("We've sent you a mail with reset password link, please look in to your mailbox.", Alert.SUCCESS, Alert.SUCCESS_CLASS));
    } catch (Exception e) {
      httpSession.setAttribute("status", new Alert("Ahh Sorry, we cannot send the mail, something went wrong from our side", Alert.ERROR, Alert.ERROR_CLASS));
    }
    return "redirect:/login?";
  }

}
