package com.example.tachesapp.Service;

import com.example.tachesapp.Dao.RoleRepo;
import com.example.tachesapp.Model.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoleServiceImpl implements RoleService{
@Autowired
    private RoleRepo roleRepo;

    @Override
    public List<Role> findAllRole() {
        return roleRepo.findAll();
    }
}
