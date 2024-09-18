package com.example.tachesapp.Controleur;

import com.example.tachesapp.Dao.*;
import com.example.tachesapp.Model.*;
import com.example.tachesapp.Service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.*;

@Controller
@RequestMapping("/")
public class MonEquipecontroleur {

    @Autowired
    NotificationsService notificationsService;
    @Autowired
    PrioriteService prioriteService;
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
    @Autowired
    TacheAdminService tacheAdminService;

    //--------------------------------------------------- MonEquipe
//----Equipe
    //Affichage
    @GetMapping("/MonEquipe")
    public String MonEquipe (Authentication authentication, Model model)
    {
        // Récupérer l'utilisateur connecté
        String login = authentication.getName();
        Utilisateur utilisateur = utilisateurRepo.findUtilisateursByMail(login);
        model.addAttribute("utilisateurC",utilisateur);
        //Cette fonction a pour but d'obtenir l'équipe et les sous-équipes(si l'un des membres est responsable d'une équipe) de l'utilisateur,
        // afin que l'utilisateur puisse envoyer les tâches uniquement à ses équipes.
        List<Utilisateur> monEquipe=tacheService.findRecepteurs(utilisateur);
        model.addAttribute("monEquipe", monEquipe);
        List<Utilisateur> Recepteurs=tacheService.findRecepteurs(utilisateur);
        //les taches de mon equipe
        //List<Tache> equipeTaches=tacheRepo.findAllByUtilisateurInAndIsmemoire(Recepteurs,false);
        List<Tache> equipeTaches=tacheRepo.findAllByRecepteurInAndIsmemoire(Recepteurs,false);
        model.addAttribute("equipeTaches",equipeTaches);

        tacheAdminService.loadReceivers(utilisateur,model);
        notificationsService.loadNotificationAndRelationType(utilisateur,model);
        prioriteService.loadPriorites(model);
        utilisateurService.loadSocietieMembers(utilisateur,model);






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
        // Tâches où l'utilisateur est à la fois récepteur et émetteur
        List<Tache> directTasks =tacheRepo.findTacheByRecepteurAndUtilisateurAndIsmemoire(utilisateur,utilisateur,false);
        // Tâches envoyées par l'utilisateur connecté
        List<Tache> sentTasks=tacheRepo.findTacheByUtilisateurAndIsmemoire(utilisateur,false);
        // l'équipe, y compris l'utilisateur connecté
        List<Utilisateur> teamReceivers =tacheService.findRecepteurs(utilisateur);
        // Tâches de l'équipe
        List<Tache> teamTasks =tacheRepo.findAllByRecepteurInAndIsmemoire(teamReceivers,false);
        // Créer un ensemble pour éliminer les doublons
        Set<Tache> uniqueTaskSet = new HashSet<>();
        uniqueTaskSet.addAll(directTasks);
        uniqueTaskSet.addAll(sentTasks);
        uniqueTaskSet.addAll(teamTasks);
        // Convert the set to a list
        List<Tache> finalTasks = new ArrayList<>(uniqueTaskSet);
        return ResponseEntity.ok(finalTasks);
    }

}
