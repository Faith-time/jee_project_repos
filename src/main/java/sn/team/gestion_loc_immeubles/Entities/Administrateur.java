package sn.team.gestion_loc_immeubles.Entities;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Entity
@DiscriminatorValue("ADMIN")
@Getter @Setter
public class Administrateur extends Utilisateur {
}
