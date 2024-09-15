package com.example.tachesapp.Model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@JsonIgnoreProperties({"taches", "notifications", "emittedNotifications", "receivedNotifications", "modifierpar", "creerpar"})
public class Utilisateur {
@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
private Long idutilisateur;
private String mail;
private String nom;
private String prenom;
private String motdepasse;
private boolean actif; // Champ pour indiquer si le compte est actif ou non

    // Cette annotation mappe le champ 'datecreation' à une colonne de la base de données nommée "datecreation".
    // L'option 'updatable = false' garantit que cette colonne ne sera pas mise à jour manuellement après l'insertion initiale.
    @Column(name = "datecreation", updatable = false)
    private LocalDateTime datecreation;

    // Cette annotation mappe le champ 'datemodif' à une colonne de la base de données nommée "datemodif".
    private LocalDateTime datemodif;

    @ManyToOne
    @JoinColumn(name = "idrole")
    private Role role;

    @ManyToOne
    @JoinColumn(name = "iddomaine")
    private Domaine domaine;

    @ManyToOne
    @JoinColumn(name = "iddepartement")
    private Departement departement;

    @ManyToOne
    @JoinColumn(name = "idsociete")
    private Societe societe;

    @OneToMany
    @JsonIgnore
    @JoinColumn(name = "idutilisateur")
    private List<Tache> taches;


    @ManyToOne(optional = true)
    @JoinColumn(name = "idequipe")
    @JsonIgnore // Exclure la sérialisation de la relation Societe
    private Equipe equipes;

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
    @JoinColumn(name = "creerpar")
    private Utilisateur creerpar;


    @OneToMany(mappedBy = "recepteur", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Notification> notifications;

    @OneToMany(mappedBy = "emetteur", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Notification> emittedNotifications;

    @OneToMany(mappedBy = "recepteur", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Notification> receivedNotifications;

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

    public boolean isActif() {
        return actif;
    }

    @Override
    public int hashCode() {
        return Objects.hash(idutilisateur);  // Utilise uniquement l'ID pour le calcul du hashCode
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Utilisateur that = (Utilisateur) o;
        return Objects.equals(idutilisateur, that.idutilisateur);
    }


    @Override
    public String toString() {
        return "Utilisateur{" +
                "idutilisateur=" + idutilisateur +
                ", mail='" + mail + '\'' +
                ", nom='" + nom + '\'' +
                ", prenom='" + prenom + '\'' +
                ", motdepasse='" + motdepasse + '\'' +
                ", role=" + role.getIdrole() +
                ", domaine=" + domaine +
                ", actif=" + actif +
                '}';
    }
}


