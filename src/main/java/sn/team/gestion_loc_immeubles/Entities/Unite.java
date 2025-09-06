package sn.team.gestion_loc_immeubles.Entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter @Setter
public class Unite {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private Integer numero;

    @Min(1)
    private Integer nbPieces;

    @Min(10)
    private Double superficie;

    @Min(0)
    private Double loyer;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatutUnite statut = StatutUnite.DISPONIBLE; // valeur par défaut

    @ManyToOne
    @JoinColumn(name = "immeuble_id")
    private Immeuble immeuble;

    // Enum interne ou séparée selon ton organisation
    public enum StatutUnite {
        DISPONIBLE,
        LOUEE
    }
}
