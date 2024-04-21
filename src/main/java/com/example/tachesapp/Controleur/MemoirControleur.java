package com.example.tachesapp.Controleur;

import com.example.tachesapp.Dao.*;
import com.example.tachesapp.Model.*;
import com.example.tachesapp.Service.TacheService;
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


    //--Memoir
    //Affichage de memoir.html
    @GetMapping("/Memoir")
    public String Memoir (Authentication authentication, Model model)
    {
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

        // Récupérer les notifications de l'utilisateur connecté
        List<Notification> notificationList = notificationsRepo.findByRecepteurOrderByDatenotifDesc(utilisateur);
        model.addAttribute("notificationList", notificationList);

        // Calculer les notifications non lues de l'utilisateur connecté;
        List<Notification> nonLuesNotificationList = notificationsRepo.findByRecepteurAndEstLu(utilisateur, false);
        // Calculer le nombre de notifications non lues
        int nbrNotifNonLu = nonLuesNotificationList.size();
        model.addAttribute("nbrNotifNonLu", nbrNotifNonLu);


        model.addAttribute("utilisateurC",utilisateur);

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

        //Les Priorités
        List<Priorite> priorites=prioriteRepo.findAll();
        model.addAttribute("priorites",priorites);



        return "/pages/memoir";
    }
    // -----------------------------------
// Créer ou mettre à jour une Memoire
// -----------------------------------
    @PostMapping("/CreateMemoire")
    public String CreateMemoire(@ModelAttribute Tache tache, RedirectAttributes redirectAttributes, Authentication authentication) {

        if (tache.getIdtache() == null) {
            // Créer une nouvelle tâche
            // Obtenez la date d'ouverture de la tâche
            Date dateouverture = tache.getDateouverture();
            LocalDate dateOuverture1 = dateouverture.toLocalDate();

            // Obtenez la durée estimée de la tâche
            int dureeEstime = tache.getDureestime();

            // Calculez la date d'objectif en ajoutant la durée estimée à la date d'ouverture
            LocalDate dateObjectif = dateOuverture1.plusDays(dureeEstime);

            // Convertissez la date d'objectif en java.sql.Date et mettez à jour la tâche
            tache.setDateobjectif(Date.valueOf(dateObjectif));

            tache.setIsmemoire(true);

            // Enregistrer la tâche memoire
            tacheRepo.save(tache);
            redirectAttributes.addFlashAttribute("msg", "Tâche créée avec succès");

        } else {
            // Créer une nouvelle tâche
            // Obtenez la date d'ouverture de la tâche
            Date dateouverture = tache.getDateouverture();
            LocalDate dateOuverture1 = dateouverture.toLocalDate();

            // Obtenez la durée estimée de la tâche
            int dureeEstime = tache.getDureestime();

            // Calculez la date d'objectif en ajoutant la durée estimée à la date d'ouverture
            LocalDate dateObjectif = dateOuverture1.plusDays(dureeEstime);

            // Convertissez la date d'objectif en java.sql.Date et mettez à jour la tâche
            tache.setDateobjectif(Date.valueOf(dateObjectif));

            tache.setIsmemoire(true);

            // Enregistrer la tâche memoire
            tacheRepo.save(tache);
            // Mettre à jour la tâche existante

            redirectAttributes.addFlashAttribute("msg1", "La tâche a été modifiée avec succès.");
        }
        return "redirect:/Memoir";
    }

    //recuperer une Memo par id (utiliser par ajax pour modifie les departements dans la meme forme)
    @GetMapping("/get-Memo/{idTache}")
    @ResponseBody
    public Tache getMemo(@PathVariable Long idTache) {
        // Récupérez la Memo
        Tache tache=tacheRepo.findByIdtache(idTache);
        return tache;
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
