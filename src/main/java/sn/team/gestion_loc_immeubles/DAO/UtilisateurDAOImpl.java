package sn.team.gestion_loc_immeubles.DAO;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import sn.team.gestion_loc_immeubles.Entities.Utilisateur;
import sn.team.gestion_loc_immeubles.Entities.Locataire;
import sn.team.gestion_loc_immeubles.Entities.Proprietaire;

import java.util.List;

public class UtilisateurDAOImpl extends GenericDAOImpl<Utilisateur> implements UtilisateurDAO {

    public UtilisateurDAOImpl() {
        super(Utilisateur.class);
    }

    @Override
    public Utilisateur findByEmail(String email) {
        EntityManager em = getEntityManager();
        try {
            TypedQuery<Utilisateur> query = em.createQuery(
                    "SELECT u FROM Utilisateur u WHERE u.email = :email", Utilisateur.class);
            query.setParameter("email", email);
            return query.getResultStream().findFirst().orElse(null);
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la recherche par email: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Locataire> findAllLocataires() {
        EntityManager em = getEntityManager();
        try {
            TypedQuery<Locataire> query = em.createQuery(
                    "SELECT l FROM Locataire l", Locataire.class);
            return query.getResultList();
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la récupération des locataires: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Proprietaire> findAllProprietaires() {
        EntityManager em = getEntityManager();
        try {
            TypedQuery<Proprietaire> query = em.createQuery(
                    "SELECT p FROM Proprietaire p", Proprietaire.class);
            return query.getResultList();
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la récupération des propriétaires: " + e.getMessage(), e);
        }
    }
}