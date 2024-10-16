package com.example.tachesapp.Model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import javax.persistence.Entity;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;


import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;


@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties({"utilisateurs", "modifierpar", "creerpar"})
public class Departement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long iddepartement;
    private String nomdepartement;

    @OneToMany
    @JoinColumn(name = "iddepartement")
    @JsonIgnore // Exclure la sérialisation de la relation Societe
    private List<Utilisateur> utilisateurs;

    // Cette annotation mappe le champ 'datecreation' à une colonne de la base de données nommée "datecreation".
    // L'option 'updatable = false' garantit que cette colonne ne sera pas mise à jour manuellement après l'insertion initiale.
    @Column(name = "datecreation", updatable = false)
    private LocalDateTime datecreation;

    // Cette annotation mappe le champ 'datemodif' à une colonne de la base de données nommée "datemodif".
    private LocalDateTime datemodif;

    @ManyToOne
    @JoinColumn(name = "idsociete")
    private Societe societe;

    // Cette annotation établit une relation Many-to-One avec le champ 'modifierpar',
    // indiquant que cette entité (Utilisateur) est le côté 'many', et 'modifierpar' est le côté propriétaire.
    // Elle utilise la colonne 'modifierpar' pour maintenir la relation dans la base de données.
    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "modifierpar")
    private Utilisateur modifierpar;

    // Cette annotation établit une relation Many-to-One avec le champ 'creerpar',
    // indiquant que cette entité (Utilisateur) est le côté 'many', et 'creerpar' est le côté propriétaire.
    // L'option 'updatable = false' garantit que cette colonne ne sera pas mise à jour manuellement après l'insertion initiale.
    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "creerpar", updatable = false)
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

    @Override
    public int hashCode() {
        return Objects.hash(iddepartement);  // Utilise uniquement l'ID pour le calcul du hashCode
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Departement that = (Departement) o;
        return Objects.equals(iddepartement, that.iddepartement);
    }

    @Override
    public String toString() {
        return "Departement{" +
                "iddepartement=" + iddepartement +
                ", nomdepartement='" + nomdepartement + '\'' +
                ", societe=" + societe +
                '}';
    }

}
