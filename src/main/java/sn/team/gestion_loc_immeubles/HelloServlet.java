package sn.team.gestion_loc_immeubles;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import sn.team.gestion_loc_immeubles.Util.JPAUtil;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

public class HelloServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        resp.setContentType("text/plain");
        PrintWriter out = resp.getWriter();
        EntityManager em = null;

        try {
            out.println("=== DÉBUT DU TEST DE CONNECTIVITÉ JPA ===");

            // Obtenir l'EntityManager via JPAUtil
            em = JPAUtil.getEntityManager();
            out.println("✓ EntityManager obtenu avec succès");

            // Tester la connexion à la base de données
            em.getTransaction().begin();
            out.println("✓ Transaction démarrée avec succès");

            // Exécuter une requête simple
            Query testQuery = em.createNativeQuery("SELECT 1");
            Object result = testQuery.getSingleResult();
            out.println("✓ Requête de test exécutée avec succès : " + result);

            // Vérifier les tables existantes
            Query tablesQuery = em.createNativeQuery(
                    "SELECT table_name FROM information_schema.tables " +
                            "WHERE table_schema = 'public' AND table_name LIKE '%utilisateur%'"
            );
            List<String> tables = tablesQuery.getResultList();

            if (!tables.isEmpty()) {
                out.println("✓ Tables trouvées dans la base :");
                tables.forEach(table -> out.println("  - " + table));
            } else {
                out.println("ℹ Aucune table 'utilisateur' trouvée - elles seront créées automatiquement");
            }

            // Vérifier le nombre d'entités configurées
            long entityCount = em.getMetamodel().getEntities().size();
            out.println("✓ Nombre d'entités JPA configurées : " + entityCount);

            // Lister toutes les entités
            out.println("✓ Entités JPA enregistrées :");
            em.getMetamodel().getEntities().forEach(entityType -> {
                out.println("  - " + entityType.getName() + " (" + entityType.getJavaType().getSimpleName() + ")");
            });

            em.getTransaction().commit();
            out.println("✓ Transaction validée avec succès");

            // Vérifier à nouveau les tables après l'initialisation
            em.getTransaction().begin();
            Query finalTablesQuery = em.createNativeQuery(
                    "SELECT table_name FROM information_schema.tables " +
                            "WHERE table_schema = 'public' ORDER BY table_name"
            );
            List<String> allTables = finalTablesQuery.getResultList();
            em.getTransaction().commit();

            out.println("✓ Tables présentes dans la base après initialisation :");
            allTables.forEach(table -> out.println("  - " + table));

            out.println("=== TEST DE CONNECTIVITÉ TERMINÉ AVEC SUCCÈS ===");

        } catch (Exception e) {
            out.println("=== ERREUR LORS DU TEST DE CONNECTIVITÉ ===");
            if (em != null && em.getTransaction().isActive()) {
                em.getTransaction().rollback();
                out.println("Transaction annulée (rollback)");
            }
            e.printStackTrace(out);

            out.println("\n=== DIAGNOSTICS ===");
            out.println("1. PostgreSQL est démarré ?");
            out.println("2. La base 'immeubles_location_db' existe ?");
            out.println("3. L'utilisateur 'postgres' peut se connecter avec 'pgpass' ?");
            out.println("4. Le driver PostgreSQL est bien dans le classpath ?");
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
                out.println("EntityManager fermé");
            }
        }
    }
}
