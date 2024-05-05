package com.example.tachesapp.Controleur;

import com.example.tachesapp.Dao.*;
import com.example.tachesapp.Model.*;
import com.example.tachesapp.Service.TacheService;
import com.example.tachesapp.Service.UtilisateurService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Controller
@RequestMapping("/")

public class TacheAdminControleur {
    @Autowired
    TacheService tacheService;
    @Autowired
TacheRepo tacheRepo;
    @Autowired
    SocieteRepo societeRepo;
    @Autowired
    DepartementRepo departementRepo;
    @Autowired
    UtilisateurService utilisateurService;
    @Autowired
    UtilisateurRepo utilisateurRepo;
    @Autowired
    NotificationsRepo notificationsRepo;
    @Autowired
    PrioriteRepo prioriteRepo;

    //Affichage de GestionTacheAdmin.html
    @GetMapping("/GestionTacheAdmin")
    public String GestionTacheAdmin( Model model,Authentication authentication)
    {
        //l utilisateur connecté
        String login = authentication.getName();
        Utilisateur utilisateur = utilisateurRepo.findUtilisateursByMail(login);
        model.addAttribute("utilisateurC",utilisateur);

        //Lister les tâches
        List<Tache> tacheList=tacheRepo.findAllByIsmemoire(false);
        model.addAttribute("tacheList",tacheList);

        //lister tous les Societes
        List<Societe> societeList=societeRepo.findAll();
        model.addAttribute("societeList",societeList);

        //lister tous les Societes
        List<Utilisateur> utilisateurs=utilisateurRepo.findAll();
        model.addAttribute("utilisateurs",utilisateurs);

        // Récupérer les notifications de l'utilisateur connecté
        List<Notification> notificationList = notificationsRepo.findByRecepteurOrderByDatenotifDesc(utilisateur);
        model.addAttribute("notificationList", notificationList);

        // Calculer les notifications non lues de l'utilisateur connecté,pour l'affiché;
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

        //Les Priorités
        List<Priorite> priorites=prioriteRepo.findAll();
        model.addAttribute("priorites",priorites);

        //les utilisteurs de la meme societé (pour le champ proprietaire)
        Societe societe= societeRepo.findAllByUtilisateurs(utilisateur);
        List<Utilisateur> utilisateurList=utilisateurRepo.findUtilisateursBySociete(societe);
        model.addAttribute("utilisateurList2",utilisateurList);

        return "/pages/GestionTacheAdmin";
    }

    //fonction utiliser dans GestionTacheAdmin.HTML POUR recuperer les tache parent
    @GetMapping("/get-tachesParents-by-idUtilisateur/{idUtilisateur}")
    @ResponseBody
    public List<Tache> getTachesParentsByidUtilisateur(@PathVariable Long idUtilisateur) {
        Utilisateur utilisateur=utilisateurRepo.findByIdutilisateur(idUtilisateur);
        // Pour mettre à jour la sélection de la tâche parente (récupérer seulement les tâches non terminées ayant des tâches successives)." +
         List<Tache> tacheList1=tacheService.findTacheParent(utilisateur,true);
        //List<Tache> tacheList1=tacheRepo.findATacheParents(utilisateur,true,"En attente","Terminée","À refaire",Programmée");
        return tacheList1;
    }

    //fonction utiliser dans GestionTacheAdmin.HTML POUR recuperer les utilisateur de la meme societe
    @GetMapping("/get-Propritair-by-idUtilisateur/{idUtilisateur}")
    @ResponseBody
    public List<Utilisateur> getPropritairByIdUtilisateur(@PathVariable Long idUtilisateur) {
        Utilisateur utilisateur=utilisateurRepo.findByIdutilisateur(idUtilisateur);
        //les utilisteurs de la meme societé (pour le champ proprietaire)
        Societe societe= societeRepo.findAllByUtilisateurs(utilisateur);
        List<Utilisateur> utilisateurList=utilisateurRepo.findUtilisateursBySociete(societe);

        //Pour mettre la liste en ordre alphabétique
        // Utilisation de la méthode sort de Collections avec un comparateur ignorant la casse
        Collections.sort(utilisateurList, new Comparator<Utilisateur>() {
            @Override
            public int compare(Utilisateur utilisateur1, Utilisateur utilisateur2) {
                // Comparez les noms des utilisateurs sans tenir compte de la casse
                return utilisateur1.getNom().compareToIgnoreCase(utilisateur2.getNom());
            }
        });
        return utilisateurList;
    }

    //fonction utiliser dans GestionTacheAdmin.HTML POUR recuperer les utilisateur de la meme societe
    @GetMapping("/get-Destinatair-by-idUtilisateur/{idUtilisateur}")
    @ResponseBody
    public List<Utilisateur> getDestinatairByIdUtilisateur(@PathVariable Long idUtilisateur) {
        Utilisateur utilisateur=utilisateurRepo.findByIdutilisateur(idUtilisateur);

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

        return Recepteurs;
    }

    //Supprimer Tache
    @Transactional
    @GetMapping(value = "/deleteTache/{id}")
    public String deleteTache(@PathVariable Long id,RedirectAttributes redirectAttributes) {
        //supprimer les taches successive
        Tache tache = tacheRepo.findTacheByIdtache(id);

        List<Tache> tacheList = tacheRepo.findAllByTacheparente(tache);

        //supprimer les tâches programmées s'il en existe.
        if (tacheList != null) {
            // Mettre les tâches programmées En attente et définir la date d'objectif
            for (Tache t : tacheList) {
                tacheRepo.deleteById(t.getIdtache());
            }
        }

        tacheRepo.deleteById(id);
        redirectAttributes.addFlashAttribute("supmessage","La tache a été suprimée  avec succès");
        return "redirect:/GestionTacheAdmin";
    }


    // -----------------------------------
    //mettre à jour une tâche
    // -----------------------------------
    @PostMapping("/UpdateTacheAdmin")
    public String UpdateTacheAdmin(@ModelAttribute Tache tache, RedirectAttributes redirectAttributes, Authentication authentication,Model model) {
        // Récupérer l'utilisateur connecté
        String login = authentication.getName();
        Utilisateur utilisateurconnecte = utilisateurRepo.findUtilisateursByMail(login);

        switch (tache.getStatut()) {
            case "En attente":
                tacheService.updateTacheWithStatus(tache, utilisateurconnecte);
                redirectAttributes.addFlashAttribute("msg", "La tâche a été modifiée avec succès.");
                break;
            case "Programmée":
                tacheService.UpdateTacheToProgramme(tache, redirectAttributes, utilisateurconnecte);
                redirectAttributes.addFlashAttribute("msg", "La tâche a été modifiée avec succès.");

                break;
            case "Terminée":
                tacheService.updateTacheWithStatus(tache, utilisateurconnecte);
                redirectAttributes.addFlashAttribute("msg", "La tâche a été modifiée avec succès.");

                break;
            case "Validée":
                    tacheService.UpdateTacheToValide(tache, redirectAttributes, utilisateurconnecte);
                    redirectAttributes.addFlashAttribute("msg", "La tâche a été modifiée avec succès.");
                break;
            case "À refaire":
                tacheService.updateTacheWithStatus(tache, utilisateurconnecte);
                redirectAttributes.addFlashAttribute("msg", "La tâche a été modifiée avec succès.");
                break;
            case "En cours":
                tacheService.updateTacheWithStatus(tache, utilisateurconnecte);
                redirectAttributes.addFlashAttribute("msg", "La tâche a été modifiée avec succès.");
                break;
            case "Annulée":
                if (tacheService.aDesTachesSecondaires(tache)) {
                    //annuler les taches Secondaires;
                   List<Tache> tachesSecondaires= tacheService.lesTacheSecondairesParTacheParent(tache,utilisateurconnecte);
                    StringBuilder idTachesSecondaires = new StringBuilder(":");
                    for (Tache t : tachesSecondaires) {
                        idTachesSecondaires.append(t.getIdtache()).append(" ,");
                    }
                    String erreurMessage = "Cette tâche a des tâches secondaires  " + idTachesSecondaires + " vous ne pouvez pas annuler cette tâche sans annuler les tâches secondaires.";
                    redirectAttributes.addFlashAttribute("erreurMessage", erreurMessage);

                }else {
                    tacheService.UpdateTacheToAnnuler(tache, utilisateurconnecte);
                    redirectAttributes.addFlashAttribute("msg", "La tâche a été modifiée avec succès.");

                }
                break;
            default:
                // Gérer les cas non prévus
                break;
        }

        return "redirect:/GestionTacheAdmin";
    }


    @GetMapping(value = "/getAllTasks")
    public ResponseEntity<List<Tache>> getAllTasks(Authentication authentication)
    {
        // Récupérer l'utilisateur connecté
        String login = authentication.getName();
        Utilisateur utilisateur = utilisateurRepo.findUtilisateursByMail(login);

        //Lister les tâches
        List<Tache> tacheList=tacheRepo.findAllByIsmemoire(false);


        return ResponseEntity.ok(tacheList);
    }


}
