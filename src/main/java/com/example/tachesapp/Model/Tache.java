package com.example.tachesapp.Model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.sql.Date;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Tache {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idtache;
    private String nomtache;
    private String type;
    private String statut;
    private String commentaire;
    private String alert;
    private Date dateouverture;
    private Date dateobjectif;
    private Date dateTermineTache;
    private int dureestime;
    private int dureretarde;
    private int dureyth;
    private float performance;
    private boolean aunetachesuccessive;
    private boolean ismemoire;

    @ManyToOne
    @JoinColumn(name = "idrecepteur")
    private Utilisateur recepteur;


    // Cette annotation mappe le champ 'datecreation' à une colonne de la base de données nommée "datecreation".
    // L'option 'updatable = false' garantit que cette colonne ne sera pas mise à jour manuellement après l'insertion initiale.
    @Column(name = "datecreation", updatable = false)
    private LocalDateTime datecreation;

    // Cette annotation mappe le champ 'datemodif' à une colonne de la base de données nommée "datemodif".
    private LocalDateTime datemodif;

    @ManyToOne
    @JoinColumn(name = "idproprietaire")
    private Utilisateur proprietaire;

    @ManyToOne
    @JoinColumn(name = "idutilisateur")
    private Utilisateur utilisateur;

    @OneToMany
    @JoinColumn(name = "idtache")
    private List<HistoriqueAffectationTache> historiqueAffectationTaches;

    //tâche précédente
    @OneToOne(optional = true)
    @JoinColumn(name = "idtacheparente")
    private Tache tacheparente;

    @ManyToOne
    @JoinColumn(name = "idpriorite")
    private Priorite priorite;

       // Cette annotation établit une relation Many-to-One avec le champ 'modifierpar',
       // indiquant que cette entité (Utilisateur) est le côté 'many', et 'modifierpar' est le côté propriétaire.
       // Elle utilise la colonne 'modifierpar' pour maintenir la relation dans la base de données.
       @ManyToOne
       @JsonIgnore
       @JoinColumn(name = "modifierpar")
       private Utilisateur modifierpar;



   // Getters et setters pour les champs 'datecreation', 'datemodif', 'modifierpar', et 'creerpar'.

       // Cette méthode est annotée avec '@PrePersist', ce qui signifie qu'elle sera exécutée automatiquement
       // avant qu'une nouvelle entité ne soit persistée (c'est-à-dire avant son insertion dans la base de données).
       // Elle initialise le champ 'datecreation' avec la date et l'heure actuelles.
      @PrePersist
       protected void onCreate() {datecreation = LocalDateTime.now();}

       // Cette méthode est annotée avec '@PreUpdate', ce qui signifie qu'elle sera exécutée automatiquement
       // avant qu'une entité existante ne soit mise à jour dans la base de données.
       // Elle met à jour le champ 'datemodif' avec la date et l'heure actuelles.
       @PreUpdate
       protected void onUpdate()
       {datemodif = LocalDateTime.now();}

}
