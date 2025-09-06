package sn.team.gestion_loc_immeubles.Services;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
import sn.team.gestion_loc_immeubles.Entities.Role;
import sn.team.gestion_loc_immeubles.Entities.Utilisateur;
import sn.team.gestion_loc_immeubles.Util.JPAUtil;

import java.time.LocalDate;
import java.util.List;

public class LocataireService {

    /**
     * Compter les locataires d'un propriétaire
     */
    public long compterLocatairesByProprietaire(Long proprietaireId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em = JPAUtil.getEntityManager();
            return em.createQuery(
                            "SELECT COUNT(DISTINCT c.locataire.id) FROM Contrat c " +
                                    "WHERE c.unite.immeuble.proprietaire.id = :proprietaireId",
                            Long.class)
                    .setParameter("proprietaireId", proprietaireId)
                    .getSingleResult();
        } catch (NoResultException e) {
            return 0;
        } catch (Exception e) {
            System.err.println("Erreur lors du comptage des locataires du propriétaire: " + e.getMessage());
            return 0;
        } finally {
            if (em != null) {
                JPAUtil.closeEntityManager();
            }
        }
    }

    /**
     * Compter les locataires actifs d'un propriétaire (avec contrats actifs)
     */
    public long compterLocatairesActifsByProprietaire(Long proprietaireId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em = JPAUtil.getEntityManager();
            return em.createQuery(
                            "SELECT COUNT(DISTINCT c.locataire.id) FROM Contrat c " +
                                    "WHERE c.unite.immeuble.proprietaire.id = :proprietaireId " +
                                    "AND (c.dateFin IS NULL OR c.dateFin > CURRENT_DATE)",
                            Long.class)
                    .setParameter("proprietaireId", proprietaireId)
                    .getSingleResult();
        } catch (NoResultException e) {
            return 0;
        } catch (Exception e) {
            System.err.println("Erreur lors du comptage des locataires actifs: " + e.getMessage());
            return 0;
        } finally {
            if (em != null) {
                JPAUtil.closeEntityManager();
            }
        }
    }

    /**
     * Compte le nombre de locataires d'un propriétaire spécifique
     * (locataires ayant des contrats actifs dans les immeubles du propriétaire)
     * @param proprietaireId L'ID du propriétaire
     * @return Le nombre de locataires
     */
    public int compterLocatairesDuProprietaire(long proprietaireId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em = JPAUtil.getEntityManager();
            TypedQuery<Long> query = em.createQuery(
                    "SELECT COUNT(DISTINCT c.locataire) FROM Contrat c " +
                            "WHERE c.unite.immeuble.proprietaire.id = :proprietaireId " +
                            "AND (c.dateFin IS NULL OR c.dateFin >= :dateActuelle)",
                    Long.class
            );
            query.setParameter("proprietaireId", proprietaireId);
            query.setParameter("dateActuelle", LocalDate.now());

            Long count = query.getSingleResult();
            return count != null ? count.intValue() : 0;

        } catch (Exception e) {
            System.err.println("Erreur lors du comptage des locataires du propriétaire "
                    + proprietaireId + ": " + e.getMessage());
            return 0;
        } finally {
            if (em != null) {
                JPAUtil.closeEntityManager();
            }
        }
    }

    /**
     * Récupère tous les locataires d'un propriétaire
     * @param proprietaireId L'ID du propriétaire
     * @return La liste des locataires
     */
    public List<Utilisateur> getLocatairesDuProprietaire(long proprietaireId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em = JPAUtil.getEntityManager();
            TypedQuery<Utilisateur> query = em.createQuery(
                    "SELECT DISTINCT c.locataire FROM Contrat c " +
                            "WHERE c.unite.immeuble.proprietaire.id = :proprietaireId " +
                            "AND (c.dateFin IS NULL OR c.dateFin >= :dateActuelle) " +
                            "ORDER BY c.locataire.nom, c.locataire.prenom",
                    Utilisateur.class
            );
            query.setParameter("proprietaireId", proprietaireId);
            query.setParameter("dateActuelle", LocalDate.now());

            return query.getResultList();

        } catch (Exception e) {
            System.err.println("Erreur lors de la récupération des locataires du propriétaire "
                    + proprietaireId + ": " + e.getMessage());
            return List.of();
        } finally {
            if (em != null) {
                JPAUtil.closeEntityManager();
            }
        }
    }

    /**
     * Compte tous les locataires actifs
     * @return Le nombre total de locataires actifs
     */
    public int compterTousLesLocatairesActifs() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em = JPAUtil.getEntityManager();
            TypedQuery<Long> query = em.createQuery(
                    "SELECT COUNT(DISTINCT c.locataire) FROM Contrat c " +
                            "WHERE c.dateFin IS NULL OR c.dateFin >= :dateActuelle",
                    Long.class
            );
            query.setParameter("dateActuelle", LocalDate.now());

            Long count = query.getSingleResult();
            return count != null ? count.intValue() : 0;

        } catch (Exception e) {
            System.err.println("Erreur lors du comptage de tous les locataires actifs: " + e.getMessage());
            return 0;
        } finally {
            if (em != null) {
                JPAUtil.closeEntityManager();
            }
        }
    }

    /**
     * Récupère tous les utilisateurs avec le rôle LOCATAIRE
     * @return La liste de tous les locataires
     */
    public List<Utilisateur> getAllLocataires() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em = JPAUtil.getEntityManager();
            TypedQuery<Utilisateur> query = em.createQuery(
                    "SELECT u FROM Utilisateur u WHERE u.role = :role ORDER BY u.nom, u.prenom",
                    Utilisateur.class
            );
            query.setParameter("role", Role.LOCATAIRE);

            return query.getResultList();

        } catch (Exception e) {
            System.err.println("Erreur lors de la récupération de tous les locataires: " + e.getMessage());
            return List.of();
        } finally {
            if (em != null) {
                JPAUtil.closeEntityManager();
            }
        }
    }

    /**
     * Trouve un locataire par son ID
     * @param id L'ID du locataire
     * @return Le locataire ou null si non trouvé
     */
    public Utilisateur findById(long id) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em = JPAUtil.getEntityManager();
            Utilisateur utilisateur = em.find(Utilisateur.class, id);
            if (utilisateur != null && utilisateur.getRole() == Role.LOCATAIRE) {
                return utilisateur;
            }
            return null;
        } catch (Exception e) {
            System.err.println("Erreur lors de la recherche du locataire " + id + ": " + e.getMessage());
            return null;
        } finally {
            if (em != null) {
                JPAUtil.closeEntityManager();
            }
        }
    }
}