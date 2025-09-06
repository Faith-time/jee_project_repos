package sn.team.gestion_loc_immeubles.DAO;

import sn.team.gestion_loc_immeubles.Entities.Role;
import sn.team.gestion_loc_immeubles.Entities.Utilisateur;

import java.util.List;

public interface UtilisateurDAO extends GenericDAO<Utilisateur> {

    /**
     * Trouver un utilisateur par email
     */
    Utilisateur findByEmail(String email);

    /**
     * Récupérer les utilisateurs par rôle (ADMIN, PROPRIETAIRE, LOCATAIRE)
     */
    List<Utilisateur> findByRole(Role role);
}
