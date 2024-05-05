package com.example.tachesapp.Service;

import com.example.tachesapp.Model.Tache;
import com.example.tachesapp.Model.Utilisateur;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.Authentication;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


import java.util.Date;
import java.util.List;

public interface TacheService {
    // pour envoyer un mail
    @Async
    public void sendTaskEmail(String recipientEmail,String setSubject,String msg) ;

    public List<Tache> findTacheParent(Utilisateur utilisateur, Boolean x);

    public List<Utilisateur> findRecepteurs(Utilisateur utilisateur);

    public List<Utilisateur> findEmetteurs(Utilisateur utilisateur);

   // public void UpdateTacheToRefair(Tache tache,RedirectAttributes redirectAttributes,Utilisateur utilisateurconnecte);

    //public void UpdateTacheToEnCours(Tache tache,RedirectAttributes redirectAttributes,Utilisateur utilisateurconnecte) ;

    public void UpdateTacheToAnnuler(Tache tache,Utilisateur utilisateurconnecte) ;

    public void UpdateTacheToProgramme(Tache tache, RedirectAttributes redirectAttributes, Utilisateur utilisateurconnecte);

    public void UpdateTacheToEnAttente(Tache tache,RedirectAttributes redirectAttributes,Utilisateur utilisateurconnecte) ;

    public void UpdateTacheToTermine(Tache tache,RedirectAttributes redirectAttributes,Utilisateur utilisateurconnecte);

    public void UpdateTacheToValide(Tache tache,RedirectAttributes redirectAttributes,Utilisateur utilisateurconnecte) ;

    public int calculerDureeRetard(Tache tache) ;

    public void demarrerTachesProgrammees(Tache tache);

    public void AnnulerTachesProgrammees(Tache tache);

    public int calculeDePerformance(Tache tache) ;

    public Date calculerDateObjectif(Tache tache) ;

    public List<Tache> findTachesByIdUtilisateurs(List<Long> utilisateursIds,Utilisateur utilisateur) ;
    public List<Tache> findTachesByIdUtilisateurs2(List<Long> utilisateursIds,Utilisateur utilisateur) ;

    List<Tache> findTachesByIdEmetteur(List<Long> idemetteur,Utilisateur recepteur);

    List<Tache> findAllEquipeTaches(List<Utilisateur> recepteurs, Utilisateur utilisateur);

    public boolean aDesTachesSecondaires(Tache tache);

    public List<Tache> lesTacheSecondairesParTacheParent(Tache tache,Utilisateur utilisateur);

    void updateTacheWithStatus(Tache tache, Utilisateur utilisateurconnecte);
}
