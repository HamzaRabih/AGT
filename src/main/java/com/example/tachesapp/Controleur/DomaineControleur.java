package com.example.tachesapp.Controleur;

import com.example.tachesapp.Dao.*;
import com.example.tachesapp.Model.*;
import com.example.tachesapp.Service.DomaineService;
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
public class DomaineControleur {

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


// -----------------------------------
// Créer ou mettre à jour un Domaine
    /*@PostMapping("/CreateDomaine")
    public String createOrUpdateDomaine(@ModelAttribute Domaine domaine, Authentication authentication, RedirectAttributes redirectAttributes) {
        // Récupérer l'utilisateur connecté
        String login = authentication.getName();
        Utilisateur utilisateurConnecte = utilisateurRepo.findUtilisateursByMail(login);

        //verifier si le DOMAINE  est existe
        Boolean existsByNomdomaine=domaineRepo.existsByNomdomaine(domaine.getNomdomaine());

            if (domaine.getIddomaine() == null)
            {
            // Cas de la création
                if (existsByNomdomaine) {  // si le DOMAINE  est existe déjà
                    redirectAttributes.addFlashAttribute("msgError", "Ce domaine existe déjà.");
                }
                else
                {//sinon
                   domaine.setCreerpar(utilisateurConnecte);
                    domaineRepo.save(domaine);
                    redirectAttributes.addFlashAttribute("msg", "Domaine ajouté avec succès");
                }
           }
            else
           {
                // Cas de la mise à jour
                Domaine domaineExist = domaineRepo.findById(domaine.getIddomaine()).orElse(null);
                if (existsByNomdomaine && domaine.getIddomaine()!=domaineExist.getIddomaine()) {
                    // si le DOMAINE  est existe déjà
                    redirectAttributes.addFlashAttribute("msgError", "Ce domaine existe déjà.");
                }
                else
                {//sinon
                    if (domaineExist == null ) { // Gérer le cas où le domaine n'existe pas
                        redirectAttributes.addFlashAttribute("msgError", "Le Domaine n'a pas été trouvé.");}

                    // Réinitialiser creerpar pour éviter qu'il devienne null lors de la mise à jour
                    domaineExist.setCreerpar(domaineExist.getCreerpar());
                    // Mettre à jour le reste des champs
                    domaineExist.setModifierpar(utilisateurConnecte);
                    domaineExist.setDescription(domaine.getDescription());
                    domaineExist.setNomdomaine(domaine.getNomdomaine());
                    domaineRepo.save(domaineExist);
                    redirectAttributes.addFlashAttribute("msg1", "Le Domaine a été modifié avec succès.");


                }
           }
        return "redirect:/domaine";
    }
*/
// -----------------------------------

