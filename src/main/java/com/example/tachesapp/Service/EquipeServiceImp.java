package com.example.tachesapp.Service;

import com.example.tachesapp.Dao.EquipeRepo;
import com.example.tachesapp.Dao.HistoriqueAffRepo;
import com.example.tachesapp.Model.Equipe;
import com.example.tachesapp.Model.Utilisateur;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class EquipeServiceImp implements EquipeService {
    @Autowired
    private EquipeRepo equipeRepo;

    @Override
    public Equipe saveEquie(Equipe e) {
        return equipeRepo.save(e);
    }


    @Override
    public List<Equipe> findAllEquipes() {
        return equipeRepo.findAll();
    }

    @Override
    public void suppEquipe(Long id) {
        equipeRepo.deleteByIdequipe(id);
    }

    @Override
    public Equipe findEquipByID(Long ide) {
        return equipeRepo.findEquipeByIdequipe(ide);
    }

    @Override
    public void deleteEquipe(Long id) {
        equipeRepo.deleteEquipeByIdequipe(id);
    }

    @Override
    public List<Long> findAllIdsReso() {
        List<Long> respoIds = new ArrayList<>();

        List<Equipe> equipeList = equipeRepo.findAll();

        for (Equipe equipe : equipeList) {
            if (equipe.getResponsable() != null) {
                respoIds.add(equipe.getResponsable().getIdutilisateur());
            }
        }
        return respoIds;
    }






}
