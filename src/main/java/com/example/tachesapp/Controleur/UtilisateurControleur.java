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
        model.addAttribute("societeList",societeList);
        model.addAttribute("roles",roles);
        model.addAttribute("departementList",departementList);
        model.addAttribute("domaineList",domaineList);
        model.addAttribute("utilisateurList",utilisateurList);

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

        //les utilisteurs de la meme societé (pour le champ proprietaire)
        Societe societe= societeRepo.findAllByUtilisateurs(utilisateur);
        List<Utilisateur> utilisateurList2=utilisateurRepo.findUtilisateursBySociete(societe);
        model.addAttribute("utilisateurList2",utilisateurList);

        return "/pages/gestionUtilisateur";
    }

    // -----------------------------------
// Créer ou mettre à jour un Utilisateur
// -----------------------------------
    @PostMapping("/CreateUtilisateur")
    public String CreeUtilisateur(@ModelAttribute Utilisateur utilisateur, RedirectAttributes redirectAttributes, Authentication authentication) {
        // Récupérer l'utilisateur connecté
        String login = authentication.getName();
        Utilisateur utilisateurConnecte = utilisateurRepo.findUtilisateursByMail(login);

        /*
        // Hasher le mot de passe
        String hashedPassword = new BCryptPasswordEncoder().encode(utilisateur.getMotdepasse());
        utilisateur.setMotdepasse(hashedPassword);
         */

        //verifier si le mail  est existe
        Boolean existsByMail=utilisateurRepo.existsByMail(utilisateur.getMail());



            // DéTerminer si c'est une mise à jour ou une création
            if (utilisateur.getIdutilisateur() == null) {
                // Cas de la création
                if (existsByMail) {
                    // si le mail  est existe déjà
                    redirectAttributes.addFlashAttribute("msgError", "Ce mail existe déjà. Veuillez saisir une nouvelle adresse mail.");
                } else {
                    //sinon
                    utilisateur.setCreerpar(utilisateurConnecte);
                    utilisateurRepo.save(utilisateur);
                    redirectAttributes.addFlashAttribute("msg", "Utilisateur ajouté avec succès.");
                }
            } else {
                // Cas de la mise à jour
                Utilisateur utilisateurExist = utilisateurRepo.findById(utilisateur.getIdutilisateur()).orElse(null);
                // Réinitialiser creerpar pour éviter qu'il devienne null lors de la mise à jour
                utilisateurExist.setCreerpar(utilisateurExist.getCreerpar());
                // Mettre à jour le reste des champs
                utilisateurExist.setModifierpar(utilisateurConnecte);
                // Mettre à jour le reste des champs
                utilisateurExist.setNom(utilisateur.getNom());
                utilisateurExist.setMail(utilisateur.getMail());
                utilisateurExist.setMotdepasse(utilisateur.getMotdepasse());
                utilisateurExist.setSociete(utilisateur.getSociete());
                utilisateurExist.setDomaine(utilisateur.getDomaine());
                utilisateurExist.setDepartement(utilisateur.getDepartement());
                utilisateurExist.setPrenom(utilisateur.getPrenom());
                utilisateurExist.setActif(utilisateur.isActif());
                utilisateurExist.setRole(utilisateur.getRole());

                utilisateur.setDepartement(utilisateur.getDepartement());
                 utilisateurRepo.save(utilisateurExist);
                redirectAttributes.addFlashAttribute("msg2", "L'utilisateur a été modifié avec succès.");

            }

        // Redirection
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
