package com.example.tachesapp.Controleur;

import com.example.tachesapp.Dao.*;
import com.example.tachesapp.Model.*;
import com.example.tachesapp.Service.EquipeService;
import com.example.tachesapp.Service.TacheService;
import com.example.tachesapp.Service.UtilisateurService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Controller
@RequestMapping("/")
public class MonEquipecontroleur {

    @Autowired
    EquipeService equipeService;
    @Autowired
    UtilisateurRepo utilisateurRepo;
    @Autowired
    NotificationsRepo notificationsRepo;
    @Autowired
    TacheService tacheService;
    @Autowired
    TacheRepo tacheRepo;
    @Autowired
    PrioriteRepo prioriteRepo;
    @Autowired
    SocieteRepo societeRepo;
    @Autowired
    UtilisateurService utilisateurService;

    //--------------------------------------------------- MonEquipe
//----Equipe
    //Affichage
    @GetMapping("/MonEquipe")
    public String MonEquipe (Authentication authentication, Model model)
    {
        // Récupérer l'utilisateur connecté
        String login = authentication.getName();
        Utilisateur utilisateur = utilisateurRepo.findUtilisateursByMail(login);

        //Cette fonction a pour but d'obtenir l'équipe et les sous-équipes(si l'un des membres est responsable d'une équipe) de l'utilisateur,
        // afin que l'utilisateur puisse envoyer les tâches uniquement à ses équipes.
        List<Utilisateur> monEquipe=tacheService.findRecepteurs(utilisateur);
        model.addAttribute("monEquipe", monEquipe);

        //Cette fonction a pour but d'obtenir l'équipe et les sous-équipes(si l'un des membres est responsable d'une équipe) de l'utilisateur,
        // afin que l'utilisateur puisse envoyer les tâches uniquement à ses équipes.
        List<Utilisateur> Recepteurs=tacheService.findRecepteurs(utilisateur);

        //Pour mettre la liste en ordre alphabétique
        // Utilisation de la méthode sort de Collections avec un comparateur ignorant la casse
        Collections.sort(Recepteurs, new Comparator<Utilisateur>() {
            @Override
            public int compare(Utilisateur utilisateur1, Utilisateur utilisateur2) {
                // Comparez les noms des utilisateurs sans tenir compte de la casse
                return utilisateur1.getNom().compareToIgnoreCase(utilisateur2.getNom());
            }
        });
        Recepteurs.add(0, utilisateur);
        // La liste Recepteurs est maintenant triée par ordre alphabétique (sans tenir compte de la casse)
        model.addAttribute("Recepteurs",Recepteurs);
        //les taches de mon equipe
        //List<Tache> equipeTaches=tacheRepo.findAllByUtilisateurInAndIsmemoire(Recepteurs,false);
        List<Tache> equipeTaches=tacheRepo.findAllByRecepteurInAndIsmemoire(Recepteurs,false);
        model.addAttribute("equipeTaches",equipeTaches);

        // Récupérer les notifications de l'utilisateur connecté
        List<Notification> notificationList = notificationsRepo.findByRecepteurOrderByDatenotifDesc(utilisateur);
        model.addAttribute("notificationList", notificationList);

        // Calculer les notifications non lues de l'utilisateur connecté;
        List<Notification> nonLuesNotificationList = notificationsRepo.findByRecepteurAndEstLu(utilisateur, false);
        // Calculer le nombre de notifications non lues
        int nbrNotifNonLu = nonLuesNotificationList.size();
        model.addAttribute("nbrNotifNonLu", nbrNotifNonLu);

        model.addAttribute("utilisateurC",utilisateur);

        //Les Priorités
        List<Priorite> priorites=prioriteRepo.findAll();
        model.addAttribute("priorites",priorites);

        //les utilisteurs de la meme societé (pour le champ proprietaire)
        Societe societe= societeRepo.findAllByUtilisateurs(utilisateur);
        List<Utilisateur> utilisateurList=utilisateurRepo.findUtilisateursBySociete(societe);
        model.addAttribute("utilisateurList",utilisateurList);


        return "/pages/equipe";
    }



    // Fonction pour récupérer les taches  associés aux utilisateurs sélectionnés
    // ResponseEntity, qui est une classe de Spring qui permet de contrôler la réponse HTTP.
    //ResponseEntity<List<taches>> : Cette déclaration de type indique que la méthode de contrôleur renverra une réponse HTTP contenant une liste d'objets de type Tache
    //ResponseEntity.ok(taches):signifie que les utilisateurs seront renvoyés au client en tant que réponse HTTP avec un statut de succès (200 OK).
    @GetMapping(value = "/taches")
    public ResponseEntity<List<Tache>> getTacheByUtilisateurs(@RequestParam("utilisateur") List<Long> idutilisateur,Authentication authentication) {
        String login=authentication.getName();
        Utilisateur utilisateurConnecté = utilisateurRepo.findUtilisateursByMail(login);
        List<Tache>  taches=tacheService.findTachesByIdUtilisateurs(idutilisateur,utilisateurConnecté);
        return ResponseEntity.ok(taches);
    }


    @GetMapping(value = "/taches2")
    public ResponseEntity<List<Tache>> getTacheByUtilisateurs2(@RequestParam("utilisateur") List<Long> idutilisateur,Authentication authentication) {
        String login=authentication.getName();
        Utilisateur utilisateurConnecté = utilisateurRepo.findUtilisateursByMail(login);
        List<Tache>  taches=tacheService.findTachesByIdUtilisateurs2(idutilisateur,utilisateurConnecté);
        return ResponseEntity.ok(taches);
    }




    @GetMapping(value = "/tachesEmeteur")
    public ResponseEntity<List<Tache>> getTacheByEmetteur(@RequestParam("utilisateur") List<Long> idemetteur,Authentication authentication) {

        // Récupérer l'utilisateur connecté
        String login = authentication.getName();
        Utilisateur recepteur = utilisateurRepo.findUtilisateursByMail(login);
        List<Tache>  taches=tacheService.findTachesByIdEmetteur(idemetteur,recepteur);
        return ResponseEntity.ok(taches);

    }

    // Fonction pour récupérer tous les taches d utilisateur et de l equipe de l utilisateur
    // utilise a par ajax et javascript  dans equipe.html
    // ResponseEntity, qui est une classe de Spring qui permet de contrôler la réponse HTTP.
    //ResponseEntity<List<taches>> : Cette déclaration de type indique que la méthode de contrôleur renverra une réponse HTTP contenant une liste d'objets de type Tache
    //ResponseEntity.ok(taches):signifie que les utilisateurs seront renvoyés au client en tant que réponse HTTP avec un statut de succès (200 OK).
    @GetMapping(value = "/getAllMembersTask")
    public ResponseEntity<List<Tache>> getAllMembersTask(Authentication authentication)
    {
        // Récupérer l'utilisateur connecté
        String login = authentication.getName();
        Utilisateur utilisateur = utilisateurRepo.findUtilisateursByMail(login);

    //Cette fonction a pour but d'obtenir l'équipe et les sous-équipes(si l'un des membres est responsable d'une équipe) de l'utilisateur,
    // afin que l'utilisateur puisse envoyer les tâches uniquement à ses équipes.
        List<Utilisateur> Recepteurs=tacheService.findRecepteurs(utilisateur);

        //Pour mettre la liste en ordre alphabétique
        // Utilisation de la méthode sort de Collections avec un comparateur ignorant la casse
        Collections.sort(Recepteurs, new Comparator<Utilisateur>() {
            @Override
            public int compare(Utilisateur utilisateur1, Utilisateur utilisateur2) {
                // Comparez les noms des utilisateurs sans tenir compte de la casse
                return utilisateur1.getNom().compareToIgnoreCase(utilisateur2.getNom());
            }
        });
        Recepteurs.add(0, utilisateur);
        // La liste Recepteurs est maintenant triée par ordre alphabétique (sans tenir compte de la casse)
        //les taches de mon equipe
        List<Tache> equipeTaches=tacheRepo.findAllByRecepteurInAndIsmemoire(Recepteurs,false);
       // List<Tache> equipeTaches=tacheService.findAllEquipeTaches(Recepteurs,utilisateur);
        return ResponseEntity.ok(equipeTaches);
    }

}
