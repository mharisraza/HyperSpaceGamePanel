package com.hyperspacegamepanel.controllers.user;

import java.security.Principal;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.hyperspacegamepanel.controllers.main.DataCenteralizedController;
import com.hyperspacegamepanel.helper.Alert;
import com.hyperspacegamepanel.models.user.UpdateUserForm;
import com.hyperspacegamepanel.services.UserService;

@Controller
@RequestMapping("/me")
public class UserController extends DataCenteralizedController {

    @Autowired
    private UserService userService;

    @Autowired
    private HttpSession httpSession;

    @Autowired
    private SecurityContextLogoutHandler securityContextLogoutHandler;

    @GetMapping(value = { "/", "/dashboard" })
    public String home(Model m) {
        m.addAttribute("title", "Home | HyperSpaceGamePanel");
        return "user/index.html";
    }

    @GetMapping("/profile")
    public String profilePage(Model m, Principal principal, @RequestParam(required = false) String action, @RequestParam(required = false) Integer userId, HttpServletRequest request) {

        if(action != null) {

            switch(action) {

                // delete user account
                case "deleteMyAccount" -> {
                    
                    try {
                        this.userService.deleteUser(userId);
                        securityContextLogoutHandler.logout(request, null, null);
                        httpSession.setAttribute("status", new Alert("Your account deleted successfully.", Alert.SUCCESS, Alert.SUCCESS_CLASS));
                        return "redirect:/login?s=accountDeleted";
                    } catch (Exception e) {
                        if(e.getMessage().contains("User not found")) {
                            httpSession.setAttribute("status", new Alert("Invalid request or action.", Alert.ERROR, Alert.ERROR_CLASS));
                            return "redirect:/me/profile";
                        }
                        httpSession.setAttribute("status", new Alert("We were unable to delete your account at this moment, please try later.", Alert.ERROR, Alert.ERROR_CLASS));
                        return "redirect:/me/profile";
                    }
                }

                default -> {
                    httpSession.setAttribute("status", new Alert("Invalid request or action.", Alert.ERROR, Alert.ERROR_CLASS));
                    return "redirect:/me/profile";
                }

            }
        }

        m.addAttribute("updateUserForm", new UpdateUserForm());
        m.addAttribute("title", getLoggedInUser(principal).getFullName() + " | HyperSpaceGamePanel");
        return "user/profile.html";
    }

    @PostMapping("/update-profile")
    public String updateProfile(@ModelAttribute UpdateUserForm updatedUser, Principal principal) {
           try {
            this.userService.updateUser(updatedUser, getLoggedInUser(principal).getId());
            httpSession.setAttribute("status", new Alert("Your account updated successfully.", Alert.SUCCESS, Alert.SUCCESS_CLASS));
            return "redirect:/me/profile";
           } catch (Exception e) {
            if(e.getMessage().equals("USER_ALREADY_EXISTS_WITH_THIS_USERNAME")) {
                httpSession.setAttribute("status", new Alert("An user already exists with the same username, please use any other.", Alert.ERROR, Alert.ERROR_CLASS));
                return "redirect:/me/profile";
            }
            httpSession.setAttribute("status", new Alert("Sorry, we cannot update your account at this moment, please try later.", Alert.ERROR, Alert.ERROR_CLASS));
            
           }
         return "redirect:/me/profile";
    } 

}
