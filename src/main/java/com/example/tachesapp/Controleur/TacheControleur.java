package com.example.tachesapp.Controleur;

import com.example.tachesapp.Dao.*;
import com.example.tachesapp.Model.*;

import com.example.tachesapp.Service.NotificationsService;
import com.example.tachesapp.Service.TacheService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.sql.Date;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Controller
@RequestMapping("/")
public class TacheControleur {
    @Autowired
    TacheRepo tacheRepo;
    @Autowired
    UtilisateurRepo utilisateurRepo;
    @Autowired
    SocieteRepo societeRepo;
    @Autowired
    TacheService tacheService;
    @Autowired
    NotificationsService notificationService;
    @Autowired
    NotificationsRepo notificationsRepo;
    @Autowired
    EquipeRepo equipeRepo;
    @Autowired
    PrioriteRepo prioriteRepo;


    //--------------------------------------Gestion Taches
    //Affichage de creeTache.HTML
    @GetMapping("/CreerTache")
    public String home(Authentication authentication, Model model)
    {
        //l utilisateur connecté
        String login = authentication.getName();
        Utilisateur utilisateur = utilisateurRepo.findUtilisateursByMail(login);
        model.addAttribute("utilisateurC",utilisateur);

        //les utilisteurs de la meme societé (pour le champ proprietaire)
        Societe societe= societeRepo.findAllByUtilisateurs(utilisateur);
        List<Utilisateur> utilisateurList=utilisateurRepo.findUtilisateursBySociete(societe);
        model.addAttribute("utilisateurList",utilisateurList);

        //Pour mettre la liste en ordre alphabétique
        // Utilisation de la méthode sort de Collections avec un comparateur ignorant la casse
        Collections.sort(utilisateurList, new Comparator<Utilisateur>() {
            @Override
            public int compare(Utilisateur utilisateur1, Utilisateur utilisateur2) {
                // Comparez les noms des utilisateurs sans tenir compte de la casse
                return utilisateur1.getNom().compareToIgnoreCase(utilisateur2.getNom());
            }
        });

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

        //les mebres d equipe de lutilisateur connecté
        Equipe equipe=equipeRepo.findEquipeByResponsable(utilisateur);
        if (equipe != null) {
            List<Utilisateur> membres = equipe.getMembres();
            model.addAttribute("membres", membres);
        } else {
            List<Utilisateur> membres=null;
            model.addAttribute("membres",membres);
        }

        //les taches de mon equipe
        //List<Tache> equipeTaches=tacheRepo.findAllByUtilisateurInAndIsmemoire(Recepteurs,false);
        List<Tache> equipeTaches=tacheRepo.findAllByRecepteurInAndIsmemoire(Recepteurs,false);
        model.addAttribute("equipeTaches",equipeTaches);

        // Récupérer les notifications de l'utilisateur connecté
        List<Notification> notificationList = notificationsRepo.findByRecepteurOrderByDatenotifDesc(utilisateur);
        model.addAttribute("notificationList", notificationList);

        // Calculer les notifications non lues de l'utilisateur connecté,pour l'affiché;
        List<Notification> nonLuesNotificationList = notificationsRepo.findByRecepteurAndEstLu(utilisateur, false);
        // Calculer le nombre de notifications non lues
        int nbrNotifNonLu = nonLuesNotificationList.size();
        model.addAttribute("nbrNotifNonLu", nbrNotifNonLu);

        //Les Priorité
        List<Priorite> priorites=prioriteRepo.findAll();
        model.addAttribute("priorites",priorites);
        System.out.println(priorites);
        return "creeTache";
    }

    //--Mes taches
    //Affichage de mesTache.html
    @GetMapping("/")
    public String MesTache (Authentication authentication,Model model)
    {
        String login = authentication.getName();
        Utilisateur utilisateur = utilisateurRepo.findUtilisateursByMail(login);
        model.addAttribute("utilisateurC",utilisateur);

        List<Tache> tacheList=tacheRepo.findAllByRecepteur(utilisateur);
        model.addAttribute("tacheList",tacheList);

        //-----Cette partie a pour but d'obtenir les respo de l'utilisateur,
        List<Utilisateur> Emetteurs=tacheService.findEmetteurs(utilisateur);

        //Pour mettre la liste en ordre alphabétique
        // Utilisation de la méthode sort de Collections avec un comparateur ignorant la casse
        Collections.sort(Emetteurs, new Comparator<Utilisateur>() {
            @Override
            public int compare(Utilisateur utilisateur1, Utilisateur utilisateur2) {
                // Comparez les noms des utilisateurs sans tenir compte de la casse
                return utilisateur1.getNom().compareToIgnoreCase(utilisateur2.getNom());
            }
        });
        Emetteurs.add(0, utilisateur);
        // La liste Recepteurs est maintenant triée par ordre alphabétique (sans tenir compte de la casse)
        model.addAttribute("Emetteurs",Emetteurs);
        //------------------

        // Récupérer les notifications de l'utilisateur connecté
        List<Notification> notificationList = notificationsRepo.findByRecepteurOrderByDatenotifDesc(utilisateur);
        model.addAttribute("notificationList", notificationList);


        // Calculer les notifications non lues de l'utilisateur connecté;
        List<Notification> nonLuesNotificationList = notificationsRepo.findByRecepteurAndEstLu(utilisateur, false);
        // Calculer le nombre de notifications non lues
        int nbrNotifNonLu = nonLuesNotificationList.size();
        model.addAttribute("nbrNotifNonLu", nbrNotifNonLu);


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

        //Les Priorité
        List<Priorite> priorites=prioriteRepo.findAll();
        model.addAttribute("priorites",priorites);


        //les utilisteurs de la meme societé (pour le champ proprietaire)
        Societe societe= societeRepo.findAllByUtilisateurs(utilisateur);
        List<Utilisateur> utilisateurList=utilisateurRepo.findUtilisateursBySociete(societe);
        model.addAttribute("utilisateurList",utilisateurList);


        return "/pages/mesTache";
    }


    @GetMapping(value = "/getAllMyTask")
    public ResponseEntity<List<Tache>> getAllMyTask(Authentication authentication, Model model) {
        return tacheService.getAllMyTask( authentication, model);
    }

    // -----------------------------------
// Créer ou mettre à jour une tâche
// -----------------------------------
    @PostMapping("/CreateTache")
    public String CreateTache(@ModelAttribute Tache tache, RedirectAttributes redirectAttributes, Authentication authentication) {
        tacheService.CreateTache(tache,redirectAttributes,authentication);
        return "redirect:/CreerTache";
    }

    // -----------------------------------
// Pour modifier le statut d'une tâche avec un clic (utilisé par modifyStatus() dans notifWebSoket.js lier par AJAX)
// -----------------------------------
    @GetMapping("/tasks/{tacheId}")
    @ResponseBody
    public Tache updateTacheStatut(@PathVariable Long tacheId,Authentication authentication) {
   return tacheService.updateTacheStatut(tacheId,authentication) ;
    }



    //Valider un statut
    @Transactional
    @GetMapping(value = "/ValiderStatut/{id}")
    @ResponseBody
    public Tache ValiderStatut(@PathVariable Long id,Authentication authentication) {
   return  tacheService.ValiderStatut(id, authentication);

    }


    //Refaire une Tache
    @Transactional
    @GetMapping(value = "/RefaireTache/{id}")
    @ResponseBody
    public Tache RefaireTache(@PathVariable Long id,Authentication authentication) {
    return tacheService.RefaireTache( id, authentication);
    }

    //Annuler un statut
    @Transactional
    @GetMapping(value = "/AnnulerTache/{id}")
    @ResponseBody
    public Tache AnnulerTache(@PathVariable Long id,Authentication authentication) {
    return tacheService.AnnulerTache(id,authentication) ;
    }

    //Indexer une tache
    @Transactional
    @GetMapping(value = "/indexerTache/{id}")
    @ResponseBody
    public Tache IndexerTache(@PathVariable Long id,Authentication authentication) {
    return tacheService.IndexerTache(id, authentication) ;
    }


    //pour afficher les taches Parentes
    @Transactional
    @GetMapping(value = "/tachesParentes")
    @ResponseBody
    public List<Tache> tachesParentes(Authentication authentication) {
    return tacheService.tachesParentes(authentication) ;
    }


    //recuperer une tache par id (utiliser par ajax pour recuperer les info des ta^che a modifié du formulaire)
    @GetMapping("/get-Tache/{idTache}")
    @ResponseBody
    public Tache getTache(@PathVariable Long idTache) {
        // Récupérez la Memo
        Tache tache=tacheRepo.findByIdtache(idTache);
        return tache;
    }

    //Trouver le role d utilisateur connecté
    @GetMapping(value = "/Role")
    @ResponseBody
    public Utilisateur Role(Authentication authentication) {
        //l utilisateur connecté
        String login = authentication.getName();
        Utilisateur utilisateur = utilisateurRepo.findUtilisateursByMail(login);
        return utilisateur;
    }



   //
    @GetMapping(value = "/membresDeLequipeDutilisateur")
    @ResponseBody
    public  List<Utilisateur> membresDeLequipeDutilisateur(Authentication authentication) {
     return tacheService.membresDeLequipeDutilisateur(authentication);
    }

}
