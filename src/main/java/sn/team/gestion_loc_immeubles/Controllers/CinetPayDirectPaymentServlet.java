package sn.team.gestion_loc_immeubles.Controllers;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import sn.team.gestion_loc_immeubles.Entities.Paiement;
import sn.team.gestion_loc_immeubles.Entities.Utilisateur;
import sn.team.gestion_loc_immeubles.Services.PaiementService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Logger;

@WebServlet("/paiement/direct")
public class CinetPayDirectPaymentServlet extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(CinetPayDirectPaymentServlet.class.getName());
    private final PaiementService paiementService = new PaiementService();
    private final ObjectMapper objectMapper = new ObjectMapper();

    // URL de l'API CinetPay
    private static final String CINETPAY_API_URL = "https://api-checkout.cinetpay.com/v2/payment";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        // Vérification de la session utilisateur
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("utilisateur") == null) {
            resp.sendRedirect(req.getContextPath() + "/auth?action=login");
            return;
        }

        Utilisateur utilisateur = (Utilisateur) session.getAttribute("utilisateur");

        // Vérifier que l'utilisateur est locataire
        if (!utilisateur.isLocataire()) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Accès non autorisé");
            return;
        }

        try {
            // Récupérer les paiements en attente pour cet utilisateur
            List<Paiement> paiementsEnAttente = paiementService.getPaiementsEnAttente(utilisateur.getId());

            if (paiementsEnAttente.isEmpty()) {
                // Pas de paiements en attente, rediriger vers la page des paiements
                session.setAttribute("errorMessage", "Aucun paiement en attente trouvé.");
                resp.sendRedirect(req.getContextPath() + "/locataire/paiements");
                return;
            }

            // Prendre le premier paiement en attente
            Paiement premierPaiement = paiementsEnAttente.get(0);

            // Initialiser directement le paiement avec CinetPay
            initialisationEtRedirectionCinetPay(premierPaiement, utilisateur, req, resp);

        } catch (Exception e) {
            LOGGER.severe("Erreur lors de l'initialisation du paiement: " + e.getMessage());
            e.printStackTrace();
            session.setAttribute("errorMessage", "Une erreur technique est survenue lors de l'initialisation du paiement.");
            resp.sendRedirect(req.getContextPath() + "/locataire/paiements");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        // Vérification de la session utilisateur
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("utilisateur") == null) {
            resp.sendRedirect(req.getContextPath() + "/auth?action=login");
            return;
        }

        Utilisateur utilisateur = (Utilisateur) session.getAttribute("utilisateur");

        // Vérifier que l'utilisateur est locataire
        if (!utilisateur.isLocataire()) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Accès non autorisé");
            return;
        }

        try {
            // Récupérer l'ID du paiement spécifique à traiter
            String paiementIdStr = req.getParameter("paiementId");

            if (paiementIdStr == null || paiementIdStr.trim().isEmpty()) {
                session.setAttribute("errorMessage", "ID de paiement manquant.");
                resp.sendRedirect(req.getContextPath() + "/locataire/paiements");
                return;
            }

            Long paiementId = Long.parseLong(paiementIdStr);
            Paiement paiement = paiementService.trouverParId(paiementId);

            if (paiement == null) {
                session.setAttribute("errorMessage", "Paiement introuvable.");
                resp.sendRedirect(req.getContextPath() + "/locataire/paiements");
                return;
            }

            // Vérifier que le paiement appartient bien à l'utilisateur connecté
            if (!paiement.getContrat().getLocataire().getId().equals(utilisateur.getId())) {
                resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Vous n'êtes pas autorisé à accéder à ce paiement");
                return;
            }

            // Vérifier que le paiement est en attente ou en retard
            if (paiement.getStatut() == Paiement.StatutPaiement.PAYE) {
                session.setAttribute("errorMessage", "Ce paiement a déjà été effectué.");
                resp.sendRedirect(req.getContextPath() + "/locataire/paiements");
                return;
            }

            // Initialiser et rediriger vers CinetPay
            initialisationEtRedirectionCinetPay(paiement, utilisateur, req, resp);

        } catch (NumberFormatException e) {
            session.setAttribute("errorMessage", "ID de paiement invalide.");
            resp.sendRedirect(req.getContextPath() + "/locataire/paiements");
        } catch (Exception e) {
            LOGGER.severe("Erreur lors du traitement de la requête de paiement: " + e.getMessage());
            e.printStackTrace();
            if (session != null) {
                session.setAttribute("errorMessage", "Une erreur technique est survenue. Veuillez réessayer.");
            }
            resp.sendRedirect(req.getContextPath() + "/locataire/paiements");
        }
    }

    /**
     * Méthode commune pour initialiser le paiement CinetPay et faire la redirection
     */
    private void initialisationEtRedirectionCinetPay(Paiement paiement, Utilisateur utilisateur,
                                                     HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        HttpSession session = req.getSession();

        try {
            // Configuration CinetPay
            String apiKey = getCinetPayApiKey();
            String siteId = getCinetPaySiteId();
            String notifyUrl = getCinetPayNotifyUrl();
            String returnUrl = getCinetPayReturnUrl();

            // Debug des valeurs récupérées
            System.out.println("=== VALEURS RÉCUPÉRÉES ===");
            System.out.println("apiKey: " + (apiKey != null && !apiKey.isEmpty() ? "OK" : "VIDE/NULL"));
            System.out.println("siteId: " + (siteId != null && !siteId.isEmpty() ? "OK" : "VIDE/NULL"));
            System.out.println("notifyUrl: " + (notifyUrl != null && !notifyUrl.isEmpty() ? "OK" : "VIDE/NULL"));
            System.out.println("returnUrl: " + (returnUrl != null && !returnUrl.isEmpty() ? "OK" : "VIDE/NULL"));

            if (apiKey == null || apiKey.isEmpty() ||
                    siteId == null || siteId.isEmpty() ||
                    notifyUrl == null || notifyUrl.isEmpty() ||
                    returnUrl == null || returnUrl.isEmpty()) {

                System.out.println("ERREUR: Configuration incomplète détectée");
                session.setAttribute("errorMessage", "Configuration CinetPay incomplète.");
                resp.sendRedirect(req.getContextPath() + "/locataire/paiements");
                return;
            }

            // Générer un ID de transaction unique
            String transactionId = "PAI-" + paiement.getId() + "-" + System.currentTimeMillis();

            // Mettre à jour le paiement avec l'ID de transaction
            paiement.setTransactionId(transactionId);
            paiementService.mettreAJourPaiement(paiement);

            // Préparer les données pour l'API CinetPay
            Map<String, Object> paymentData = new HashMap<>();
            paymentData.put("apikey", apiKey);
            paymentData.put("site_id", siteId);
            paymentData.put("transaction_id", transactionId);
            paymentData.put("amount", paiement.getMontant().intValue()); // CinetPay attend un entier
            paymentData.put("currency", "XOF"); // Franc CFA
            paymentData.put("description", "Paiement de loyer - Contrat #" + paiement.getContrat().getId());
            paymentData.put("return_url", returnUrl);
            paymentData.put("notify_url", notifyUrl);

            // Informations client
            Map<String, String> customerInfo = new HashMap<>();
            customerInfo.put("name", utilisateur.getPrenom() + " " + utilisateur.getNom());
            customerInfo.put("surname", utilisateur.getNom());
            customerInfo.put("email", utilisateur.getEmail());
            customerInfo.put("phone_number", utilisateur.getTelephone() != null ? utilisateur.getTelephone() : "");
            paymentData.put("customer_name", customerInfo.get("name"));
            paymentData.put("customer_surname", customerInfo.get("surname"));
            paymentData.put("customer_email", customerInfo.get("email"));
            paymentData.put("customer_phone_number", customerInfo.get("phone_number"));

            // Appel à l'API CinetPay
            String response = appelApiCinetPay(paymentData);
            System.out.println("Réponse CinetPay: " + response);

            // Parser la réponse JSON
            JsonNode responseNode = objectMapper.readTree(response);

            // Sauvegarder la réponse brute
            paiement.setRawResponse(response);

            if (responseNode.has("code") && "201".equals(responseNode.get("code").asText())) {
                // Succès - récupérer l'URL de paiement
                JsonNode dataNode = responseNode.get("data");
                String paymentUrl = dataNode.get("payment_url").asText();
                String paymentToken = dataNode.has("payment_token") ? dataNode.get("payment_token").asText() : null;

                // Mettre à jour le paiement
                paiement.setPaymentUrl(paymentUrl);
                paiement.setPaymentToken(paymentToken);
                paiementService.mettreAJourPaiement(paiement);

                System.out.println("URL de paiement générée: " + paymentUrl);

                // Rediriger vers l'interface de paiement CinetPay
                resp.sendRedirect(paymentUrl);

            } else {
                // Erreur dans la réponse
                String errorMessage = "Erreur lors de l'initialisation du paiement";
                if (responseNode.has("message")) {
                    errorMessage = responseNode.get("message").asText();
                }

                System.out.println("Erreur CinetPay: " + errorMessage);
                session.setAttribute("errorMessage", "Erreur CinetPay: " + errorMessage);
                resp.sendRedirect(req.getContextPath() + "/locataire/paiements");
            }

        } catch (Exception e) {
            LOGGER.severe("Erreur lors de l'appel à l'API CinetPay: " + e.getMessage());
            e.printStackTrace();
            session.setAttribute("errorMessage", "Une erreur technique est survenue lors de l'initialisation du paiement.");
            resp.sendRedirect(req.getContextPath() + "/locataire/paiements");
        }
    }

    /**
     * Effectue l'appel HTTP vers l'API CinetPay
     */
    private String appelApiCinetPay(Map<String, Object> paymentData) throws IOException {
        URL url = new URL(CINETPAY_API_URL);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        try {
            // Configuration de la connexion
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Accept", "application/json");
            connection.setDoOutput(true);

            // Convertir les données en JSON
            String jsonData = objectMapper.writeValueAsString(paymentData);
            System.out.println("Données envoyées à CinetPay: " + jsonData);

            // Envoyer les données
            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = jsonData.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            // Lire la réponse
            int responseCode = connection.getResponseCode();
            System.out.println("Code de réponse HTTP: " + responseCode);

            StringBuilder response = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(
                            responseCode >= 200 && responseCode < 300
                                    ? connection.getInputStream()
                                    : connection.getErrorStream(),
                            StandardCharsets.UTF_8))) {

                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
            }

            return response.toString();

        } finally {
            connection.disconnect();
        }
    }

    // Méthodes utilitaires pour récupérer la configuration CinetPay
    private String getCinetPayApiKey() {
        try {
            javax.naming.InitialContext ctx = new javax.naming.InitialContext();
            String jndiValue = (String) ctx.lookup("java:comp/env/CINETPAY_APIKEY");
            if (jndiValue != null && !jndiValue.trim().isEmpty()) {
                return jndiValue;
            }
        } catch (Exception e) {
            LOGGER.warning("Impossible de récupérer CINETPAY_APIKEY depuis JNDI: " + e.getMessage());
        }

        return "171302123268bf578d753cf5.30754439";
    }

    private String getCinetPaySiteId() {
        try {
            javax.naming.InitialContext ctx = new javax.naming.InitialContext();
            String jndiValue = (String) ctx.lookup("java:comp/env/CINETPAY_SITEID");
            if (jndiValue != null && !jndiValue.trim().isEmpty()) {
                return jndiValue;
            }
        } catch (Exception e) {
            LOGGER.warning("Impossible de récupérer CINETPAY_SITEID depuis JNDI: " + e.getMessage());
        }

        return "105906272";
    }

    private String getCinetPayNotifyUrl() {
        try {
            javax.naming.InitialContext ctx = new javax.naming.InitialContext();
            String jndiValue = (String) ctx.lookup("java:comp/env/CINETPAY_NOTIFY_URL");
            if (jndiValue != null && !jndiValue.trim().isEmpty()) {
                return jndiValue;
            }
        } catch (Exception e) {
            LOGGER.warning("Impossible de récupérer CINETPAY_NOTIFY_URL depuis JNDI: " + e.getMessage());
        }

        return "https://b90ff648b0b4.ngrok-free.app/gestion_loc_immeubles/cinetpay/notify";
    }

    private String getCinetPayReturnUrl() {
        try {
            javax.naming.InitialContext ctx = new javax.naming.InitialContext();
            String jndiValue = (String) ctx.lookup("java:comp/env/CINETPAY_RETURN_URL");
            if (jndiValue != null && !jndiValue.trim().isEmpty()) {
                return jndiValue;
            }
        } catch (Exception e) {
            LOGGER.warning("Impossible de récupérer CINETPAY_RETURN_URL depuis JNDI: " + e.getMessage());
        }

        return "https://b90ff648b0b4.ngrok-free.app/gestion_loc_immeubles/cinetpay/return";
    }
}