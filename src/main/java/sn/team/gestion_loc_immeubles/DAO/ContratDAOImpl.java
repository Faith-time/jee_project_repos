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
        try {
            TypedQuery<Contrat> query = em.createQuery(
                    "SELECT DISTINCT c FROM Contrat c " +
                            "JOIN FETCH c.locataire " +
                            "JOIN FETCH c.unite u " +
                            "JOIN FETCH u.immeuble " +
                            "WHERE c.locataire.id = :lid " +
                            "ORDER BY c.dateDebut DESC",
                    Contrat.class
            );
            query.setParameter("lid", locataireId);

            List<Contrat> contrats = query.getResultList();

            // Debug détaillé
            System.out.println("=== DEBUG ContratDAO ===");
            System.out.println("Locataire ID recherché: " + locataireId);
            System.out.println("Nombre de contrats trouvés: " + contrats.size());

            for (Contrat c : contrats) {
                System.out.println("Contrat #" + c.getId() +
                        " - Locataire: " + c.getLocataire().getId() +
                        " - Unité: " + c.getUnite().getNumero() +
                        " - Immeuble: " + c.getUnite().getImmeuble().getNom());
            }

            return contrats;
        } finally {
            em.close();
        }
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
