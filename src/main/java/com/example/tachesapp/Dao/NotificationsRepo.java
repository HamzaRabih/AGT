package com.example.tachesapp.Dao;

import com.example.tachesapp.Model.Notification;
import com.example.tachesapp.Model.Utilisateur;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface NotificationsRepo extends JpaRepository<Notification,Long> {

        List<Notification> findByRecepteurOrderByDatenotifDesc(Utilisateur recepteur);

    List<Notification> findByRecepteurAndEstLu(Utilisateur recepteur, Boolean estLu);


}
