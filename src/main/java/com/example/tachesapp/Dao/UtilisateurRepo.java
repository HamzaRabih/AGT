package com.example.tachesapp.Dao;

import com.example.tachesapp.Model.Departement;
import com.example.tachesapp.Model.Equipe;
import com.example.tachesapp.Model.Societe;
import com.example.tachesapp.Model.Utilisateur;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UtilisateurRepo extends JpaRepository<Utilisateur,Long> {

    public List<Utilisateur> findAllByOrderBySociete();

    public List<Utilisateur> findBySocieteIdsociete(Long ids);

    public List<Utilisateur> findByIdutilisateurIn(List<Long> ids);

    public  Utilisateur findByIdutilisateur(Long id);

    public  List<Utilisateur> findByDepartementIddepartement(Long id);


   public List<Utilisateur> findUtilisateursByDepartement(Departement departement);

   List<Utilisateur> findUtilisateursByDepartementIn(List<Departement> departements);

   Utilisateur findUtilisateursByMail(String mail);

    public List<Utilisateur> findUtilisateursBySociete(Societe societe);


    public  boolean existsByMail(String mail);

    public Boolean existsBySociete(Societe societe);

    public Boolean existsByDepartement(Departement departement);

    public Long countByDepartement(Departement departement);

    public Long countBySociete(Societe societe);

    public boolean existsByMailAndIdutilisateurNot(String mail,Long id);


}
