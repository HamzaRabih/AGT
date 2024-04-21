package com.example.tachesapp.Model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idnotification;
    private String detail;
    private LocalDateTime datenotif;
    private Boolean estLu;

    @ManyToOne
    @JoinColumn(name = "idemetteur")
    private Utilisateur emetteur;

    @ManyToOne
    @JoinColumn(name = "idrecepteur")
    private Utilisateur recepteur;

}
