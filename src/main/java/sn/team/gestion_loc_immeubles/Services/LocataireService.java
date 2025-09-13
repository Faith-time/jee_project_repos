package sn.team.gestion_loc_immeubles.Services;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
import sn.team.gestion_loc_immeubles.Entities.Role;
import sn.team.gestion_loc_immeubles.Entities.Utilisateur;
import sn.team.gestion_loc_immeubles.Util.JPAUtil;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Service pour la gestion des locataires
 * Fournit les opérations CRUD et de recherche pour les locataires
 */
public class LocataireService {

    /**
     * Compte le nombre total de locataires d'un propriétaire (tous contrats confondus)
     * @param proprietaireId L'ID du propriétaire
     * @return Le nombre total de locataires ayant eu des contrats avec ce propriétaire
     */
    public long compterLocatairesByProprietaire(Long proprietaireId) {

        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<Long> query = em.createQuery(
                    "SELECT COUNT(DISTINCT c.locataire.id) FROM Contrat c " +
                            "WHERE c.unite.immeuble.proprietaire.id = :proprietaireId",
                    Long.class);
            query.setParameter("proprietaireId", proprietaireId);

            Long result = query.getSingleResult();
            return result != null ? result : 0;

        } catch (NoResultException e) {
            return 0;
        } catch (Exception e) {
            System.err.println("Erreur lors du comptage des locataires du propriétaire " + proprietaireId + ": " + e.getMessage());
            return 0;
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }

    /**
     * Compte le nombre de locataires actifs d'un propriétaire (avec contrats actifs uniquement)
     * @param proprietaireId L'ID du propriétaire
     * @return Le nombre de locataires ayant des contrats actifs
     */
    public long compterLocatairesActifsByProprietaire(Long proprietaireId) {
        if (proprietaireId == null || proprietaireId <= 0) {
            return 0;
        }

        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<Long> query = em.createQuery(
                    "SELECT COUNT(DISTINCT c.locataire.id) FROM Contrat c " +
                            "WHERE c.unite.immeuble.proprietaire.id = :proprietaireId " +
                            "AND (c.dateFin IS NULL OR c.dateFin >= :dateActuelle)",
                    Long.class);
            query.setParameter("proprietaireId", proprietaireId);
            query.setParameter("dateActuelle", LocalDate.now());

            Long result = query.getSingleResult();
            return result != null ? result : 0;

        } catch (NoResultException e) {
            return 0;
        } catch (Exception e) {
            System.err.println("Erreur lors du comptage des locataires actifs du propriétaire " + proprietaireId + ": " + e.getMessage());
            return 0;
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }

    /**
     * Récupère la liste des locataires actifs d'un propriétaire
     * @param proprietaireId L'ID du propriétaire
     * @return La liste des locataires ayant des contrats actifs
     */
    public List<Utilisateur> getLocatairesDuProprietaire(long proprietaireId) {
        if (proprietaireId <= 0) {
            return new ArrayList<>();
        }

        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<Utilisateur> query = em.createQuery(
                    "SELECT DISTINCT c.locataire FROM Contrat c " +
                            "WHERE c.unite.immeuble.proprietaire.id = :proprietaireId " +
                            "AND (c.dateFin IS NULL OR c.dateFin >= :dateActuelle) " +
                            "ORDER BY c.locataire.nom, c.locataire.prenom",
                    Utilisateur.class);
            query.setParameter("proprietaireId", proprietaireId);
            query.setParameter("dateActuelle", LocalDate.now());

            return query.getResultList();

        } catch (Exception e) {
            System.err.println("Erreur lors de la récupération des locataires du propriétaire " +
                    proprietaireId + ": " + e.getMessage());
            return new ArrayList<>();
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }

    /**
     * Récupère tous les utilisateurs avec le rôle LOCATAIRE (pour admin)
     * @return La liste de tous les locataires du système
     */
    public List<Utilisateur> getAllLocataires() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<Utilisateur> query = em.createQuery(
                    "SELECT u FROM Utilisateur u WHERE u.role = :role ORDER BY u.nom, u.prenom",
                    Utilisateur.class);
            query.setParameter("role", Role.LOCATAIRE);

            return query.getResultList();

        } catch (Exception e) {
            System.err.println("Erreur lors de la récupération de tous les locataires: " + e.getMessage());
            return new ArrayList<>();
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }

    /**
     * Trouve un locataire par son ID
     * @param id L'ID du locataire à rechercher
     * @return Le locataire ou null si non trouvé ou si ce n'est pas un locataire
     */
    public Utilisateur findById(long id) {
        if (id <= 0) {
            return null;
        }

        EntityManager em = JPAUtil.getEntityManager();
        try {
            Utilisateur utilisateur = em.find(Utilisateur.class, id);
            // Vérifier que l'utilisateur existe ET qu'il a bien le rôle LOCATAIRE
            if (utilisateur != null && utilisateur.getRole() == Role.LOCATAIRE) {
                return utilisateur;
            }
            return null;
        } catch (Exception e) {
            System.err.println("Erreur lors de la recherche du locataire " + id + ": " + e.getMessage());
            return null;
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }

    /**
     * Vérifie si un utilisateur est locataire chez un propriétaire spécifique
     * @param locataireId L'ID du locataire
     * @param proprietaireId L'ID du propriétaire
     * @return true si le locataire a un contrat actif chez ce propriétaire
     */
    public boolean estLocataireDuProprietaire(long locataireId, long proprietaireId) {
        if (locataireId <= 0 || proprietaireId <= 0) {
            return false;
        }

        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<Long> query = em.createQuery(
                    "SELECT COUNT(c) FROM Contrat c " +
                            "WHERE c.locataire.id = :locataireId " +
                            "AND c.unite.immeuble.proprietaire.id = :proprietaireId " +
                            "AND (c.dateFin IS NULL OR c.dateFin >= :dateActuelle)",
                    Long.class);
            query.setParameter("locataireId", locataireId);
            query.setParameter("proprietaireId", proprietaireId);
            query.setParameter("dateActuelle", LocalDate.now());

            Long result = query.getSingleResult();
            return result != null && result > 0;

        } catch (Exception e) {
            System.err.println("Erreur lors de la vérification locataire-propriétaire: " + e.getMessage());
            return false;
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }

    /**
     * Récupère tous les locataires d'un propriétaire (y compris anciens locataires)
     * @param proprietaireId L'ID du propriétaire
     * @return La liste de tous les locataires (actuels et passés)
     */
    public List<Utilisateur> getTousLesLocatairesDuProprietaire(long proprietaireId) {
        if (proprietaireId <= 0) {
            return new ArrayList<>();
        }

        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<Utilisateur> query = em.createQuery(
                    "SELECT DISTINCT c.locataire FROM Contrat c " +
                            "WHERE c.unite.immeuble.proprietaire.id = :proprietaireId " +
                            "ORDER BY c.locataire.nom, c.locataire.prenom",
                    Utilisateur.class);
            query.setParameter("proprietaireId", proprietaireId);

            return query.getResultList();

        } catch (Exception e) {
            System.err.println("Erreur lors de la récupération de tous les locataires du propriétaire " +
                    proprietaireId + ": " + e.getMessage());
            return new ArrayList<>();
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }

    /**
     * Compte le nombre total d'utilisateurs ayant le rôle LOCATAIRE dans le système
     * @return Le nombre total de locataires dans le système
     */
    public long compterTousLesLocataires(long proprietaireId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<Long> query = em.createQuery(
                    "SELECT COUNT(u) FROM Utilisateur u WHERE u.role = :role",
                    Long.class);
            query.setParameter("role", Role.LOCATAIRE);

            Long result = query.getSingleResult();
            return result != null ? result : 0;

        } catch (Exception e) {
            System.err.println("Erreur lors du comptage de tous les locataires: " + e.getMessage());
            return 0;
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }

    /**
     * Recherche des locataires par nom ou prénom (pour fonctionnalité de recherche)
     * @param searchTerm Le terme de recherche
     * @return La liste des locataires correspondant au critère de recherche
     */
    public List<Utilisateur> rechercherLocataires(String searchTerm) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return new ArrayList<>();
        }

        EntityManager em = JPAUtil.getEntityManager();
        try {
            String searchPattern = "%" + searchTerm.trim().toLowerCase() + "%";

            TypedQuery<Utilisateur> query = em.createQuery(
                    "SELECT u FROM Utilisateur u " +
                            "WHERE u.role = :role " +
                            "AND (LOWER(u.nom) LIKE :searchTerm " +
                            "OR LOWER(u.prenom) LIKE :searchTerm " +
                            "OR LOWER(u.email) LIKE :searchTerm) " +
                            "ORDER BY u.nom, u.prenom",
                    Utilisateur.class);
            query.setParameter("role", Role.LOCATAIRE);
            query.setParameter("searchTerm", searchPattern);

            return query.getResultList();

        } catch (Exception e) {
            System.err.println("Erreur lors de la recherche de locataires avec le terme '" +
                    searchTerm + "': " + e.getMessage());
            return new ArrayList<>();
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }
}