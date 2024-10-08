package com.example.tachesapp.Service;

import com.example.tachesapp.Dao.EquipeRepo;
import com.example.tachesapp.Dao.NotificationsRepo;
import com.example.tachesapp.Dao.TacheRepo;
import com.example.tachesapp.Dao.UtilisateurRepo;
import com.example.tachesapp.Model.*;
import com.example.tachesapp.Utilité.RelationType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.sql.Date;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
import java.util.*;

@Service
public class TacheServiceImpl implements TacheService {
    @Autowired
    public TacheRepo tacheRepo;
    @Autowired
    UtilisateurRepo utilisateurRepo;
    @Autowired
    EquipeRepo equipeRepo;
    @Autowired
    NotificationsService notificationService;
    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    @Autowired
    NotificationsRepo notificationsRepo;
    @Autowired
    NotificationsService notificationsService;




    public void UpdateTacheToProgramme(Tache tache, RedirectAttributes redirectAttributes, Utilisateur utilisateurconnecte) {
        Tache tacheExist = tacheRepo.findByIdtache(tache.getIdtache());
        // cas ou l utilisateur choisis le statut programme sans choisir la tache parente
        if (tache.getTacheparente() == null) {
            redirectAttributes.addFlashAttribute("error", "Il faut choisir une tâche parente pour une tâche programmée.");
        } else {
            //Effacer la date de fin si le statut précédent était 'Terminé'
            tacheExist.setUtilisateur(tache.getUtilisateur());
            mettreAJourProprietesTache(tache,utilisateurconnecte);
            tacheExist.setStatut(tache.getStatut());
            tacheExist.setTacheparente(tache.getTacheparente());
            tacheExist.setDateTermineTache(null);
            tacheExist.setDateobjectif(null);
            tacheExist.setDateouverture(null);
            tacheRepo.save(tacheExist);
        }
    }

    public void updateTacheWithStatus(Tache tache,Utilisateur utilisateurconnecte) {
        Tache tacheExist = tacheRepo.findTacheByIdtache(tache.getIdtache());
        Utilisateur ancienRecepteur=tacheExist.getRecepteur();
        // enregistré le modificateur de la statut
        mettreAJourProprietesTache(tache,utilisateurconnecte);
        //Effacer la date de fin si le statut précédent était 'Terminé'
        tacheExist.setDateTermineTache(null);
        tacheExist.setTacheparente(null);
        tacheExist.setDureretarde(0);
        tacheExist.setPerformance(0);
        //calculer la date d'objectif
        tacheExist.setDateobjectif( calculerDateObjectif(tache));
        //changer le statut
        tacheExist.setStatut(tache.getStatut());
        if (ancienRecepteur!=tache.getRecepteur()) {
            //------------------envoiyer un mail
            if (tache.getStatut().equals("À refaire")) {notificationsService.sendEmailRefaire(tache);}
            if (tache.getStatut().equals("Terminée")) {notificationsService.sendEmailTerminee(tache);}
            else {notificationsService.sendEmailForANewTask(tache);}
        }
        tacheRepo.save(tacheExist);
    }

    public void addSuccessMessage(RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("msg", "La tâche a été modifiée avec succès.");
    }

    public void UpdateTacheToValide(Tache tache,Utilisateur utilisateurconnecte) {
        Tache tacheExist = tacheRepo.findByIdtache(tache.getIdtache());
        // enregistré le modificateur de la statut
       tacheExist.setModifierpar(utilisateurconnecte);
       mettreAJourProprietesTache(tache,utilisateurconnecte);
       tacheExist.setTacheparente(null);
       //-Démarrer les tâches programmées s'il en existe.
        demarrerTachesProgrammees(tache,utilisateurconnecte);
        //Calcul de performance
        tacheExist.setPerformance( calculeDePerformance(tache));
        //Calcul de Durée retard
        tacheExist.setDureretarde(calculerDureeRetard(tache));
        // Stocker la date actuelle
        tacheExist.setDateTermineTache(Date.valueOf(LocalDate.now()));
        tacheExist.setStatut("Validée");
        tacheRepo.save(tacheExist);
        //------------------envoiyer un mail
        if (utilisateurconnecte.getIdutilisateur()!=tache.getRecepteur().getIdutilisateur()) {
            notificationsService.sendEmailValidation(tache);
        }
    }

    public void UpdateTacheToAnnuler(Tache tache,Utilisateur utilisateurconnecte) {
        Tache tacheExist = tacheRepo.findByIdtache(tache.getIdtache());
        // enregistré le modificateur de la statut
        mettreAJourProprietesTache(tache,utilisateurconnecte);
        tacheExist.setDateTermineTache(null);
        tacheExist.setAunetachesuccessive(false);
        tacheExist.setTacheparente(null);
        tacheExist.setPerformance(0);
        tacheExist.setDureretarde(0);
        //changer le statut
        tacheExist.setStatut("Annulée");
        AnnulerTachesProgrammees(tache,utilisateurconnecte);
        tacheRepo.save(tacheExist);
        //envoiyer un mail
        if (utilisateurconnecte.getIdutilisateur()!=tache.getRecepteur().getIdutilisateur()) {
            notificationsService.sendEmailAnnulation(tache);
        }
    }

    public void mettreAJourProprietesTache(Tache tache,Utilisateur utilisateurconnecte){
        Tache tacheExist = tacheRepo.findByIdtache(tache.getIdtache());
        tacheExist.setModifierpar(utilisateurconnecte);
        tacheExist.setNomtache(tache.getNomtache());
        tacheExist.setRecepteur(tache.getRecepteur());
        tacheExist.setDureestime(tache.getDureestime());
        tacheExist.setPriorite(tache.getPriorite());
        tacheExist.setProprietaire(tache.getProprietaire());
        tacheExist.setDateouverture(tache.getDateouverture());
        tacheExist.setAunetachesuccessive(tache.isAunetachesuccessive());
        tacheExist.setType(tache.getType());
        tacheExist.setEtape(tache.getEtape());
    }

    public int calculerDureeRetard(Tache tache) {
        Tache ExisteTache=tacheRepo.findTacheByIdtache(tache.getIdtache());
        LocalDate dateTermineTache1 = LocalDate.now();
        Date DateObjectif=ExisteTache.getDateobjectif();
        LocalDate Dateobjectif1 = DateObjectif.toLocalDate();
        long Dureeretard = ChronoUnit.DAYS.between(Dateobjectif1, dateTermineTache1);
        int DureeretardEnEntier = Math.toIntExact(Dureeretard);
        return DureeretardEnEntier;
    }

    public void demarrerTachesProgrammees(Tache tache,Utilisateur utilisateurconnecte) {
        List<Tache> tacheList = tacheRepo.findAllByTacheparente(tache);
        // Stocker la date actuelle
        LocalDate currentDate = LocalDate.now();
        //---------------------------Démarrer les tâches programmées s'il en existe.
            if (tacheList != null) {
                // Mettre les tâches programmées En attente et définir la date d'objectif
                for (Tache t : tacheList) {
                    t.setStatut("En attente");
                    // Date d'objectif = date de fin du parent + durée estimée de la tâche fille
                    LocalDate dateOuverture2 = currentDate;
                    LocalDate DateObjectif3 = dateOuverture2.plus(t.getDureestime(), ChronoUnit.DAYS);
                    t.setDateobjectif(Date.valueOf(DateObjectif3));
                    t.setDateouverture(Date.valueOf(dateOuverture2));
                    // Envoyer des e-mails
                    if (utilisateurconnecte.getIdutilisateur()!=t.getRecepteur().getIdutilisateur()) {
                        notificationsService.sendEmailForANewTask(t);
                    }
                }
        }
    }

    public void AnnulerTachesProgrammees(Tache tache,Utilisateur utilisateurconnecte) {
        List<Tache> tacheList = tacheRepo.findAllByTacheparente(tache);
        //---------------------------Démarrer les tâches programmées s'il en existe.
        if (tacheList != null) {
            // Mettre les tâches programmées En attente et définir la date d'objectif
            for (Tache t : tacheList) {
                t.setStatut("Annulée");
                // Date d'objectif = date de fin du parent + durée estimée de la tâche fille
                t.setDateobjectif(null);
                t.setDateouverture(null);
                t.setDateTermineTache(null);
                tache.setTacheparente(null);
                t.setDureretarde(0);
                t.setPerformance(0);
                tacheRepo.save(t);
                // Envoyer des e-mails
                if (utilisateurconnecte.getIdutilisateur()!=tache.getRecepteur().getIdutilisateur()) {
                    notificationsService.sendEmailAnnulation(tache);
                }
            }
        }
    }

    public int calculeDePerformance(Tache tache){
        Date dateOuverture = tache.getDateouverture();
        LocalDate dateOuverture1 = dateOuverture.toLocalDate();
        Date dateobjectif = tache.getDateobjectif();
        LocalDate dateobjectif1 = dateobjectif.toLocalDate();
        //Date dateTermineTache = tache.getDateTermineTache();
        LocalDate dateTermineTache1 = LocalDate.now();;
        int performanceEnEntier;
        // Calcul de la durée consommée en jours
        long dureeConsommee = ChronoUnit.DAYS.between(dateOuverture1,dateTermineTache1)+1;
        //cas où l'utilisateur réalise la tâche avant la date d'ouverture
        if (dureeConsommee <= 0) {
            long dureeConsommee2 =Math.abs(ChronoUnit.DAYS.between(dateOuverture1,dateTermineTache1));
            // Éviter une division par zéro et calculer la performance
            long dureeEstime = ChronoUnit.DAYS.between(dateOuverture1,dateobjectif1)+1;
           // double performanceDouble = (dureeConsommee2 != 0) ? ((double) dureeEstime / dureeConsommee2) * 100 : 0;
            double performanceDouble = ((double) dureeEstime + dureeConsommee2) * 100;
            // Convertir la performance en entier
            performanceEnEntier = (int) performanceDouble;
        }
        //cas normale
        else {
            // Éviter une division par zéro et calculer la performance
           // long dureeEstime = tache.getDureestime()+1;
            long dureeEstime = ChronoUnit.DAYS.between(dateOuverture1,dateobjectif1)+1;
            double performanceDouble = (dureeConsommee != 0) ? ((double) dureeEstime / dureeConsommee) * 100 : 0;
            // Convertir la performance en entier
            performanceEnEntier = (int) performanceDouble;
        }
        return performanceEnEntier;
    }

    public Date calculerDateObjectif(Tache tache) {
        // Obtenez la date d'ouverture de la tâche
        Date dateOuverture = tache.getDateouverture();
        LocalDate dateOuverture1 = dateOuverture.toLocalDate();
        // Obtenez la durée estimée de la tâche
        int dureeEstime = tache.getDureestime();
        // Calculez la date d'objectif en ajoutant la durée estimée à la date d'ouverture
        LocalDate dateObjectif = dateOuverture1.plusDays(dureeEstime-1);
        // Convertissez et retournez la date d'objectif en java.sql.Date et mettez à jour la tâche
        return Date.valueOf(dateObjectif);
    }

    @Override
    public List<Tache> findTachesByIdEmetteur(List<Long> idemetteur,Utilisateur recepteur) {
        List<Utilisateur> utilisateurList = utilisateurRepo.findAllById(idemetteur);
        // Créez une liste vide pour stocker les utilisateurs
        List<Tache> tacheList = new ArrayList<>();
        // Parcourez la liste des utilisateurs
        for (Utilisateur utilisateur : utilisateurList) {
            // Obtenez la liste des taches du utilisateurs
            List<Tache> tachesutilisateurList = tacheRepo.findTacheByUtilisateurAndIsmemoireAndRecepteur(utilisateur,false,recepteur);
            // Ajoutez la liste des taches de l'utilisateur à la liste globale
            tacheList.addAll(tachesutilisateurList);
        }
        tacheList.sort(Comparator.comparing(Tache::getIdtache).reversed());
        // Retournez la liste des utilisateurs
        return tacheList;
    }

    @Override
    public List<Tache> lesTacheSecondairesParTacheParent(Tache tache,Utilisateur utilisateur) {

        List<Tache> tachesSecondaires=tacheRepo.findAllByTacheparente(tache);
        return tachesSecondaires;
    }

    @Override
    public boolean aDesTachesSecondaires(Tache tache) {
        List<Tache> tachesSecondaires=tacheRepo.findAllByTacheparente(tache);
        if (tachesSecondaires.size() != 0) {
            return true;
        }
        return false;
    }

    @Override
    public ResponseEntity<List<Tache>> getAllMyTask(Authentication authentication, Model model) {
        String login = authentication.getName();
        Utilisateur utilisateur = utilisateurRepo.findUtilisateursByMail(login);
        List<Tache> tacheList=tacheRepo.findAllByRecepteur(utilisateur);
        tacheList.sort(Comparator.comparing(Tache::getIdtache).reversed());
        model.addAttribute("tacheList",tacheList);
        return ResponseEntity.ok(tacheList);
    }

    @Override
    public ResponseEntity<List<Tache>> getTasksExcludingValidatedAndCancelled(Authentication authentication, Model model) {
        String login = authentication.getName();
        Utilisateur utilisateur = utilisateurRepo.findUtilisateursByMail(login);
        List<String> excludedStatuts = Arrays.asList("Validée", "Annulée");
        // Tâches de l'équipe sans les tâches validées et annulées
        List<Tache> teamTasks = tacheRepo.findAllByRecepteurAndIsmemoireAndStatutNotIn(utilisateur,false,excludedStatuts);
        teamTasks.sort(Comparator.comparing(Tache::getIdtache).reversed());
        return ResponseEntity.ok(teamTasks);
    }

    @Override
    public void CreateTache(Tache tache, RedirectAttributes redirectAttributes, Authentication authentication) {
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
                LocalDate dateObjectif = dateOuverture1.plusDays(dureeEstime-1);
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
                if (utilisateurConnecte.getIdutilisateur()!=tache.getRecepteur().getIdutilisateur()) {
                    notificationsService.sendEmailForANewTask(tache);
                }
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
            LocalDate dateObjectif = dateOuverture1.plusDays(dureeEstime-1);
            // Convertissez la date d'objectif en java.sql.Date et mettez à jour la tâche
            tacheExist.setDateobjectif(Date.valueOf(dateObjectif));
            tacheExist.setStatut(tacheExist.getStatut());
            tacheExist.setDureestime(tache.getDureestime());
            tacheExist.setDateouverture(tache.getDateouverture());
            tacheExist.setPriorite(tache.getPriorite());
            tacheExist.setType(tache.getType());
            tacheExist.setAunetachesuccessive(tache.isAunetachesuccessive());
            tacheExist.setProprietaire(tache.getProprietaire());
            tacheExist.setEtape(tache.getEtape());
            // Mettre à jour le reste des champs
            tacheExist.setModifierpar(utilisateurConnecte);
            tacheRepo.save(tacheExist);
            redirectAttributes.addFlashAttribute("msg1", "La tâche a été modifiée avec succès.");
        }
    }

    @Override
    public Tache updateTacheStatut(Long tacheId, Authentication authentication) {
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
            if (utilisateurconnecte.getIdutilisateur()!=existingTache.getRecepteur().getIdutilisateur()) {
                notificationsService.sendEmailTerminee(existingTache);
            }
        }
        // enregistré le modificateur de la statut
        existingTache.setModifierpar(utilisateurconnecte);
        // Enregistrer les modifications de la tâche
        tacheRepo.save(existingTache);
        // Retourner la tâche mise à jour
        return existingTache;
    }

    @Override
    public Tache ValiderStatut(Long id, Authentication authentication) {
        String login=authentication.getName();
        Utilisateur utilisateur=utilisateurRepo.findUtilisateursByMail(login);
        //recuperer la tache par l idTache
        Tache tache=tacheRepo.findByIdtache(id);
        // Stocker la date actuelle
        tache.setDateTermineTache(Date.valueOf(LocalDate.now()));
        //changer le statut
        tache.setStatut("Validée");
        tache.setModifierpar(utilisateur);
        //--------------------------------------------Calcul de performance
        tache.setPerformance(calculeDePerformance(tache));
        //--------------------------------------------Calcul de Durée retard
        tache.setDureretarde(calculerDureeRetard(tache));
        //------------------envoiyer un mail
        if (utilisateur.getIdutilisateur()!=tache.getRecepteur().getIdutilisateur()) {
            notificationsService.sendEmailValidation(tache);
        }
        //---------------------------Démarrer les tâches programmées s'il en existe.
        demarrerTachesProgrammees(tache,utilisateur);
        return tache;
    }

    @Override
    public Tache RefaireTache(Long id, Authentication authentication) {
        String login=authentication.getName();
        Utilisateur utilisateur=utilisateurRepo.findUtilisateursByMail(login);
        //recuperer la tache par l idTache
        Tache tache=tacheRepo.findByIdtache(id);
        //changer le statut
        tache.setStatut("À refaire");
        tache.setDateTermineTache(null);
        tache.setModifierpar(utilisateur);
        //envoiyer un mail
        if (utilisateur.getIdutilisateur()!=tache.getRecepteur().getIdutilisateur()) {
            notificationsService.sendEmailRefaire(tache);
        }
        return tache;
    }

    @Override
    public Tache AnnulerTache(Long id, Authentication authentication) {
        String login=authentication.getName();
        Utilisateur utilisateur=utilisateurRepo.findUtilisateursByMail(login);
        //recuperer la tache par l idTache
        Tache tache=tacheRepo.findByIdtache(id);
        //changer le statut
        tache.setStatut("Annulée");
        tache.setDateTermineTache(null);
        tache.setDateobjectif(null);
        tache.setDateouverture(null);
        tache.setDureretarde(0);
        tache.setPerformance(0);
        tache.setTacheparente(null);
        AnnulerTachesProgrammees(tache,utilisateur);
        tache.setModifierpar(utilisateur);
        tacheRepo.save(tache);
        //envoiyer un mail
        if (utilisateur.getIdutilisateur()!=tache.getRecepteur().getIdutilisateur()) {
            notificationsService.sendEmailAnnulation(tache);
        }
        return tache;
    }

    @Override
    public List<Tache> tachesParentes(Authentication authentication) {
        String login=authentication.getName();
        Utilisateur utilisateur=utilisateurRepo.findUtilisateursByMail(login);
        //Liste des tâches ayant des tâches successives (tâches parentes).
        // Pour mettre à jour l champ selecte des tâches parentes (récupérer seulement les tâches non terminées ayant des tâches successives)." +
        List<Tache> tacheList1=findTacheParent(utilisateur,true);
        return tacheList1;
    }

    @Override
    public List<Utilisateur> membresDeLequipeDutilisateur(Authentication authentication) {
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

    @Override
    public Tache IndexerTache(Long id, Authentication authentication) {
        String login=authentication.getName();
        Utilisateur utilisateur=utilisateurRepo.findUtilisateursByMail(login);
        //recuperer la tache par l idTache
        Tache tache=tacheRepo.findByIdtache(id);
        //indexer la tache
        tache.setAunetachesuccessive(true);
        tache.setModifierpar(utilisateur);
        return tache;
    }

    @Override
    public List<Tache> findTacheParent(Utilisateur utilisateur, Boolean x) {
        // Initialiser la liste pour stocker le résultat
        List<Tache> tacheList = new ArrayList<>();
        // Rechercher les tâches pour l'utilisateur donné et les statuts
        String[] statuts = {"En attente", "Programmée", "À refaire", "Terminée", "En cours"};
        for (String statut : statuts) {
            // Ajouter les tâches uniques à la liste
            ajouterTachesUniques(tacheList, tacheRepo.findDistinctByUtilisateurAndAunetachesuccessiveAndStatut(utilisateur, true, statut));
            ajouterTachesUniques(tacheList, tacheRepo.findDistinctByRecepteurAndAunetachesuccessiveAndStatut(utilisateur, true, statut));
        }
        // Rechercher les tâches pour l'équipe de l'utilisateur
        List<Utilisateur> equipe = findRecepteurs(utilisateur);
        for (Utilisateur u : equipe) {
            for (String statut : statuts) {
                // Ajouter les tâches uniques à la liste pour l'utilisateur
                ajouterTachesUniques(tacheList, tacheRepo.findDistinctByUtilisateurAndAunetachesuccessiveAndStatut(u, true, statut));
                // Ajouter les tâches uniques à la liste pour le récepteur
                ajouterTachesUniques(tacheList, tacheRepo.findDistinctByRecepteurAndAunetachesuccessiveAndStatut(u, true, statut));
            }
        }
        return tacheList;
    }

    public List<Tache> findTachesByIdUtilisateurs2(List<Long> utilisateursIds,Utilisateur utilisateurConnecté) {
        List<Utilisateur> utilisateurList = utilisateurRepo.findAllById(utilisateursIds);
        // Créez une liste vide pour stocker les utilisateurs
        List<Tache> tacheList = new ArrayList<>();
        // Parcourez la liste des utilisateurs
        for (Utilisateur utilisateur : utilisateurList) {
            // Ajoutez la liste des taches de l'utilisateur à la liste globale
            ajouterTachesUniques(tacheList, tacheRepo.findTacheByUtilisateurAndIsmemoire(utilisateur, false));
            ajouterTachesUniques(tacheList, tacheRepo.findTacheByRecepteurAndIsmemoire(utilisateur, false));
        }
        tacheList.sort(Comparator.comparing(Tache::getIdtache).reversed());
        // Retournez la liste des utilisateurs
        return tacheList;
    }

    public List<Tache> findTachesByIdUtilisateurs(List<Long> utilisateursIds,Utilisateur utilisateurConnecté) {
        List<Utilisateur> utilisateurList = utilisateurRepo.findAllById(utilisateursIds);
        // Créez une liste vide pour stocker les utilisateurs
        List<Tache> tacheList = new ArrayList<>();
        // Parcourez la liste des utilisateurs
        for (Utilisateur utilisateur : utilisateurList) {
            if (utilisateur==utilisateurConnecté) {
                // Ajoutez la liste des taches de l'utilisateur à la liste globale
                ajouterTachesUniques(tacheList,tacheRepo.findTacheByUtilisateurAndIsmemoire(utilisateur,false));
                //ajouterTachesUniques(tacheList,tacheRepo.findTacheByRecepteurAndIsmemoire(utilisateur,false));
            }else {
                // Obtenez la liste des taches du utilisateurs
                List<Tache> tachesutilisateurList = tacheRepo.findTacheByRecepteurAndIsmemoire(utilisateur,false);
                // Ajoutez la liste des taches de l'utilisateur à la liste globale
                tacheList.addAll(tachesutilisateurList);
            }
        }
        tacheList.sort(Comparator.comparing(Tache::getIdtache).reversed());
        // Retournez la liste des utilisateurs
        return tacheList;
    }

    // Méthode auxiliaire pour ajouter des tâches uniques à la liste
    private void ajouterTachesUniques(List<Tache> tacheList, List<Tache> nouvellesTaches) {
        for (Tache tache : nouvellesTaches) {
            // Vérifier si la tâche est déjà dans la liste
            if (!tacheList.contains(tache)) {
                // Ajouter la tâche uniquement si elle n'est pas déjà présente dans la liste
                tacheList.add(tache);
            }
        }
    }

    // Cette fonction a pour but d'obtenir l'équipe et les sous-équipes
    // (si l'un des membres est responsable d'une équipe) de l'utilisateur.
    @Override
    public List<Utilisateur> findRecepteurs(Utilisateur utilisateur) {
        // Vérifier si l'utilisateur est responsable d'une équipe
        Boolean isResponsable = equipeRepo.existsByResponsable(utilisateur);
        // Initialiser une liste pour stocker les utilisateurs qui seront les destinataires (équipe et sous-équipes)
        List<Utilisateur> recepteurs = new ArrayList<>();
        // Si l'utilisateur est responsable d'une équipe
        if (isResponsable) {
            // Trouver l'équipe de l'utilisateur et les membres de cette équipe
            Equipe equipe = equipeRepo.findEquipeByResponsable(utilisateur);
            recepteurs.addAll(equipe.getMembres());
            // Faire une copie de la liste pour l'itération
            List<Utilisateur> recepteursCopy = new ArrayList<>(recepteurs);
            // Récursion pour chaque membre de l'équipe
            for (Utilisateur u : recepteursCopy) {
                // Pour éviter les boucles infinies, vérifie si l'utilisateur courant ou son responsable est l'utilisateur initial
                if (u == utilisateur ) {
                    continue;
                }
                // Utiliser la récursion pour explorer récursivement les sous-équipes et les membres associés
                recepteurs.addAll(findRecepteurs(u));
            }
        }
        // Retourner la liste des destinataires (équipe et sous-équipes)
        return recepteurs;
    }

    //fonction pour trouver les responsables de l utilisateur connecté
    @Override
    public List<Utilisateur> findEmetteurs(Utilisateur utilisateur) {
        List<Utilisateur> Emetteurs = new ArrayList<>();
        Equipe equipeUtilisateur = utilisateur.getEquipes();
        if (equipeUtilisateur != null)
        {
            Utilisateur respo = equipeUtilisateur.getResponsable();
            if (respo!=null) {
                Emetteurs.add(respo);
                Emetteurs.addAll(findEmetteurs(respo));
            }
        }
        return Emetteurs;
    }

    public void loadRelationType(Model model) {
        List<RelationType> relationTypes=new ArrayList<>();
        relationTypes.add(RelationType.SUBORDONNE);
        relationTypes.add(RelationType.COLLEGUE);
        relationTypes.add(RelationType.SUPERIEUR);
        model.addAttribute("relationTypes", relationTypes);   // Ajouter l'attribut "relationType" au modèle
    }


}