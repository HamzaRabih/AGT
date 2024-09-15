package com.example.tachesapp.Controleur;

import com.example.tachesapp.Dao.*;
import com.example.tachesapp.Model.*;
import com.example.tachesapp.Service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.sql.Date;
import java.time.LocalDate;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/")
public class MemoirControleur {

    @Autowired
    NotificationsService notificationsService;
    @Autowired
    PrioriteService prioriteService;
    @Autowired
    TacheRepo tacheRepo;
    @Autowired
    UtilisateurRepo utilisateurRepo;
    @Autowired
    SocieteRepo societeRepo;
    @Autowired
    TacheService tacheService;
    @Autowired
    NotificationsRepo notificationsRepo;
    @Autowired
    PrioriteRepo prioriteRepo;
    @Autowired
    TacheAdminService tacheAdminService;
    @Autowired
    MemoireService memoireService;


    //--Memoir
    //Affichage de memoir.html
    @GetMapping("/Memoir")
    public String Memoir (Authentication authentication, Model model) {
        //récupérer l utilisateur connecté
        String login = authentication.getName();
        Utilisateur utilisateur = utilisateurRepo.findUtilisateursByMail(login);
        //récupérer la societe de l'utilisateur connecté
        Societe societe= societeRepo.findAllByUtilisateurs(utilisateur);
        //récupérer tous les utilisateurs de cette societé
        List<Utilisateur> utilisateurList=utilisateurRepo.findUtilisateursBySociete(societe);
        Long Idutilisateur=utilisateur.getIdutilisateur();
        //récupérer tous les memoire de l'utilisateur connecté
        List<Tache> tacheList=tacheRepo.findAllByUtilisateurAndIsmemoire(utilisateur,true);
        model.addAttribute("tacheList",tacheList);
        model.addAttribute("utilisateurList",utilisateurList);
        model.addAttribute("Idutilisateur",Idutilisateur);
        model.addAttribute("utilisateur",utilisateur);
        notificationsService.loadNotification(utilisateur,model);
        model.addAttribute("utilisateurC",utilisateur);
        tacheAdminService.loadReceivers(utilisateur,model);
        prioriteService.loadPriorites(model);
        return "/pages/memoir";
    }

    // -----------------------------------
// Créer ou mettre à jour une Memoire
// -----------------------------------
    @PostMapping("/CreateMemoire")
    public String CreateMemoire(@ModelAttribute Tache tache, RedirectAttributes redirectAttributes, Authentication authentication) {
        if (tache.getIdtache() == null) {memoireService.createMemoire(tache,redirectAttributes);}
        else {memoireService.updateMemoire(tache,redirectAttributes);}
        return "redirect:/Memoir";
    }

    @GetMapping("/get-Memo/{idTache}")
    @ResponseBody
    public Tache getMemo(@PathVariable Long idTache) {
        // Récupérez la Memo
        return tacheRepo.findByIdtache(idTache);
    }

    //Supprimer une Memo
    @Transactional
    @GetMapping(value = "/deleteMemo/{id}")
    public String deleteMemo(@PathVariable Long id,RedirectAttributes redirectAttributes) {
         tacheRepo.deleteByIdtache(id);
            redirectAttributes.addFlashAttribute("msg", "La Memoir a été suprimée  avec succès");
        return "redirect:/Memoir";
    }
}
