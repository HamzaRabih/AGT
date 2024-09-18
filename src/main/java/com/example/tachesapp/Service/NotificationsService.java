package com.example.tachesapp.Service;

import com.example.tachesapp.Model.Notification;
import com.example.tachesapp.Model.Tache;
import com.example.tachesapp.Model.Utilisateur;
import org.springframework.scheduling.annotation.Async;
import org.springframework.ui.Model;


public interface NotificationsService {

    Notification creerNotificationDeCreationTache(Long idrecepteur, Tache tache);

    // pour envoyer un mail

    public void sendEmailValidation(Tache tache) ;
    public void sendEmailAnnulation(Tache tache);
    public void sendEmailForANewTask(Tache tache);
    public void sendEmailRefaire(Tache tache) ;
    public void sendEmailTerminee(Tache existingTache) ;
    public void loadNotificationAndRelationType(Utilisateur utilisateur, Model model);
    }
