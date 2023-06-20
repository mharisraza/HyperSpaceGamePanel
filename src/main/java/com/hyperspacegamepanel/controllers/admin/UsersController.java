package com.hyperspacegamepanel.controllers.admin;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.hyperspacegamepanel.controllers.main.DataCenteralizedController;
import com.hyperspacegamepanel.helper.Alert;
import com.hyperspacegamepanel.helper.Helper;
import com.hyperspacegamepanel.models.user.User;
import com.hyperspacegamepanel.services.MailService;
import com.hyperspacegamepanel.services.UserService;

@Controller
@RequestMapping("/admin/user")
public class UsersController extends DataCenteralizedController {

    @Autowired
    private HttpSession httpSession;

    @Autowired
    private UserService userService;

    @Autowired
    private MailService mailService;

    @GetMapping("/view/{id}")
    public String userDetails(@PathVariable(required = false) Integer id, Model m) {
        
        if (id == null) {
            httpSession.setAttribute("status", new Alert("Cannot find the user.", Alert.ERROR, Alert.ERROR_CLASS));
            return "redirect:/admin/users";
        }

        try {
           User user = this.userService.getUser(id).join();
           m.addAttribute("user", user);
           m.addAttribute("title", user.getFullName() + " | HyperSpaceGamePanel");

        } catch (Exception e) {
            httpSession.setAttribute("status", new Alert("Something went wrong, please try later.", Alert.ERROR, Alert.ERROR_CLASS));
            return "redirect:/admin/dashboard";
        }        
        return "admin/user_module/user.html";
    }


    // showing page where admin can add user.
    @GetMapping("/new")
    public String newUser(Model m) {
        m.addAttribute("title", "Add New User | HyperSpaceGamePanel");
        return "admin/user_module/new_user.html";
    }

    // processing and handling logic for adding new user via Admin Panel
    @PostMapping("/new")
    public String processNewUser(
        @RequestParam(required = false) String fullName, 
        @RequestParam(required = false) String email, 
        @RequestParam(required = false) String role,
        @RequestParam(required = false) String username,
         Model m) {

            if(fullName.isBlank() || email.isBlank() || role.isBlank() || username.isBlank()) {
                httpSession.setAttribute("status", new Alert("Please fill all the below fields.", Alert.ERROR, Alert.ERROR_CLASS));
                return "redirect:/admin/user/new";
            }

            String randomGeneratedPassword = Helper.randomPasswordGenerator();

            User user = new User();

            user.setEmail(email);
            user.setRole(role);
            user.setFullName(fullName);
            user.setUsername(username);
            user.setPassword(randomGeneratedPassword);

            try {
                 this.userService.createUser(user);
                 this.mailService.sendAccountCreatedSuccessMail(email, user);
            } catch (Exception e) {
                if(e.getMessage().equals("USER_ALREADY_EXISTS")) {
                    httpSession.setAttribute("status", new Alert("An user already exists with same username or email, please try another one.", Alert.ERROR, Alert.ERROR_CLASS));
                }
                httpSession.setAttribute("status", new Alert("Unable to create the user at this moment, please try later.", Alert.ERROR, Alert.ERROR_CLASS));
                return "redirect:/admin/user/new";
            }

            return "redirect:/admin/user/new/";

    }
    
}
