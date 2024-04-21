package com.example.tachesapp.Service;

import com.example.tachesapp.Model.Equipe;
import com.example.tachesapp.Model.Utilisateur;

import java.util.List;

public interface EquipeService {

    public Equipe saveEquie(Equipe e);

    public List<Equipe> findAllEquipes();
    public void suppEquipe(Long id);

    public Equipe findEquipByID(Long ide);

    void deleteEquipe(Long id);

    public List<Long> findAllIdsReso();

}
