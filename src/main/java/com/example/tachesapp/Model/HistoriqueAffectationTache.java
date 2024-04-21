package com.example.tachesapp.Model;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class HistoriqueAffectationTache {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idaffectationtache;
    private Long idrecptuertache;
    private String type;
    private String statut;
    private String commentaire;
    private String alert;
    private Date dateouverture;
    private Date dateobjectif;
    private int dureestime;
    private int dureretarde;
    private int dureyth;
    private float performance;

    @ManyToOne
    @JoinColumn(name = "idtache")
    private Tache tache;
}
