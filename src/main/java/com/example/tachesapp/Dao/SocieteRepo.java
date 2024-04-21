package com.example.tachesapp.Dao;

import com.example.tachesapp.Model.Departement;
import com.example.tachesapp.Model.Societe;
import com.example.tachesapp.Model.Utilisateur;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SocieteRepo extends JpaRepository<Societe,Long> {
    public Societe   save(Societe societe);
    public List<Societe> findAll();

    public List<Societe> findAllByOrderByNomsociete();
    public void deleteByIdsociete(Long ids);
    public Societe findByIdsociete(Long ids) ;



    public Societe findAllByUtilisateurs(Utilisateur utilisateur);



    public  boolean existsByNomsociete(String nom);


}
