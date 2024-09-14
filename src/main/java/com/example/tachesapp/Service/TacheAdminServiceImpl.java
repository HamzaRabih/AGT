package com.example.tachesapp.Service;

import com.example.tachesapp.Dao.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TacheAdminServiceImpl implements TacheAdminService{
    @Autowired
    TacheService tacheService;
    @Autowired
    TacheRepo tacheRepo;
    @Autowired
    SocieteRepo societeRepo;
    @Autowired
    DepartementRepo departementRepo;
    @Autowired
    UtilisateurService utilisateurService;
    @Autowired
    UtilisateurRepo utilisateurRepo;
    @Autowired
    NotificationsRepo notificationsRepo;
    @Autowired
    PrioriteRepo prioriteRepo;
}
