package us.redsols.todo.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.*;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import us.redsols.todo.model.Email;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

@Service
public class EmailService {

    private JavaMailSender javaMailSender;


    @Autowired
    public EmailService(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }



    public void sendEmail(Email email) {

        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper;
        try {
            helper = new MimeMessageHelper(message, true);
            helper.setTo(email.getTo());
            helper.setSubject(email.getSubject());
            helper.setText(email.getHtml(), true);

            helper.setFrom("Redsols <hello@redsols.us>");
        } catch (MessagingException e) {
            e.printStackTrace();
        }

        // Send email
        javaMailSender.send(message);

    }


}
