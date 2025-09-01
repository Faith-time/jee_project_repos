package sn.team.gestion_loc_immeubles.Entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@DiscriminatorValue("LOCATAIRE")
@Getter @Setter
public class Locataire extends Utilisateur {

    @OneToMany(mappedBy = "locataire", cascade = CascadeType.ALL)
    private List<Contrat> contrats;
}
