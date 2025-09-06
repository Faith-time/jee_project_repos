package sn.team.gestion_loc_immeubles.Services;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
import sn.team.gestion_loc_immeubles.Entities.Role;
import sn.team.gestion_loc_immeubles.Entities.Utilisateur;
import sn.team.gestion_loc_immeubles.Util.JPAUtil;

import java.util.List;

public class UtilisateurService {

    /**
     * Créer un nouvel utilisateur
     */
    public void creerUtilisateur(Utilisateur utilisateur) {
        EntityManager em = null;
        try {
            System.out.println("=== UtilisateurService.creerUtilisateur() ===");
            System.out.println("Utilisateur à créer: " + utilisateur);

            em = JPAUtil.getEntityManager();
            JPAUtil.beginTransaction();

            // Persister l'utilisateur
            em.persist(utilisateur);

            // Commit de la transaction
            JPAUtil.commitTransaction();

            System.out.println("Utilisateur créé avec succès. ID: " + utilisateur.getId());

        } catch (Exception e) {
            System.err.println("Erreur lors de la création de l'utilisateur:");
            e.printStackTrace();

            // Rollback en cas d'erreur
            if (em != null && em.getTransaction().isActive()) {
                JPAUtil.rollbackTransaction();
            }

            throw new RuntimeException("Erreur lors de la création de l'utilisateur: " + e.getMessage(), e);
        } finally {
            JPAUtil.closeEntityManager();
        }
    }

    /**
     * Rechercher un utilisateur par ID
     */
    public Utilisateur trouverParId(Long id) {
        EntityManager em = null;
        try {
            em = JPAUtil.getEntityManager();
            return em.find(Utilisateur.class, id);
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la recherche par ID: " + e.getMessage(), e);
        } finally {
            if (em != null) {
                JPAUtil.closeEntityManager();
            }
        }
    }

    /**
     * Rechercher un utilisateur par email
     */
    public Utilisateur trouverParEmail(String email) {
        EntityManager em = null;
        try {
            em = JPAUtil.getEntityManager();
            TypedQuery<Utilisateur> query = em.createQuery(
                    "SELECT u FROM Utilisateur u WHERE u.email = :email",
                    Utilisateur.class);
            query.setParameter("email", email);

            return query.getSingleResult();
        } catch (NoResultException e) {
            return null; // Aucun utilisateur trouvé
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la recherche par email: " + e.getMessage(), e);
        } finally {
            if (em != null) {
                JPAUtil.closeEntityManager();
            }
        }
    }

    /**
     * Lister tous les utilisateurs
     */
    public List<Utilisateur> getTousLesUtilisateurs() {
        EntityManager em = null;
        try {
            System.out.println("=== UtilisateurService.getTousLesUtilisateurs() ===");

            em = JPAUtil.getEntityManager();
            TypedQuery<Utilisateur> query = em.createQuery(
                    "SELECT u FROM Utilisateur u ORDER BY u.nom, u.prenom",
                    Utilisateur.class);

            List<Utilisateur> utilisateurs = query.getResultList();
            System.out.println("Nombre d'utilisateurs trouvés: " + utilisateurs.size());

            return utilisateurs;
        } catch (Exception e) {
            System.err.println("Erreur lors de la récupération des utilisateurs:");
            e.printStackTrace();
            throw new RuntimeException("Erreur lors de la récupération des utilisateurs: " + e.getMessage(), e);
        } finally {
            if (em != null) {
                JPAUtil.closeEntityManager();
            }
        }
    }

    /**
     * Lister les utilisateurs par rôle
     */
    public List<Utilisateur> getUtilisateursByRole(Role role) {
        EntityManager em = null;
        try {
            em = JPAUtil.getEntityManager();
            TypedQuery<Utilisateur> query = em.createQuery(
                    "SELECT u FROM Utilisateur u WHERE u.role = :role ORDER BY u.nom, u.prenom",
                    Utilisateur.class);
            query.setParameter("role", role);

            return query.getResultList();
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la récupération des utilisateurs avec rôle " + role + ": " + e.getMessage(), e);
        } finally {
            if (em != null) {
                JPAUtil.closeEntityManager();
            }
        }
    }

    /**
     * Mettre à jour un utilisateur existant
     */
    public void mettreAJourUtilisateur(Utilisateur utilisateur) {
        EntityManager em = null;
        try {
            System.out.println("=== UtilisateurService.mettreAJourUtilisateur() ===");
            System.out.println("Utilisateur à modifier: " + utilisateur);

            em = JPAUtil.getEntityManager();
            JPAUtil.beginTransaction();

            // Merge pour mettre à jour
            em.merge(utilisateur);

            JPAUtil.commitTransaction();

            System.out.println("Utilisateur mis à jour avec succès");

        } catch (Exception e) {
            System.err.println("Erreur lors de la mise à jour de l'utilisateur:");
            e.printStackTrace();

            if (em != null && em.getTransaction().isActive()) {
                JPAUtil.rollbackTransaction();
            }

            throw new RuntimeException("Erreur lors de la mise à jour de l'utilisateur: " + e.getMessage(), e);
        } finally {
            JPAUtil.closeEntityManager();
        }
    }

    /**
     * Supprimer un utilisateur par ID
     */
    public void supprimerUtilisateur(Long id) {
        EntityManager em = null;
        try {
            System.out.println("=== UtilisateurService.supprimerUtilisateur() ===");
            System.out.println("ID utilisateur à supprimer: " + id);

            em = JPAUtil.getEntityManager();
            JPAUtil.beginTransaction();

            Utilisateur utilisateur = em.find(Utilisateur.class, id);
            if (utilisateur != null) {
                em.remove(utilisateur);
                System.out.println("Utilisateur supprimé: " + utilisateur.getNomComplet());
            } else {
                System.out.println("Utilisateur avec ID " + id + " non trouvé");
            }

            JPAUtil.commitTransaction();

        } catch (Exception e) {
            System.err.println("Erreur lors de la suppression de l'utilisateur:");
            e.printStackTrace();

            if (em != null && em.getTransaction().isActive()) {
                JPAUtil.rollbackTransaction();
            }

            throw new RuntimeException("Erreur lors de la suppression de l'utilisateur: " + e.getMessage(), e);
        } finally {
            JPAUtil.closeEntityManager();
        }
    }

    /**
     * Compter tous les utilisateurs
     */
    public long compterTousUtilisateurs() {
        EntityManager em = null;
        try {
            em = JPAUtil.getEntityManager();
            TypedQuery<Long> query = em.createQuery("SELECT COUNT(u) FROM Utilisateur u", Long.class);
            return query.getSingleResult();
        } catch (NoResultException e) {
            return 0;
        } catch (Exception e) {
            System.err.println("Erreur lors du comptage des utilisateurs: " + e.getMessage());
            return 0;
        } finally {
            if (em != null) {
                JPAUtil.closeEntityManager();
            }
        }
    }

    /**
     * Compter les utilisateurs par rôle
     */
    public long compterParRole(Role role) {
        EntityManager em = null;
        try {
            em = JPAUtil.getEntityManager();
            TypedQuery<Long> query = em.createQuery(
                    "SELECT COUNT(u) FROM Utilisateur u WHERE u.role = :role",
                    Long.class);
            query.setParameter("role", role);
            return query.getSingleResult();
        } catch (NoResultException e) {
            return 0;
        } catch (Exception e) {
            System.err.println("Erreur lors du comptage des utilisateurs avec rôle " + role + ": " + e.getMessage());
            return 0;
        } finally {
            if (em != null) {
                JPAUtil.closeEntityManager();
            }
        }
    }

    /**
     * Authentification basique
     */
    public Utilisateur connexion(String email, String motDePasse) {
        try {
            Utilisateur utilisateur = trouverParEmail(email);
            if (utilisateur != null && utilisateur.getMotDePasse().equals(motDePasse)) {
                return utilisateur;
            }
            return null;
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la connexion: " + e.getMessage(), e);
        }
    }

    /**
     * Vérifier si un email existe déjà
     */
    public boolean emailExiste(String email) {
        try {
            Utilisateur utilisateur = trouverParEmail(email);
            return utilisateur != null;
        } catch (Exception e) {
            System.err.println("Erreur lors de la vérification de l'email: " + e.getMessage());
            return false;
        }
    }
}