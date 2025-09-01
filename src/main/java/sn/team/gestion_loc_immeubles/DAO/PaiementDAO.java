package sn.team.gestion_loc_immeubles.DAO;

import sn.team.gestion_loc_immeubles.Entities.Paiement;

import java.util.List;

public interface PaiementDAO extends GenericDAO<Paiement> {
    List<Paiement> findByContrat(Long contratId);
    List<Paiement> findByStatut(Paiement.StatutPaiement status);

}

