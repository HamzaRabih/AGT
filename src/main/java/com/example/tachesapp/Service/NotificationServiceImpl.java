package com.example.tachesapp.Service;

import com.example.tachesapp.Dao.NotificationsRepo;
import com.example.tachesapp.Dao.UtilisateurRepo;
import com.example.tachesapp.Model.Notification;
import com.example.tachesapp.Model.Tache;
import com.example.tachesapp.Model.Utilisateur;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class NotificationServiceImpl implements NotificationsService{
    @Autowired
    NotificationsRepo notificationsRepo;
    @Autowired
    UtilisateurRepo utilisateurRepo;
    @Autowired
    private JavaMailSender javaMailSender;

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

    public void sendEmailValidation(Tache tache) {
        Utilisateur recepteur = tache.getRecepteur();
        String subject = "Tâche Validée: " + tache.getNomtache();
        String msg = "Votre tâche '" + tache.getNomtache() + "' a été validée par "
                + tache.getUtilisateur().getNom() + " " + tache.getUtilisateur().getPrenom() + ".";
        sendTaskEmail(recepteur.getMail(), subject, msg);
    }

    public void sendEmailAnnulation(Tache tache) {
        Utilisateur recepteur1 =tache.getRecepteur();
        Utilisateur emetteur=utilisateurRepo.findByIdutilisateur(tache.getUtilisateur().getIdutilisateur());
        String Subject="Tâche annulée";
        String msg = "La tâche '" + tache.getNomtache() + "' soumise par " + emetteur.getNom() + " " + emetteur.getPrenom() + " a été annulée.";
        sendTaskEmail(recepteur1.getMail(),Subject,msg);
    }

    public void sendEmailForANewTask(Tache tache) {
        Utilisateur recepteur = tache.getRecepteur();
        Utilisateur emetteur=utilisateurRepo.findByIdutilisateur(tache.getUtilisateur().getIdutilisateur());
        String Subject="";
        String msg="";
        Subject = "Vous avez une nouvelle tâche de : " + emetteur.getNom() + " " + emetteur.getPrenom();
        msg = "nouvelle tâche :" + tache.getNomtache();
        sendTaskEmail(recepteur.getMail(),Subject,msg);
    }

    public void sendEmailRefaire(Tache tache) {
        Utilisateur recepteur1 =tache.getRecepteur();
        Utilisateur emetteur=utilisateurRepo.findByIdutilisateur(tache.getUtilisateur().getIdutilisateur());
        String Subject="Tâche à refaire";
        String msg = "La tâche '" + tache.getNomtache() + "' soumise par " + emetteur.getNom() + " " + emetteur.getPrenom() + " nécessite des ajustements. Veuillez la revoir et effectuer les modifications nécessaires. Merci.";
        sendTaskEmail(recepteur1.getMail(),Subject,msg);
    }

    public void sendEmailTerminee(Tache existingTache) {
        Utilisateur recepteur1 =existingTache.getRecepteur();
        Utilisateur emetteur=utilisateurRepo.findByIdutilisateur(existingTache.getUtilisateur().getIdutilisateur());
        String Subject="Tâche terminée";
        String msg = "La tâche '" + existingTache.getNomtache() + "' de " + recepteur1.getNom() + " " + recepteur1.getPrenom() + " est terminée.";
        sendTaskEmail(emetteur.getMail(),Subject,msg);
    }
}
