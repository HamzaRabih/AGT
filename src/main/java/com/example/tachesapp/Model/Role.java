package com.example.tachesapp.Model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity


public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idrole;
    private String role;



    @OneToMany
    @JoinColumn(name = "idrole")
    @JsonIgnore // Exclure la sérialisation de la relation Societe
    private List<Utilisateur> utilisateurs;


    public Role(Long idrole, String role) {
        this.idrole = idrole;
        this.role = role;
    }
}
