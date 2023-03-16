package com.hyperspacegamepanel.controllers.main;

import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.hyperspacegamepanel.dtos.UserDto;
import com.hyperspacegamepanel.entities.User;
import com.hyperspacegamepanel.exceptions.UserAlreadyExistException;
import com.hyperspacegamepanel.helper.Alert;
import com.hyperspacegamepanel.helper.Token;
import com.hyperspacegamepanel.repositories.UserRepository;
import com.hyperspacegamepanel.services.MailService;
import com.hyperspacegamepanel.services.TokenService;
import com.hyperspacegamepanel.services.UserService;

@Controller
public class RegisterController {

    @Autowired
    private MailService mailService;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private ModelMapper mapper;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private HttpSession httpSession;


     // showing page for registration page
    @GetMapping("/register")
    public String registerPage(Model m) {
       m.addAttribute("title", "Register | HyperSpaceGamePanel");
       m.addAttribute("user", new UserDto());
       return "register.html";
    }

    // process registration here
    @PostMapping("/create-account")
    public String processRegistration(@Valid @ModelAttribute("user") UserDto user, BindingResult bindingResult,
            Model m, HttpServletResponse response) {

       try {

        if (!user.getConfirmPassword().equals(user.getPassword())) {
            bindingResult.rejectValue("confirmPassword", "error.user", "Password and confirm password do not match.");
        }
        if (bindingResult.hasErrors()) {
           m.addAttribute("user", user);
            return "register";
        }

        if(this.userRepo.existsByEmail(user.getEmail()) || this.userRepo.existsByUsername(user.getUsername())) {
            throw new UserAlreadyExistException(user.getEmail());
        }
        
        
         user.setRole("ROLE_NORMAL");
         UserDto createdUserDto = this.userService.createUser(user);

          User createdUser = this.mapper.map(createdUserDto, User.class);
 
            httpSession.setAttribute("status", new Alert("Registered successfully, please check your mail to login.", Alert.SUCCESS, Alert.SUCCESS_CLASS));
            this.mailService.sendAccountConfirmationMail(user.getEmail(), createdUser);

       } catch(Exception e) {
        httpSession.setAttribute("status", new Alert("Ahh Sorry, something went wrong from our side.", Alert.ERROR, Alert.ERROR_CLASS));
       }

        return "redirect:/register?";
    }

     // showing page for verify account if account is non-verified after registration
     @GetMapping("/verifyAccount")
     public String verifyAccount(@RequestParam(name = "email", required = false) String userEmail, Model m, HttpSession httpSession) {

        if(userEmail != null) {

            Optional<User> userOptional = this.userRepo.findByEmail(userEmail);

            if(userOptional.isPresent()) {
    
                   User user = userOptional.get();

                   if(user.isVerified()) {
                    httpSession.setAttribute("status", new Alert("User with provided email address is already verified.", Alert.ERROR, Alert.ERROR_CLASS));
                    return "redirect:/verifyAccount?e=userAlreadyVerified";
                 }

                 try {
                    this.mailService.sendAccountConfirmationMail(userEmail, user);
                    httpSession.setAttribute("status", new Alert("An email sent with verification link to the provided email address.", Alert.SUCCESS, Alert.SUCCESS_CLASS));
                 } catch(Exception e) {
                    e.printStackTrace();
                    httpSession.setAttribute("status", new Alert("Ahh Sorry, We were unable to send the verification link, something went wrong from our side.", Alert.ERROR, Alert.ERROR_CLASS));
                 }

            } else {
                httpSession.setAttribute("status", new Alert("User with provided email address doesn't exist, please try valid email address", Alert.ERROR, Alert.ERROR_CLASS));
            }
            
        } 
         m.addAttribute("title", "Verify Your Account | HyperSpaceGamePanel");
         return "verify_account.html";
 
     } 

    // account confirmation/activation:
    @GetMapping("/accountVerify")
    public String verifyAccount(@RequestParam(name = "token", required = false) String tokenValue) {
          try {

            if(tokenValue == null) {
                httpSession.setAttribute("status", new Alert("Ahh Sorry, it was an invalid request.", Alert.ERROR, Alert.ERROR_CLASS));
                return "redirect:/verifyAccount?error=invalidToken";
            }
    
            if(!this.tokenService.isTokenValid(tokenValue)) {
                httpSession.setAttribute("status", new Alert("Ahh Sorry, cannot verify account, invalid token.", Alert.ERROR, Alert.ERROR_CLASS));
                return "redirect:/verifyAccount?error=invalidToken";
            }
    
             Token token = this.tokenService.getToken(tokenValue);

    
             User user = token.getUser();

             System.out.println("User id is:" + user.getId());


             if(user.isVerified()) {
                httpSession.setAttribute("status", new Alert("Your account is already verified.", Alert.ERROR, Alert.ERROR_CLASS));
                return "redirect:/login?e=AccountAlreadyVerified";
             }

             user.setVerified(true);
             this.userRepo.save(user);

             httpSession.setAttribute("status", new Alert("Your account verified successfully, you can login now.", Alert.SUCCESS, Alert.SUCCESS_CLASS));

          } catch(Exception e) {
            e.printStackTrace();
            httpSession.setAttribute("status", new Alert("Ahh Sorry, cannot verify the account, something went wrong from our side", Alert.ERROR, Alert.ERROR_CLASS));
            return "redirect:/verifyAccount?";
          }
          
          return "redirect:/login";
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
 
            httpSession.setAttribute("status", new Alert("Admin registered successfully", Alert.SUCCESS, Alert.SUCCESS_CLASS));

       } catch(Exception e) {
        httpSession.setAttribute("status", new Alert("Ahh Sorry, something went wrong from our side.", Alert.ERROR, Alert.ERROR_CLASS));
        return "redirect:/register";
       }

        return "redirect:/login";
    }



}
