package sn.team.gestion_loc_immeubles.Services;

import sn.team.gestion_loc_immeubles.DAO.PaiementDAO;
import sn.team.gestion_loc_immeubles.DAO.PaiementDAOImpl;
import sn.team.gestion_loc_immeubles.Entities.Paiement;

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
}
