package com.example.tachesapp.Service;

import com.example.tachesapp.Dao.*;
import com.example.tachesapp.Model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Service
public class TacheAdminServiceImpl implements TacheAdminService{
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
    @Autowired
    PrioriteService prioriteService;




    @Override
    public void GestionTacheAdmin(Model model, Authentication authentication) {
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

        loadReceivers(utilisateur,model);

        prioriteService.loadPriorites(model);

        utilisateurService.loadSocietieMembers(utilisateur,model);

    }


    public void loadReceivers(Utilisateur utilisateur, Model model) {
        // Récupérer les récepteurs
       // List<Utilisateur> recepteurs = tacheService.findRecepteurs(utilisateur);
        List<Utilisateur> recepteurs = utilisateurRepo.findUtilisateursBySociete(utilisateur.getSociete());
        // Supprimer l'utilisateur actuel de la liste s'il est présent (pour éviter les doublons)
        recepteurs.removeIf(u -> u.getIdutilisateur().equals(utilisateur.getIdutilisateur()));
        // Trier la liste par ordre alphabétique sans tenir compte de la casse
        recepteurs.sort(Comparator.comparing(Utilisateur::getNom, String.CASE_INSENSITIVE_ORDER));
        // Ajouter l'utilisateur actuel en tête de la liste
        recepteurs.add(0, utilisateur);
        model.addAttribute("Recepteurs", recepteurs);
    }

    @Override
    public void updateTacheAdmin(Tache tache, RedirectAttributes redirectAttributes, Authentication authentication) {
        // Récupérer l'utilisateur connecté
        String login = authentication.getName();
        Utilisateur utilisateurconnecte = utilisateurRepo.findUtilisateursByMail(login);
        switch (tache.getStatut()) {
            case "En attente":
            case "Terminée":
            case "À refaire":
            case "En cours":
                // Mise à jour des statuts courants
                tacheService.updateTacheWithStatus(tache, utilisateurconnecte);
                tacheService.addSuccessMessage(redirectAttributes);
                break;
            case "Programmée":
                tacheService.UpdateTacheToProgramme(tache, redirectAttributes, utilisateurconnecte);
                redirectAttributes.addFlashAttribute("msg", "La tâche a été modifiée avec succès.");
                break;
            case "Validée":
                tacheService.UpdateTacheToValide(tache, utilisateurconnecte);
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
    }

    @Override
    public ResponseEntity<List<Tache>> getAllTasks(Authentication authentication) {
        // Récupérer l'utilisateur connecté
        String login = authentication.getName();
        Utilisateur utilisateur = utilisateurRepo.findUtilisateursByMail(login);
        //Lister les tâches
        List<Tache> tacheList=tacheRepo.findAllByIsmemoire(false);
        return ResponseEntity.ok(tacheList);
    }

    @Override
    public List<Tache> getTachesParentsByidUtilisateur(Long idUtilisateur) {
        Utilisateur utilisateur=utilisateurRepo.findByIdutilisateur(idUtilisateur);
        // Pour mettre à jour la sélection de la tâche parente (récupérer seulement les tâches non terminées ayant des tâches successives)." +
        List<Tache> tacheList1=tacheService.findTacheParent(utilisateur,true);
        //List<Tache> tacheList1=tacheRepo.findATacheParents(utilisateur,true,"En attente","Terminée","À refaire",Programmée");
        return tacheList1;
    }

    @Override
    public List<Utilisateur> getPropritairByIdUtilisateur(Long idUtilisateur) {
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

    @Override
    public List<Utilisateur> getDestinatairByIdUtilisateur(Long idUtilisateur) {
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

    @Override
    public void deleteTache(Long id, RedirectAttributes redirectAttributes) {
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
    }

}
