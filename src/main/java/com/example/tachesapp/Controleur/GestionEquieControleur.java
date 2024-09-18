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

import java.util.*;


@Controller
@RequestMapping("/")
public class GestionEquieControleur {

    @Autowired
    NotificationsService notificationsService;
    @Autowired
    PrioriteService prioriteService;
    @Autowired
    UtilisateurService utilisateurService;
    @Autowired
    SocieteRepo societeRepo;
    @Autowired
    SocieteService societeService;
    @Autowired
    EquipeService equipeService;
    @Autowired
    DeparetementService deparetementService;
    @Autowired
    EquipeRepo equipeRepo;
    @Autowired
    UtilisateurRepo utilisateurRepo;
    @Autowired
    NotificationsRepo notificationsRepo;
    @Autowired
    PrioriteRepo prioriteRepo;
    @Autowired
    TacheService tacheService;
    @Autowired
    TacheAdminService tacheAdminService;
    //--------------------------------------------------- Administration
//----gestion utilisateur
    //Affichage d Equipe
    @GetMapping("/gestionEquipe")
    public String gestionEquipe(Model model,Authentication authentication) {
        // Récupérer l'utilisateur connecté
        String login = authentication.getName();
        Utilisateur utilisateur = utilisateurRepo.findUtilisateursByMail(login);
        model.addAttribute("utilisateurC",utilisateur);
        List<Equipe> equipeList = equipeService.findAllEquipes();
        List<Utilisateur> utilisateurs = utilisateurService.findUtilisateurs();
        List<Societe> societeList = societeService.findAllSociete();
        List<Departement> departementList = deparetementService.findAllDepart();
        model.addAttribute("equipeList", equipeList);
        model.addAttribute("utilisateurs", utilisateurs);
        model.addAttribute("societeList", societeList);
        model.addAttribute("departementList", departementList);
        notificationsService.loadNotificationAndRelationType(utilisateur,model);
        tacheAdminService.loadReceivers(utilisateur,model);
        prioriteService.loadPriorites(model);
        utilisateurService.loadSocietieMembers(utilisateur,model);
        return "/pages/gestionEquipe";
    }


    //pour le script de filtrage d utilisteur par societé
    @GetMapping("/get-utilisateur-by-societe/{societeId}")
    @ResponseBody
    public List<Utilisateur> getUtilisateursBySociete(@PathVariable Long societeId) {
        // Récupérez la liste des départements de la société sélectionnée
        List<Utilisateur> utilisateurs = utilisateurService.getUtilisateursBySociete(societeId);
        return utilisateurs;
    }


    //pour le script de filtrage de departement par societé
    @GetMapping("/get-Departement-by-societe/{societeId}")
    @ResponseBody
    public List<Departement> getDepartementsBySociete(@PathVariable Long societeId) {
        // Récupérez la liste des départements de la société sélectionnée
        List<Departement> departements = deparetementService.getDepartementsBySociete(societeId);
        return departements;
    }



    //pour le script de filtrage d utilisteur par societé
    @GetMapping("/get-utilisateur-by-Departement/{departementId}")
    @ResponseBody
    public List<Utilisateur> getUtilisateursByDepartement(@PathVariable String departementId) {
        // Récupérez la liste des départements de la société sélectionnée
        long departementId1 = Long.parseLong(departementId);
        List<Utilisateur> utilisateurList = utilisateurService.findUtilisateurByIdDepartement(departementId1);
        return utilisateurList;
    }


    //fonction pour la creation et le misse a jour d'une equipe
    @PostMapping("/CreateEquipe")
    public String createEquipe(Equipe equipe,
                               @RequestParam("nomequipe") String nomequipe,
                               @RequestParam("idutilresponsabledequipe") Long idResponsableEquipe,
                               @RequestParam(value = "idutilisateur", required = false) List<Long> idUtilisateurs,
                               @RequestParam("soci") Long idsoc
                                ,RedirectAttributes redirectAttributes, Authentication authentication) {
        String login = authentication.getName();
        Utilisateur utilisateurConnecte = utilisateurRepo.findUtilisateursByMail(login);
        Utilisateur responsable = utilisateurService.findUtilisateurById(idResponsableEquipe);
        boolean isResponsableOfAnotherTeam = equipeRepo.existsByResponsable(responsable);
        if (equipe.getIdequipe() == null) {
           equipeService.handleEquipeCreation(equipe, nomequipe, idResponsableEquipe, idUtilisateurs, isResponsableOfAnotherTeam, redirectAttributes, utilisateurConnecte,idsoc);
        } else {
            equipeService.handleEquipeModification(equipe, nomequipe, idResponsableEquipe, idUtilisateurs, redirectAttributes, utilisateurConnecte,idsoc);
        }
        return "redirect:/gestionEquipe";
    }

    //supprimer une equipe
    @GetMapping(value = "/deleteEquipe/{id}")
    @Transactional
    public String deleteEquipe(@PathVariable Long id) {
        equipeService.deleteEquipe(id);
        return "redirect:/gestionEquipe";
    }


    // Fonction pour récupérer les utilisateurs associés aux départements sélectionnés
    // utilise a par ajax et javascript dans le script 2 dans gestionEquipe.html
    // ResponseEntity, qui est une classe de Spring qui permet de contrôler la réponse HTTP.
    //ResponseEntity<List<Utilisateur>> : Cette déclaration de type indique que la méthode de contrôleur renverra une réponse HTTP contenant une liste d'objets de type Utilisateur
    //ResponseEntity.ok(utilisateurs):signifie que les utilisateurs seront renvoyés au client en tant que réponse HTTP avec un statut de succès (200 OK).
    @GetMapping(value = "/utilisateurs")
    public ResponseEntity<List<Utilisateur>> getUtilisateursByDepartement(@RequestParam("departements") List<Long> departements) {
        // Call the service to retrieve the list of users based on selected department IDs
        List<Utilisateur> utilisateurs = utilisateurService.findUtilisateurByIdDepartements(departements);
        return ResponseEntity.ok(utilisateurs);
    }

    // Fonction pour récupérer Une equipe s
    //recuperer une Equipe par id (utilisé par ajax et javascript dans le script 3 dans gestionEquipe.html)
    @GetMapping("/get-Equipe/{idEquipe}")
    @ResponseBody
    public Equipe getDepartement(@PathVariable Long idEquipe) {
        // Récupérez l'equipe
        Equipe equipe = equipeService.findEquipByID(idEquipe);
        System.out.println(equipe);
        return equipe;
    }

    // Fonction pour récupérer les utilisateurs de departements selectionne lorsque une modification
    @PostMapping("/get-utilisateur-by-Selected-Departements")
    public List<Utilisateur> getUtilisateurBySelectedDepartement(@RequestBody List<Departement> DepartementsSelected) {
        // Récupérez l'equipe
        List<Utilisateur> utilisateurs = utilisateurRepo.findUtilisateursByDepartementIn(DepartementsSelected);
        return utilisateurs;
    }

    //getAllIdresponsable
    // Fonction pour récupérer les utilisateurs de departements selectionne lorsque une modification
    @GetMapping("/getAllIdresponsable")
    public ResponseEntity<List<Equipe>> getAllIdresponsableq() {
        // Récupérez les idRespo
        List<Equipe> equipeList = equipeService.findAllEquipes();
        // Si la liste est vide, renvoyez une liste vide
        if (equipeList.isEmpty()) {
            return ResponseEntity.ok(equipeList);
        }
        // Sinon, renvoyez la liste
        return ResponseEntity.ok(equipeList);
    }





    @GetMapping(value = "/getAllSuperieursForUtilisateurByIdUtilisateur")
    public ResponseEntity<List<Utilisateur>> getAllSuperieursForUtilisateurByIdUtilisateur(@RequestParam("idutilisateur") Long idutilisateur) {
        List<Utilisateur> superieursList=new ArrayList<>();
        //cas de sans responsable
        if (idutilisateur==(-1)) {return ResponseEntity.ok(superieursList);}
        // Trouver l'utilisateur par ID
        Optional<Utilisateur> utilisateurOptional = utilisateurRepo.findById(idutilisateur);
        if (utilisateurOptional.isPresent()) {
            Utilisateur utilisateur = utilisateurOptional.get();
            // Appeler le service pour récupérer la liste des utilisateurs supérieurs
             superieursList = utilisateurService.findAllSuperieursForUtilisateurByIdUtilisateur(utilisateur);
            System.out.println(superieursList);
            return ResponseEntity.ok(superieursList);
        } else {
            return ResponseEntity.notFound().build();
        }
    }



}



















/*
     //fonction pour la creation et le misse a jour d'une equipe
    @PostMapping("/CreateEquipe")
    public String createEquipe(Equipe equipe,
                               @RequestParam("nomequipe") String nomequipe,
                               @RequestParam("idutilresponsabledequipe")  Long idResponsableEquipe,
                               @RequestParam(value = "idutilisateur", required = false) List<Long> idUtilisateurs,
                               RedirectAttributes redirectAttributes, Authentication authentication) {

        // Récupérer l'utilisateur connecté
        String login = authentication.getName();
        Utilisateur utilisateurConnecte = utilisateurRepo.findUtilisateursByMail(login);

        //RMQ: j'ai donné la valeur -1 pour idResponsableEquipe pour creer une equipe sans responsable => (idResponsableEquipe=null)
        //recuperer le responsable d equipe
        Utilisateur responsable=utilisateurService.findUtilisateurById(idResponsableEquipe);
        //verifier si l utilisateur est responsable
        boolean isResponsableOfAnotherTeam =equipeRepo.existsByResponsable(responsable);
        //cas de creation
        if (equipe.getIdequipe()==null)
        {
            //si l utilisateur est deja  responsable d'une equipe
            if (idResponsableEquipe != -1) {

                if (isResponsableOfAnotherTeam) {
                    redirectAttributes.addFlashAttribute("msg", "l utilisateur est déjà Responsable d'une equipe");
                } else {
                    //cas ou l utilisteur ne choisis pas les membre d equipe
                    if (idUtilisateurs == null || idUtilisateurs.isEmpty()) {
                        // Affichez un message d'erreur si idUtilisateurs est nul ou vide
                        // Set the responsible user and the name of equipe
                        Utilisateur responsableEquipe = utilisateurService.findUtilisateurById(idResponsableEquipe);
                        equipe.setResponsable(responsableEquipe);
                        equipe.setNomequipe(nomequipe);

                        // Save the equipe
                        equipeService.saveEquie(equipe);
                        redirectAttributes.addFlashAttribute("msg", "Attention ,vous avez creer une equipe sans membres, il faut choisir des membre pour l'equipe .");
                        return "redirect:/gestionEquipe";
                    } else {
                        // Set the responsible user and the name of equipe
                        Utilisateur responsableEquipe = utilisateurService.findUtilisateurById(idResponsableEquipe);
                        equipe.setResponsable(responsableEquipe);
                        equipe.setNomequipe(nomequipe);
                        // Set the members of the equipe
                        List<Utilisateur> membresEquipe = utilisateurService.findUtilisateursById(idUtilisateurs);
                        equipe.setMembres(membresEquipe);
                        // Set the ID of the equipe
                        // Save the equipe
                        Equipe equipe1 = equipeService.saveEquie(equipe);

                        if (equipe1 != null) {
                            redirectAttributes.addFlashAttribute("msg", "Equipe ajoutée avec succès");
                        } else {
                            redirectAttributes.addFlashAttribute("msg1", "Échec de l'opération");
                        }
                    }
                }
            } else {
                //cas ou l utilisteur ne choisis pas les membre d equipe
                if (idUtilisateurs == null || idUtilisateurs.isEmpty()) {
                    // Affichez un message d'erreur si idUtilisateurs est nul ou vide
                    // Set the responsible user and the name of equipe
                    Utilisateur responsableEquipe = utilisateurService.findUtilisateurById(idResponsableEquipe);
                    equipe.setResponsable(responsableEquipe);
                    equipe.setNomequipe(nomequipe);

                    // Save the equipe
                    equipeService.saveEquie(equipe);
                    redirectAttributes.addFlashAttribute("msg", "Attention ,vous avez creer une equipe sans membres, il faut choisir des membre pour l'equipe .");
                    return "redirect:/gestionEquipe";
                } else {
                    // Set the responsible user and the name of equipe
                    Utilisateur responsableEquipe = utilisateurService.findUtilisateurById(idResponsableEquipe);
                    equipe.setResponsable(responsableEquipe);
                    equipe.setNomequipe(nomequipe);
                    // Set the members of the equipe
                    List<Utilisateur> membresEquipe = utilisateurService.findUtilisateursById(idUtilisateurs);
                    equipe.setMembres(membresEquipe);
                    // Set the ID of the equipe
                    // Save the equipe
                    Equipe equipe1 = equipeService.saveEquie(equipe);

                    if (equipe1 != null) {
                        redirectAttributes.addFlashAttribute("msg", "Equipe ajoutée avec succès");
                    } else {
                        redirectAttributes.addFlashAttribute("msg1", "Échec de l'opération");
                    }
                }
            }
        }
        //cas de modification
        else
        {//cas ou l utilisteur ne choisis pas les membre d equipe
                if (idUtilisateurs == null || idUtilisateurs.isEmpty()) {
                    // Set the responsible user and the name of equipe
                    Utilisateur responsableEquipe = utilisateurService.findUtilisateurById(idResponsableEquipe);
                    equipe.setResponsable(responsableEquipe);
                    equipe.setNomequipe(nomequipe);
                    // Save the equipe
                   equipeService.saveEquie(equipe);
                    // Affichez un message
                    redirectAttributes.addFlashAttribute("msg", "Attention ,vous avez creer une equipe sans membres, il faut choisir des membre pour l'equipe .");
                    return "redirect:/gestionEquipe";
                }else {
                    // Set the responsible user and the name of equipe
                    Utilisateur responsableEquipe = utilisateurService.findUtilisateurById(idResponsableEquipe);
                    equipe.setResponsable(responsableEquipe);
                    equipe.setNomequipe(nomequipe);
                    // Set the members of the equipe
                    List<Utilisateur> membresEquipe = utilisateurService.findUtilisateursById(idUtilisateurs);
                    equipe.setMembres(membresEquipe);
                    // Save the equipe
                    Equipe equipe1 = equipeService.saveEquie(equipe);

                    if (equipe1 != null) {
                        redirectAttributes.addFlashAttribute("msg2", "L'equipe a été modifiée avec succès.");
                    } else {
                        redirectAttributes.addFlashAttribute("msg1", "Échec de l'opération");
                    }
                }
            }
        return "redirect:/gestionEquipe";
    }
            */