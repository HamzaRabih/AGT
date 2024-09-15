package com.example.tachesapp.Service;

import com.example.tachesapp.Dao.PrioriteRepo;
import com.example.tachesapp.Model.Priorite;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import java.util.List;

@Service
public class PrioriteServiceImpl implements PrioriteService{

    @Autowired
    PrioriteRepo prioriteRepo;

    public void loadPriorites(Model model){
        //Les Priorités
        List<Priorite> priorites=prioriteRepo.findAll();
        model.addAttribute("priorites",priorites);
    }
}
