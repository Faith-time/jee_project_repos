package sn.team.gestion_loc_immeubles.Entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.FutureOrPresent;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Entity
@Getter @Setter
public class Contrat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @FutureOrPresent
    private LocalDate dateDebut;

    private LocalDate dateFin;

    @NotNull
    private Double montant;

    // Maintenant un locataire est un Utilisateur avec role LOCATAIRE
    @ManyToOne
    @JoinColumn(name = "locataire_id", nullable = false)
    private Utilisateur locataire;

    // Unité louée
    @ManyToOne
    @JoinColumn(name = "unite_id", nullable = false)
    private Unite unite;

    @OneToMany(mappedBy = "contrat", cascade = CascadeType.ALL)
    private List<Paiement> paiements;
}
