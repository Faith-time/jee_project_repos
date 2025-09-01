package sn.team.gestion_loc_immeubles.DAO;

import sn.team.gestion_loc_immeubles.Entities.Immeuble;

import java.util.List;

public interface ImmeubleDAO extends GenericDAO<Immeuble> {
    // méthodes spécifiques à Immeuble si besoin
    List<Immeuble> findByProprietaire(Long utilisateurId);
}
