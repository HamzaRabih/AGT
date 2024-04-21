package com.example.tachesapp.Model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
//réinitialisation du mot de passe
public class ReinitDuMotDePasse {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String token;
    private LocalDateTime expiryDate;


    @OneToOne(targetEntity = Utilisateur.class, fetch = FetchType.EAGER,cascade =CascadeType.ALL)
    @JoinColumn(nullable = false,name = "idutilisateur")
    private Utilisateur utilisateur ;


}
