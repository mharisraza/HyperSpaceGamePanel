package com.hyperspacegamepanel.services.impl;


import java.util.Properties;
import java.util.concurrent.CompletableFuture;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import com.hyperspacegamepanel.helper.Constants;
import com.hyperspacegamepanel.helper.Helper;
import com.hyperspacegamepanel.models.user.User;
import com.hyperspacegamepanel.services.MailService;
import com.hyperspacegamepanel.services.TokenService;

@Service
public class MailServiceImpl implements MailService {

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private TemplateEngine templateEngine;

    @Override
    @Async
    public CompletableFuture<Void> sendMail(String to, String subject, String body) throws Exception {

        //if you want to use your own Mail service, you'll need to change the below details to
        // correctly configure mail server, currently this is configured for gmail mail service.

        Properties props = new Properties();

        props.put("mail.smtp.auth", true);
        props.put("mail.smtp.starttls.enable", true);
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", 587);

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(Constants.EMAIL_FROM, Constants.EMAIL_APP_PASSWORD);
            }
        });

        InternetAddress sendTo = new InternetAddress(to);

        MimeMessage mimeMessage = new MimeMessage(session);
        mimeMessage.setFrom(Constants.EMAIL_FROM);
        mimeMessage.setRecipient(Message.RecipientType.TO, sendTo);
        mimeMessage.setSubject(subject);
        mimeMessage.setContent(body, "text/html");
        Transport.send(mimeMessage);
        return CompletableFuture.completedFuture(null);
        
    }

    @Override
    @Async
    public CompletableFuture<Void> sendAccountConfirmationMail(String to, User user) throws Exception {

        String generatedToken = this.tokenService.generateToken(user).get();

        Context context = new Context();
        context.setVariable("userName", user.getFullName());
        context.setVariable("accountActivationLink", Helper.getBaseURL(request) + "/verifyAccount?token=" + generatedToken);

        String message = templateEngine.process("mails/account-confirmation.html", context);
        this.sendMail(to, "Account Activation Confirmation", message);
        return CompletableFuture.completedFuture(null);
    }

    @Override
    @Async
    public CompletableFuture<Void> sendResetPasswordMail(String to, User user) throws Exception {
        String generatedToken = this.tokenService.generateToken(user).get();

        Context context = new Context();
        context.setVariable("userName", user.getFullName());
        context.setVariable("resetPasswordLink", Helper.getBaseURL(request) + "/resetPassword?token=" + generatedToken);

        String message = templateEngine.process("mails/reset-password-request.html", context);
        this.sendMail(to, "Password Reset Request", message);

        return CompletableFuture.completedFuture(null);
    }    
    
}
