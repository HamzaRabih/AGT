package com.example.tachesapp.Controleur;

import com.example.tachesapp.Dao.*;
import com.example.tachesapp.Model.*;

import com.example.tachesapp.Service.*;
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
    @Autowired
    TacheAdminService tacheAdminService;
    @Autowired
    PrioriteService prioriteService;
    @Autowired
    UtilisateurService utilisateurService;


    //--------------------------------------Gestion Taches
    //Affichage de creeTache.HTML
    @GetMapping("/CreerTache")
    public String home(Authentication authentication, Model model)
    {
        //l utilisateur connecté
        String login = authentication.getName();
        Utilisateur utilisateur = utilisateurRepo.findUtilisateursByMail(login);
        model.addAttribute("utilisateurC",utilisateur);
        utilisateurService.loadSocietieMembers(utilisateur,model);
        tacheAdminService.loadReceivers(utilisateur,model);
        notificationService.loadNotificationAndRelationType(utilisateur,model);
        prioriteService.loadPriorites(model);
        //tacheService.loadRelationType(model);
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
       tacheAdminService.loadReceivers(utilisateur,model);
       notificationService.loadNotificationAndRelationType(utilisateur,model);
       prioriteService.loadPriorites(model);
       utilisateurService.loadSocietieMembers(utilisateur,model);
       //tacheService.loadRelationType(model);
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
