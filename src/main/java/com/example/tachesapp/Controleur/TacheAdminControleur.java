package com.example.tachesapp.Controleur;

import com.example.tachesapp.Dao.*;
import com.example.tachesapp.Model.*;
import com.example.tachesapp.Service.TacheAdminService;
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
    @Autowired
    TacheAdminService tacheAdminService;

    //Affichage de GestionTacheAdmin.html
    @GetMapping("/GestionTacheAdmin")
    public String GestionTacheAdmin( Model model,Authentication authentication) {
        tacheAdminService.GestionTacheAdmin(model,authentication);
        return "/pages/GestionTacheAdmin";
    }

    //fonction utiliser dans GestionTacheAdmin.HTML POUR recuperer les tache parent
    @GetMapping("/get-tachesParents-by-idUtilisateur/{idUtilisateur}")
    @ResponseBody
    public List<Tache> getTachesParentsByidUtilisateur(@PathVariable Long idUtilisateur) {
        return tacheAdminService.getTachesParentsByidUtilisateur(idUtilisateur);
    }

    //fonction utiliser dans GestionTacheAdmin.HTML POUR recuperer les utilisateur de la meme societe
    @GetMapping("/get-Propritair-by-idUtilisateur/{idUtilisateur}")
    @ResponseBody
    public List<Utilisateur> getPropritairByIdUtilisateur(@PathVariable Long idUtilisateur) {
        return tacheAdminService.getPropritairByIdUtilisateur(idUtilisateur);
    }

    //fonction utiliser dans GestionTacheAdmin.HTML POUR recuperer les utilisateur de la meme societe
    @GetMapping("/get-Destinatair-by-idUtilisateur/{idUtilisateur}")
    @ResponseBody
    public List<Utilisateur> getDestinatairByIdUtilisateur(@PathVariable Long idUtilisateur) {
        return tacheAdminService.getDestinatairByIdUtilisateur(idUtilisateur);
    }

    //Supprimer Tache
    @Transactional
    @GetMapping(value = "/deleteTache/{id}")
    public String deleteTache(@PathVariable Long id,RedirectAttributes redirectAttributes) {
        tacheAdminService.deleteTache(id,redirectAttributes);
        return "redirect:/GestionTacheAdmin";
    }


    // -----------------------------------
    //mettre à jour une tâche
    // -----------------------------------
    @PostMapping("/UpdateTacheAdmin")
    public String UpdateTacheAdmin(@ModelAttribute Tache tache, RedirectAttributes redirectAttributes, Authentication authentication) {
        tacheAdminService.updateTacheAdmin(tache,redirectAttributes,authentication);
        return "redirect:/GestionTacheAdmin";
    }



    @GetMapping(value = "/getAllTasks")
    public ResponseEntity<List<Tache>> getAllTasks(Authentication authentication) {
        return tacheAdminService.getAllTasks(authentication);
    }


}
