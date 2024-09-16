package com.example.tachesapp.Service;

import com.example.tachesapp.Model.Tache;
import com.example.tachesapp.Model.Utilisateur;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


import java.util.Date;
import java.util.List;

public interface TacheService {

    public List<Tache> findTacheParent(Utilisateur utilisateur, Boolean x);

    public List<Utilisateur> findRecepteurs(Utilisateur utilisateur);

    public List<Utilisateur> findEmetteurs(Utilisateur utilisateur);

    public void UpdateTacheToAnnuler(Tache tache,Utilisateur utilisateurconnecte) ;

    public void UpdateTacheToProgramme(Tache tache, RedirectAttributes redirectAttributes, Utilisateur utilisateurconnecte);

    public void UpdateTacheToValide(Tache tache,Utilisateur utilisateurconnecte) ;

    public int calculerDureeRetard(Tache tache) ;

    public void demarrerTachesProgrammees(Tache tache,Utilisateur utilisateur);

    public void AnnulerTachesProgrammees(Tache tache,Utilisateur utilisateur);

    public int calculeDePerformance(Tache tache) ;

    public Date calculerDateObjectif(Tache tache) ;

    public List<Tache> findTachesByIdUtilisateurs(List<Long> utilisateursIds,Utilisateur utilisateur) ;
    public List<Tache> findTachesByIdUtilisateurs2(List<Long> utilisateursIds,Utilisateur utilisateur) ;

    List<Tache> findTachesByIdEmetteur(List<Long> idemetteur,Utilisateur recepteur);

    public boolean aDesTachesSecondaires(Tache tache);

    public List<Tache> lesTacheSecondairesParTacheParent(Tache tache,Utilisateur utilisateur);

    void updateTacheWithStatus(Tache tache, Utilisateur utilisateurconnecte);
    
    public ResponseEntity<List<Tache>> getAllMyTask(Authentication authentication, Model model);

    void CreateTache(Tache tache, RedirectAttributes redirectAttributes, Authentication authentication);

    Tache updateTacheStatut(Long tacheId, Authentication authentication);

    Tache ValiderStatut(Long id, Authentication authentication);

    Tache RefaireTache(Long id, Authentication authentication);

    Tache AnnulerTache(Long id, Authentication authentication);

    Tache IndexerTache(Long id, Authentication authentication);

    List<Tache> tachesParentes(Authentication authentication);

    List<Utilisateur> membresDeLequipeDutilisateur(Authentication authentication);

    public void addSuccessMessage(RedirectAttributes redirectAttributes) ;

    // public void UpdateTacheToRefair(Tache tache,RedirectAttributes redirectAttributes,Utilisateur utilisateurconnecte);
    //public void UpdateTacheToEnCours(Tache tache,RedirectAttributes redirectAttributes,Utilisateur utilisateurconnecte) ;
    //List<Tache> findAllEquipeTaches(List<Utilisateur> recepteurs, Utilisateur utilisateur);
    //public void UpdateTacheToEnAttente(Tache tache,RedirectAttributes redirectAttributes,Utilisateur utilisateurconnecte) ;
    //public void UpdateTacheToTermine(Tache tache,RedirectAttributes redirectAttributes,Utilisateur utilisateurconnecte);

}
