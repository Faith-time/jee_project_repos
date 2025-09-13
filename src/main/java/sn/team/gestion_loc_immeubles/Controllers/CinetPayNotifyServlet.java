package sn.team.gestion_loc_immeubles.Controllers;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import sn.team.gestion_loc_immeubles.Services.PaiementService;
import java.io.IOException;
import java.util.logging.Logger;

/**
 * Servlet pour gérer les notifications (webhooks) de CinetPay
 */
@WebServlet("/cinetpay/notify")
public class CinetPayNotifyServlet extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(CinetPayNotifyServlet.class.getName());
    private final PaiementService paiementService = new PaiementService();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        try {
            String transactionId = req.getParameter("cpm_trans_id");
            String status = req.getParameter("cpm_result");
            String operatorId = req.getParameter("cpm_phone_prefixe");

            LOGGER.info("Notification CinetPay reçue - Transaction: " + transactionId + ", Status: " + status);

            if (transactionId != null && status != null) {
                paiementService.traiterNotificationCinetPay(transactionId, status, operatorId);

                // Répondre OK à CinetPay
                resp.setStatus(HttpServletResponse.SC_OK);
                resp.getWriter().write("OK");
            } else {
                LOGGER.warning("Paramètres de notification manquants");
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write("MISSING_PARAMS");
            }

        } catch (Exception e) {
            LOGGER.severe("Erreur lors du traitement de la notification: " + e.getMessage());
            e.printStackTrace();
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("ERROR");
        }
    }
}

