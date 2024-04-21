package com.example.tachesapp.Service;

import com.example.tachesapp.Dao.NotificationsRepo;
import com.example.tachesapp.Dao.UtilisateurRepo;
import com.example.tachesapp.Model.Notification;
import com.example.tachesapp.Model.Tache;
import com.example.tachesapp.Model.Utilisateur;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class NotificationServiceImpl implements NotificationsService{
    @Autowired
    NotificationsRepo notificationsRepo;
    @Autowired
    UtilisateurRepo utilisateurRepo;
    @Override
    public Notification creerNotificationDeCreationTache(Long idrecepteur, Tache tache) {
        Notification notification=new Notification();
        LocalDateTime dateNotif=LocalDateTime.now();

        //modifier la date
        notification.setDatenotif(dateNotif);

        //trouver et modifier l emetteur et le recepteur par id
        Utilisateur emetteur=utilisateurRepo.findByIdutilisateur(tache.getUtilisateur().getIdutilisateur());
        Utilisateur recepteur=utilisateurRepo.findByIdutilisateur(idrecepteur);

        //creer le message de notif
        String Detail="Nouvelle tache de "+emetteur.getNom()+" "+emetteur.getPrenom();
        notification.setRecepteur(recepteur);
        //Mettre la notification en non lu
        notification.setEstLu(false);
        notification.setEmetteur(emetteur);
        notification.setDetail(Detail);


        //cree la notification
       return notificationsRepo.save(notification);
    }
}
