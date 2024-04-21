package com.example.tachesapp.Model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Equipe {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idequipe;
    private String nomequipe;


    // Cette annotation mappe le champ 'datecreation' à une colonne de la base de données nommée "datecreation".
    // L'option 'updatable = false' garantit que cette colonne ne sera pas mise à jour manuellement après l'insertion initiale.
    @Column(name = "datecreation", updatable = false)
    private LocalDateTime datecreation;

    // Cette annotation mappe le champ 'datemodif' à une colonne de la base de données nommée "datemodif".
    private LocalDateTime datemodif;

    // Cette annotation établit une relation Many-to-One avec le champ 'modifierpar',
    // indiquant que cette entité (Utilisateur) est le côté 'many', et 'modifierpar' est le côté propriétaire.
    // Elle utilise la colonne 'modifierpar' pour maintenir la relation dans la base de données.
    @ManyToOne
    @JoinColumn(name = "modifierpar")
    private Utilisateur modifierpar;

    // Cette annotation établit une relation Many-to-One avec le champ 'creerpar',
    // indiquant que cette entité (Utilisateur) est le côté 'many', et 'creerpar' est le côté propriétaire.
    // L'option 'updatable = false' garantit que cette colonne ne sera pas mise à jour manuellement après l'insertion initiale.
    @ManyToOne
    @JoinColumn(name = "creerpar")
    private Utilisateur creerpar;

    // Getters et setters pour les champs 'datecreation', 'datemodif', 'modifierpar', et 'creerpar'.

    // Cette méthode est annotée avec '@PrePersist', ce qui signifie qu'elle sera exécutée automatiquement
    // avant qu'une nouvelle entité ne soit persistée (c'est-à-dire avant son insertion dans la base de données).
    // Elle initialise le champ 'datecreation' avec la date et l'heure actuelles.
    @PrePersist
    protected void onCreate() {
        datecreation = LocalDateTime.now();
    }

    // Cette méthode est annotée avec '@PreUpdate', ce qui signifie qu'elle sera exécutée automatiquement
    // avant qu'une entité existante ne soit mise à jour dans la base de données.
    // Elle met à jour le champ 'datemodif' avec la date et l'heure actuelles.
    @PreUpdate
    protected void onUpdate() {
        datemodif = LocalDateTime.now();
    }

    @OneToMany
    @JoinColumn(name = "idequipe")
    private List<Utilisateur> membres;

    @OneToOne(optional = true)
    @JoinColumn(name = "idutilresponsabledequipe")
    private Utilisateur responsable;


}
