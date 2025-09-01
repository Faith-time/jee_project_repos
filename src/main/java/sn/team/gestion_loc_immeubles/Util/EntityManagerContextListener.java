package sn.team.gestion_loc_immeubles.Util;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

/**
 * Listener pour gérer le cycle de vie de l'EntityManagerFactory
 */
@WebListener
public class EntityManagerContextListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        System.out.println("🚀 Initialisation de l'application - Configuration JPA");
        try {
            // Force l'initialisation de l'EntityManagerFactory
            JPAUtil.getEntityManager();
            System.out.println("✅ EntityManagerFactory initialisé avec succès");
        } catch (Exception e) {
            System.err.println("❌ Erreur lors de l'initialisation de JPA: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        System.out.println("🛑 Arrêt de l'application - Fermeture des connexions JPA");
        try {
            JPAUtil.closeEntityManagerFactory();
            System.out.println("✅ EntityManagerFactory fermé proprement");
        } catch (Exception e) {
            System.err.println("❌ Erreur lors de la fermeture de JPA: " + e.getMessage());
            e.printStackTrace();
        }
    }
}