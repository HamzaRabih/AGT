package com.example.tachesapp.Controleur;

import com.example.tachesapp.Dao.*;
import com.example.tachesapp.Model.*;
import com.example.tachesapp.Service.*;
import org.springframework.beans.factory.annotation.Autowired;
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
public class DomaineControleur {

    @Autowired
    NotificationsService notificationsService;
    @Autowired
    UtilisateurService utilisateurService;
    @Autowired
    DomaineService domaineService;
    @Autowired
    UtilisateurRepo utilisateurRepo;
    @Autowired
    DomaineRepo domaineRepo;
    @Autowired
    NotificationsRepo notificationsRepo;
    @Autowired
    PrioriteRepo prioriteRepo;
    @Autowired
    TacheService tacheService;
    @Autowired
    SocieteRepo societeRepo;
    @Autowired
    TacheAdminService tacheAdminService;
    @Autowired
    PrioriteService prioriteService;

    // Constantes
    private static final String DOMAINE_EXISTE_DEJA = "Ce domaine existe déjà.";
    private static final String DOMAINE_AJOUTE = "Domaine ajouté avec succès";
    private static final String DOMAINE_MODIFIE = "Le Domaine a été modifié avec succès.";



    //Affichage
    @GetMapping("/domaine")
    public String domaine (Model model,Authentication authentication)
    {
        // Récupérer l'utilisateur connecté
        String login = authentication.getName();
        Utilisateur utilisateur = utilisateurRepo.findUtilisateursByMail(login);
        List<Domaine> domaineList=domaineService.findAllDomaine();
        model.addAttribute("domaineList",domaineList);
        notificationsService.loadNotificationAndRelationType(utilisateur,model);
        model.addAttribute("utilisateurC",utilisateur);
        tacheAdminService.loadReceivers(utilisateur,model);
        prioriteService.loadPriorites(model);
        utilisateurService.loadSocietieMembers(utilisateur,model);
        return "/pages/domaine";
    }


    // -----------------------------------
// Créer ou mettre à jour un Utilisateur
// -----------------------------------
    @PostMapping("/CreateDomaine")
    public String creerDomaine(@ModelAttribute Domaine domaine, RedirectAttributes redirectAttributes, Authentication authentication) {
        String login = authentication.getName();
        Utilisateur utilisateurConnecte = utilisateurRepo.findUtilisateursByMail(login);
        //verifier si le DOMAINE  est existe
        Boolean existsByNomdomaine=domaineRepo.existsByNomdomaine(domaine.getNomdomaine());
        if (domaine.getIddomaine() == null) {
            creerNouvelUtilisateur(domaine, utilisateurConnecte, redirectAttributes);
        } else {
            modifierUtilisateur(domaine, utilisateurConnecte, redirectAttributes);
        }
        return "redirect:/domaine";
    }

    private void creerNouvelUtilisateur(Domaine domaine, Utilisateur utilisateurConnecte, RedirectAttributes redirectAttributes) {
        Boolean existsByNomdomaine=domaineRepo.existsByNomdomaine(domaine.getNomdomaine());
        if (existsByNomdomaine) {
            redirectAttributes.addFlashAttribute("msgError", DOMAINE_EXISTE_DEJA);
        } else {
            domaine.setCreerpar(utilisateurConnecte);
            domaineRepo.save(domaine);
            redirectAttributes.addFlashAttribute("msg", DOMAINE_AJOUTE);
        }
    }

    private void modifierUtilisateur(Domaine  nouvelleDomainer, Utilisateur utilisateurConnecte, RedirectAttributes redirectAttributes) {
        Domaine domaineAModifie = domaineRepo.findById(nouvelleDomainer.getIddomaine()).orElse(null);
        Boolean existsByNomdomaine = domaineRepo.existsByNomdomaineAndIddomaineNot(nouvelleDomainer.getNomdomaine(), domaineAModifie.getIddomaine());
        if (existsByNomdomaine) {
            redirectAttributes.addFlashAttribute("msgError", DOMAINE_EXISTE_DEJA);
        } else {
            // Réinitialiser creerpar pour éviter qu'il devienne null lors de la mise à jour
            domaineAModifie.setCreerpar(utilisateurConnecte);
            // Mettre à jour le reste des champs
            domaineAModifie.setModifierpar(utilisateurConnecte);
            domaineAModifie.setDescription(nouvelleDomainer.getDescription());
            domaineAModifie.setNomdomaine(nouvelleDomainer.getNomdomaine());
            domaineRepo.save(domaineAModifie);
            redirectAttributes.addFlashAttribute("msg2", DOMAINE_MODIFIE);
        }
    }



    //Supprimer domaine
    @Transactional
    @GetMapping(value = "/deleteDomaine/{id}")
    public String deleteDomaine(@PathVariable Long id) {
        domaineService.suppDomaineById(id);
        return "redirect:/domaine";
    }

    //recuperer un domaine par id (utilise par ajax pour modifie dans la meme forme)
    @GetMapping("/get-Domaine/{idDomaine}")
    @ResponseBody
    public Domaine getDomaine(@PathVariable Long idDomaine) {
        // Récupérez la liste des départements de la société sélectionnée
        Domaine domaine=domaineService.findeByIdDomaine(idDomaine);
        return domaine;
    }

}



