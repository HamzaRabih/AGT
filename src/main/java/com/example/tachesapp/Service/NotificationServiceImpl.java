package com.example.tachesapp.Service;

import com.example.tachesapp.Dao.NotificationsRepo;
import com.example.tachesapp.Dao.UtilisateurRepo;
import com.example.tachesapp.Model.Notification;
import com.example.tachesapp.Model.Tache;
import com.example.tachesapp.Model.Utilisateur;
import com.example.tachesapp.Utilité.RelationType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class NotificationServiceImpl implements NotificationsService{
    @Autowired
    NotificationsRepo notificationsRepo;
    @Autowired
    UtilisateurRepo utilisateurRepo;
    @Autowired
    MailService mailService;

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



    public void sendEmailValidation(Tache tache) {
        Utilisateur recepteur = tache.getRecepteur();
        String subject = "Tâche Validée: " + tache.getNomtache();
        String msg = "Votre tâche '" + tache.getNomtache() + "' a été validée par "
                + tache.getUtilisateur().getNom() + " " + tache.getUtilisateur().getPrenom() + ".";
        mailService.sendTaskEmail(recepteur.getMail(), subject, msg);
    }

    public void sendEmailAnnulation(Tache tache) {
        Utilisateur recepteur1 =tache.getRecepteur();
        Utilisateur emetteur=utilisateurRepo.findByIdutilisateur(tache.getUtilisateur().getIdutilisateur());
        String Subject="Tâche annulée";
        String msg = "La tâche '" + tache.getNomtache() + "' soumise par " + emetteur.getNom() + " " + emetteur.getPrenom() + " a été annulée.";
        mailService.sendTaskEmail(recepteur1.getMail(),Subject,msg);
    }

    public void sendEmailForANewTask(Tache tache) {
        Utilisateur recepteur = tache.getRecepteur();
        Utilisateur emetteur=utilisateurRepo.findByIdutilisateur(tache.getUtilisateur().getIdutilisateur());
        String Subject="";
        String msg="";
        Subject = "Vous avez une nouvelle tâche de : " + emetteur.getNom() + " " + emetteur.getPrenom();
        msg = "nouvelle tâche :" + tache.getNomtache();
        mailService.sendTaskEmail(recepteur.getMail(),Subject,msg);
    }

    public void sendEmailRefaire(Tache tache) {
        Utilisateur recepteur1 =tache.getRecepteur();
        Utilisateur emetteur=utilisateurRepo.findByIdutilisateur(tache.getUtilisateur().getIdutilisateur());
        String Subject="Tâche à refaire";
        String msg = "La tâche '" + tache.getNomtache() + "' soumise par " + emetteur.getNom() + " " + emetteur.getPrenom() + " nécessite des ajustements. Veuillez la revoir et effectuer les modifications nécessaires. Merci.";
        mailService.sendTaskEmail(recepteur1.getMail(),Subject,msg);
    }

    public void sendEmailTerminee(Tache existingTache) {
        Utilisateur recepteur1 =existingTache.getRecepteur();
        Utilisateur emetteur=utilisateurRepo.findByIdutilisateur(existingTache.getUtilisateur().getIdutilisateur());
        String Subject="Tâche terminée";
        String msg = "La tâche '" + existingTache.getNomtache() + "' de " + recepteur1.getNom() + " " + recepteur1.getPrenom() + " est terminée.";
        mailService.sendTaskEmail(emetteur.getMail(),Subject,msg);
    }

    public void loadNotificationAndRelationType(Utilisateur utilisateur,Model model){
        // Récupérer les notifications de l'utilisateur connecté
        List<Notification> notificationList = notificationsRepo.findByRecepteurOrderByDatenotifDesc(utilisateur);
        model.addAttribute("notificationList", notificationList);

        // Calculer les notifications non lues de l'utilisateur connecté;
        List<Notification> nonLuesNotificationList = notificationsRepo.findByRecepteurAndEstLu(utilisateur, false);
        // Calculer le nombre de notifications non lues
        int nbrNotifNonLu = nonLuesNotificationList.size();
        model.addAttribute("nbrNotifNonLu", nbrNotifNonLu);

        List<RelationType> relationTypes=new ArrayList<>();
        relationTypes.add(RelationType.SUBORDONNE);
        relationTypes.add(RelationType.COLLEGUE);
        relationTypes.add(RelationType.SUPERIEUR);
        model.addAttribute("relationTypes", relationTypes);   // Ajouter l'attribut "relationType" au modèle

    }
}
