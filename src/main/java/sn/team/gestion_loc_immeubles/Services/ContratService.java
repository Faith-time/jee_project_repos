package sn.team.gestion_loc_immeubles.Services;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import sn.team.gestion_loc_immeubles.DAO.ContratDAO;
import sn.team.gestion_loc_immeubles.DAO.ContratDAOImpl;
import sn.team.gestion_loc_immeubles.Entities.Contrat;
import sn.team.gestion_loc_immeubles.Entities.Unite;
import sn.team.gestion_loc_immeubles.Entities.Utilisateur;
import sn.team.gestion_loc_immeubles.Util.JPAUtil;

import java.time.LocalDate;
import java.util.List;

public class ContratService {

    private final ContratDAO contratDAO;

    public ContratService() {
        this.contratDAO = new ContratDAOImpl();
    }

    /**
     * Créer un nouveau contrat
     */
    public void creerContrat(Contrat contrat) {
        contratDAO.save(contrat);
    }

    /**
     * Créer automatiquement un contrat lors de la location d'une unité
     */
    public Contrat creerContratLocation(Long locataireId, Long uniteId, Double montantLoyer) {
        EntityManager em = null;
        try {
            em = JPAUtil.getEntityManager();
            em.getTransaction().begin();

            // Récupérer le locataire et l'unité
            Utilisateur locataire = em.find(Utilisateur.class, locataireId);
            Unite unite = em.find(Unite.class, uniteId);

            if (locataire == null || unite == null) {
                em.getTransaction().rollback();
                throw new IllegalArgumentException("Locataire ou unité introuvable");
            }

            // Créer le contrat
            Contrat contrat = new Contrat();
            contrat.setDateDebut(LocalDate.now()); // Date d'aujourd'hui
            contrat.setDateFin(LocalDate.now().plusYears(1)); // Dans un an
            contrat.setMontant(montantLoyer); // Loyer de l'unité
            contrat.setLocataire(locataire);
            contrat.setUnite(unite);

            // Persister le contrat
            em.persist(contrat);
            em.getTransaction().commit();

            return contrat;

        } catch (Exception e) {
            if (em != null && em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            System.err.println("Erreur lors de la création du contrat: " + e.getMessage());
            throw new RuntimeException("Impossible de créer le contrat: " + e.getMessage(), e);
        } finally {
            if (em != null) {
                JPAUtil.closeEntityManager();
            }
        }
    }

    /**
     * Rechercher un contrat par ID
     */
    public Contrat trouverParId(Long id) {
        return contratDAO.findById(id);
    }

    /**
     * Lister tous les contrats
     */
    public List<Contrat> listerContrats() {
        return contratDAO.findAll();
    }

    /**
     * Lister les contrats d'un locataire
     */
    public List<Contrat> listerParLocataire(Long locataireId) {
        return contratDAO.findByLocataire(locataireId);
    }

    /**
     * Lister les contrats d'une unité
     */
    public List<Contrat> listerParUnite(Long uniteId) {
        return contratDAO.findByUnite(uniteId);
    }

    /**
     * Vérifier si une unité a déjà un contrat actif
     */
    public boolean uniteAContratActif(Long uniteId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em = JPAUtil.getEntityManager();
            LocalDate aujourdhui = LocalDate.now();

            Long count = em.createQuery(
                            "SELECT COUNT(c) FROM Contrat c " +
                                    "WHERE c.unite.id = :uniteId " +
                                    "AND c.dateDebut <= :aujourdhui " +
                                    "AND c.dateFin >= :aujourdhui",
                            Long.class)
                    .setParameter("uniteId", uniteId)
                    .setParameter("aujourdhui", aujourdhui)
                    .getSingleResult();

            return count > 0;
        } catch (Exception e) {
            System.err.println("Erreur lors de la vérification du contrat actif: " + e.getMessage());
            return false;
        } finally {
            if (em != null) {
                JPAUtil.closeEntityManager();
            }
        }
    }

    /**
     * Obtenir le contrat actif d'une unité
     */
    public Contrat obtenirContratActif(Long uniteId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em = JPAUtil.getEntityManager();
            LocalDate aujourdhui = LocalDate.now();

            return em.createQuery(
                            "SELECT c FROM Contrat c " +
                                    "WHERE c.unite.id = :uniteId " +
                                    "AND c.dateDebut <= :aujourdhui " +
                                    "AND c.dateFin >= :aujourdhui",
                            Contrat.class)
                    .setParameter("uniteId", uniteId)
                    .setParameter("aujourdhui", aujourdhui)
                    .getSingleResult();

        } catch (NoResultException e) {
            return null; // Aucun contrat actif
        } catch (Exception e) {
            System.err.println("Erreur lors de la récupération du contrat actif: " + e.getMessage());
            return null;
        } finally {
            if (em != null) {
                JPAUtil.closeEntityManager();
            }
        }
    }

    /**
     * Mettre à jour un contrat
     */
    public void mettreAJourContrat(Contrat contrat) {
        contratDAO.update(contrat);
    }

    /**
     * Supprimer un contrat
     */
    public void supprimerContrat(Long id) {
        contratDAO.delete(id);
    }

    /**
     * Compter les contrats d'un locataire
     */
    public long compterContratsByLocataire(Long locataireId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em = JPAUtil.getEntityManager();
            return em.createQuery(
                            "SELECT COUNT(c) FROM Contrat c WHERE c.locataire.id = :locataireId",
                            Long.class)
                    .setParameter("locataireId", locataireId)
                    .getSingleResult();
        } catch (NoResultException e) {
            return 0;
        } catch (Exception e) {
            System.err.println("Erreur lors du comptage des contrats du locataire: " + e.getMessage());
            return 0;
        } finally {
            if (em != null) {
                JPAUtil.closeEntityManager();
            }
        }
    }

    /**
     * Compter tous les contrats
     */
    public long compterTousContrats() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em = JPAUtil.getEntityManager();
            return em.createQuery("SELECT COUNT(c) FROM Contrat c", Long.class)
                    .getSingleResult();
        } catch (NoResultException e) {
            return 0;
        } catch (Exception e) {
            System.err.println("Erreur lors du comptage des contrats: " + e.getMessage());
            return 0;
        } finally {
            if (em != null) {
                JPAUtil.closeEntityManager();
            }
        }
    }

    /**
     * Compter les contrats actifs
     */
    public long compterContratsActifs() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em = JPAUtil.getEntityManager();
            LocalDate aujourdhui = LocalDate.now();

            return em.createQuery(
                            "SELECT COUNT(c) FROM Contrat c " +
                                    "WHERE c.dateDebut <= :aujourdhui AND c.dateFin >= :aujourdhui",
                            Long.class)
                    .setParameter("aujourdhui", aujourdhui)
                    .getSingleResult();
        } catch (NoResultException e) {
            return 0;
        } catch (Exception e) {
            System.err.println("Erreur lors du comptage des contrats actifs: " + e.getMessage());
            return 0;
        } finally {
            if (em != null) {
                JPAUtil.closeEntityManager();
            }
        }
    }

    /**
     * Lister les contrats expirés
     */
    public List<Contrat> listerContratsExpires() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em = JPAUtil.getEntityManager();
            LocalDate aujourdhui = LocalDate.now();

            return em.createQuery(
                            "SELECT c FROM Contrat c WHERE c.dateFin < :aujourdhui " +
                                    "ORDER BY c.dateFin DESC",
                            Contrat.class)
                    .setParameter("aujourdhui", aujourdhui)
                    .getResultList();
        } catch (Exception e) {
            System.err.println("Erreur lors de la récupération des contrats expirés: " + e.getMessage());
            return List.of();
        } finally {
            if (em != null) {
                JPAUtil.closeEntityManager();
            }
        }
    }

    /**
     * Lister les contrats qui expirent bientôt (dans les 30 jours)
     */
    public List<Contrat> listerContratsExpirantBientot() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em = JPAUtil.getEntityManager();
            LocalDate aujourdhui = LocalDate.now();
            LocalDate dans30Jours = aujourdhui.plusDays(30);

            return em.createQuery(
                            "SELECT c FROM Contrat c " +
                                    "WHERE c.dateFin BETWEEN :aujourdhui AND :dans30Jours " +
                                    "ORDER BY c.dateFin ASC",
                            Contrat.class)
                    .setParameter("aujourdhui", aujourdhui)
                    .setParameter("dans30Jours", dans30Jours)
                    .getResultList();
        } catch (Exception e) {
            System.err.println("Erreur lors de la récupération des contrats expirant bientôt: " + e.getMessage());
            return List.of();
        } finally {
            if (em != null) {
                JPAUtil.closeEntityManager();
            }
        }
    }
}