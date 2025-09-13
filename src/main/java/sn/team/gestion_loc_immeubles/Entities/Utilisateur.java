package sn.team.gestion_loc_immeubles.Entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "utilisateur")
@Getter
@Setter
public class Utilisateur {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(min = 2, max = 50)
    private String nom;

    @NotBlank
    @Size(min = 2, max = 50)
    private String prenom;

    @Column(length = 20)
    private String telephone;

    @Email
    @NotBlank
    @Column(unique = true, nullable = false)
    private String email;

    @NotBlank
    @Size(min = 6, max = 100)
    private String motDePasse;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role = Role.VISITEUR; // Valeur par défaut changée à VISITEUR

    // Relations spécifiques selon le rôle
    @OneToMany(mappedBy = "proprietaire", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Immeuble> immeubles; // Pour les propriétaires

    @OneToMany(mappedBy = "locataire", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Contrat> contrats; // Pour les locataires

    // Constructeur vide requis par JPA
    public Utilisateur() {}

    // Constructeur pratique
    public Utilisateur(String nom, String prenom,String telephone, String email, String motDePasse, Role role) {
        this.nom = nom;
        this.prenom = prenom;
        this.telephone = telephone;
        this.email = email;
        this.motDePasse = motDePasse;
        this.role = role != null ? role : Role.VISITEUR;
    }

    // Constructeur sans rôle (par défaut VISITEUR)
    public Utilisateur(String nom, String prenom,String telephone, String email, String motDePasse) {
        this(nom, prenom,telephone, email, motDePasse, Role.VISITEUR);
    }

    /**
     * Vérifie si l'utilisateur a un rôle spécifique
     */
    public boolean hasRole(Role role) {
        return this.role == role;
    }

    /**
     * Vérifie si l'utilisateur est administrateur
     */
    public boolean isAdmin() {
        return this.role == Role.ADMIN;
    }

    /**
     * Vérifie si l'utilisateur est propriétaire
     */
    public boolean isProprietaire() {
        return this.role == Role.PROPRIETAIRE;
    }

    /**
     * Vérifie si l'utilisateur est locataire
     */
    public boolean isLocataire() {
        return this.role == Role.LOCATAIRE;
    }

    /**
     * Vérifie si l'utilisateur est visiteur
     */
    public boolean isVisiteur() {
        return this.role == Role.VISITEUR;
    }

    /**
     * Retourne le nom du rôle en string (pour compatibilité)
     */
    public String getTypeUtilisateur() {
        return this.role.name();
    }

    /**
     * Retourne le nom complet de l'utilisateur
     */
    public String getNomComplet() {
        return this.prenom + " " + this.nom;
    }

    @Override
    public String toString() {
        return "Utilisateur{" +
                "id=" + id +
                ", nom='" + nom + '\'' +
                ", prenom='" + prenom + '\'' +
                ", email='" + email + '\'' +
                ", role=" + role +
                '}';
    }
}