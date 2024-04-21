package com.example.tachesapp.Controleur;

import com.example.tachesapp.Dao.NotificationsRepo;
import com.example.tachesapp.Dao.PrioriteRepo;
import com.example.tachesapp.Dao.SocieteRepo;
import com.example.tachesapp.Dao.UtilisateurRepo;
import com.example.tachesapp.Model.Notification;
import com.example.tachesapp.Model.Priorite;
import com.example.tachesapp.Model.Societe;
import com.example.tachesapp.Model.Utilisateur;
import com.example.tachesapp.Service.TacheService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Controller
@RequestMapping("/")
public class ProfileControleur {
    @Autowired
    UtilisateurRepo utilisateurRepo;
    @Autowired
    NotificationsRepo notificationsRepo;
    @Autowired
    PrioriteRepo prioriteRepo;
    @Autowired
    TacheService tacheService;
    @Autowired
    private SocieteRepo societeRepo;
    //--------------------------------------------------- MonProfile
    //Affichage
    @GetMapping("/MonProfile")
    public String MonProfile (Authentication authentication, Model model)
    {
        // Récupérer l'utilisateur connecté
        String login = authentication.getName();
        Utilisateur utilisateur = utilisateurRepo.findUtilisateursByMail(login);
        model.addAttribute("utilisateur",utilisateur);

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

        //Les Priorités
        List<Priorite> priorites=prioriteRepo.findAll();
        model.addAttribute("priorites",priorites);

        //les utilisteurs de la meme societé (pour le champ proprietaire)
        Societe societe= societeRepo.findAllByUtilisateurs(utilisateur);
        List<Utilisateur> utilisateurList=utilisateurRepo.findUtilisateursBySociete(societe);
        model.addAttribute("utilisateurList2",utilisateurList);


        return "/pages/MonProfile";
    }

    @PostMapping("/ChangPassWord")
    public String ChangPassWord(@Param("motdepasse") String motdepasse, Authentication authentication, RedirectAttributes redirectAttributes) {
        // Récupérer l'utilisateur connecté
        String login = authentication.getName();
        Utilisateur utilisateur = utilisateurRepo.findUtilisateursByMail(login);
        utilisateur.setMotdepasse(motdepasse);
        utilisateurRepo.save(utilisateur);
        redirectAttributes.addFlashAttribute("msg","Le mot de passe a été modifié avec succès. ");
        System.out.println(motdepasse);
        return "redirect:/MonProfile";
    }

}
