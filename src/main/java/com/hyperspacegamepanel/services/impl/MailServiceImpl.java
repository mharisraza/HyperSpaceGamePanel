package com.hyperspacegamepanel.services.impl;

import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.springframework.stereotype.Service;

import com.hyperspacegamepanel.helper.Constants;
import com.hyperspacegamepanel.services.MailService;

@Service
public class MailServiceImpl implements MailService {

    @Override
    public void sendMail(String emailAddress, String subject, String message) throws MessagingException {

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

        InternetAddress internetAddress = new InternetAddress(emailAddress);

        MimeMessage mimeMessage = new MimeMessage(session);
        mimeMessage.setFrom(internetAddress);
        mimeMessage.setRecipient(Message.RecipientType.TO, internetAddress);
        mimeMessage.setSubject(subject);
        mimeMessage.setContent(message, "text/html");
        Transport.send(mimeMessage);
        
    }

    
}
