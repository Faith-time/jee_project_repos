package sn.team.gestion_loc_immeubles.Services;

import sn.team.gestion_loc_immeubles.DAO.UniteDAO;
import sn.team.gestion_loc_immeubles.DAO.UniteDAOImpl;
import sn.team.gestion_loc_immeubles.Entities.Unite;

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
     * Lister les unités d’un immeuble
     */
    public List<Unite> listerParImmeuble(Long immeubleId) {
        return uniteDAO.findByImmeuble(immeubleId);
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
}
