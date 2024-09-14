package com.example.tachesapp.Service;

import com.example.tachesapp.Model.Notification;
import com.example.tachesapp.Model.Tache;
import com.example.tachesapp.Model.Utilisateur;
import org.springframework.scheduling.annotation.Async;


public interface NotificationsService {

    Notification creerNotificationDeCreationTache(Long idrecepteur, Tache tache);

    // pour envoyer un mail
    @Async
    public void sendTaskEmail(String recipientEmail,String setSubject,String msg) ;

    public void sendEmailValidation(Tache tache) ;

    public void sendEmailAnnulation(Tache tache);

    public void sendEmailForANewTask(Tache tache);
    public void sendEmailRefaire(Tache tache) ;
    public void sendEmailTerminee(Tache existingTache) ;

    }
