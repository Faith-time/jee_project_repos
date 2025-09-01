package sn.team.gestion_loc_immeubles.DAO;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import sn.team.gestion_loc_immeubles.Entities.Paiement;

import java.util.List;

public class PaiementDAOImpl extends GenericDAOImpl<Paiement> implements PaiementDAO {

    public PaiementDAOImpl() {
        super(Paiement.class);
    }

    @Override
    public List<Paiement> findByContrat(Long contratId) {
        EntityManager em = getEntityManager();
        TypedQuery<Paiement> query = em.createQuery(
                "SELECT p FROM Paiement p WHERE p.contrat.id = :cid", Paiement.class);
        query.setParameter("cid", contratId);
        return query.getResultList();
    }

    @Override
    public List<Paiement> findByStatut(Paiement.StatutPaiement statut) {
        EntityManager em = getEntityManager();

        TypedQuery<Paiement> query = em.createQuery(
                "SELECT p FROM Paiement p WHERE p.statut = :statut", Paiement.class);
        query.setParameter("statut", statut);
        return query.getResultList();
    }
}
