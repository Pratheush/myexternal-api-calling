package com.mylearning.journalapp.service;

import com.mylearning.journalapp.exception.EmailException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class EmailService {

    private final JavaMailSender javaMailSender;

    @Autowired
    public EmailService(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    public void sendEmail(String to, String subject, String body) {
        log.info("EmailService sendEmail called");
        try {
            // just simply sending mail to myself only to stop sending unknown but also setting subject with to-address to know whom we are sending just for references
            subject = subject.concat(" ::::  "+to);
            to = "myjunkins@gmail.com";
            SimpleMailMessage mail = new SimpleMailMessage();
            mail.setTo(to);
            mail.setSubject(subject);
            mail.setText(body);
            javaMailSender.send(mail);
            log.info("EmailService sendEmail called Email Sent");
        } catch (Exception e) {
            log.error("Exception while sendEmail ", e);
            throw new EmailException("Email Sending Failed " + e.getMessage());
        }
    }
}
