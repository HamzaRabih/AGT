package com.example.tachesapp.Service;

import com.example.tachesapp.Model.Departement;
import com.example.tachesapp.Model.Societe;

import java.util.List;

public interface DeparetementService {
    public List<Departement> findAllDepart();


    public List<Departement> getDepartementsBySociete(Long ids);

    public Departement findDepById(Long idd);

    //public Departement updateDepartement(Departement departement);


}
