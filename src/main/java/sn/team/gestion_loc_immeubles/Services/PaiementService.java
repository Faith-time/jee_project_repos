package sn.team.gestion_loc_immeubles.Services;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import sn.team.gestion_loc_immeubles.DAO.PaiementDAO;
import sn.team.gestion_loc_immeubles.DAO.PaiementDAOImpl;
import sn.team.gestion_loc_immeubles.Entities.Paiement;
import sn.team.gestion_loc_immeubles.Util.JPAUtil;

import java.math.BigDecimal;
import java.util.List;

public class PaiementService {

    private final PaiementDAO paiementDAO;

    public PaiementService() {
        this.paiementDAO = new PaiementDAOImpl();
    }

    /**
     * Enregistrer un paiement
     */
    public void creerPaiement(Paiement paiement) {
        paiementDAO.save(paiement);
    }

    /**
     * Rechercher un paiement par ID
     */
    public Paiement trouverParId(Long id) {
        return paiementDAO.findById(id);
    }

    /**
     * Lister tous les paiements
     */
    public List<Paiement> listerPaiements() {
        return paiementDAO.findAll();
    }

    /**
     * Lister les paiements d’un contrat
     */
    public List<Paiement> listerParContrat(Long contratId) {
        return paiementDAO.findByContrat(contratId);
    }

    /**
     * Lister les paiements selon un statut
     */
    public List<Paiement> listerParStatut(Paiement.StatutPaiement statut) {
        return paiementDAO.findByStatut(statut);
    }

    /**
     * Mettre à jour un paiement
     */
    public void mettreAJourPaiement(Paiement paiement) {
        paiementDAO.update(paiement);
    }

    /**
     * Supprimer un paiement
     */
    public void supprimerPaiement(Long id) {
        paiementDAO.delete(id);
    }

    /**
     * Compter les paiements d'un propriétaire
     */
    public long compterPaiementsByProprietaire(Long proprietaireId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em = JPAUtil.getEntityManager();
            return em.createQuery(
                            "SELECT COUNT(p) FROM Paiement p " +
                                    "WHERE p.contrat.unite.immeuble.proprietaire.id = :proprietaireId",
                            Long.class)
                    .setParameter("proprietaireId", proprietaireId)
                    .getSingleResult();
        } catch (NoResultException e) {
            return 0;
        } catch (Exception e) {
            System.err.println("Erreur lors du comptage des paiements du propriétaire: " + e.getMessage());
            return 0;
        } finally {
            if (em != null) {
                JPAUtil.closeEntityManager();
            }
        }
    }

    /**
     * Compter les paiements payés d'un locataire
     */
    public long compterPaiementsPayesByLocataire(Long locataireId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em = JPAUtil.getEntityManager();
            return em.createQuery(
                            "SELECT COUNT(p) FROM Paiement p " +
                                    "WHERE p.contrat.locataire.id = :locataireId " +
                                    "AND p.statut = :statut",
                            Long.class)
                    .setParameter("locataireId", locataireId)
                    .setParameter("statut", Paiement.StatutPaiement.PAYE)
                    .getSingleResult();
        } catch (NoResultException e) {
            return 0;
        } catch (Exception e) {
            System.err.println("Erreur lors du comptage des paiements payés: " + e.getMessage());
            return 0;
        } finally {
            if (em != null) {
                JPAUtil.closeEntityManager();
            }
        }
    }

    /**
     * Compter les paiements en attente d'un locataire
     */
    public long compterPaiementsEnAttenteByLocataire(Long locataireId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em = JPAUtil.getEntityManager();
            return em.createQuery(
                            "SELECT COUNT(p) FROM Paiement p " +
                                    "WHERE p.contrat.locataire.id = :locataireId " +
                                    "AND p.statut = :statut",
                            Long.class)
                    .setParameter("locataireId", locataireId)
                    .setParameter("statut", Paiement.StatutPaiement.EN_ATTENTE)
                    .getSingleResult();
        } catch (NoResultException e) {
            return 0;
        } catch (Exception e) {
            System.err.println("Erreur lors du comptage des paiements en attente: " + e.getMessage());
            return 0;
        } finally {
            if (em != null) {
                JPAUtil.closeEntityManager();
            }
        }
    }

    /**
     * Compter les paiements en retard d'un locataire
     */
    public long compterPaiementsEnRetardByLocataire(Long locataireId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em = JPAUtil.getEntityManager();
            return em.createQuery(
                            "SELECT COUNT(p) FROM Paiement p " +
                                    "WHERE p.contrat.locataire.id = :locataireId " +
                                    "AND p.statut = :statut",
                            Long.class)
                    .setParameter("locataireId", locataireId)
                    .setParameter("statut", Paiement.StatutPaiement.EN_RETARD)
                    .getSingleResult();
        } catch (NoResultException e) {
            return 0;
        } catch (Exception e) {
            System.err.println("Erreur lors du comptage des paiements en retard: " + e.getMessage());
            return 0;
        } finally {
            if (em != null) {
                JPAUtil.closeEntityManager();
            }
        }
    }

    /**
     * Calculer le solde restant d'un locataire (montant total des paiements en attente)
     */
    public BigDecimal calculerSoldeRestantByLocataire(Long locataireId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em = JPAUtil.getEntityManager();
            Double result = em.createQuery(
                            "SELECT COALESCE(SUM(p.montant), 0.0) FROM Paiement p " +
                                    "WHERE p.contrat.locataire.id = :locataireId " +
                                    "AND p.statut = :statut",
                            Double.class)
                    .setParameter("locataireId", locataireId)
                    .setParameter("statut", Paiement.StatutPaiement.EN_ATTENTE)
                    .getSingleResult();

            return result != null ? BigDecimal.valueOf(result) : BigDecimal.ZERO;
        } catch (NoResultException e) {
            return BigDecimal.ZERO;
        } catch (Exception e) {
            System.err.println("Erreur lors du calcul du solde restant: " + e.getMessage());
            return BigDecimal.ZERO;
        } finally {
            if (em != null) {
                JPAUtil.closeEntityManager();
            }
        }
    }

    /**
     * Calculer le montant total des paiements payés d'un locataire
     */
    public BigDecimal calculerMontantTotalPayeByLocataire(Long locataireId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em = JPAUtil.getEntityManager();
            Double result = em.createQuery(
                            "SELECT COALESCE(SUM(p.montant), 0.0) FROM Paiement p " +
                                    "WHERE p.contrat.locataire.id = :locataireId " +
                                    "AND p.statut = :statut",
                            Double.class)
                    .setParameter("locataireId", locataireId)
                    .setParameter("statut", Paiement.StatutPaiement.PAYE)
                    .getSingleResult();

            return result != null ? BigDecimal.valueOf(result) : BigDecimal.ZERO;
        } catch (NoResultException e) {
            return BigDecimal.ZERO;
        } catch (Exception e) {
            System.err.println("Erreur lors du calcul du montant total payé: " + e.getMessage());
            return BigDecimal.ZERO;
        } finally {
            if (em != null) {
                JPAUtil.closeEntityManager();
            }
        }
    }

    /**
     * Calculer le revenu total d'un propriétaire (paiements reçus)
     */
    public BigDecimal calculerRevenuTotalByProprietaire(Long proprietaireId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em = JPAUtil.getEntityManager();
            Double result = em.createQuery(
                            "SELECT COALESCE(SUM(p.montant), 0.0) FROM Paiement p " +
                                    "WHERE p.contrat.unite.immeuble.proprietaire.id = :proprietaireId " +
                                    "AND p.statut = :statut",
                            Double.class)
                    .setParameter("proprietaireId", proprietaireId)
                    .setParameter("statut", Paiement.StatutPaiement.PAYE)
                    .getSingleResult();

            return result != null ? BigDecimal.valueOf(result) : BigDecimal.ZERO;
        } catch (NoResultException e) {
            return BigDecimal.ZERO;
        } catch (Exception e) {
            System.err.println("Erreur lors du calcul du revenu total: " + e.getMessage());
            return BigDecimal.ZERO;
        } finally {
            if (em != null) {
                JPAUtil.closeEntityManager();
            }
        }
    }

    /**
     * Compter tous les paiements
     */
    public long compterTousPaiements() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em = JPAUtil.getEntityManager();
            return em.createQuery("SELECT COUNT(p) FROM Paiement p", Long.class)
                    .getSingleResult();
        } catch (NoResultException e) {
            return 0;
        } catch (Exception e) {
            System.err.println("Erreur lors du comptage des paiements: " + e.getMessage());
            return 0;
        } finally {
            if (em != null) {
                JPAUtil.closeEntityManager();
            }
        }
    }
}
