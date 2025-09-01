package sn.team.gestion_loc_immeubles.Services;

import sn.team.gestion_loc_immeubles.DAO.ImmeubleDAO;
import sn.team.gestion_loc_immeubles.DAO.ImmeubleDAOImpl;
import sn.team.gestion_loc_immeubles.Entities.Immeuble;

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
     * Lister les immeubles d’un propriétaire
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
}
