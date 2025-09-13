package sn.team.gestion_loc_immeubles.Entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Getter @Setter
public class Paiement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Min(0)
    private Double montant;

    private LocalDate datePaiement;

    @NotNull
    private LocalDate dateEcheance;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatutPaiement statut = StatutPaiement.EN_ATTENTE;

    @ManyToOne
    @JoinColumn(name = "contrat_id", nullable = false)
    private Contrat contrat;

    @Column(unique = true, length = 100)
    private String transactionId;   // transaction_id envoyé à CinetPay (ex: "PAI-123-1623")

    @Column(length = 500)
    private String paymentToken;    // payment_token retourné par CinetPay (optionnel)

    @Column(length = 1000)
    private String paymentUrl;      // payment_url retourné par CinetPay (optionnel)

    @Column(length = 100)
    private String operatorId;      // id de l'opérateur retourné après paiement

    @Column(columnDefinition = "TEXT")  // Utilise TEXT au lieu de VARCHAR(255)
    private String rawResponse;

    public enum StatutPaiement {
        EN_ATTENTE,   // le paiement est attendu mais pas encore effectué
        PAYE,         // le paiement a été effectué à temps
        EN_RETARD     // le paiement est en retard ou non effectué après la date d'échéance
    }
}