package sn.team.gestion_loc_immeubles.Entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@DiscriminatorValue("PROPRIETAIRE")
@Getter @Setter
public class Proprietaire extends Utilisateur {

    @OneToMany(mappedBy = "proprietaire", cascade = CascadeType.ALL)
    private List<Immeuble> immeubles;
}
