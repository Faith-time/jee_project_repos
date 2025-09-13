package sn.team.gestion_loc_immeubles.Services;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
import sn.team.gestion_loc_immeubles.DAO.ImmeubleDAO;
import sn.team.gestion_loc_immeubles.DAO.ImmeubleDAOImpl;
import sn.team.gestion_loc_immeubles.Entities.Immeuble;
import sn.team.gestion_loc_immeubles.Entities.Unite;
import sn.team.gestion_loc_immeubles.Util.JPAUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Service pour la gestion des immeubles
 * Fournit les opérations CRUD et de statistiques pour les immeubles
 */
public class ImmeubleService {

    private final ImmeubleDAO immeubleDAO;

    public ImmeubleService() {
        this.immeubleDAO = new ImmeubleDAOImpl();
    }

    // =================== MÉTHODES CRUD DE BASE ===================

    /**
     * Créer un immeuble
     */
    public void creerImmeuble(Immeuble immeuble) {
        if (immeuble == null) {
            throw new IllegalArgumentException("L'immeuble ne peut pas être null");
        }
        immeubleDAO.save(immeuble);
    }

    /**
     * Rechercher un immeuble par ID
     */
    public Immeuble trouverParId(Long id) {
        if (id == null || id <= 0) {
            return null;
        }
        return immeubleDAO.findById(id);
    }

    /**
     * Lister tous les immeubles
     */
    public List<Immeuble> listerImmeubles() {
        return immeubleDAO.findAll();
    }

    /**
     * Lister les immeubles d'un propriétaire
     */
    public List<Immeuble> listerParProprietaire(Long proprietaireId) {
        if (proprietaireId == null || proprietaireId <= 0) {
            return new ArrayList<>();
        }
        return immeubleDAO.findByProprietaire(proprietaireId);
    }

    /**
     * Mettre à jour un immeuble
     */
    public void mettreAJourImmeuble(Immeuble immeuble) {
        if (immeuble == null) {
            throw new IllegalArgumentException("L'immeuble ne peut pas être null");
        }
        immeubleDAO.update(immeuble);
    }

    /**
     * Supprimer un immeuble
     */
    public void supprimerImmeuble(Long id) {
        if (id != null && id > 0) {
            immeubleDAO.delete(id);
        }
    }

    // =================== MÉTHODES DE COMPTAGE GÉNÉRALES ===================

    /**
     * Compter tous les immeubles du système
     */
    public long compterTousImmeubles() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<Long> query = em.createQuery("SELECT COUNT(i) FROM Immeuble i", Long.class);
            Long result = query.getSingleResult();
            return result != null ? result : 0;
        } catch (Exception e) {
            System.err.println("Erreur lors du comptage des immeubles: " + e.getMessage());
            return 0;
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }

    // =================== MÉTHODES SPÉCIFIQUES AU PROPRIÉTAIRE ===================

    /**
     * Compte le nombre d'immeubles d'un propriétaire
     */
    public int compterImmeublesDuProprietaire(long proprietaireId) {
        if (proprietaireId <= 0) {
            return 0;
        }

        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<Long> query = em.createQuery(
                    "SELECT COUNT(i) FROM Immeuble i WHERE i.proprietaire.id = :proprietaireId",
                    Long.class);
            query.setParameter("proprietaireId", proprietaireId);

            Long count = query.getSingleResult();
            return count != null ? count.intValue() : 0;

        } catch (Exception e) {
            System.err.println("Erreur lors du comptage des immeubles du propriétaire " +
                    proprietaireId + ": " + e.getMessage());
            return 0;
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }

    /**
     * Récupère tous les immeubles d'un propriétaire
     */
    public List<Immeuble> getImmeublesDuProprietaire(long proprietaireId) {
        if (proprietaireId <= 0) {
            return new ArrayList<>();
        }

        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<Immeuble> query = em.createQuery(
                    "SELECT i FROM Immeuble i WHERE i.proprietaire.id = :proprietaireId ORDER BY i.nom",
                    Immeuble.class);
            query.setParameter("proprietaireId", proprietaireId);

            return query.getResultList();

        } catch (Exception e) {
            System.err.println("Erreur lors de la récupération des immeubles du propriétaire " +
                    proprietaireId + ": " + e.getMessage());
            return new ArrayList<>();
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }

    // =================== STATISTIQUES UNITÉS POUR PROPRIÉTAIRE ===================

    /**
     * Compte le nombre total d'unités d'un propriétaire
     */
    public int compterTotalUnitesDuProprietaire(long proprietaireId) {
        if (proprietaireId <= 0) {
            return 0;
        }

        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<Long> query = em.createQuery(
                    "SELECT COUNT(u) FROM Unite u WHERE u.immeuble.proprietaire.id = :proprietaireId",
                    Long.class);
            query.setParameter("proprietaireId", proprietaireId);

            Long count = query.getSingleResult();
            return count != null ? count.intValue() : 0;

        } catch (Exception e) {
            System.err.println("Erreur lors du comptage des unités du propriétaire " +
                    proprietaireId + ": " + e.getMessage());
            return 0;
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }

    /**
     * Compte le nombre d'unités disponibles d'un propriétaire
     */
    public int compterUnitesDisponiblesDuProprietaire(long proprietaireId) {
        if (proprietaireId <= 0) {
            return 0;
        }

        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<Long> query = em.createQuery(
                    "SELECT COUNT(u) FROM Unite u WHERE u.immeuble.proprietaire.id = :proprietaireId " +
                            "AND u.statut = :statut",
                    Long.class);
            query.setParameter("proprietaireId", proprietaireId);
            query.setParameter("statut", Unite.StatutUnite.DISPONIBLE);

            Long count = query.getSingleResult();
            return count != null ? count.intValue() : 0;

        } catch (Exception e) {
            System.err.println("Erreur lors du comptage des unités disponibles du propriétaire " +
                    proprietaireId + ": " + e.getMessage());
            return 0;
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }

    /**
     * Compte le nombre d'unités louées d'un propriétaire
     */
    public int compterUnitesLoueesDuProprietaire(long proprietaireId) {
        if (proprietaireId <= 0) {
            return 0;
        }

        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<Long> query = em.createQuery(
                    "SELECT COUNT(u) FROM Unite u WHERE u.immeuble.proprietaire.id = :proprietaireId " +
                            "AND u.statut = :statut",
                    Long.class);
            query.setParameter("proprietaireId", proprietaireId);
            query.setParameter("statut", Unite.StatutUnite.LOUEE);

            Long count = query.getSingleResult();
            return count != null ? count.intValue() : 0;

        } catch (Exception e) {
            System.err.println("Erreur lors du comptage des unités louées du propriétaire " +
                    proprietaireId + ": " + e.getMessage());
            return 0;
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }

    // =================== STATISTIQUES POUR UN IMMEUBLE SPÉCIFIQUE ===================

    /**
     * Compte le nombre total d'unités d'un immeuble
     */
    public int compterUnitesDeLImmeuble(long immeubleId) {
        if (immeubleId <= 0) {
            return 0;
        }

        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<Long> query = em.createQuery(
                    "SELECT COUNT(u) FROM Unite u WHERE u.immeuble.id = :immeubleId",
                    Long.class);
            query.setParameter("immeubleId", immeubleId);

            Long count = query.getSingleResult();
            return count != null ? count.intValue() : 0;

        } catch (Exception e) {
            System.err.println("Erreur lors du comptage des unités de l'immeuble " +
                    immeubleId + ": " + e.getMessage());
            return 0;
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }

    /**
     * Compte le nombre d'unités disponibles d'un immeuble
     */
    public int compterUnitesDisponiblesDeLImmeuble(long immeubleId) {
        if (immeubleId <= 0) {
            return 0;
        }

        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<Long> query = em.createQuery(
                    "SELECT COUNT(u) FROM Unite u WHERE u.immeuble.id = :immeubleId " +
                            "AND u.statut = :statut",
                    Long.class);
            query.setParameter("immeubleId", immeubleId);
            query.setParameter("statut", Unite.StatutUnite.DISPONIBLE);

            Long count = query.getSingleResult();
            return count != null ? count.intValue() : 0;

        } catch (Exception e) {
            System.err.println("Erreur lors du comptage des unités disponibles de l'immeuble " +
                    immeubleId + ": " + e.getMessage());
            return 0;
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }

    /**
     * Compte le nombre d'unités louées d'un immeuble
     */
    public int compterUnitesLoueesDeLImmeuble(long immeubleId) {
        if (immeubleId <= 0) {
            return 0;
        }

        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<Long> query = em.createQuery(
                    "SELECT COUNT(u) FROM Unite u WHERE u.immeuble.id = :immeubleId " +
                            "AND u.statut = :statut",
                    Long.class);
            query.setParameter("immeubleId", immeubleId);
            query.setParameter("statut", Unite.StatutUnite.LOUEE);

            Long count = query.getSingleResult();
            return count != null ? count.intValue() : 0;

        } catch (Exception e) {
            System.err.println("Erreur lors du comptage des unités louées de l'immeuble " +
                    immeubleId + ": " + e.getMessage());
            return 0;
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }

    // =================== CALCULS DE TAUX D'OCCUPATION ===================

    /**
     * Calcule le taux d'occupation d'un propriétaire (en pourcentage)
     */
    public double calculerTauxOccupationProprietaire(long proprietaireId) {
        int totalUnites = compterTotalUnitesDuProprietaire(proprietaireId);
        if (totalUnites == 0) {
            return 0.0;
        }

        int unitesLouees = compterUnitesLoueesDuProprietaire(proprietaireId);
        return Math.round(((double) unitesLouees / totalUnites) * 100 * 100.0) / 100.0;
    }

    /**
     * Calcule le taux d'occupation d'un immeuble (en pourcentage)
     */
    public double calculerTauxOccupationImmeuble(long immeubleId) {
        int totalUnites = compterUnitesDeLImmeuble(immeubleId);
        if (totalUnites == 0) {
            return 0.0;
        }

        int unitesLouees = compterUnitesLoueesDeLImmeuble(immeubleId);
        return Math.round(((double) unitesLouees / totalUnites) * 100 * 100.0) / 100.0;
    }

    // =================== MÉTHODES DE RECHERCHE AVANCÉE ===================

    /**
     * Recherche des immeubles par nom (pour fonctionnalité de recherche)
     */
    public List<Immeuble> rechercherImmeubles(String searchTerm, long proprietaireId) {
        if (searchTerm == null || searchTerm.trim().isEmpty() || proprietaireId <= 0) {
            return new ArrayList<>();
        }

        EntityManager em = JPAUtil.getEntityManager();
        try {
            String searchPattern = "%" + searchTerm.trim().toLowerCase() + "%";

            TypedQuery<Immeuble> query = em.createQuery(
                    "SELECT i FROM Immeuble i " +
                            "WHERE i.proprietaire.id = :proprietaireId " +
                            "AND (LOWER(i.nom) LIKE :searchTerm " +
                            "OR LOWER(i.adresse) LIKE :searchTerm " +
                            "OR LOWER(i.description) LIKE :searchTerm) " +
                            "ORDER BY i.nom",
                    Immeuble.class);
            query.setParameter("proprietaireId", proprietaireId);
            query.setParameter("searchTerm", searchPattern);

            return query.getResultList();

        } catch (Exception e) {
            System.err.println("Erreur lors de la recherche d'immeubles avec le terme '" +
                    searchTerm + "': " + e.getMessage());
            return new ArrayList<>();
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }

    /**
     * Vérifie si un immeuble appartient à un propriétaire
     */
    public boolean appartientAuProprietaire(long immeubleId, long proprietaireId) {
        if (immeubleId <= 0 || proprietaireId <= 0) {
            return false;
        }

        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<Long> query = em.createQuery(
                    "SELECT COUNT(i) FROM Immeuble i WHERE i.id = :immeubleId AND i.proprietaire.id = :proprietaireId",
                    Long.class);
            query.setParameter("immeubleId", immeubleId);
            query.setParameter("proprietaireId", proprietaireId);

            Long result = query.getSingleResult();
            return result != null && result > 0;

        } catch (Exception e) {
            System.err.println("Erreur lors de la vérification de propriété de l'immeuble: " + e.getMessage());
            return false;
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }
}