package com.example.tachesapp.Controleur;

import com.example.tachesapp.Dao.*;
import com.example.tachesapp.Model.*;
import com.example.tachesapp.Service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Controller
@RequestMapping("/")
public class DepartemenControleur {
    @Autowired
    DeparetementService deparetementService;
    @Autowired
    SocieteService societeService;
    @Autowired
    SocieteRepo societeRepo;
    @Autowired
    UtilisateurRepo utilisateurRepo;
    @Autowired
    DepartementRepo departementRepo;
    @Autowired
    NotificationsRepo notificationsRepo;
    @Autowired
    PrioriteRepo prioriteRepo;
    @Autowired
    TacheService tacheService;
    @Autowired
    TacheAdminService tacheAdminService;
    @Autowired
    PrioriteService prioriteService;
    @Autowired
    UtilisateurService utilisateurService;
    @Autowired
    NotificationsService notificationsService;

    // Constantes
    private static final String DEPARTEMENT_EXISTE_DEJA = "Cette département existe déjà dans la société.";
    private static final String DEPARTEMENT_AJOUTE = "Département ajoutée avec succès";
    private static final String DEPARTEMENT_MODIFIE = "La département a été modifiée avec succès.";



    //l'Affichage de la page departement.html
    @GetMapping("/departement")
    public String departement (Model model,Authentication authentication)
    {
        // Récupérer l'utilisateur connecté
        String login = authentication.getName();
        Utilisateur utilisateur = utilisateurRepo.findUtilisateursByMail(login);
        //les lists envoyer a la page departement.html
        List<Societe> societeList=societeService.findAllSociete();
        List<Departement> departements=deparetementService.findAllDepart();
        model.addAttribute("departements",departements);
        model.addAttribute("societeList",societeList);
        notificationsService.loadNotificationAndRelationType(utilisateur,model);
        model.addAttribute("utilisateurC",utilisateur);
        tacheAdminService.loadReceivers(utilisateur,model);
        prioriteService.loadPriorites(model);
        //les utilisteurs de la meme societé (pour le champ proprietaire)
        utilisateurService.loadSocietieMembers(utilisateur,model);
        return "/pages/departement";
    }




    //Supprimer une departement
    @Transactional
    @GetMapping(value = "/deleteDepartement/{id}")
    public String deleteDepartement(@PathVariable Long id,RedirectAttributes redirectAttributes) {
        Departement departement=departementRepo.findByIddepartement(id);
        boolean utilExistsBydepartement=utilisateurRepo.existsByDepartement(departement);
        Long numUtil=utilisateurRepo.countByDepartement(departement);
        if (utilExistsBydepartement) {
            redirectAttributes.addFlashAttribute("msgError", "cette departemet a " + numUtil + " Utilisateurs ,vous ne pouvez pas supprimer  cette departement");
        } else {
            departementRepo.deleteByIddepartement(id);
            redirectAttributes.addFlashAttribute("msg", "La departement a été suprimée  avec succès");
        }
        return "redirect:/departement";
    }


     //créér ou mettre a jour Departement
    @PostMapping("/CreateDepartement")
    public String creerDomaine(@ModelAttribute Departement departement, RedirectAttributes redirectAttributes, Authentication authentication) {
        String login = authentication.getName();
        Utilisateur utilisateurConnecte = utilisateurRepo.findUtilisateursByMail(login);
        //verifier si le DOMAINE  est existe
        Boolean existsByNomDepAndSociete=departementRepo.existsByNomdepartementAndSociete(departement.getNomdepartement(),departement.getSociete());
        if (departement.getIddepartement() == null) {
            creerNouvelDepartement(departement, utilisateurConnecte, redirectAttributes);
        } else {
            modifierDepartement(departement, utilisateurConnecte, redirectAttributes);
        }
        return "redirect:/departement";
    }

    private void creerNouvelDepartement(Departement departement, Utilisateur utilisateurConnecte, RedirectAttributes redirectAttributes) {
        Boolean existsByNomDepAndSociete=departementRepo.existsByNomdepartementAndSociete(departement.getNomdepartement(),departement.getSociete());
        if (existsByNomDepAndSociete) {
            redirectAttributes.addFlashAttribute("msgError", DEPARTEMENT_EXISTE_DEJA);
        } else {
            departement.setCreerpar(utilisateurConnecte);
            departementRepo.save(departement);
            redirectAttributes.addFlashAttribute("msg", DEPARTEMENT_AJOUTE);
        }
    }

    private void modifierDepartement(Departement  nouvelleDepartement, Utilisateur utilisateurConnecte, RedirectAttributes redirectAttributes) {
        Departement departementAModifie = departementRepo.findById(nouvelleDepartement.getIddepartement()).orElse(null);
        Boolean existsByNomDepAndSociete = departementRepo.existsByNomdepartementAndSocieteAndIddepartementNot(nouvelleDepartement.getNomdepartement(),nouvelleDepartement.getSociete(), departementAModifie.getIddepartement());
        if (existsByNomDepAndSociete) {
            redirectAttributes.addFlashAttribute("msgError", DEPARTEMENT_EXISTE_DEJA);
        } else {
            // Réinitialiser creerpar pour éviter qu'il devienne null lors de la mise à jour
            departementAModifie.setCreerpar(utilisateurConnecte);
            // Mettre à jour le reste des champs
            departementAModifie.setModifierpar(utilisateurConnecte);
            departementAModifie.setSociete(nouvelleDepartement.getSociete());
            departementAModifie.setNomdepartement(nouvelleDepartement.getNomdepartement());
            departementRepo.save(departementAModifie);
            redirectAttributes.addFlashAttribute("msg2", DEPARTEMENT_MODIFIE);
        }
    }

    //recuperer une Departement par id (utiliser par ajax pour modifie les departements dans la meme forme)
    @GetMapping("/get-Departement/{idDepartement}")
    @ResponseBody
    public ResponseEntity<Departement> getDepartement(@PathVariable Long idDepartement) {
       // Récupérez la liste des départements
       Departement departement=deparetementService.findDepById(idDepartement);
       System.out.println(departement);
       return ResponseEntity.ok(departement);
    }





}


