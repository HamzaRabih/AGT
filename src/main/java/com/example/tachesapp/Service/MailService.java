package com.example.tachesapp.Service;

import org.springframework.scheduling.annotation.Async;

public interface MailService {

    @Async
    public void sendTaskEmail(String recipientEmail,String setSubject,String msg) ;
}
