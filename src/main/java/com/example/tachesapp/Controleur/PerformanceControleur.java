package com.example.tachesapp.Controleur;

import com.example.tachesapp.Dao.NotificationsRepo;
import com.example.tachesapp.Dao.UtilisateurRepo;
import com.example.tachesapp.Model.Notification;
import com.example.tachesapp.Model.Utilisateur;
import com.example.tachesapp.Service.NotificationsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/")
public class PerformanceControleur {

    @Autowired
    NotificationsService notificationsService;
    @Autowired
    UtilisateurRepo utilisateurRepo;
    @Autowired
    NotificationsRepo notificationsRepo;
    //---------------------------------------------------Performance


    //--perform
    //Affichage
    @GetMapping("/performance")
    public String Performance(Authentication authentication, Model model)
    {
        // Récupérer l'utilisateur connecté
        String login = authentication.getName();
        Utilisateur utilisateur = utilisateurRepo.findUtilisateursByMail(login);

        notificationsService.loadNotification(utilisateur,model);

        return "/pages/performance";
    }





}
