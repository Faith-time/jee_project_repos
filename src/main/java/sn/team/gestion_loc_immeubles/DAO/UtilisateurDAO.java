package sn.team.gestion_loc_immeubles.DAO;

import sn.team.gestion_loc_immeubles.Entities.Locataire;
import sn.team.gestion_loc_immeubles.Entities.Proprietaire;
import sn.team.gestion_loc_immeubles.Entities.Utilisateur;

import java.util.List;

public interface UtilisateurDAO extends GenericDAO<Utilisateur> {
    Utilisateur findByEmail(String email);
    List<Locataire> findAllLocataires();
    List<Proprietaire> findAllProprietaires();

}
