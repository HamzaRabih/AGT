package com.example.tachesapp.Service;

import com.example.tachesapp.Model.Tache;
import com.example.tachesapp.Model.Utilisateur;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class MailServiceImpl implements MailService{

    @Autowired
    private JavaMailSender javaMailSender;

    // pour envoyer un mail
    @Async//@Async will make it execute in a separate thread
    public void sendTaskEmail(String recipientEmail,String setSubject,String msg) {
        // pour envoiyer un mail
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(recipientEmail);
        message.setSubject(setSubject);
        message.setText(msg+"\n");
        javaMailSender.send(message);
    }

}
