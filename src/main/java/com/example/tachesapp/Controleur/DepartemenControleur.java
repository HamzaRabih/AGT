package com.example.tachesapp.Controleur;

import com.example.tachesapp.Dao.*;
import com.example.tachesapp.Model.*;
import com.example.tachesapp.Service.DeparetementService;
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
        List<Utilisateur> utilisateurList=utilisateurRepo.findUtilisateursBySociete(societe);
        model.addAttribute("utilisateurList2",utilisateurList);



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
    public String createOrUpdateDepartement(@ModelAttribute Departement departement, Authentication authentication, RedirectAttributes redirectAttributes) {
        // Récupérer l'utilisateur connecté
        String login = authentication.getName();
        Utilisateur utilisateurconnecte = utilisateurRepo.findUtilisateursByMail(login);
        //verifier si la département  est existe
        Boolean existsByNomDepAndSociete=departementRepo.existsByNomdepartementAndSociete(departement.getNomdepartement(),departement.getSociete());

        if (departement.getIddepartement() == null) {
            // Create a new departement
            //cas de creation
            if (existsByNomDepAndSociete) {  // si la département  est existe déjà
                redirectAttributes.addFlashAttribute("msgError", "Cette département existe déjà dans la société.");

            } else {
                departement.setCreerpar(utilisateurconnecte);
                departementRepo.save(departement);
                redirectAttributes.addFlashAttribute("msg", "Departement ajouté avec succès");
            }
        }
        else
        {// Update the existing Departemnt
            //cas de mis a jour
            Departement ddepartementExist = departementRepo.findById(departement.getIddepartement()).orElse(null);

            if (existsByNomDepAndSociete && departement.getIddepartement()!=ddepartementExist.getIddepartement()) {
                // si la département  est existe déjà
                redirectAttributes.addFlashAttribute("msgError", "Cette département existe déjà dans la société.");
            } else {
                // Réinitialiser creerpar pour éviter qu'il devienne null lors de la mise à jour
                ddepartementExist.setCreerpar(ddepartementExist.getCreerpar());
                // Mettre à jour le reste des champs
                ddepartementExist.setModifierpar(utilisateurconnecte);

                ddepartementExist.setSociete(departement.getSociete());
                ddepartementExist.setNomdepartement(departement.getNomdepartement());
                departementRepo.save(ddepartementExist);
                redirectAttributes.addFlashAttribute("msg1", "La Departement a été modifiée avec succès.");
            }
        }
        return "redirect:/departement";
    }


    //recuperer une Departement par id (utiliser par ajax pour modifie les departements dans la meme forme)
    @GetMapping("/get-Departement/{idDepartement}")
    @ResponseBody
    public Departement getDepartement(@PathVariable Long idDepartement) {
        // Récupérez la liste des départements
       Departement departement=deparetementService.findDepById(idDepartement);
       System.out.println(departement);
          return departement;
    }





}
