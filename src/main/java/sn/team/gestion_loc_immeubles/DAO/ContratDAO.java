package sn.team.gestion_loc_immeubles.DAO;

import sn.team.gestion_loc_immeubles.Entities.Contrat;

import java.util.List;

public interface ContratDAO extends GenericDAO<Contrat> {
    List<Contrat> findByLocataire(Long locataireId);
    List<Contrat> findByUnite(Long uniteId);
}
