package com.example.tachesapp.Service;

import com.example.tachesapp.Dao.TacheRepo;
import com.example.tachesapp.Model.Tache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.sql.Date;
import java.time.LocalDate;

@Service
public class MemoireServiceImpl implements MemoireService{

    @Autowired
    TacheRepo tacheRepo;

    @Override
    public void createMemoire(Tache tache, RedirectAttributes redirectAttributes) {
        // Créer une nouvelle tâche
        // Obtenez la date d'ouverture de la tâche
        Date dateouverture = tache.getDateouverture();
        LocalDate dateOuverture1 = dateouverture.toLocalDate();
        // Obtenez la durée estimée de la tâche
        int dureeEstime = tache.getDureestime();
        // Calculez la date d'objectif en ajoutant la durée estimée à la date d'ouverture
        LocalDate dateObjectif = dateOuverture1.plusDays(dureeEstime-1);
        // Convertissez la date d'objectif en java.sql.Date et mettez à jour la tâche
        tache.setDateobjectif(Date.valueOf(dateObjectif));
        tache.setIsmemoire(true);
        tache.setProprietaire(tache.getProprietaire());
        tache.setRecepteur(null);
        tache.setUtilisateur(tache.getUtilisateur());
        // Enregistrer la tâche memoire
        tacheRepo.save(tache);
        redirectAttributes.addFlashAttribute("msg", "Tâche créée avec succès");
    }

    @Override
    public void updateMemoire(Tache tache, RedirectAttributes redirectAttributes) {
        // Créer une nouvelle tâche
        // Obtenez la date d'ouverture de la tâche
        Date dateouverture = tache.getDateouverture();
        LocalDate dateOuverture1 = dateouverture.toLocalDate();
        // Obtenez la durée estimée de la tâche
        int dureeEstime = tache.getDureestime();
        // Calculez la date d'objectif en ajoutant la durée estimée à la date d'ouverture
        LocalDate dateObjectif = dateOuverture1.plusDays(dureeEstime-1);
        // Convertissez la date d'objectif en java.sql.Date et mettez à jour la tâche
        tache.setDateobjectif(Date.valueOf(dateObjectif));
        tache.setIsmemoire(true);
        // Enregistrer la tâche memoire
        tacheRepo.save(tache);
        // Mettre à jour la tâche existante
        redirectAttributes.addFlashAttribute("msg1", "La tâche a été modifiée avec succès.");
    }


}
