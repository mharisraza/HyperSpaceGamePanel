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
      
      
      
  }
