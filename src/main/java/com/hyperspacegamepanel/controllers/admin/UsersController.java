package com.hyperspacegamepanel.controllers.admin;

import java.util.Optional;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.hyperspacegamepanel.controllers.main.HelperController;
import com.hyperspacegamepanel.dtos.UserDto;
import com.hyperspacegamepanel.entities.User;
import com.hyperspacegamepanel.helper.Helper;
import com.hyperspacegamepanel.repositories.UserRepository;
import com.hyperspacegamepanel.services.MailService;
import com.hyperspacegamepanel.services.UserService;

@Controller
@RequestMapping("/admin/user")
public class UsersController extends HelperController {

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private HttpSession httpSession;

    @Autowired
    private UserService userService;

    @Autowired
    private MailService mailService;

    @GetMapping("")
    public String userDetails(@RequestParam(required = false) Integer id, Model m) {
        if (id == null) {
            httpSession.setAttribute("status", "CANT_FIND_USER_WITH_PROVIDED_ID");
            return "redirect:/admin/users/all";
        }
        Optional<User> user = this.userRepo.findById(id);
        if (!user.isPresent()) {
            httpSession.setAttribute("status", "CANT_FIND_USER_WITH_PROVIDED_ID");
            return "redirect:/admin/users/all";
        }

        m.addAttribute("user", user.get());
        m.addAttribute("title", user.get().getFullName() + " | HyperSpaceGamePanel");
        return "admin/user.html";
    }


    // showing page where admin can add user.
    @GetMapping("/new")
    public String newUser(Model m) {
        m.addAttribute("title", "Add New User | HyperSpaceGamePanel");
        return "admin/new_user.html";
    }

    // processing and handling logic for adding new user via Admin Panel
    @PostMapping("/new")
    public String processNewUser(
        @RequestParam(required = false) String fullName, 
        @RequestParam(required = false) String email, 
        @RequestParam(required = false) String role,
         Model m) {

            if(fullName.isBlank() || email.isBlank() || role.isBlank()) {
                httpSession.setAttribute("status", "REQUIRED_FIELDS_ARE_BLANK");
                return "redirect:/admin/user/new";
            }

            if(this.userRepo.existsByEmail(email)) {
                httpSession.setAttribute("status", "USER_ALREADY_EXISTS_WITH_PROVIDED_EMAIL");
                return "redirect:/admin/user/new";
            }

            if(role.equals("admin")) {
                role = "ROLE_ADMIN";
            } else if (role.equals("user")) {
                role = "ROLE_NORMAL";
            }

            String randomGeneratedPassword = Helper.randomPasswordGenerator();
            String message = String.format("<h1 style='font-size: 18px;'>Dear, %s!</h1> <br>Your account at <strong>HyperSpaceGamePanel</strong> created successfully with the role of <strong>%s</strong> <br> Please use the following credentials to access your account: <br> Email Address:  <strong>%s</strong> <br>Password: <strong>%s</strong> (this password is purely random generated) <br>Navigate to Account Settings to change the credentials to your needs. <br>Thank you.", fullName, role, email, randomGeneratedPassword);

            UserDto userDto = new UserDto();
            userDto.setFullName(fullName);
            userDto.setEmail(email);
            userDto.setEnabled(true);
            userDto.setPassword(randomGeneratedPassword);
            userDto.setRole(role);

            try {
                mailService.sendMail(email, "Account Created Successfully", message);
                UserDto user = userService.createUser(userDto);
                httpSession.setAttribute("status", "USER_CREATED_SUCCESSFULLY");
                return "redirect:/admin/user?id="+user.getId();
            } catch (Exception e) {
                httpSession.setAttribute("status", "SOMETHING_WENT_WRONG");
            }

            return "redirect:/admin/user/new/";

    }
    
}
