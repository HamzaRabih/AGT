package com.example.tachesapp.Dao;

import com.example.tachesapp.Model.Tache;
import com.example.tachesapp.Model.Utilisateur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface TacheRepo extends JpaRepository<Tache,Long> {

    public List<Tache> findTacheByRecepteurAndIsmemoire(Utilisateur utilisateursList, boolean ismemoire);
    public List<Tache> findTacheByRecepteurAndUtilisateurAndIsmemoire(Utilisateur recepteur,Utilisateur utilisateur, boolean ismemoire);
    public  List<Tache> findTacheByUtilisateurAndIsmemoireAndRecepteur(Utilisateur utilisateur,boolean ismemoire,Utilisateur recepteur);
    public List<Tache> findTacheByUtilisateurAndIsmemoire(Utilisateur utilisateur, boolean ismemoire);
    public List<Tache> findAllByUtilisateurAndIsmemoireAndStatutNotIn(Utilisateur utilisateur, boolean ismemoire, List<String> excludedStatuts);
    public List<Tache> findAllByRecepteurInAndIsmemoire(List<Utilisateur> utilisateursList, boolean ismemoire);
    List<Tache> findAllByRecepteurInAndIsmemoireAndStatutNotIn(List<Utilisateur> utilisateursList, boolean ismemoire, List<String> excludedStatuts);
    public List<Tache> findAllByRecepteur(Utilisateur utilisateur);
    public List<Tache> findAll();
    public List<Tache> findAllByUtilisateurAndAunetachesuccessiveAndStatut(Utilisateur utilisateur,Boolean x,String statut);
    //@Query("SELECT t FROM Tache t WHERE (t.utilisateur=?1 AND t.ismemoire = ?2 AND t.statut=?3)  OR  (t.utilisateur=?4 AND t.ismemoire = ?5 AND t.statut=?6) ")
    //@Query("SELECT t FROM Tache t WHERE t.utilisateur = ?1 AND t.ismemoire = ?2 AND t.statut =?3 AND t.aunetachesuccessive = ?5 OR t.recepteur = ?4 AND t.ismemoire =?2 AND t.statut =?3 AND t.aunetachesuccessive = ?5")
    public List<Tache> findDistinctByUtilisateurAndAunetachesuccessiveAndStatut(Utilisateur utilisateur,Boolean x,String statut);
    public List<Tache> findDistinctByRecepteurAndAunetachesuccessiveAndStatut(Utilisateur utilisateur,Boolean x,String statut);
    /*@Query("SELECT t FROM Tache t WHERE t.utilisateur=?1 AND t.aunetachesuccessive = ?2 AND t.statut IN (?3,?4,?5,?6)")
    public List<Tache> findATacheParents(Utilisateur utilisateur,Boolean x,String statut,String statut2,String statut3,String statut4);*/
    public List<Tache> findAllByTacheparente(Tache tache);
    public List<Tache> findAllByUtilisateurAndIsmemoire(Utilisateur utilisateur,boolean ismemo);
    public Tache findByIdtache(Long idt);
    public Tache findTacheByIdtache(Long idt);
    public List<Tache> findAllByIsmemoire(boolean ismemoire);
    public boolean existsByRecepteur(Utilisateur utilisateur);
    public boolean existsByUtilisateur(Utilisateur utilisateur);
    public boolean existsByProprietaire(Utilisateur utilisateur);
    public void deleteByIdtache(Long idt);
}
