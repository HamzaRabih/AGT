package com.example.tachesapp.Service;

import com.example.tachesapp.Dao.EquipeRepo;
import com.example.tachesapp.Dao.TacheRepo;
import com.example.tachesapp.Dao.UtilisateurRepo;
import com.example.tachesapp.Model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.sql.Date;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
import java.util.ArrayList;
import java.util.List;
import java.util.HashSet;
import java.util.Set;

@Service
public class TacheServiceImpl implements TacheService {
    @Autowired
    public TacheRepo tacheRepo;
    @Autowired
    UtilisateurRepo utilisateurRepo;
    @Autowired
    EquipeRepo equipeRepo;
    @Autowired
    private JavaMailSender javaMailSender;

    // pour envoyer un mail
    @Async//@Async will make it execute in a separate thread
    public void sendTaskEmail(String recipientEmail,String setSubject,String msg) {

        // variable pour calculer le temps d'envoi des emails
        //long startTime = System.currentTimeMillis();

        // pour envoiyer un mail
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(recipientEmail);
        message.setSubject(setSubject);
        message.setText(msg+"\n");

        javaMailSender.send(message);

        // pour calculer le temps d'envoi des emails
        long endTime = System.currentTimeMillis();
       // System.out.println("Temps d'envoi d'e-mail : " + (endTime - startTime) + " ms");

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

        // Afficher la taille de la liste des tâches
        //System.out.println(tacheList.size());

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

        //stocker les responsable d equipe pour eviter les boucle infini
         List<Utilisateur> responsables=new ArrayList<>();

        // Initialiser une liste pour stocker les utilisateurs qui seront les destinataires (équipe et sous-équipes)
        List<Utilisateur> recepteurs = new ArrayList<>();

        // Si l'utilisateur est responsable d'une équipe
        if (isResponsable) {
            responsables.add(utilisateur);

            // Trouver l'équipe de l'utilisateur et les membres de cette équipe
            Equipe equipe = equipeRepo.findEquipeByResponsable(utilisateur);
            recepteurs.addAll(equipe.getMembres());


            // Faire une copie de la liste pour l'itération
            List<Utilisateur> recepteursCopy = new ArrayList<>(recepteurs);

            // Récursion pour chaque membre de l'équipe
            for (Utilisateur u : recepteursCopy) {
                // Obtient le responsable de l'utilisateur courant
                Utilisateur respoDeU=u.getEquipes().getResponsable();
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
        //System.out.println("wwww"+Emetteurs);
        return Emetteurs;
    }


    public void UpdateTacheToProgramme(Tache tache, RedirectAttributes redirectAttributes, Utilisateur utilisateurconnecte) {
        Tache tacheExist = tacheRepo.findByIdtache(tache.getIdtache());

        // cas ou l utilisateur choisis le statut programme sans choisir la tache parente
        if (tache.getTacheparente() == null) {
            redirectAttributes.addFlashAttribute("error", "Il faut choisir une tâche parente pour une tâche programmée.");
        } else {

            //Effacer la date de fin si le statut précédent était 'Terminé'
            tacheExist.setUtilisateur(tache.getUtilisateur());
            tacheExist.setNomtache(tache.getNomtache());
            tacheExist.setRecepteur(tache.getRecepteur());
            tacheExist.setDureestime(tache.getDureestime());
            tacheExist.setPriorite(tache.getPriorite());
            tacheExist.setProprietaire(tache.getProprietaire());
            tacheExist.setDateouverture(tache.getDateouverture());
            tacheExist.setAunetachesuccessive(tache.isAunetachesuccessive());
            tacheExist.setType(tache.getType());
            tacheExist.setProprietaire(tache.getProprietaire());

            tacheExist.setDateTermineTache(null);
            tacheExist.setDateobjectif(null);
            tacheExist.setDateouverture(null);
            tacheExist.setStatut(tache.getStatut());
            tacheExist.setTacheparente(tache.getTacheparente());
            // enregistré le modificateur de la statut
            tacheExist.setModifierpar(utilisateurconnecte);

            tacheRepo.save(tacheExist);


            //redirectAttributes.addFlashAttribute("msg1", "La tâche a été modifiée avec succès.");
        }
    }

    public void UpdateTacheToEnAttente(Tache tache,RedirectAttributes redirectAttributes,Utilisateur utilisateurconnecte) {
        //Tache tacheExist = tacheRepo.findByIdtache(tache.getIdtache());
        Tache tacheExist = tacheRepo.findTacheByIdtache(tache.getIdtache());
        Utilisateur ancienRecepteur=tacheExist.getRecepteur();

        // enregistré le modificateur de la statut
        tacheExist.setModifierpar(utilisateurconnecte);
        //
        tacheExist.setNomtache(tache.getNomtache());
        tacheExist.setRecepteur(tache.getRecepteur());
        tacheExist.setDureestime(tache.getDureestime());
        tacheExist.setPriorite(tache.getPriorite());
        tacheExist.setDateouverture(tache.getDateouverture());
       // tacheExist.setTacheparente(tache.getTacheparente());
        tacheExist.setAunetachesuccessive(tache.isAunetachesuccessive());
        //Effacer la date de fin si le statut précédent était 'Terminé'
        tacheExist.setDateTermineTache(null);
        tacheExist.setTacheparente(null);
        tacheExist.setDureretarde(0);
        tacheExist.setPerformance(0);
        //calculer la date d'objectif
        tacheExist.setDateobjectif( calculerDateObjectif(tache));
        tacheExist.setStatut("En attente");

        if (ancienRecepteur!=tache.getRecepteur()) {
            //------------------envoiyer un mail
            // Envoyer des e-mails
            Utilisateur recepteur = tache.getRecepteur();
            Utilisateur emetteur=utilisateurRepo.findByIdutilisateur(tache.getUtilisateur().getIdutilisateur());
            String Subject="Vous avez une nouvelle tâche de : "+emetteur.getNom()+" "+emetteur.getPrenom();
            String msg="nouvelle tache :"+tache.getNomtache();
            sendTaskEmail(recepteur.getMail(),Subject,msg);
        }
        tacheRepo.save(tacheExist);
    }


    public void updateTacheWithStatus(Tache tache,Utilisateur utilisateurconnecte) {
        //Tache tacheExist = tacheRepo.findByIdtache(tache.getIdtache());
        Tache tacheExist = tacheRepo.findTacheByIdtache(tache.getIdtache());
        Utilisateur ancienRecepteur=tacheExist.getRecepteur();

        // enregistré le modificateur de la statut
        tacheExist.setModifierpar(utilisateurconnecte);
        //
        tacheExist.setNomtache(tache.getNomtache());
        tacheExist.setRecepteur(tache.getRecepteur());
        tacheExist.setDureestime(tache.getDureestime());
        tacheExist.setPriorite(tache.getPriorite());
        tacheExist.setProprietaire(tache.getProprietaire());
        tacheExist.setDateouverture(tache.getDateouverture());
       // tacheExist.setTacheparente(tache.getTacheparente());
        tacheExist.setAunetachesuccessive(tache.isAunetachesuccessive());
        tacheExist.setType(tache.getType());
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
            // Envoyer des e-mails
            Utilisateur recepteur = tache.getRecepteur();
            Utilisateur emetteur=utilisateurRepo.findByIdutilisateur(tache.getUtilisateur().getIdutilisateur());
            String Subject="";
            String msg="";
            if (tache.getStatut().equals("À refaire")) {
                 Subject="Tâche à refaire";
                 msg = "La tâche '" + tache.getNomtache() + "' soumise par " + emetteur.getNom() + " " + emetteur.getPrenom() + " nécessite des ajustements. Veuillez la revoir et effectuer les modifications nécessaires. Merci.";
            }
            if (tache.getStatut().equals("Terminée")) {


            } else {
                Subject = "Vous avez une nouvelle tâche de : " + emetteur.getNom() + " " + emetteur.getPrenom();
                msg = "nouvelle tâche :" + tache.getNomtache();
            }
            sendTaskEmail(recepteur.getMail(),Subject,msg);
        }

        tacheRepo.save(tacheExist);
    }


    public void UpdateTacheToTermine(Tache tache,RedirectAttributes redirectAttributes,Utilisateur utilisateurconnecte) {
        Tache tacheExist = tacheRepo.findByIdtache(tache.getIdtache());

        //Si l'utilisateur clique sur un statut "En cours" : modifier le statut à "terminé"
        tacheExist.setStatut("Terminée");
        // enregistré le modificateur de la statut
        tacheExist.setModifierpar(utilisateurconnecte);
        tacheExist.setDateTermineTache(null);
        tacheExist.setPerformance(0);
        //calculer la date d'objectif
        tacheExist.setDateobjectif(calculerDateObjectif(tache));


        tacheRepo.save(tacheExist);
       // redirectAttributes.addFlashAttribute("msg1", "La tâche a été modifiée avec succès.");
    }


    public void UpdateTacheToValide(Tache tache,RedirectAttributes redirectAttributes,Utilisateur utilisateurconnecte) {
        Tache tacheExist = tacheRepo.findByIdtache(tache.getIdtache());

        // enregistré le modificateur de la statut
       tacheExist.setModifierpar(utilisateurconnecte);

       //
        tacheExist.setNomtache(tache.getNomtache());
        tacheExist.setRecepteur(tache.getRecepteur());
        tacheExist.setDureestime(tache.getDureestime());
        tacheExist.setPriorite(tache.getPriorite());
        tacheExist.setProprietaire(tache.getProprietaire());
        tacheExist.setDateouverture(tache.getDateouverture());
        tacheExist.setTacheparente(null);
        tacheExist.setAunetachesuccessive(tache.isAunetachesuccessive());
        tacheExist.setType(tache.getType());


        //-Démarrer les tâches programmées s'il en existe.
        demarrerTachesProgrammees(tache);
        //Calcul de performance
        tacheExist.setPerformance( calculeDePerformance(tache));
        //Calcul de Durée retard
        tacheExist.setDureretarde(calculerDureeRetard(tache));

        // Stocker la date actuelle
        LocalDate currentDate = LocalDate.now();
        tacheExist.setDateTermineTache(Date.valueOf(currentDate));

        //changer le statut
        tacheExist.setStatut("Validée");
        tacheRepo.save(tacheExist);

        //------------------envoiyer un mail
        Utilisateur recepteur = tache.getRecepteur();
        String Subject="Tâche Validée: ";
        String msg="Votre tâche: ' "+tache.getNomtache()+"' a été validée par "+tache.getUtilisateur().getNom()+" "+tache.getUtilisateur().getPrenom();
        sendTaskEmail(recepteur.getMail(),Subject,msg);
        //redirectAttributes.addFlashAttribute("msg1", "La tâche a été modifiée avec succès.");
    }




    public void UpdateTacheToAnnuler(Tache tache,Utilisateur utilisateurconnecte) {
        Tache tacheExist = tacheRepo.findByIdtache(tache.getIdtache());
        // enregistré le modificateur de la statut
        tacheExist.setNomtache(tache.getNomtache());
        tacheExist.setRecepteur(tache.getRecepteur());
        tacheExist.setDureestime(tache.getDureestime());
        tacheExist.setPriorite(tache.getPriorite());
        tacheExist.setProprietaire(tache.getProprietaire());
        tacheExist.setDateouverture(tache.getDateouverture());
        tacheExist.setAunetachesuccessive(tache.isAunetachesuccessive());
        tacheExist.setType(tache.getType());

        tacheExist.setDateTermineTache(null);
        tacheExist.setAunetachesuccessive(false);
        tacheExist.setTacheparente(null);
        tacheExist.setPerformance(0);
        tacheExist.setDureretarde(0);




        //envoiyer un mail
        Utilisateur recepteur1 =tache.getRecepteur();
        Utilisateur emetteur=utilisateurRepo.findByIdutilisateur(tache.getUtilisateur().getIdutilisateur());
        String Subject="Tâche annulée";
        String msg = "La tâche '" + tache.getNomtache() + "' soumise par " + emetteur.getNom() + " " + emetteur.getPrenom() + " a été annulée.";
        sendTaskEmail(recepteur1.getMail(),Subject,msg);

        //changer le statut
        tacheExist.setStatut("Annulée");
        tacheRepo.save(tacheExist);
        //redirectAttributes.addFlashAttribute("msg1", "La tâche a été modifiée avec succès.");
    }

    public int calculerDureeRetard(Tache tache) {

        Tache ExisteTache=tacheRepo.findTacheByIdtache(tache.getIdtache());
        Date dateTermineTache = ExisteTache.getDateTermineTache();
        LocalDate dateTermineTache1 = LocalDate.now();
        ///
        Date DateObjectif=ExisteTache.getDateobjectif();
        LocalDate Dateobjectif1 = DateObjectif.toLocalDate();
        long Dureeretard = ChronoUnit.DAYS.between(Dateobjectif1, dateTermineTache1);
        int DureeretardEnEntier = Math.toIntExact(Dureeretard);

        return DureeretardEnEntier;

    }


    public void demarrerTachesProgrammees(Tache tache) {
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
                    Utilisateur recepteur1 = t.getRecepteur();
                    Utilisateur emetteur=utilisateurRepo.findByIdutilisateur(t.getUtilisateur().getIdutilisateur());
                    String Subject1="Vous avez une nouvelle tâche de :"+emetteur.getNom()+" "+emetteur.getPrenom();
                    String  msg1 ="Nouvelle tâche : "+ t.getNomtache();
                    sendTaskEmail(recepteur1.getMail(),Subject1,msg1);
                }
        }
        //------------------------------------------------------
    }



    public int calculeDePerformance(Tache tache){

        Date dateOuverture = tache.getDateouverture();
        LocalDate dateOuverture1 = dateOuverture.toLocalDate();

        //Date dateTermineTache = tache.getDateTermineTache();
        LocalDate dateTermineTache1 = LocalDate.now();;
        int performanceEnEntier;

        // Calcul de la durée consommée en jours
        long dureeConsommee = ChronoUnit.DAYS.between(dateOuverture1,dateTermineTache1)+1;

        //cas où l'utilisateur réalise la tâche avant la date d'ouverture
        if (dureeConsommee < 0) {
            long dureeConsommee2 = ChronoUnit.DAYS.between(dateTermineTache1,dateTermineTache1)+1;
            // Éviter une division par zéro et calculer la performance
            long dureeEstime = tache.getDureestime()+1;
            double performanceDouble = (dureeConsommee != 0) ? ((double) dureeEstime / dureeConsommee2) * 100 : 0;
            // Convertir la performance en entier
            performanceEnEntier = (int) performanceDouble;
        }
        //cas normale
        else {
            // Éviter une division par zéro et calculer la performance
            long dureeEstime = tache.getDureestime()+1;
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
        LocalDate dateObjectif = dateOuverture1.plusDays(dureeEstime);

        // Convertissez et retournez la date d'objectif en java.sql.Date et mettez à jour la tâche

        return Date.valueOf(dateObjectif);
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
                ajouterTachesUniques(tacheList,tacheRepo.findTacheByRecepteurAndIsmemoire(utilisateur,false));

            }else {
                // Obtenez la liste des taches du utilisateurs
                List<Tache> tachesutilisateurList = tacheRepo.findTacheByRecepteurAndIsmemoire(utilisateur,false);
                // Ajoutez la liste des taches de l'utilisateur à la liste globale
                tacheList.addAll(tachesutilisateurList);
            }

        }

        // Retournez la liste des utilisateurs
        return tacheList;
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

        // Retournez la liste des utilisateurs
        return tacheList;
    }

    //pour obtenir tous les tache de l equipe de l utilisateur connecté et les tache envoye par l utilisateur connecté
    @Override
    public List<Tache> findAllEquipeTaches(List<Utilisateur> recepteurs, Utilisateur utilisateurConnecté) {

        List<Tache> tacheList = new ArrayList<>();

        // Parcourez la liste des utilisateurs
        for (Utilisateur u : recepteurs) {
            if (u==utilisateurConnecté) {
                List<Tache> taches = tacheRepo.findTacheByUtilisateurAndIsmemoire(u,false);
                // Ajoutez la liste des taches de l'utilisateur à la liste globale
                tacheList.addAll(taches);

            }else {
                // Obtenez la liste des taches du utilisateurs
                List<Tache> tachesutilisateurList = tacheRepo.findTacheByRecepteurAndIsmemoire(u,false);
                // Ajoutez la liste des taches de l'utilisateur à la liste globale
                tacheList.addAll(tachesutilisateurList);
            }
        }

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

    /*
    public void UpdateTacheToEnCours(Tache tache,RedirectAttributes redirectAttributes,Utilisateur utilisateurconnecte) {
        //Tache tacheExist = tacheRepo.findByIdtache(tache.getIdtache());
        Tache tacheExist = tacheRepo.findTacheByIdtache(tache.getIdtache());
        Utilisateur ancienRecepteur=tacheExist.getRecepteur();

        // enregistré le modificateur de la statut
        tacheExist.setModifierpar(utilisateurconnecte);
        //
        tacheExist.setNomtache(tache.getNomtache());
        tacheExist.setRecepteur(tache.getRecepteur());
        tacheExist.setDureestime(tache.getDureestime());
        tacheExist.setPriorite(tache.getPriorite());
        tacheExist.setDateouverture(tache.getDateouverture());
        tacheExist.setTacheparente(tache.getTacheparente());
        tacheExist.setAunetachesuccessive(tache.isAunetachesuccessive());
        //Effacer la date de fin si le statut précédent était 'Terminé'
        tacheExist.setDateTermineTache(null);
        //tacheExist.setTacheparente(null);
        tacheExist.setDureretarde(0);
        tacheExist.setPerformance(0);
        //calculer la date d'objectif
        tacheExist.setDateobjectif( calculerDateObjectif(tache));
        //changer le statut
        tacheExist.setStatut("En cours");

        if (ancienRecepteur!=tache.getRecepteur()) {
            //------------------envoiyer un mail
            // Envoyer des e-mails
            Utilisateur recepteur = tache.getRecepteur();
            Utilisateur emetteur=utilisateurRepo.findByIdutilisateur(tache.getUtilisateur().getIdutilisateur());
            String Subject="Vous avez une nouvelle tâche de : "+emetteur.getNom()+" "+emetteur.getPrenom();
            String msg="nouvelle tache :"+tache.getNomtache();
            sendTaskEmail(recepteur.getMail(),Subject,msg);
        }
        tacheRepo.save(tacheExist);
    }

    public void UpdateTacheToRefair(Tache tache,RedirectAttributes redirectAttributes,Utilisateur utilisateurconnecte) {
        Tache tacheExist = tacheRepo.findByIdtache(tache.getIdtache());

        // enregistré le modificateur de la statut
        tacheExist.setModifierpar(utilisateurconnecte);

        //calculer la date d'objectif
        calculerDateObjectif(tache);
        tacheExist.setDateTermineTache(null);
        tacheExist.setPerformance(0);
        tacheExist.setDateobjectif(tache.getDateobjectif());
        //envoiyer un mail
        Utilisateur recepteur1 =tache.getRecepteur();
        Utilisateur emetteur=utilisateurRepo.findByIdutilisateur(tache.getUtilisateur().getIdutilisateur());
        String Subject="Tâche à refaire";
        String msg = "La tâche '" + tache.getNomtache() + "' soumise par " + emetteur.getNom() + " " + emetteur.getPrenom() + " nécessite des ajustements. Veuillez la revoir et effectuer les modifications nécessaires. Merci.";
        sendTaskEmail(recepteur1.getMail(),Subject,msg);

        //changer le statut
        tacheExist.setStatut("refaire");
        tacheRepo.save(tacheExist);
        // redirectAttributes.addFlashAttribute("msg1", "La tâche a été modifiée avec succès.");
    }
 */

}