package com.example.tachesapp.Dao;

import com.example.tachesapp.Model.Departement;
import com.example.tachesapp.Model.Societe;
import com.example.tachesapp.Model.Utilisateur;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DepartementRepo extends JpaRepository<Departement,Long> {
    @Override
    public List<Departement> findAll();

    public List<Departement> findAllByOrderBySociete();
    public void deleteByIddepartement(Long ids);

    public List<Departement> findBySocieteIdsociete(Long ids);
    public Departement findByIddepartement(Long idd);

    public Boolean existsBySociete(Societe societe);

    public Long countBySociete(Societe societe);


    public  boolean existsByNomdepartementAndSociete(String dep,Societe societe);

    Boolean existsByNomdepartementAndSocieteAndIddepartementNot(String nomdepartement, Societe societe,Long iddepartement);
}
