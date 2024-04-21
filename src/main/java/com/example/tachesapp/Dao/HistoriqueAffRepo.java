package com.example.tachesapp.Dao;

import com.example.tachesapp.Model.HistoriqueAffectationTache;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HistoriqueAffRepo extends JpaRepository<HistoriqueAffectationTache,Long> {
}
