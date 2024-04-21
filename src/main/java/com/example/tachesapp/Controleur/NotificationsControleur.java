package com.example.tachesapp.Controleur;

import com.example.tachesapp.Dao.NotificationsRepo;
import com.example.tachesapp.Dao.UtilisateurRepo;
import com.example.tachesapp.Model.Departement;
import com.example.tachesapp.Model.Notification;
import com.example.tachesapp.Model.Utilisateur;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.persistence.Access;
import java.util.List;

@Controller
@RequestMapping("/")
public class NotificationsControleur {

    @Autowired
    UtilisateurRepo utilisateurRepo;
    @Autowired
    NotificationsRepo notificationsRepo;



    @GetMapping("/lireLesNotif")
    @ResponseBody
    public ResponseEntity<String> lireLesNotif(Authentication authentication) {
        try {
            // Récupérer l'utilisateur connecté
            String login = authentication.getName();
            Utilisateur utilisateur = utilisateurRepo.findUtilisateursByMail(login);

            // Récupérer les notifications de l'utilisateur connecté
            List<Notification> notificationList = notificationsRepo.findByRecepteurOrderByDatenotifDesc(utilisateur);

            // Marquer toutes les notifications comme lues
            notificationList.forEach(notification -> {
                notification.setEstLu(true);
            });

            // Sauvegarder les modifications
            notificationsRepo.saveAll(notificationList);

            return ResponseEntity.ok("Toutes les notifications non lues ont été marquées comme lues.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Une erreur s'est produite lors de la mise à jour des notifications.");
        }

    }


    //Cette fonction permet d'afficher les notifications non lues.
    // Elle est utilisée par la fonction afficherNotif() dans le fichier notifWebSocket.js pour afficher les notifications sur place a laide de Ajax & WebSoket
    @GetMapping("/afficherNotifNonLu")
    @ResponseBody
    public int afficherNotifNonLu(Authentication authentication) {
        // Récupérer l'utilisateur connecté
        String login = authentication.getName();
        Utilisateur utilisateur = utilisateurRepo.findUtilisateursByMail(login);

        // Calculer les notifications non lues de l'utilisateur connecté;
        List<Notification> nonLuesNotificationList = notificationsRepo.findByRecepteurAndEstLu(utilisateur, false);
        // Calculer le nombre de notifications non lues
        int nbrNotifNonLu = nonLuesNotificationList.size();

        return nbrNotifNonLu;
    }
}
