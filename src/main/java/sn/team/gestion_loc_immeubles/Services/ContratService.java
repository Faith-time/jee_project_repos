package sn.team.gestion_loc_immeubles.Services;

import sn.team.gestion_loc_immeubles.DAO.ContratDAO;
import sn.team.gestion_loc_immeubles.DAO.ContratDAOImpl;
import sn.team.gestion_loc_immeubles.Entities.Contrat;

import java.util.List;

public class ContratService {

    private final ContratDAO contratDAO;

    public ContratService() {
        this.contratDAO = new ContratDAOImpl() {
            @Override
            public List<Contrat> findAll() {
                return List.of();
            }
        };
    }

    /**
     * Créer un nouveau contrat
     */
    public void creerContrat(Contrat contrat) {
        contratDAO.save(contrat);
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
}
