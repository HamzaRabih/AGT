package com.example.tachesapp.Service;

import com.example.tachesapp.Dao.DepartementRepo;
import com.example.tachesapp.Model.Departement;
import com.example.tachesapp.Model.Societe;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.util.List;
import java.util.Optional;

@Service
public class DepartementServiceImpl implements DeparetementService{
    @Autowired
    private DepartementRepo departementRepo;

    @Override
    public List<Departement> findAllDepart() {
        return departementRepo.findAllByOrderBySociete();
    }

    @Override
    public List<Departement> getDepartementsBySociete(Long ids) {
        return departementRepo.findBySocieteIdsociete(ids);
    }

    @Override
    public Departement findDepById(Long idd) {
        return departementRepo.findByIddepartement(idd);
    }





    /*
    @Override
    public Departement updateDepartement(Departement departement) {
        // Check if the Departement with the provided ID exists
        Optional<Departement> existingDepartementOptional = departementRepo.findById(departement.getIddepartement());

        if (existingDepartementOptional.isPresent()) {
            // If the Domaine exists, update its properties
            Departement existingDepartement= existingDepartementOptional.get();

            // Update the properties you want to change
            existingDepartement.setSociete(departement.getSociete());
            existingDepartement.setNomdepartement(departement.getNomdepartement());

            // Save the updated Domaine
            return departementRepo.save(existingDepartement);
        } else {
            // Handle the case where the Domaine with the provided ID doesn't exist
            // You can throw an exception, return null, or handle it as needed.
            return null;
        }
    }
     */


}
