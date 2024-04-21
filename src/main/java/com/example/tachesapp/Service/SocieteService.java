package com.example.tachesapp.Service;

import com.example.tachesapp.Model.Domaine;
import com.example.tachesapp.Model.Societe;

import java.util.List;

public interface SocieteService {

    public List<Societe> findAllSociete();

    public Societe findeByIds(Long ids);



}
