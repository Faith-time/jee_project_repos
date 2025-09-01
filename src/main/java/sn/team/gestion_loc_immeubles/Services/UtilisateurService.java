package sn.team.gestion_loc_immeubles.Services;

import sn.team.gestion_loc_immeubles.DAO.UtilisateurDAO;
import sn.team.gestion_loc_immeubles.DAO.UtilisateurDAOImpl;
import sn.team.gestion_loc_immeubles.Entities.Utilisateur;
import sn.team.gestion_loc_immeubles.Entities.Locataire;
import sn.team.gestion_loc_immeubles.Entities.Proprietaire;
import sn.team.gestion_loc_immeubles.Util.JPAUtil;

import java.util.List;

public class UtilisateurService {

    private final UtilisateurDAO utilisateurDAO;

    public UtilisateurService() {
        this.utilisateurDAO = new UtilisateurDAOImpl();
    }

    /**
     * Enregistrer un nouvel utilisateur
     */
    public void creerUtilisateur(Utilisateur utilisateur) {
        try {
            utilisateurDAO.save(utilisateur);
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la création de l'utilisateur: " + e.getMessage(), e);
        } finally {
            JPAUtil.closeEntityManager();
        }
    }

    /**
     * Rechercher un utilisateur par ID
     */
    public Utilisateur trouverParId(Long id) {
        try {
            return utilisateurDAO.findById(id);
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la recherche par ID: " + e.getMessage(), e);
        } finally {
            JPAUtil.closeEntityManager();
        }
    }

    /**
     * Rechercher un utilisateur par email
     */
    public Utilisateur trouverParEmail(String email) {
        try {
            return utilisateurDAO.findByEmail(email);
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la recherche par email: " + e.getMessage(), e);
        } finally {
            JPAUtil.closeEntityManager();
        }
    }

    /**
     * Lister tous les utilisateurs
     */
    public List<Utilisateur> listerUtilisateurs() {
        try {
            return utilisateurDAO.findAll();
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la récupération des utilisateurs: " + e.getMessage(), e);
        } finally {
            JPAUtil.closeEntityManager();
        }
    }

    /**
     * Lister tous les locataires
     */
    public List<Locataire> listerLocataires() {
        try {
            return utilisateurDAO.findAllLocataires();
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la récupération des locataires: " + e.getMessage(), e);
        } finally {
            JPAUtil.closeEntityManager();
        }
    }

    /**
     * Lister tous les propriétaires
     */
    public List<Proprietaire> listerProprietaires() {
        try {
            return utilisateurDAO.findAllProprietaires();
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la récupération des propriétaires: " + e.getMessage(), e);
        } finally {
            JPAUtil.closeEntityManager();
        }
    }

    /**
     * Mettre à jour un utilisateur existant
     */
    public void mettreAJourUtilisateur(Utilisateur utilisateur) {
        try {
            utilisateurDAO.update(utilisateur);
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la mise à jour de l'utilisateur: " + e.getMessage(), e);
        } finally {
            JPAUtil.closeEntityManager();
        }
    }

    /**
     * Supprimer un utilisateur par ID
     */
    public void supprimerUtilisateur(Long id) {
        try {
            utilisateurDAO.delete(id);
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la suppression de l'utilisateur: " + e.getMessage(), e);
        } finally {
            JPAUtil.closeEntityManager();
        }
    }
}