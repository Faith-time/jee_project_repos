package sn.team.gestion_loc_immeubles.Services;

import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
import sn.team.gestion_loc_immeubles.DAO.PaiementDAO;
import sn.team.gestion_loc_immeubles.DAO.PaiementDAOImpl;
import sn.team.gestion_loc_immeubles.Entities.Contrat;
import sn.team.gestion_loc_immeubles.Entities.Paiement;
import sn.team.gestion_loc_immeubles.Entities.Unite;
import sn.team.gestion_loc_immeubles.Util.JPAUtil;

import java.io.StringReader;
import java.math.BigDecimal;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PaiementService {

    private static final Logger LOGGER = Logger.getLogger(PaiementService.class.getName());
    private final PaiementDAO paiementDAO;

    // Configuration CinetPay
    private final String apiKey;
    private final String siteId;
    private final String notifyUrl;
    private final String returnUrl;
    private final String apiUrl = "https://api-checkout.cinetpay.com/v2/payment";

    public PaiementService() {
        this.paiementDAO = new PaiementDAOImpl();

        // Configuration depuis variables d'environnement ou propriétés système
        this.apiKey = System.getenv("CINETPAY_APIKEY");
        this.siteId = System.getenv("CINETPAY_SITEID");
        this.notifyUrl = System.getenv("CINETPAY_NOTIFY_URL");
        this.returnUrl = System.getenv("CINETPAY_RETURN_URL");

        // Vérification des variables d'environnement
        if (apiKey == null || siteId == null || notifyUrl == null || returnUrl == null) {
            LOGGER.severe("Variables d'environnement CinetPay manquantes. Vérifiez: CINETPAY_APIKEY, CINETPAY_SITEID, CINETPAY_NOTIFY_URL, CINETPAY_RETURN_URL");
        }
    }

    // Constructeur pour les tests avec paramètres personnalisés
    public PaiementService(String apiKey, String siteId, String notifyUrl, String returnUrl) {
        this.paiementDAO = new PaiementDAOImpl();
        this.apiKey = apiKey;
        this.siteId = siteId;
        this.notifyUrl = notifyUrl;
        this.returnUrl = returnUrl;
    }

    /**
     * Créer un paiement avec génération automatique du transactionId
     */
    public Paiement creerPaiement(Paiement paiement) {
        // Générer un transactionId unique si pas défini
        if (paiement.getTransactionId() == null || paiement.getTransactionId().isEmpty()) {
            paiement.setTransactionId(genererTransactionId());
        }

        // S'assurer que le statut est défini
        if (paiement.getStatut() == null) {
            paiement.setStatut(Paiement.StatutPaiement.EN_ATTENTE);
        }

        paiementDAO.save(paiement);
        LOGGER.info("Paiement créé avec ID: " + paiement.getId() +
                " et transaction_id: " + paiement.getTransactionId());
        return paiement;
    }

    /**
     * Initialiser un paiement avec CinetPay
     */
// Ajoutez cette correction dans votre méthode initialiserPaiementCinetPay de PaiementService

    public CinetPayResponse initialiserPaiementCinetPay(Long paiementId, String customerName, String customerEmail) {
        // Vérification des paramètres de configuration
        if (apiKey == null || siteId == null || notifyUrl == null || returnUrl == null) {
            throw new IllegalStateException("Configuration CinetPay incomplète. Vérifiez les variables d'environnement.");
        }

        Paiement paiement = trouverParId(paiementId);
        if (paiement == null) {
            throw new IllegalArgumentException("Paiement introuvable avec l'ID: " + paiementId);
        }

        if (paiement.getStatut() != Paiement.StatutPaiement.EN_ATTENTE &&
                paiement.getStatut() != Paiement.StatutPaiement.EN_RETARD) {
            throw new IllegalStateException("Le paiement n'est pas en attente");
        }

        try {
            // Construire le JSON pour CinetPay
            JsonObject requestBody = Json.createObjectBuilder()
                    .add("apikey", apiKey)
                    .add("site_id", siteId)
                    .add("transaction_id", paiement.getTransactionId())
                    .add("amount", convertirMontantPourCinetPay(paiement.getMontant()))
                    .add("currency", "XOF")
                    .add("description", "Paiement loyer - ID: " + paiement.getId())
                    .add("customer_name", customerName != null ? customerName : "Locataire")
                    .add("customer_email", customerEmail != null ? customerEmail : "locataire@example.com")
                    .add("notify_url", notifyUrl)
                    .add("return_url", returnUrl)
                    .add("metadata", "payment_id=" + paiementId)
                    .build();

            LOGGER.info("Envoi de la requête à CinetPay: " + requestBody.toString());

            // Appel API CinetPay
            HttpClient client = HttpClient.newBuilder()
                    .connectTimeout(Duration.ofSeconds(10))
                    .build();

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(apiUrl))
                    .header("Content-Type", "application/json")
                    .header("Accept", "application/json")
                    .timeout(Duration.ofSeconds(30))
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody.toString()))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            LOGGER.info("Réponse CinetPay (Code: " + response.statusCode() + "): " + response.body());

            // Sauvegarder la réponse brute
            paiement.setRawResponse(response.body());

            // Parser la réponse
            JsonReader jsonReader = Json.createReader(new StringReader(response.body()));
            JsonObject responseJson = jsonReader.readObject();

            CinetPayResponse cinetPayResponse = new CinetPayResponse();
            String code = responseJson.getString("code", "");
            String message = responseJson.getString("message", "Erreur inconnue");

            // CORRECTION: Code 201 ou 00 peuvent indiquer un succès selon la version de l'API
            boolean isSuccess = "201".equals(code) || "00".equals(code);
            cinetPayResponse.setSuccess(isSuccess);
            cinetPayResponse.setMessage(message);
            cinetPayResponse.setRawResponse(response.body());

            if (isSuccess && responseJson.containsKey("data")) {
                JsonObject data = responseJson.getJsonObject("data");
                String paymentUrl = data.getString("payment_url", "");
                String paymentToken = data.getString("payment_token", "");

                // Mettre à jour le paiement avec les informations CinetPay
                paiement.setPaymentUrl(paymentUrl);
                paiement.setPaymentToken(paymentToken);

                // CORRECTION: S'assurer que l'URL est bien définie dans la réponse
                cinetPayResponse.setPaymentUrl(paymentUrl);
                cinetPayResponse.setPaymentToken(paymentToken);
                cinetPayResponse.setTransactionId(paiement.getTransactionId());

                mettreAJourPaiement(paiement);

                LOGGER.info("Paiement initialisé avec succès: " + paiement.getTransactionId() +
                        ", URL: " + paymentUrl);
            } else {
                String errorMsg = "Erreur CinetPay (" + code + "): " + message;
                LOGGER.warning(errorMsg);
                cinetPayResponse.setMessage(errorMsg);
            }

            return cinetPayResponse;

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de l'initialisation CinetPay", e);

            CinetPayResponse errorResponse = new CinetPayResponse();
            errorResponse.setSuccess(false);
            errorResponse.setMessage("Erreur technique: " + e.getMessage());
            return errorResponse;
        }
    }
    /**
     * Vérifier le statut d'un paiement auprès de CinetPay
     */
    public boolean verifierStatutPaiement(String transactionId) {
        if (apiKey == null || siteId == null) {
            LOGGER.severe("Configuration CinetPay manquante pour la vérification");
            return false;
        }

        try {
            JsonObject requestBody = Json.createObjectBuilder()
                    .add("apikey", apiKey)
                    .add("site_id", siteId)
                    .add("transaction_id", transactionId)
                    .build();

            HttpClient client = HttpClient.newBuilder()
                    .connectTimeout(Duration.ofSeconds(10))
                    .build();

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://api-checkout.cinetpay.com/v2/payment/check"))
                    .header("Content-Type", "application/json")
                    .timeout(Duration.ofSeconds(20))
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody.toString()))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            JsonReader jsonReader = Json.createReader(new StringReader(response.body()));
            JsonObject responseJson = jsonReader.readObject();

            if ("00".equals(responseJson.getString("code", ""))) {
                JsonObject data = responseJson.getJsonObject("data");
                String status = data.getString("status", "");

                LOGGER.info("Statut CinetPay pour " + transactionId + ": " + status);

                // Mettre à jour le paiement local
                Paiement paiement = trouverParTransactionId(transactionId);
                if (paiement != null) {
                    if ("ACCEPTED".equals(status)) {
                        paiement.setStatut(Paiement.StatutPaiement.PAYE);
                        paiement.setDatePaiement(LocalDate.now());
                        paiement.setOperatorId(data.getString("operator_id", ""));
                        mettreAJourPaiement(paiement);
                        return true;
                    }
                }
            }

            return false;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la vérification du statut", e);
            return false;
        }
    }

    /**
     * Traiter la notification de CinetPay (webhook)
     */
    public void traiterNotificationCinetPay(String transactionId, String status, String operatorId) {
        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction transaction = em.getTransaction();

        try {
            transaction.begin();

            Paiement paiement = em.createQuery(
                            "SELECT p FROM Paiement p WHERE p.transactionId = :transactionId",
                            Paiement.class)
                    .setParameter("transactionId", transactionId)
                    .getSingleResult();

            if (paiement == null) {
                LOGGER.warning("Paiement introuvable pour transaction_id: " + transactionId);
                return;
            }

            if ("ACCEPTED".equals(status) && paiement.getStatut() != Paiement.StatutPaiement.PAYE) {
                paiement.setStatut(Paiement.StatutPaiement.PAYE);
                paiement.setDatePaiement(LocalDate.now());
                paiement.setOperatorId(operatorId);

                em.merge(paiement);
                transaction.commit();

                LOGGER.info("Paiement confirmé par notification CinetPay: " + transactionId);
            } else {
                LOGGER.info("Statut de paiement non accepté ou déjà traité: " + status + " pour " + transactionId);
            }
        } catch (NoResultException e) {
            LOGGER.warning("Paiement non trouvé pour transaction_id: " + transactionId);
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            LOGGER.log(Level.SEVERE, "Erreur lors du traitement de la notification", e);
        } finally {
            JPAUtil.closeEntityManager();
        }
    }

    /**
     * Mettre à jour automatiquement les statuts des paiements en retard
     */
    public int mettreAJourPaiementsEnRetard() {
        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction transaction = em.getTransaction();

        try {
            transaction.begin();

            int updated = em.createQuery(
                            "UPDATE Paiement p SET p.statut = :retard " +
                                    "WHERE p.statut = :enAttente AND p.dateEcheance < :today")
                    .setParameter("retard", Paiement.StatutPaiement.EN_RETARD)
                    .setParameter("enAttente", Paiement.StatutPaiement.EN_ATTENTE)
                    .setParameter("today", LocalDate.now())
                    .executeUpdate();

            transaction.commit();
            LOGGER.info(updated + " paiements mis à jour en retard");
            return updated;
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            LOGGER.log(Level.SEVERE, "Erreur lors de la mise à jour des paiements en retard", e);
            return 0;
        } finally {
            JPAUtil.closeEntityManager();
        }
    }

    // ============= MÉTHODES CRUD DE BASE =============

    public Paiement trouverParId(Long id) {
        return paiementDAO.findById(id);
    }

    public Paiement trouverParTransactionId(String transactionId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery(
                            "SELECT p FROM Paiement p WHERE p.transactionId = :transactionId",
                            Paiement.class)
                    .setParameter("transactionId", transactionId)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        } finally {
            JPAUtil.closeEntityManager();
        }
    }

    public List<Paiement> listerPaiements() {
        return paiementDAO.findAll();
    }

    public List<Paiement> listerParContrat(Long contratId) {
        return paiementDAO.findByContrat(contratId);
    }

    public List<Paiement> listerParStatut(Paiement.StatutPaiement statut) {
        return paiementDAO.findByStatut(statut);
    }

    public void mettreAJourPaiement(Paiement paiement) {
        paiementDAO.update(paiement);
    }

    public void supprimerPaiement(Long id) {
        paiementDAO.delete(id);
    }

    public List<Paiement> listerPaiementsParLocataire(Long locataireId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery(
                            "SELECT p FROM Paiement p " +
                                    "WHERE p.contrat.locataire.id = :locataireId " +
                                    "ORDER BY p.dateEcheance DESC",
                            Paiement.class)
                    .setParameter("locataireId", locataireId)
                    .getResultList();
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Erreur lors de la récupération des paiements du locataire ID: " + locataireId, e);
            return new ArrayList<>();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    // ============= MÉTHODES STATISTIQUES =============

    public long compterPaiementsPayesByLocataire(Long locataireId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery(
                            "SELECT COUNT(p) FROM Paiement p " +
                                    "WHERE p.contrat.locataire.id = :locataireId",
                            Long.class)
                    .setParameter("locataireId", locataireId)
                    .getSingleResult();
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Erreur lors du comptage des paiements du locataire", e);
            return 0;
        } finally {
            JPAUtil.closeEntityManager();
        }
    }


    public long compterPaiementsByLocataireEtStatut(Long locataireId, Paiement.StatutPaiement statut) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery(
                            "SELECT COUNT(p) FROM Paiement p " +
                                    "WHERE p.contrat.locataire.id = :locataireId AND p.statut = :statut",
                            Long.class)
                    .setParameter("locataireId", locataireId)
                    .setParameter("statut", statut)
                    .getSingleResult();
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Erreur lors du comptage des paiements", e);
            return 0;
        } finally {
            JPAUtil.closeEntityManager();
        }
    }


    public BigDecimal calculerRevenuTotalByProprietaire(Long proprietaireId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            Double result = em.createQuery(
                            "SELECT COALESCE(SUM(p.montant), 0.0) FROM Paiement p " +
                                    "WHERE p.contrat.unite.immeuble.proprietaire.id = :proprietaireId " +
                                    "AND p.statut = :statut",
                            Double.class)
                    .setParameter("proprietaireId", proprietaireId)
                    .setParameter("statut", Paiement.StatutPaiement.PAYE)
                    .getSingleResult();

            return result != null ? BigDecimal.valueOf(result) : BigDecimal.ZERO;
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Erreur lors du calcul du revenu total", e);
            return BigDecimal.ZERO;
        } finally {
            JPAUtil.closeEntityManager();
        }
    }

    // Ajouter cette méthode dans votre classe PaiementService

    /**
     * Récupère les paiements en attente ou en retard pour un locataire
     */
    public List<Paiement> getPaiementsEnAttente(Long locataireId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            String jpql = "SELECT p FROM Paiement p " +
                    "JOIN p.contrat c " +
                    "WHERE c.locataire.id = :locataireId " +
                    "AND (p.statut = :enAttente OR p.statut = :enRetard) " +
                    "ORDER BY p.dateEcheance ASC";

            return em.createQuery(jpql, Paiement.class)
                    .setParameter("locataireId", locataireId)
                    .setParameter("enAttente", Paiement.StatutPaiement.EN_ATTENTE)
                    .setParameter("enRetard", Paiement.StatutPaiement.EN_RETARD)
                    .getResultList();

        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        } finally {
            em.close();
        }
    }


    /**
     * Récupère la liste des unités louées par un locataire avec leurs informations de paiement
     * @param locataireId ID du locataire
     * @return Liste des unités avec leurs contrats actifs
     */
    public List<Unite> listerUnitesLoueesParLocataire(Long locataireId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery(
                            "SELECT u FROM Contrat c " +
                                    "JOIN c.unite u " +
                                    "JOIN u.immeuble i " +
                                    "WHERE c.locataire.id = :locataireId " +
                                    "AND (c.dateFin IS NULL OR c.dateFin >= :dateActuelle) " +
                                    "ORDER BY u.numero ASC, u.id ASC", Unite.class)
                    .setParameter("locataireId", locataireId)
                    .setParameter("dateActuelle", LocalDate.now())
                    .getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Erreur lors de la récupération des unités louées", e);
        } finally {
            em.close();
        }
    }

    /**
     * Récupère le contrat actif d'une unité pour un locataire donné
     * @param uniteId ID de l'unité
     * @param locataireId ID du locataire
     * @return Le contrat actif ou null si aucun contrat trouvé
     */
    public Contrat getContratActifParUniteEtLocataire(Long uniteId, Long locataireId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<Contrat> query = em.createQuery(
                    "SELECT c FROM Contrat c " +
                            "WHERE c.unite.id = :uniteId " +
                            "AND c.locataire.id = :locataireId " +
                            "AND (c.dateFin IS NULL OR c.dateFin >= :dateActuelle) " +
                            "ORDER BY c.dateDebut DESC",
                    Contrat.class);

            query.setParameter("uniteId", uniteId);
            query.setParameter("locataireId", locataireId);
            query.setParameter("dateActuelle", LocalDate.now());
            query.setMaxResults(1);

            List<Contrat> resultats = query.getResultList();
            return resultats.isEmpty() ? null : resultats.get(0);

        } catch (Exception e) {
            System.out.println("ERREUR lors de la récupération du contrat: " + e.getMessage());
            e.printStackTrace();
            return null;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    // ============= MÉTHODES UTILITAIRES =============

    private String genererTransactionId() {
        return "PAI-" + System.currentTimeMillis() + "-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    private int convertirMontantPourCinetPay(Double montant) {
        if (montant == null) {
            return 0;
        }

        // CinetPay exige des montants en entiers (centimes pour XOF)
        // Arrondir et s'assurer que c'est un multiple de 5 pour XOF
        int montantInt = (int) Math.round(montant);

        // S'assurer que le montant est au moins de 100 FCFA
        if (montantInt < 100) {
            montantInt = 100;
        }

        // Arrondir au multiple de 5 le plus proche pour XOF
        return (montantInt / 5) * 5;
    }

    // ============= CLASSE INTERNE POUR LA RÉPONSE CINETPAY =============

    public static class CinetPayResponse {
        private boolean success;
        private String message;
        private String paymentUrl;
        private String paymentToken;
        private String rawResponse;
        private String transactionId;
        private String apiTransactionId;



        // Getters et Setters
        public boolean isSuccess() { return success; }
        public void setSuccess(boolean success) { this.success = success; }

        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }

        public String getPaymentUrl() { return paymentUrl; }
        public void setPaymentUrl(String paymentUrl) { this.paymentUrl = paymentUrl; }

        public String getPaymentToken() { return paymentToken; }
        public void setPaymentToken(String paymentToken) { this.paymentToken = paymentToken; }

        public String getRawResponse() { return rawResponse; }
        public void setRawResponse(String rawResponse) { this.rawResponse = rawResponse; }


        public String getTransactionId() {
            return transactionId;
        }

        public void setTransactionId(String transactionId) {
            this.transactionId = transactionId;
        }


        public String getApiTransactionId() {
            return apiTransactionId;
        }

        public void setApiTransactionId(String apiTransactionId) {
            this.apiTransactionId = apiTransactionId;
        }
        @Override
        public String toString() {
            return "CinetPayResponse{" +
                    "success=" + success +
                    ", message='" + message + '\'' +
                    ", paymentUrl='" + paymentUrl + '\'' +
                    ", paymentToken='" + paymentToken + '\'' +
                    ", transactionId='" + transactionId + '\'' +
                    ", apiTransactionId='" + apiTransactionId + '\'' +
                    '}';
        }
    }
}