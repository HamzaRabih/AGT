package com.example.tachesapp.Dao;

import com.example.tachesapp.Model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RoleRepo extends JpaRepository<Role,Long> {
    public List<Role> findAll();
}
