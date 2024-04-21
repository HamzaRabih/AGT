package com.example.tachesapp.Service;

import com.example.tachesapp.Model.Notification;
import com.example.tachesapp.Model.Tache;


public interface NotificationsService {

    Notification creerNotificationDeCreationTache(Long idrecepteur, Tache tache);
}
