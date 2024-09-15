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
import java.util.Objects;

@Controller
@RequestMapping("/")
public class SocieteControleur {

    @Autowired
    NotificationsService notificationsService;
    @Autowired
    UtilisateurService utilisateurService;
    @Autowired
    PrioriteService prioriteService;
    @Autowired
    private SocieteService societeService;
    @Autowired
    private SocieteRepo societeRepo;
    @Autowired
    private UtilisateurRepo utilisateurRepo;
    @Autowired
    NotificationsRepo notificationsRepo;
    @Autowired
    DepartementRepo departementRepo;
    @Autowired
    EquipeRepo equipeRepo;
    @Autowired
    TacheRepo tacheRepo;
    @Autowired
    PrioriteRepo prioriteRepo;
    @Autowired
    TacheService tacheService;
    @Autowired
    TacheAdminService tacheAdminService;


    //Affichage
    @GetMapping("/societe")
    public String societe (Authentication authentication, Model model) {
        // Récupérer l'utilisateur connecté
        String login = authentication.getName();
        Utilisateur utilisateur = utilisateurRepo.findUtilisateursByMail(login);
        model.addAttribute("utilisateurC",utilisateur);
        List<Societe> societeList=societeService.findAllSociete();
        model.addAttribute("societeList",societeList);
        notificationsService.loadNotification(utilisateur,model);
        tacheAdminService.loadReceivers(utilisateur,model);
        prioriteService.loadPriorites(model);
        utilisateurService.loadSocietieMembers(utilisateur,model);
        return "/pages/societe";
    }



    //Supprimer Societé
    @Transactional
    @GetMapping(value = "/deleteSociete/{id}")
    public String deleteSociete(@PathVariable Long id,RedirectAttributes redirectAttributes) {
        Societe societe=societeRepo.findByIdsociete(id);
        boolean DepExistsBySociete=departementRepo.existsBySociete(societe);
        boolean utilExistsBySociete=utilisateurRepo.existsBySociete(societe);
        Long numUtil=utilisateurRepo.countBySociete(societe);
        Long numDep=departementRepo.countBySociete(societe);
        if (DepExistsBySociete || utilExistsBySociete) {
            redirectAttributes.addFlashAttribute("msgError", "cette société a " + numUtil + " Utilisateurs " + "et " + numDep + " departement " + ",vous ne pouvez pas supprimer  cette société");
        } else {
            societeRepo.deleteByIdsociete(id);
            redirectAttributes.addFlashAttribute("msg", "La Société a été suprimée  avec succès");
        }
        return "redirect:/societe";
    }


    // -----------------------------------
// Créer ou mettre à jour une Societe
// -----------------------------------
    @PostMapping("/CreateSociete")
    public String createOrUpdateSociete(@ModelAttribute Societe societe, Authentication authentication,RedirectAttributes redirectAttributes) {
        // Récupérer l'utilisateur connecté
        String login = authentication.getName();
        Utilisateur utilisateurConnecte = utilisateurRepo.findUtilisateursByMail(login);
        Societe Existesociete=societeRepo.findByIdsociete(societe.getIdsociete());
        //verifier si la société est existé déjà
        Boolean existsByNomsociete=societeRepo.existsByNomsociete(societe.getNomsociete());

        // Cas de la création
        if (societe.getIdsociete() == null) {
            handleSocieteCreation(societe,existsByNomsociete,utilisateurConnecte,redirectAttributes);
        }
        // Cas de la mise à jour
        else {
            modifierSociete(Existesociete,societe,utilisateurConnecte,redirectAttributes);
        }
        return "redirect:/societe";
    }

    public void handleSocieteCreation(Societe societe,Boolean existsByNomsociete,Utilisateur utilisateurConnecte,RedirectAttributes redirectAttributes) {
        if (existsByNomsociete) {  // si la société est existé déjà
            redirectAttributes.addFlashAttribute("msgError", "Cette société existe déjà.");
        }
        else {//sinon
            societe.setCreerpar(utilisateurConnecte);
            societeRepo.save(societe);
            redirectAttributes.addFlashAttribute("msg", "Société ajouté avec succès");
        }
    }


    public void modifierSociete(Societe societeAModifier, Societe nouvelleSociete, Utilisateur utilisateurConnecte, RedirectAttributes redirectAttributes) {
        // Vérifier si une autre société avec le même nom existe déjà
        Boolean existeSocieteAvecMemeNom = societeRepo.existsByNomsocieteAndIdsocieteNot(nouvelleSociete.getNomsociete(), nouvelleSociete.getIdsociete());

        if (existeSocieteAvecMemeNom) {
            redirectAttributes.addFlashAttribute("msgError", "Une société avec ce nom existe déjà.");
        } else {
            // Récupérer la société à modifier
            Societe societeExistante = societeRepo.findById(societeAModifier.getIdsociete()).orElse(null);

            if (societeExistante != null) {
                // Mettre à jour les informations de la société existante
                societeExistante.setModifierpar(utilisateurConnecte);
                societeExistante.setAdressesociete(nouvelleSociete.getAdressesociete());
                societeExistante.setMailsociete(nouvelleSociete.getMailsociete());
                societeExistante.setNomsociete(nouvelleSociete.getNomsociete());
                societeExistante.setTelephone(nouvelleSociete.getTelephone());

                // Sauvegarder les modifications
                societeRepo.save(societeExistante);

                redirectAttributes.addFlashAttribute("msgSuccess", "La société a été modifiée avec succès.");
            } else {
                redirectAttributes.addFlashAttribute("msgError", "La société à modifier n'a pas été trouvée.");
            }
        }
    }

    //recuperer une societe par id (utiliser par ajax pour modifie dans la meme forme)
    @GetMapping("/get-Societe/{idSociete}")
    @ResponseBody
    public Societe getSociete(@PathVariable Long idSociete) {
        // Récupérez la liste des sociétés
        Societe societe=societeService.findeByIds(idSociete);
        return societe;
    }

}
