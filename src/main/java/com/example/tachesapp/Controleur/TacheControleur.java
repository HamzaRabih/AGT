package com.example.tachesapp.Controleur;

import com.example.tachesapp.Dao.*;
import com.example.tachesapp.Model.*;

import com.example.tachesapp.Service.NotificationsService;
import com.example.tachesapp.Service.TacheService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.sql.Date;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Controller
@RequestMapping("/")
public class TacheControleur {
    @Autowired
    TacheRepo tacheRepo;
    @Autowired
    UtilisateurRepo utilisateurRepo;
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
    private SimpMessagingTemplate messagingTemplate;

    //--------------------------------------Gestion Taches
    //Affichage de creeTache.HTML
    @GetMapping("/CreerTache")
    public String home(Authentication authentication, Model model)
    {
        //l utilisateur connecté
        String login = authentication.getName();
        Utilisateur utilisateur = utilisateurRepo.findUtilisateursByMail(login);
        model.addAttribute("utilisateurC",utilisateur);

        //les utilisteurs de la meme societé (pour le champ proprietaire)
        Societe societe= societeRepo.findAllByUtilisateurs(utilisateur);
        List<Utilisateur> utilisateurList=utilisateurRepo.findUtilisateursBySociete(societe);
        model.addAttribute("utilisateurList",utilisateurList);

        //Pour mettre la liste en ordre alphabétique
        // Utilisation de la méthode sort de Collections avec un comparateur ignorant la casse
        Collections.sort(utilisateurList, new Comparator<Utilisateur>() {
            @Override
            public int compare(Utilisateur utilisateur1, Utilisateur utilisateur2) {
                // Comparez les noms des utilisateurs sans tenir compte de la casse
                return utilisateur1.getNom().compareToIgnoreCase(utilisateur2.getNom());
            }
        });

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

        //les mebres d equipe de lutilisateur connecté
        Equipe equipe=equipeRepo.findEquipeByResponsable(utilisateur);
        if (equipe != null) {
            List<Utilisateur> membres = equipe.getMembres();
            model.addAttribute("membres", membres);
        } else {
            List<Utilisateur> membres=null;
            model.addAttribute("membres",membres);
        }

        //les taches de mon equipe
        //List<Tache> equipeTaches=tacheRepo.findAllByUtilisateurInAndIsmemoire(Recepteurs,false);
        List<Tache> equipeTaches=tacheRepo.findAllByRecepteurInAndIsmemoire(Recepteurs,false);
        model.addAttribute("equipeTaches",equipeTaches);

        // Récupérer les notifications de l'utilisateur connecté
        List<Notification> notificationList = notificationsRepo.findByRecepteurOrderByDatenotifDesc(utilisateur);
        model.addAttribute("notificationList", notificationList);

        // Calculer les notifications non lues de l'utilisateur connecté,pour l'affiché;
        List<Notification> nonLuesNotificationList = notificationsRepo.findByRecepteurAndEstLu(utilisateur, false);
        // Calculer le nombre de notifications non lues
        int nbrNotifNonLu = nonLuesNotificationList.size();
        model.addAttribute("nbrNotifNonLu", nbrNotifNonLu);

        //Les Priorité
        List<Priorite> priorites=prioriteRepo.findAll();
        model.addAttribute("priorites",priorites);
        System.out.println(priorites);
        return "creeTache";
    }

    //--Mes taches
    //Affichage de mesTache.html
    @GetMapping("/")
    public String MesTache (Authentication authentication,Model model)
    {
        String login = authentication.getName();
        Utilisateur utilisateur = utilisateurRepo.findUtilisateursByMail(login);
        model.addAttribute("utilisateurC",utilisateur);

        List<Tache> tacheList=tacheRepo.findAllByRecepteur(utilisateur);
        model.addAttribute("tacheList",tacheList);

        //-----Cette partie a pour but d'obtenir les respo de l'utilisateur,
        List<Utilisateur> Emetteurs=tacheService.findEmetteurs(utilisateur);

        //Pour mettre la liste en ordre alphabétique
        // Utilisation de la méthode sort de Collections avec un comparateur ignorant la casse
        Collections.sort(Emetteurs, new Comparator<Utilisateur>() {
            @Override
            public int compare(Utilisateur utilisateur1, Utilisateur utilisateur2) {
                // Comparez les noms des utilisateurs sans tenir compte de la casse
                return utilisateur1.getNom().compareToIgnoreCase(utilisateur2.getNom());
            }
        });
        Emetteurs.add(0, utilisateur);
        // La liste Recepteurs est maintenant triée par ordre alphabétique (sans tenir compte de la casse)
        model.addAttribute("Emetteurs",Emetteurs);
        //------------------

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

        //Les Priorité
        List<Priorite> priorites=prioriteRepo.findAll();
        model.addAttribute("priorites",priorites);

        //les utilisteurs de la meme societé (pour le champ proprietaire)
        Societe societe= societeRepo.findAllByUtilisateurs(utilisateur);
        List<Utilisateur> utilisateurList=utilisateurRepo.findUtilisateursBySociete(societe);
        model.addAttribute("utilisateurList",utilisateurList);

        return "/pages/mesTache";
    }


    @GetMapping(value = "/getAllMyTask")
    public ResponseEntity<List<Tache>> getAllMyTask(Authentication authentication, Model model)
    {
        String login = authentication.getName();
        Utilisateur utilisateur = utilisateurRepo.findUtilisateursByMail(login);
        List<Tache> tacheList=tacheRepo.findAllByRecepteur(utilisateur);
        model.addAttribute("tacheList",tacheList);
        return ResponseEntity.ok(tacheList);
    }

    // -----------------------------------
// Créer ou mettre à jour une tâche
// -----------------------------------
    @PostMapping("/CreateTache")
    public String CreateTache(@ModelAttribute Tache tache, RedirectAttributes redirectAttributes, Authentication authentication) {
        // Récupérer l'utilisateur recepteur de la tache
        Utilisateur utilisateurRecepeur = tache.getRecepteur();

        // Récupérer l'utilisateur connecté
        String login = authentication.getName();
        Utilisateur utilisateurConnecte = utilisateurRepo.findUtilisateursByMail(login);

        //cas de creation
        if (tache.getIdtache() == null) {
            // Modifier le statut de la tâche programmée (si une tâche a une tâche parent, le statut est automatiquement défini sur "programmé").            if (tache.getTacheparente() != null) {
            if (tache.getTacheparente() != null) {
                tache.setStatut("Programmée");
                tache.setDateobjectif(null);
                tache.setDateouverture(null);
            }else {
                // Pour une tâche non programmée, définir le statut sur "En attente" et calculer la date d'objectif
                tache.setStatut("En attente"); // "En attente"
                // Obtenez la date d'ouverture de la tâche
                Date dateouverture = tache.getDateouverture();
                LocalDate dateOuverture1 = dateouverture.toLocalDate();
                // Obtenez la durée estimée de la tâche
                int dureeEstime = tache.getDureestime();
                // Calculez la date d'objectif en ajoutant la durée estimée à la date d'ouverture
                LocalDate dateObjectif = dateOuverture1.plusDays(dureeEstime);
                // Convertissez la date d'objectif en java.sql.Date et mettez à jour la tâche
                tache.setDateobjectif(Date.valueOf(dateObjectif));

            }
            // Enregistrer la tâche
            tacheRepo.save(tache);

            //creer la notification
            Notification notification=notificationService.creerNotificationDeCreationTache(tache.getRecepteur().getIdutilisateur(),tache);

            redirectAttributes.addFlashAttribute("msg", "Tâche créée avec succès");

            // Envoyer le message à l'aide de webSoket
            messagingTemplate.convertAndSend("/topic/", tache);
            // Envoyer la notif à un utilisateur spécifié(lie avec la foction du webSoket dans notifWebSoket.Js )
            messagingTemplate.convertAndSendToUser(utilisateurRecepeur.getMail(), "/topic/private", new Object[]{tache, notification});

            // Envoyer un e-mail pour une tâche non programmée
            if (tache.getTacheparente() == null) {
                // Envoyer des e-mails
                Utilisateur recepteur = tache.getRecepteur();
                Utilisateur emetteur=utilisateurRepo.findByIdutilisateur(tache.getUtilisateur().getIdutilisateur());
                String Subject="Vous avez une nouvelle tâche de : "+emetteur.getNom()+" "+emetteur.getPrenom();
                String msg="nouvelle tâche :"+tache.getNomtache();
                tacheService.sendTaskEmail(recepteur.getMail(),Subject,msg);
            }

        } else {
            // Mettre à jour la tâche existante

            Tache tacheExist = tacheRepo.findById(tache.getIdtache()).orElse(null);

            // Pour une tâche non programmée, définir le statut sur "En attente" et calculer la date d'objectif
            //tacheExist.setStatut("En attente"); // "En attente"
            tacheExist.setRecepteur(tache.getRecepteur());
            // Obtenez la date d'ouverture de la tâche
            Date dateouverture = tache.getDateouverture();
            LocalDate dateOuverture1 = dateouverture.toLocalDate();
            // Obtenez la durée estimée de la tâche
            int dureeEstime = tache.getDureestime();
            // Calculez la date d'objectif en ajoutant la durée estimée à la date d'ouverture
            LocalDate dateObjectif = dateOuverture1.plusDays(dureeEstime);
            // Convertissez la date d'objectif en java.sql.Date et mettez à jour la tâche
            tacheExist.setDateobjectif(Date.valueOf(dateObjectif));
            tacheExist.setStatut(tacheExist.getStatut());
            tacheExist.setDureestime(tache.getDureestime());
            tacheExist.setDateouverture(tache.getDateouverture());
            tacheExist.setPriorite(tache.getPriorite());
            tacheExist.setType(tache.getType());
            tacheExist.setAunetachesuccessive(tache.isAunetachesuccessive());
            tacheExist.setProprietaire(tache.getProprietaire());


            // Mettre à jour le reste des champs
            tacheExist.setModifierpar(utilisateurConnecte);

            tacheRepo.save(tacheExist);
            redirectAttributes.addFlashAttribute("msg1", "La tâche a été modifiée avec succès.");
        }
        return "redirect:/CreerTache";
    }

    // -----------------------------------
// Pour modifier le statut d'une tâche avec un clic (utilisé par modifyStatus() dans notifWebSoket.js lier par AJAX)
// -----------------------------------
    @GetMapping("/tasks/{tacheId}")
    @ResponseBody
    public Tache updateTacheStatut(@PathVariable Long tacheId,Authentication authentication) {
        // Récupérer l'utilisateur connecté
        String login = authentication.getName();
        Utilisateur utilisateurconnecte = utilisateurRepo.findUtilisateursByMail(login);

        // recuperer la tâche existe
        Tache existingTache = tacheRepo.findById(tacheId).orElse(null);

        // Récupérer le statut de la tache
        String statut = existingTache.getStatut();
        if ("En attente".equals(statut) ||  "À refaire".equals(statut)) {
            // Si l'utilisateur clique sur un statut En attente (statut == En attente) : modifier le statut à "En cours"
            existingTache.setStatut("En cours");

        } else if ("En cours".equals(statut)) {
            //Si l'utilisateur clique sur un statut "En cours" : modifier le statut à "terminé"
            existingTache.setStatut("Terminée");
            existingTache.setDateTermineTache(null);

            //envoiyer un mail
            Utilisateur recepteur1 =existingTache.getRecepteur();
            Utilisateur emetteur=utilisateurRepo.findByIdutilisateur(existingTache.getUtilisateur().getIdutilisateur());
            String Subject="Tâche terminée";
            String msg = "La tâche '" + existingTache.getNomtache() + "' de " + recepteur1.getNom() + " " + recepteur1.getPrenom() + " est terminée.";
            tacheService.sendTaskEmail(emetteur.getMail(),Subject,msg);
        }
        // enregistré le modificateur de la statut
        existingTache.setModifierpar(utilisateurconnecte);

        // Enregistrer les modifications de la tâche
        tacheRepo.save(existingTache);

        // Retourner la tâche mise à jour
        return existingTache;
    }



    //Valider un statut
    @Transactional
    @GetMapping(value = "/ValiderStatut/{id}")
    @ResponseBody
    public Tache ValiderStatut(@PathVariable Long id,Authentication authentication) {

        String login=authentication.getName();
        Utilisateur utilisateur=utilisateurRepo.findUtilisateursByMail(login);

        //recuperer la tache par l idTache
        Tache tache=tacheRepo.findByIdtache(id);

        //--------------------------------------------Calcul de performance
        tache.setPerformance(tacheService.calculeDePerformance(tache));

        //--------------------------------------------Calcul de Durée retard
        tache.setDureretarde(tacheService.calculerDureeRetard(tache));

        //------------------envoiyer un mail
        Utilisateur recepteur = tache.getRecepteur();
        String Subject="Tâche Validée: ";
        String msg="Votre tâche: ' "+tache.getNomtache()+"' a été validée par "+tache.getUtilisateur().getNom()+" "+tache.getUtilisateur().getPrenom();
        tacheService.sendTaskEmail(recepteur.getMail(),Subject,msg);

        //---------------------------Démarrer les tâches programmées s'il en existe.
        tacheService.demarrerTachesProgrammees(tache);

        // Stocker la date actuelle
        LocalDate currentDate = LocalDate.now();
        tache.setDateTermineTache(Date.valueOf(currentDate));
        //changer le statut
        tache.setStatut("Validée");
        tache.setModifierpar(utilisateur);
        return tache;
    }


    //Refaire une Tache
    @Transactional
    @GetMapping(value = "/RefaireTache/{id}")
    @ResponseBody
    public Tache RefaireTache(@PathVariable Long id,Authentication authentication) {


        String login=authentication.getName();
        Utilisateur utilisateur=utilisateurRepo.findUtilisateursByMail(login);

        //recuperer la tache par l idTache
        Tache tache=tacheRepo.findByIdtache(id);

        //envoiyer un mail
        Utilisateur recepteur1 =tache.getRecepteur();
        Utilisateur emetteur=utilisateurRepo.findByIdutilisateur(tache.getUtilisateur().getIdutilisateur());
        String Subject="Tâche à refaire";
        String msg = "La tâche '" + tache.getNomtache() + "' soumise par " + emetteur.getNom() + " " + emetteur.getPrenom() + " nécessite des ajustements. Veuillez la revoir et effectuer les modifications nécessaires. Merci.";
         tacheService.sendTaskEmail(recepteur1.getMail(),Subject,msg);

        //changer le statut
        tache.setStatut("À refaire");
        tache.setDateTermineTache(null);
        tache.setModifierpar(utilisateur);


        return tache;
    }

    //Annuler un statut
    @Transactional
    @GetMapping(value = "/AnnulerTache/{id}")
    @ResponseBody
    public Tache AnnulerTache(@PathVariable Long id,Authentication authentication) {


        String login=authentication.getName();
        Utilisateur utilisateur=utilisateurRepo.findUtilisateursByMail(login);

        //recuperer la tache par l idTache
        Tache tache=tacheRepo.findByIdtache(id);

        //envoiyer un mail
        Utilisateur recepteur1 =tache.getRecepteur();
        Utilisateur emetteur=utilisateurRepo.findByIdutilisateur(tache.getUtilisateur().getIdutilisateur());
        String Subject="Tâche annulée";
        String msg = "La tâche '" + tache.getNomtache() + "' soumise par " + emetteur.getNom() + " " + emetteur.getPrenom() + "a été annulée.";
        tacheService.sendTaskEmail(recepteur1.getMail(),Subject,msg);

        //changer le statut
        tache.setStatut("Annulée");
        tache.setDateTermineTache(null);
        tache.setDateobjectif(null);
        tache.setDateouverture(null);
        tache.setDureretarde(0);
        tache.setPerformance(0);
        tache.setTacheparente(null);

        tacheService.AnnulerTachesProgrammees(tache);
        tacheRepo.save(tache);

        tache.setModifierpar(utilisateur);

        return tache;
    }

    //Indexer une tache
    @Transactional
    @GetMapping(value = "/indexerTache/{id}")
    @ResponseBody
    public Tache IndexerTache(@PathVariable Long id,Authentication authentication) {

        String login=authentication.getName();
        Utilisateur utilisateur=utilisateurRepo.findUtilisateursByMail(login);

        //recuperer la tache par l idTache
        Tache tache=tacheRepo.findByIdtache(id);
        //indexer la tache
        tache.setAunetachesuccessive(true);
        tache.setModifierpar(utilisateur);

        return tache;
    }


    //pour afficher les taches Parentes
    @Transactional
    @GetMapping(value = "/tachesParentes")
    @ResponseBody
    public List<Tache> tachesParentes(Authentication authentication) {

        String login=authentication.getName();
        Utilisateur utilisateur=utilisateurRepo.findUtilisateursByMail(login);

        //Liste des tâches ayant des tâches successives (tâches parentes).
        // Pour mettre à jour l champ selecte des tâches parentes (récupérer seulement les tâches non terminées ayant des tâches successives)." +
        List<Tache> tacheList1=tacheService.findTacheParent(utilisateur,true);

        return tacheList1;
    }


    //recuperer une tache par id (utiliser par ajax pour modifie les taches dans la meme forme)
    @GetMapping("/get-Tache/{idTache}")
    @ResponseBody
    public Tache getTache(@PathVariable Long idTache) {
        // Récupérez la Memo
        Tache tache=tacheRepo.findByIdtache(idTache);
        return tache;
    }

    //Trouver le role d utilisateur connecté
    @GetMapping(value = "/Role")
    @ResponseBody
    public Utilisateur Role(Authentication authentication)
    {
        //l utilisateur connecté
        String login = authentication.getName();
        Utilisateur utilisateur = utilisateurRepo.findUtilisateursByMail(login);
        return utilisateur;
    }


    //Trouver le role d utilisateur connecté
    @GetMapping(value = "/membresDeLequipeDutilisateur")
    @ResponseBody
    public  List<Utilisateur> membresDeLequipeDutilisateur(Authentication authentication)
    {
        //l utilisateur connecté
        String login = authentication.getName();
        Utilisateur utilisateur = utilisateurRepo.findUtilisateursByMail(login);
        List<Utilisateur> membres=new ArrayList<>();

        //les mebres d equipe de lutilisateur connecté
        Equipe equipe=equipeRepo.findEquipeByResponsable(utilisateur);
        if (equipe != null) {
            membres = equipe.getMembres();
        }
        return membres;
    }

}
