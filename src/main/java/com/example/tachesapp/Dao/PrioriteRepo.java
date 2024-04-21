package com.example.tachesapp.Dao;

import com.example.tachesapp.Model.Priorite;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PrioriteRepo extends JpaRepository<Priorite,Long> {

    public List<Priorite> findAll();
}
