package com.example.tachesapp.Service;

import com.example.tachesapp.Dao.DomaineRepo;
import com.example.tachesapp.Model.Domaine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DomaineServiceImp implements DomaineService{

    @Autowired
    private DomaineRepo domaineRepo;


    @Override
    public List<Domaine> findAllDomaine() {
        return domaineRepo.findAllByOrderByNomdomaine();
    }

    @Override
    public void suppDomaineById(Long id) {
    domaineRepo.deleteByIddomaine(id);
    }


    @Override
    public Domaine findeByIdDomaine(Long id) {
        return domaineRepo.findByIddomaine(id);
    }

   /*
   public Domaine updateDomaine(Domaine domaine) {
        // Check if the Domaine with the provided ID exists
        Optional<Domaine> existingDomaineOptional = domaineRepo.findById(domaine.getIddomaine());

        if (existingDomaineOptional.isPresent()) {
            // If the Domaine exists, update its properties
            Domaine existingDomaine = existingDomaineOptional.get();

            // Update the properties you want to change
            existingDomaine.setNomdomaine(domaine.getNomdomaine());
            existingDomaine.setDescription(domaine.getDescription());

            // Save the updated Domaine
            return domaineRepo.save(existingDomaine);
        } else {
            // Handle the case where the Domaine with the provided ID doesn't exist
            // You can throw an exception, return null, or handle it as needed.
            return null;
        }
    }
    */
}
