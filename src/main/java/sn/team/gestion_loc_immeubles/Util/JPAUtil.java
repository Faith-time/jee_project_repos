package sn.team.gestion_loc_immeubles.Util;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

/**
 * Utilitaire pour gérer l'EntityManager JPA
 */
public class JPAUtil {

    private static final String PERSISTENCE_UNIT_NAME = "gestionLocationPU";
    private static EntityManagerFactory emf;
    private static final ThreadLocal<EntityManager> threadLocal = new ThreadLocal<>();

    static {
        try {
            System.out.println("🔧 Initialisation de l'EntityManagerFactory...");
            emf = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
            System.out.println("✅ EntityManagerFactory initialisé avec succès");
        } catch (Exception e) {
            System.err.println("❌ Erreur lors de l'initialisation de l'EntityManagerFactory:");
            e.printStackTrace();
            throw new ExceptionInInitializerError(e);
        }
    }

    /**
     * Obtient l'EntityManager pour le thread courant
     */
    public static EntityManager getEntityManager() {
        EntityManager em = threadLocal.get();
        if (em == null || !em.isOpen()) {
            try {
                em = emf.createEntityManager();
                threadLocal.set(em);
                System.out.println("🔗 Nouvel EntityManager créé pour le thread: " + Thread.currentThread().getName());
            } catch (Exception e) {
                System.err.println("❌ Erreur lors de la création de l'EntityManager:");
                e.printStackTrace();
                throw new RuntimeException("Impossible de créer l'EntityManager", e);
            }
        }
        return em;
    }

    /**
     * Ferme l'EntityManager du thread courant
     */
    public static void closeEntityManager() {
        EntityManager em = threadLocal.get();
        if (em != null && em.isOpen()) {
            try {
                // Rollback si transaction active
                if (em.getTransaction().isActive()) {
                    System.out.println("⚠️ Transaction active détectée - rollback automatique");
                    em.getTransaction().rollback();
                }
                em.close();
                threadLocal.remove();
                System.out.println("🔚 EntityManager fermé pour le thread: " + Thread.currentThread().getName());
            } catch (Exception e) {
                System.err.println("❌ Erreur lors de la fermeture de l'EntityManager:");
                e.printStackTrace();
            }
        }
    }

    /**
     * Démarre une transaction
     */
    public static void beginTransaction() {
        EntityManager em = getEntityManager();
        try {
            if (!em.getTransaction().isActive()) {
                em.getTransaction().begin();
                System.out.println("🚀 Transaction démarrée");
            } else {
                System.out.println("⚠️ Transaction déjà active");
            }
        } catch (Exception e) {
            System.err.println("❌ Erreur lors du démarrage de la transaction:");
            e.printStackTrace();
            throw new RuntimeException("Impossible de démarrer la transaction", e);
        }
    }

    /**
     * Valide la transaction
     */
    public static void commitTransaction() {
        EntityManager em = getEntityManager();
        try {
            if (em.getTransaction().isActive()) {
                em.getTransaction().commit();
                System.out.println("✅ Transaction validée (commit)");
            } else {
                System.out.println("⚠️ Aucune transaction active à valider");
            }
        } catch (Exception e) {
            System.err.println("❌ Erreur lors du commit de la transaction:");
            e.printStackTrace();
            // Tentative de rollback automatique
            try {
                if (em.getTransaction().isActive()) {
                    em.getTransaction().rollback();
                    System.out.println("🔄 Rollback automatique effectué");
                }
            } catch (Exception rollbackException) {
                System.err.println("❌ Erreur lors du rollback automatique:");
                rollbackException.printStackTrace();
            }
            throw new RuntimeException("Erreur lors du commit", e);
        }
    }

    /**
     * Annule la transaction
     */
    public static void rollbackTransaction() {
        EntityManager em = getEntityManager();
        try {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
                System.out.println("🔄 Transaction annulée (rollback)");
            } else {
                System.out.println("⚠️ Aucune transaction active à annuler");
            }
        } catch (Exception e) {
            System.err.println("❌ Erreur lors du rollback de la transaction:");
            e.printStackTrace();
        }
    }

    /**
     * Exécute une opération dans une transaction
     */
    public static <T> T executeInTransaction(TransactionCallback<T> callback) {
        EntityManager em = null;
        try {
            em = getEntityManager();
            beginTransaction();

            T result = callback.execute(em);

            commitTransaction();
            return result;

        } catch (Exception e) {
            System.err.println("❌ Erreur dans executeInTransaction:");
            e.printStackTrace();
            rollbackTransaction();
            throw new RuntimeException("Erreur lors de l'exécution de la transaction", e);
        } finally {
            closeEntityManager();
        }
    }

    /**
     * Interface pour les callbacks de transaction
     */
    @FunctionalInterface
    public interface TransactionCallback<T> {
        T execute(EntityManager em) throws Exception;
    }

    /**
     * Ferme l'EntityManagerFactory
     */
    public static void closeEntityManagerFactory() {
        if (emf != null && emf.isOpen()) {
            try {
                emf.close();
                System.out.println("🛑 EntityManagerFactory fermé");
            } catch (Exception e) {
                System.err.println("❌ Erreur lors de la fermeture de l'EntityManagerFactory:");
                e.printStackTrace();
            }
        }
    }

    /**
     * Vérifie si l'EntityManagerFactory est disponible
     */
    public static boolean isEntityManagerFactoryAvailable() {
        return emf != null && emf.isOpen();
    }

    /**
     * Obtient des informations de statut pour le debugging
     */
    public static String getStatus() {
        StringBuilder status = new StringBuilder();
        status.append("EntityManagerFactory: ").append(emf != null && emf.isOpen() ? "OUVERT" : "FERMÉ");

        EntityManager em = threadLocal.get();
        if (em != null) {
            status.append(", EntityManager: ").append(em.isOpen() ? "OUVERT" : "FERMÉ");
            if (em.isOpen()) {
                status.append(", Transaction: ").append(em.getTransaction().isActive() ? "ACTIVE" : "INACTIVE");
            }
        } else {
            status.append(", EntityManager: NULL");
        }

        return status.toString();
    }
}