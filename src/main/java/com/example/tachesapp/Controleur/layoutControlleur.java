package com.example.tachesapp.Controleur;

import com.example.tachesapp.Dao.*;
import com.example.tachesapp.Model.Societe;
import com.example.tachesapp.Model.Tache;
import com.example.tachesapp.Model.Utilisateur;
import com.example.tachesapp.Service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/")
public class layoutControlleur {
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


    public String  layout(Authentication authentication, Model model)
    {
        //l utilisateur connecté
        String login = authentication.getName();
        Utilisateur utilisateur = utilisateurRepo.findUtilisateursByMail(login);
        Societe societe= societeRepo.findAllByUtilisateurs(utilisateur);
        //model.addAttribute("utilisateurC",utilisateur);
        //utilisateurService.loadSocietieMembers(utilisateur,model);
        //tacheAdminService.loadReceivers(utilisateur,model);
        //notificationService.loadNotificationAndRelationType(utilisateur,model);
        //prioriteService.loadPriorites(model);
        //model.addAttribute("utilisateurListPR",utilisateurRepo.findUtilisateursBySociete(societe));
        return "layout";
    }
}
