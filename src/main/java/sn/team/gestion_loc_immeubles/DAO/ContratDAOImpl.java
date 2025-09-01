package sn.team.gestion_loc_immeubles.DAO;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import sn.team.gestion_loc_immeubles.Entities.Contrat;

import java.util.List;

public class ContratDAOImpl extends GenericDAOImpl<Contrat> implements ContratDAO {

    public ContratDAOImpl() {
        super(Contrat.class);
    }

    @Override
    public List<Contrat> findByLocataire(Long locataireId) {
        EntityManager em = getEntityManager();

        TypedQuery<Contrat> query = em.createQuery(
                "SELECT c FROM Contrat c WHERE c.locataire.id = :lid", Contrat.class);
        query.setParameter("lid", locataireId);
        return query.getResultList();
    }

    @Override
    public List<Contrat> findByUnite(Long uniteId) {
        EntityManager em = getEntityManager();

        TypedQuery<Contrat> query = em.createQuery(
                "SELECT c FROM Contrat c WHERE c.unite.id = :uid", Contrat.class);
        query.setParameter("uid", uniteId);
        return query.getResultList();
    }
}
