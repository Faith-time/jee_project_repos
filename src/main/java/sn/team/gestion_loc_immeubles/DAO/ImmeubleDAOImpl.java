package sn.team.gestion_loc_immeubles.DAO;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import sn.team.gestion_loc_immeubles.Entities.Immeuble;

import java.util.List;

public class ImmeubleDAOImpl extends GenericDAOImpl<Immeuble> implements ImmeubleDAO {

    public ImmeubleDAOImpl() {
        super(Immeuble.class);
    }

    @Override
    public List<Immeuble> findByProprietaire(Long utilisateurId) {
        EntityManager em = getEntityManager();
        TypedQuery<Immeuble> query = em.createQuery(
                "SELECT i FROM Immeuble i WHERE i.proprietaire.id = :uid", Immeuble.class);
        query.setParameter("uid", utilisateurId);
        return query.getResultList();
    }
}
