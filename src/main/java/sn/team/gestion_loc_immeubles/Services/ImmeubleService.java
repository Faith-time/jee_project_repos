package sn.team.gestion_loc_immeubles.Services;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
import sn.team.gestion_loc_immeubles.DAO.ImmeubleDAO;
import sn.team.gestion_loc_immeubles.DAO.ImmeubleDAOImpl;
import sn.team.gestion_loc_immeubles.Entities.Immeuble;
import sn.team.gestion_loc_immeubles.Entities.Unite;
import sn.team.gestion_loc_immeubles.Util.JPAUtil;

import java.util.List;

public class ImmeubleService {

    private final ImmeubleDAO immeubleDAO;

    public ImmeubleService() {
        this.immeubleDAO = new ImmeubleDAOImpl() {
            @Override
            public List<Immeuble> findAll() {
                return List.of();
            }
        };
    }

    /**
     * Créer un immeuble
     */
    public void creerImmeuble(Immeuble immeuble) {
        immeubleDAO.save(immeuble);
    }

    /**
     * Rechercher un immeuble par ID
     */
    public Immeuble trouverParId(Long id) {
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
        return immeubleDAO.findByProprietaire(proprietaireId);
    }

    /**
     * Mettre à jour un immeuble
     */
    public void mettreAJourImmeuble(Immeuble immeuble) {
        immeubleDAO.update(immeuble);
    }

    /**
     * Supprimer un immeuble
     */
    public void supprimerImmeuble(Long id) {
        immeubleDAO.delete(id);
    }

    /**
     * Compter tous les immeubles
     */
    public long compterTousImmeubles() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery("SELECT COUNT(i) FROM Immeuble i", Long.class)
                    .getSingleResult();
        } catch (NoResultException e) {
            return 0;
        } catch (Exception e) {
            System.err.println("Erreur lors du comptage des immeubles: " + e.getMessage());
            return 0;
        } finally {
            if (em != null) {
                JPAUtil.closeEntityManager();
            }
        }
    }

    /**
     * Compte le nombre d'immeubles appartenant à un propriétaire spécifique
     * @param proprietaireId L'ID du propriétaire
     * @return Le nombre d'immeubles
     */
    public int compterImmeublesDuProprietaire(long proprietaireId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<Long> query = em.createQuery(
                    "SELECT COUNT(i) FROM Immeuble i WHERE i.proprietaire.id = :proprietaireId",
                    Long.class
            );
            query.setParameter("proprietaireId", proprietaireId);

            Long count = query.getSingleResult();
            return count != null ? count.intValue() : 0;

        } catch (Exception e) {
            System.err.println("Erreur lors du comptage des immeubles du propriétaire "
                    + proprietaireId + ": " + e.getMessage());
            return 0;
        } finally {
            em.close();
        }
    }

    /**
     * Récupère tous les immeubles d'un propriétaire
     * @param proprietaireId L'ID du propriétaire
     * @return La liste des immeubles
     */
    public List<Immeuble> getImmeublesDuProprietaire(long proprietaireId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<Immeuble> query = em.createQuery(
                    "SELECT i FROM Immeuble i WHERE i.proprietaire.id = :proprietaireId ORDER BY i.nom",
                    Immeuble.class
            );
            query.setParameter("proprietaireId", proprietaireId);

            return query.getResultList();

        } catch (Exception e) {
            System.err.println("Erreur lors de la récupération des immeubles du propriétaire "
                    + proprietaireId + ": " + e.getMessage());
            return List.of(); // Retourne une liste vide en cas d'erreur
        } finally {
            em.close();
        }
    }

    // =================== NOUVELLES MÉTHODES POUR LES STATISTIQUES ===================

    /**
     * Compte le nombre total d'unités d'un propriétaire
     * @param proprietaireId L'ID du propriétaire
     * @return Le nombre total d'unités
     */
    public int compterTotalUnitesDuProprietaire(long proprietaireId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<Long> query = em.createQuery(
                    "SELECT COUNT(u) FROM Unite u WHERE u.immeuble.proprietaire.id = :proprietaireId",
                    Long.class
            );
            query.setParameter("proprietaireId", proprietaireId);

            Long count = query.getSingleResult();
            return count != null ? count.intValue() : 0;

        } catch (Exception e) {
            System.err.println("Erreur lors du comptage des unités du propriétaire "
                    + proprietaireId + ": " + e.getMessage());
            return 0;
        } finally {
            em.close();
        }
    }

    /**
     * Compte le nombre d'unités disponibles d'un propriétaire
     * @param proprietaireId L'ID du propriétaire
     * @return Le nombre d'unités disponibles
     */
    public int compterUnitesDisponiblesDuProprietaire(long proprietaireId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<Long> query = em.createQuery(
                    "SELECT COUNT(u) FROM Unite u WHERE u.immeuble.proprietaire.id = :proprietaireId " +
                            "AND u.statut = :statut",
                    Long.class
            );
            query.setParameter("proprietaireId", proprietaireId);
            query.setParameter("statut", Unite.StatutUnite.DISPONIBLE);

            Long count = query.getSingleResult();
            return count != null ? count.intValue() : 0;

        } catch (Exception e) {
            System.err.println("Erreur lors du comptage des unités disponibles du propriétaire "
                    + proprietaireId + ": " + e.getMessage());
            return 0;
        } finally {
            em.close();
        }
    }

    /**
     * Compte le nombre total d'unités d'un immeuble spécifique
     * @param immeubleId L'ID de l'immeuble
     * @return Le nombre total d'unités
     */
    public int compterUnitesDeLImmeuble(long immeubleId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<Long> query = em.createQuery(
                    "SELECT COUNT(u) FROM Unite u WHERE u.immeuble.id = :immeubleId",
                    Long.class
            );
            query.setParameter("immeubleId", immeubleId);

            Long count = query.getSingleResult();
            return count != null ? count.intValue() : 0;

        } catch (Exception e) {
            System.err.println("Erreur lors du comptage des unités de l'immeuble "
                    + immeubleId + ": " + e.getMessage());
            return 0;
        } finally {
            em.close();
        }
    }

    /**
     * Compte le nombre d'unités disponibles d'un immeuble spécifique
     * @param immeubleId L'ID de l'immeuble
     * @return Le nombre d'unités disponibles
     */
    public int compterUnitesDisponiblesDeLImmeuble(long immeubleId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<Long> query = em.createQuery(
                    "SELECT COUNT(u) FROM Unite u WHERE u.immeuble.id = :immeubleId " +
                            "AND u.statut = :statut",
                    Long.class
            );
            query.setParameter("immeubleId", immeubleId);
            query.setParameter("statut", Unite.StatutUnite.DISPONIBLE);

            Long count = query.getSingleResult();
            return count != null ? count.intValue() : 0;

        } catch (Exception e) {
            System.err.println("Erreur lors du comptage des unités disponibles de l'immeuble "
                    + immeubleId + ": " + e.getMessage());
            return 0;
        } finally {
            em.close();
        }
    }

    /**
     * Compte le nombre d'unités louées d'un propriétaire
     * @param proprietaireId L'ID du propriétaire
     * @return Le nombre d'unités louées
     */
    public int compterUnitesLoueesDuProprietaire(long proprietaireId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<Long> query = em.createQuery(
                    "SELECT COUNT(u) FROM Unite u WHERE u.immeuble.proprietaire.id = :proprietaireId " +
                            "AND u.statut = :statut",
                    Long.class
            );
            query.setParameter("proprietaireId", proprietaireId);
            query.setParameter("statut", Unite.StatutUnite.LOUEE);

            Long count = query.getSingleResult();
            return count != null ? count.intValue() : 0;

        } catch (Exception e) {
            System.err.println("Erreur lors du comptage des unités louées du propriétaire "
                    + proprietaireId + ": " + e.getMessage());
            return 0;
        } finally {
            em.close();
        }
    }

    /**
     * Compte le nombre d'unités louées d'un immeuble spécifique
     * @param immeubleId L'ID de l'immeuble
     * @return Le nombre d'unités louées
     */
    public int compterUnitesLoueesDeLImmeuble(long immeubleId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<Long> query = em.createQuery(
                    "SELECT COUNT(u) FROM Unite u WHERE u.immeuble.id = :immeubleId " +
                            "AND u.statut = :statut",
                    Long.class
            );
            query.setParameter("immeubleId", immeubleId);
            query.setParameter("statut", Unite.StatutUnite.LOUEE);

            Long count = query.getSingleResult();
            return count != null ? count.intValue() : 0;

        } catch (Exception e) {
            System.err.println("Erreur lors du comptage des unités louées de l'immeuble "
                    + immeubleId + ": " + e.getMessage());
            return 0;
        } finally {
            em.close();
        }
    }

    /**
     * Calcule le taux d'occupation d'un propriétaire (en pourcentage)
     * @param proprietaireId L'ID du propriétaire
     * @return Le taux d'occupation en pourcentage (0-100)
     */
    public double calculerTauxOccupationProprietaire(long proprietaireId) {
        int totalUnites = compterTotalUnitesDuProprietaire(proprietaireId);
        if (totalUnites == 0) {
            return 0.0;
        }

        int unitesLouees = compterUnitesLoueesDuProprietaire(proprietaireId);
        return ((double) unitesLouees / totalUnites) * 100;
    }

    /**
     * Calcule le taux d'occupation d'un immeuble spécifique (en pourcentage)
     * @param immeubleId L'ID de l'immeuble
     * @return Le taux d'occupation en pourcentage (0-100)
     */
    public double calculerTauxOccupationImmeuble(long immeubleId) {
        int totalUnites = compterUnitesDeLImmeuble(immeubleId);
        if (totalUnites == 0) {
            return 0.0;
        }

        int unitesLouees = compterUnitesLoueesDeLImmeuble(immeubleId);
        return ((double) unitesLouees / totalUnites) * 100;
    }
}