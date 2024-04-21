package com.example.tachesapp.Service;

import com.example.tachesapp.Model.Domaine;
import com.example.tachesapp.Model.Societe;

import java.util.List;

public interface DomaineService {
    public List<Domaine> findAllDomaine();

    public void suppDomaineById(Long id);

    public Domaine findeByIdDomaine(Long id);
   // public Domaine updateDomaine(Domaine domaine);
}
