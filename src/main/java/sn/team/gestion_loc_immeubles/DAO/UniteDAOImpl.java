package sn.team.gestion_loc_immeubles.DAO;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import sn.team.gestion_loc_immeubles.Entities.Unite;

import java.util.List;

public class UniteDAOImpl extends GenericDAOImpl<Unite> implements UniteDAO {

    public UniteDAOImpl() {
        super(Unite.class);
    }

    @Override
    public List<Unite> findByImmeuble(Long immeubleId) {
        EntityManager em = getEntityManager();
        TypedQuery<Unite> query = em.createQuery(
                "SELECT u FROM Unite u WHERE u.immeuble.id = :iid", Unite.class);
        query.setParameter("iid", immeubleId);
        return query.getResultList();
    }
}
