package com.example.tachesapp.Controleur;

import com.example.tachesapp.Dao.*;
import com.example.tachesapp.Model.*;
import com.example.tachesapp.Service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Controller
@RequestMapping("/")
public class UtilisateurControleur {

    @Autowired
    NotificationsService notificationsService;
    @Autowired
    PrioriteService prioriteService;
    @Autowired
    UtilisateurService utilisateurService;
    @Autowired
    SocieteService societeService;
    @Autowired
    RoleService roleService;
    @Autowired
    DepartementRepo departementRepo;
    @Autowired
    DeparetementService deparetementService;
    @Autowired
    DomaineService domaineService;
    @Autowired
    DomaineRepo domaineRepo;
    @Autowired
    EquipeService equipeService;
    @Autowired
    UtilisateurRepo utilisateurRepo;
    @Autowired
    TacheRepo tacheRepo;
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




    //--------------------------------------------------- Administration
//----gestion utilisateur
    //Affichage
    @GetMapping("/gestUtilisateur")
    public String gestUtilisateur (Model model,Authentication authentication)
    {
        // Récupérer l'utilisateur connecté
        String login = authentication.getName();
        Utilisateur utilisateur = utilisateurRepo.findUtilisateursByMail(login);
        List<Utilisateur> utilisateurList=utilisateurService.findUtilisateurs();
        List<Domaine> domaineList=domaineService.findAllDomaine();
        List<Departement> departementList=deparetementService.findAllDepart();
        List<Role> roles=roleService.findAllRole();
        List<Societe> societeList=societeService.findAllSociete();
        model.addAttribute("utilisateurC",utilisateur);
        model.addAttribute("societeList",societeList);
        model.addAttribute("roles",roles);
        model.addAttribute("departementList",departementList);
        model.addAttribute("domaineList",domaineList);
        model.addAttribute("utilisateurList",utilisateurList);


        notificationsService.loadNotification(utilisateur,model);
        tacheAdminService.loadReceivers(utilisateur,model);
        prioriteService.loadPriorites(model);
        utilisateurService.loadSocietieMembers(utilisateur,model);
        return "/pages/gestionUtilisateur";
    }

    // -----------------------------------
// Créer ou mettre à jour un Utilisateur
// -----------------------------------
    @PostMapping("/CreateUtilisateur")
    public String creerUtilisateur(@ModelAttribute Utilisateur utilisateur, RedirectAttributes redirectAttributes, Authentication authentication) {
        String login = authentication.getName();
        Utilisateur utilisateurConnecte = utilisateurRepo.findUtilisateursByMail(login);
        if (utilisateur.getIdutilisateur() == null) {
            utilisateurService.creerNouvelUtilisateur(utilisateur, utilisateurConnecte, redirectAttributes);
        } else {
            utilisateurService.modifierUtilisateur(utilisateur, utilisateurConnecte, redirectAttributes);
        }
        return "redirect:/gestUtilisateur";
    }


    //fonction utilise pour trouver les departement d'une societe par idsociete
// (j ai utilise cette fonction dans le scripte3 gestionEquipe.html & script2 gestionUtilisateur.html)
    @GetMapping("/get-departements-by-societe/{societeId}")
    @ResponseBody
    public List<Departement> getDepartementsBySociete(@PathVariable Long societeId) {
        // Récupérez la liste des départements de la société sélectionnée
        List<Departement> departements = deparetementService.getDepartementsBySociete(societeId);
        return departements;
    }

    //Supprimer utilisateur
    @Transactional
    @GetMapping(value = "/deleteUtilisateur/{id}")
    public String deleteUtilisateur(@PathVariable Long id,RedirectAttributes redirectAttributes) {
       String msg= utilisateurService.supputilisateurById(id);
       redirectAttributes.addFlashAttribute("msg",msg);
        return "redirect:/gestUtilisateur";
    }

    //recuperer un Utilisateurs par id (utiliser par ajax pour modifie dans la meme forme)
    @GetMapping("/get-Utilisaeur/{idUtilisateur}")
    @ResponseBody
    public Utilisateur getDepartement(@PathVariable Long idUtilisateur) {
        // Récupérez la liste des départements
        Utilisateur utilisateur=utilisateurService.findUtilisateurById(idUtilisateur) ;
        System.out.println(utilisateur);
        return utilisateur;
    }

}
