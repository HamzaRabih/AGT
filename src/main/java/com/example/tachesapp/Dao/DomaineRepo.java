package com.example.tachesapp.Dao;

import com.example.tachesapp.Model.Departement;
import com.example.tachesapp.Model.Domaine;
import com.example.tachesapp.Model.Societe;
import com.example.tachesapp.Model.Utilisateur;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DomaineRepo extends JpaRepository<Domaine,Long> {

    public Domaine save(Domaine domaine);

    public List<Domaine> findAll();

    public List<Domaine> findAllByOrderByNomdomaine();
    public void deleteByIddomaine(Long id);
    public Domaine findByIddomaine(Long id) ;


    public  boolean existsByNomdomaine(String nom);



}
