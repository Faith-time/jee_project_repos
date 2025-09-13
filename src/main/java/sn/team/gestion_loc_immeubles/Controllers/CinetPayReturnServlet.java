package sn.team.gestion_loc_immeubles.Controllers;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import sn.team.gestion_loc_immeubles.Entities.Paiement;
import sn.team.gestion_loc_immeubles.Services.PaiementService;

import java.io.IOException;
import java.util.logging.Logger;

/**
 * Servlet pour gérer le retour de CinetPay après paiement
 */
@WebServlet("/cinetpay/return")
class CinetPayReturnServlet extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(CinetPayReturnServlet.class.getName());
    private final PaiementService paiementService = new PaiementService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        processReturn(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        processReturn(req, resp);
    }

    private void processReturn(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        HttpSession session = req.getSession();

        try {
            String transactionId = req.getParameter("transaction_id");
            String token = req.getParameter("token");

            LOGGER.info("Retour CinetPay - Transaction: " + transactionId + ", Token: " + token);

            if (transactionId != null) {
                // Vérifier le statut du paiement auprès de CinetPay
                boolean paiementReussi = paiementService.verifierStatutPaiement(transactionId);

                if (paiementReussi) {
                    session.setAttribute("successMessage", "Votre paiement a été effectué avec succès !");
                    LOGGER.info("Paiement confirmé pour la transaction: " + transactionId);
                } else {
                    session.setAttribute("errorMessage", "Le paiement n'a pas pu être confirmé. Veuillez vérifier votre historique de paiements.");
                    LOGGER.warning("Paiement non confirmé pour la transaction: " + transactionId);
                }
            } else {
                session.setAttribute("errorMessage", "Informations de retour incomplètes.");
                LOGGER.warning("Transaction ID manquant dans le retour CinetPay");
            }

            // Rediriger vers la page des paiements du locataire
            resp.sendRedirect(req.getContextPath() + "/locataire/paiements");

        } catch (Exception e) {
            LOGGER.severe("Erreur lors du traitement du retour: " + e.getMessage());
            e.printStackTrace();
            session.setAttribute("errorMessage", "Une erreur est survenue lors du traitement de votre retour de paiement.");
            resp.sendRedirect(req.getContextPath() + "/locataire/paiements");
        }
    }
}