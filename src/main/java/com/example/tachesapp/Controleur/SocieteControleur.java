package com.example.tachesapp.Controleur;

import com.example.tachesapp.Dao.*;
import com.example.tachesapp.Model.*;
import com.example.tachesapp.Service.SocieteService;
import com.example.tachesapp.Service.TacheService;
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


    //Affichage
    @GetMapping("/societe")
    public String societe (Authentication authentication, Model model)
    {
        // Récupérer l'utilisateur connecté
        String login = authentication.getName();
        Utilisateur utilisateur = utilisateurRepo.findUtilisateursByMail(login);
        model.addAttribute("utilisateurC",utilisateur);

        List<Societe> societeList=societeService.findAllSociete();
        model.addAttribute("societeList",societeList);

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
        //verifier si la société  est existe
        Boolean existsByNomsociete=societeRepo.existsByNomsociete(societe.getNomsociete());

        // Cas de la création
        if (societe.getIdsociete() == null) {
            handleSocieteCreation(societe,existsByNomsociete,utilisateurConnecte,redirectAttributes);
        }
        // Cas de la mise à jour
        else {
            handleSocieteModification(societe,existsByNomsociete,utilisateurConnecte,redirectAttributes);
        }
        return "redirect:/societe";
    }

    public void handleSocieteCreation(Societe societe,Boolean existsByNomsociete,Utilisateur utilisateurConnecte,RedirectAttributes redirectAttributes) {
        if (existsByNomsociete) {  // si le DOMAINE  est existe déjà
            redirectAttributes.addFlashAttribute("msgError", "Cette société existe déjà.");
        }
        else {//sinon
            societe.setCreerpar(utilisateurConnecte);
            societeRepo.save(societe);
            redirectAttributes.addFlashAttribute("msg", "Société ajouté avec succès");
        }
    }

    public  void handleSocieteModification(Societe societe,Boolean existsByNomsociete,Utilisateur utilisateurConnecte,RedirectAttributes redirectAttributes) {
        Societe societeExist = societeRepo.findById(societe.getIdsociete()).orElse(null);
        //System.out.println();
        if (societeExist == null)
        { // Gérer le cas où la societé n'existe pas
            redirectAttributes.addFlashAttribute("msgError", "La société n'a pas été trouvé.");
        }

        // si la societe  est existe déjà //
        if (existsByNomsociete && !Objects.equals(societe.getIdsociete(), Objects.requireNonNull(societeExist).getIdsociete())) {
            redirectAttributes.addFlashAttribute("msgError", "Cette société existe déjà.");
        }
        else {//sinon
            // Réinitialiser creerpar pour éviter qu'il devienne null lors de la mise à jour
            //assert societeExist != null;
            societeExist.setCreerpar(societeExist.getCreerpar());
            // Mettre à jour le reste des champs
            societeExist.setModifierpar(utilisateurConnecte);

            societeExist.setAdressesociete(societe.getAdressesociete());
            societeExist.setMailsociete(societe.getMailsociete());
            societeExist.setNomsociete(societe.getNomsociete());
            societeExist.setTelephone(societe.getTelephone());
            societeRepo.save(societeExist);
            redirectAttributes.addFlashAttribute("msg1", "La société a été modifiée avec succès.");
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
