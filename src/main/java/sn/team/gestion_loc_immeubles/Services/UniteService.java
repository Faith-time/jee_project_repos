package sn.team.gestion_loc_immeubles.Services;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
import sn.team.gestion_loc_immeubles.DAO.UniteDAO;
import sn.team.gestion_loc_immeubles.DAO.UniteDAOImpl;
import sn.team.gestion_loc_immeubles.Entities.Unite;
import sn.team.gestion_loc_immeubles.Entities.Contrat;
import sn.team.gestion_loc_immeubles.Entities.Utilisateur;
import sn.team.gestion_loc_immeubles.Util.JPAUtil;
import java.time.LocalDate;

import java.util.List;


public class UniteService {

    private final UniteDAO uniteDAO;

    public UniteService() {
        this.uniteDAO = new UniteDAOImpl();
    }

    /**
     * Enregistrer une unité
     */
    public void creerUnite(Unite unite) {
        uniteDAO.save(unite);
    }

    /**
     * Rechercher une unité par ID
     */
    public Unite trouverParId(Long id) {
        return uniteDAO.findById(id);
    }

    /**
     * Lister toutes les unités
     */
    public List<Unite> listerUnites() {
        return uniteDAO.findAll();
    }

    /**
     * Lister les unités disponibles seulement
     */
    public List<Unite> listerUnitesDisponibles() {
        EntityManager em = null;
        try {
            em = JPAUtil.getEntityManager();
            return em.createQuery(
                            "SELECT u FROM Unite u WHERE u.statut = :statut ORDER BY u.immeuble.nom, u.numero",
                            Unite.class)
                    .setParameter("statut", Unite.StatutUnite.DISPONIBLE)
                    .getResultList();
        } catch (Exception e) {
            System.err.println("Erreur lors de la récupération des unités disponibles: " + e.getMessage());
            return List.of();
        } finally {
            if (em != null) {
                JPAUtil.closeEntityManager();
            }
        }
    }

    /**
     * Lister les unités d'un immeuble
     */
    public List<Unite> listerParImmeuble(Long immeubleId) {
        return uniteDAO.findByImmeuble(immeubleId);
    }

    /**
     * Louer une unité (changer le statut de DISPONIBLE à LOUEE)
     */
    public boolean louerUnite(Long uniteId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em = JPAUtil.getEntityManager();
            em.getTransaction().begin();

            Unite unite = em.find(Unite.class, uniteId);
            if (unite != null && unite.getStatut() == Unite.StatutUnite.DISPONIBLE) {
                unite.setStatut(Unite.StatutUnite.LOUEE);
                em.merge(unite);
                em.getTransaction().commit();
                return true;
            } else {
                em.getTransaction().rollback();
                return false;
            }
        } catch (Exception e) {
            if (em != null && em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            System.err.println("Erreur lors de la location de l'unité: " + e.getMessage());
            return false;
        } finally {
            if (em != null) {
                JPAUtil.closeEntityManager();
            }
        }
    }

    /**
     * Libérer une unité (changer le statut de LOUEE à DISPONIBLE)
     */
    public boolean libererUnite(Long uniteId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em = JPAUtil.getEntityManager();
            em.getTransaction().begin();

            Unite unite = em.find(Unite.class, uniteId);
            if (unite != null && unite.getStatut() == Unite.StatutUnite.LOUEE) {
                unite.setStatut(Unite.StatutUnite.DISPONIBLE);
                em.merge(unite);
                em.getTransaction().commit();
                return true;
            } else {
                em.getTransaction().rollback();
                return false;
            }
        } catch (Exception e) {
            if (em != null && em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            System.err.println("Erreur lors de la libération de l'unité: " + e.getMessage());
            return false;
        } finally {
            if (em != null) {
                JPAUtil.closeEntityManager();
            }
        }
    }

    /**
     * Mettre à jour une unité
     */
    public void mettreAJourUnite(Unite unite) {
        uniteDAO.update(unite);
    }

    /**
     * Supprimer une unité
     */
    public void supprimerUnite(Long id) {
        uniteDAO.delete(id);
    }

    /**
     * Compter les unités d'un propriétaire
     */
    public long compterUnitesByProprietaire(Long proprietaireId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em = JPAUtil.getEntityManager();
            return em.createQuery(
                            "SELECT COUNT(u) FROM Unite u " +
                                    "WHERE u.immeuble.proprietaire.id = :proprietaireId",
                            Long.class)
                    .setParameter("proprietaireId", proprietaireId)
                    .getSingleResult();
        } catch (NoResultException e) {
            return 0;
        } catch (Exception e) {
            System.err.println("Erreur lors du comptage des unités du propriétaire: " + e.getMessage());
            return 0;
        } finally {
            if (em != null) {
                JPAUtil.closeEntityManager();
            }
        }
    }

    /**
     * Compter toutes les unités
     */
    public long compterToutesUnites() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em = JPAUtil.getEntityManager();
            return em.createQuery("SELECT COUNT(u) FROM Unite u", Long.class)
                    .getSingleResult();
        } catch (NoResultException e) {
            return 0;
        } catch (Exception e) {
            System.err.println("Erreur lors du comptage des unités: " + e.getMessage());
            return 0;
        } finally {
            if (em != null) {
                JPAUtil.closeEntityManager();
            }
        }
    }

    /**
     * Compter les unités disponibles
     */
    public long compterUnitesDisponibles() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em = JPAUtil.getEntityManager();
            return em.createQuery(
                            "SELECT COUNT(u) FROM Unite u WHERE u.statut = :statut",
                            Long.class)
                    .setParameter("statut", Unite.StatutUnite.DISPONIBLE)
                    .getSingleResult();
        } catch (NoResultException e) {
            return 0;
        } catch (Exception e) {
            System.err.println("Erreur lors du comptage des unités disponibles: " + e.getMessage());
            return 0;
        } finally {
            if (em != null) {
                JPAUtil.closeEntityManager();
            }
        }
    }


    public long compterUnitesDisponiblesDuProprietaire(Long proprietaireId) {
        EntityManager em = null;
        try {
            em = JPAUtil.getEntityManager();
            return em.createQuery(
                            "SELECT COUNT(u) FROM Unite u " +
                                    "WHERE u.immeuble.proprietaire.id = :proprietaireId " +
                                    "AND u.statut = :statut",
                            Long.class)
                    .setParameter("proprietaireId", proprietaireId)
                    .setParameter("statut", Unite.StatutUnite.DISPONIBLE)
                    .getSingleResult();
        } catch (Exception e) {
            System.err.println("Erreur lors du comptage des unités disponibles du propriétaire: " + e.getMessage());
            e.printStackTrace();
            return 0;
        } finally {
            if (em != null) {
                JPAUtil.closeEntityManager();
            }
        }
    }

    /**
     * Compter les unités louées
     */
    public long compterUnitesLouees() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em = JPAUtil.getEntityManager();
            return em.createQuery(
                            "SELECT COUNT(u) FROM Unite u WHERE u.statut = :statut",
                            Long.class)
                    .setParameter("statut", Unite.StatutUnite.LOUEE)
                    .getSingleResult();
        } catch (NoResultException e) {
            return 0;
        } catch (Exception e) {
            System.err.println("Erreur lors du comptage des unités louées: " + e.getMessage());
            return 0;
        } finally {
            if (em != null) {
                JPAUtil.closeEntityManager();
            }
        }
    }

    /**
     * Compter les unités d'un immeuble
     */
    public long compterUnitesByImmeuble(Long immeubleId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em = JPAUtil.getEntityManager();
            return em.createQuery(
                            "SELECT COUNT(u) FROM Unite u WHERE u.immeuble.id = :immeubleId",
                            Long.class)
                    .setParameter("immeubleId", immeubleId)
                    .getSingleResult();
        } catch (NoResultException e) {
            return 0;
        } catch (Exception e) {
            System.err.println("Erreur lors du comptage des unités de l'immeuble: " + e.getMessage());
            return 0;
        } finally {
            if (em != null) {
                JPAUtil.closeEntityManager();
            }
        }
    }

    /**
     * Louer une unité et créer automatiquement un contrat d'un an
     */
    public boolean louerUniteAvecContrat(Long uniteId, Long utilisateurId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em = JPAUtil.getEntityManager();
            em.getTransaction().begin();

            // Récupérer l'unité et le locataire
            Unite unite = em.find(Unite.class, uniteId);
            Utilisateur locataire = em.find(Utilisateur.class, utilisateurId);

            if (unite == null || locataire == null) {
                em.getTransaction().rollback();
                return false;
            }

            // Vérifier que l'unité est disponible
            if (unite.getStatut() != Unite.StatutUnite.DISPONIBLE) {
                em.getTransaction().rollback();
                return false;
            }

            // Mettre à jour le statut de l'unité
            unite.setStatut(Unite.StatutUnite.LOUEE);
            em.merge(unite);

            // Créer un contrat (1 an à partir d'aujourd'hui)
            Contrat contrat = new Contrat();
            contrat.setUnite(unite);
            contrat.setLocataire(locataire);
            contrat.setDateDebut(LocalDate.now());
            contrat.setDateFin(LocalDate.now().plusYears(1));
            contrat.setMontant(unite.getLoyer()); // montant fixé au loyer de l'unité

            em.persist(contrat);

            em.getTransaction().commit();
            return true;

        } catch (Exception e) {
            if (em != null && em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            System.err.println("Erreur lors de la location avec contrat : " + e.getMessage());
            return false;
        } finally {
            if (em != null) {
                JPAUtil.closeEntityManager();
            }
        }
    }

    public int compterUnitesDuProprietaire(long proprietaireId) {
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
     * Récupère toutes les unités d'un propriétaire
     * @param proprietaireId L'ID du propriétaire
     * @return La liste des unités
     */
    public List<Unite> getUnitesDuProprietaire(long proprietaireId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<Unite> query = em.createQuery(
                    "SELECT u FROM Unite u WHERE u.immeuble.proprietaire.id = :proprietaireId ORDER BY u.immeuble.nom, u.numero",
                    Unite.class
            );
            query.setParameter("proprietaireId", proprietaireId);

            return query.getResultList();

        } catch (Exception e) {
            System.err.println("Erreur lors de la récupération des unités du propriétaire "
                    + proprietaireId + ": " + e.getMessage());
            return List.of();
        } finally {
            em.close();
        }
    }

    /**
     * Compte les unités disponibles d'un propriétaire
     * @param proprietaireId L'ID du propriétaire
     * @return Le nombre d'unités disponibles
     */
    public int compterUnitesDisponiblesDuProprietaire(long proprietaireId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<Long> query = em.createQuery(
                    "SELECT COUNT(u) FROM Unite u WHERE u.immeuble.proprietaire.id = :proprietaireId AND u.statut = :statut",
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
     * Compte les unités louées d'un propriétaire
     * @param proprietaireId L'ID du propriétaire
     * @return Le nombre d'unités louées
     */
    public int compterUnitesLoueesDuProprietaire(long proprietaireId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<Long> query = em.createQuery(
                    "SELECT COUNT(u) FROM Unite u WHERE u.immeuble.proprietaire.id = :proprietaireId AND u.statut = :statut",
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
     * Récupère les unités d'un immeuble spécifique
     * @param immeubleId L'ID de l'immeuble
     * @return La liste des unités
     */
    public List<Unite> getUnitesDuImmeuble(long immeubleId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<Unite> query = em.createQuery(
                    "SELECT u FROM Unite u WHERE u.immeuble.id = :immeubleId ORDER BY u.numero",
                    Unite.class
            );
            query.setParameter("immeubleId", immeubleId);

            return query.getResultList();

        } catch (Exception e) {
            System.err.println("Erreur lors de la récupération des unités de l'immeuble "
                    + immeubleId + ": " + e.getMessage());
            return List.of();
        } finally {
            em.close();
        }
    }


}