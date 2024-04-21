package com.example.tachesapp.Dao;

import com.example.tachesapp.Model.Equipe;
import com.example.tachesapp.Model.Societe;
import com.example.tachesapp.Model.Utilisateur;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EquipeRepo extends JpaRepository<Equipe,Long> {


    public List<Equipe> findAll();


    public Equipe findEquipeByIdequipe(Long ide);

   public void deleteEquipeByIdequipe(Long id);
    public void deleteByIdequipe(Long id);

    public boolean existsByResponsable(Utilisateur utilisateur);

    public Equipe findEquipeByResponsable(Utilisateur utilisateur);



    public  boolean existsByNomequipeAndResponsableSociete(String dep,Societe societe);




}
