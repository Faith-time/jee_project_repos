package sn.team.gestion_loc_immeubles.DAO;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import sn.team.gestion_loc_immeubles.Entities.Role;
import sn.team.gestion_loc_immeubles.Entities.Utilisateur;

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
        } finally {
            em.close();
        }
    }

    @Override
    public List<Utilisateur> findByRole(Role role) {
        EntityManager em = getEntityManager();
        try {
            TypedQuery<Utilisateur> query = em.createQuery(
                    "SELECT u FROM Utilisateur u WHERE u.role = :role ORDER BY u.nom, u.prenom",
                    Utilisateur.class);
            query.setParameter("role", role);
            return query.getResultList();
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la recherche des utilisateurs par rôle: " + e.getMessage(), e);
        } finally {
            em.close();
        }
    }
}
