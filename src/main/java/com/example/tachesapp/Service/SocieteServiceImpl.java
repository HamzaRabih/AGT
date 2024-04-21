package com.example.tachesapp.Service;

import com.example.tachesapp.Dao.SocieteRepo;
import com.example.tachesapp.Dao.TacheRepo;
import com.example.tachesapp.Model.Domaine;
import com.example.tachesapp.Model.Societe;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SocieteServiceImpl implements SocieteService{
    @Autowired
    private SocieteRepo societeRepo;

    @Override
    public List<Societe> findAllSociete() {
        return societeRepo.findAllByOrderByNomsociete();
    }

    @Override
    public Societe findeByIds(Long ids) {
        return societeRepo.findByIdsociete(ids);
    }


}
