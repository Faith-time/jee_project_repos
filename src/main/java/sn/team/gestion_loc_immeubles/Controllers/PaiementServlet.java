package sn.team.gestion_loc_immeubles.Controllers;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import sn.team.gestion_loc_immeubles.DAO.ContratDAOImpl;
import sn.team.gestion_loc_immeubles.DAO.PaiementDAO;
import sn.team.gestion_loc_immeubles.DAO.PaiementDAOImpl;
import sn.team.gestion_loc_immeubles.Entities.Paiement;
import sn.team.gestion_loc_immeubles.Entities.Contrat;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@WebServlet("/paiements")
public class PaiementServlet extends HttpServlet {

    private PaiementDAO paiementDAO;
    private ContratDAOImpl contratDAO;

    @Override
    public void init() {
        this.paiementDAO = new PaiementDAOImpl();
        this.contratDAO = new ContratDAOImpl();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String contratIdParam = req.getParameter("contratId");
        String statutParam = req.getParameter("statut");
        String idParam = req.getParameter("id");
        String role = req.getParameter("role"); // "admin", "locataire", "proprietaire"

        if (idParam != null) {
            // 🔎 Détail d’un paiement
            Long id = Long.parseLong(idParam);
            Paiement paiement = paiementDAO.findById(id);
            req.setAttribute("paiement", paiement);

            req.getRequestDispatcher("/WEB-INF/jsp/locataire/paiements.jsp").forward(req, resp);

        } else if (contratIdParam != null) {
            // 📌 Paiements liés à un contrat
            Long contratId = Long.parseLong(contratIdParam);
            List<Paiement> paiements = paiementDAO.findByContrat(contratId);
            req.setAttribute("paiements", paiements);

            if ("locataire".equalsIgnoreCase(role)) {
                req.getRequestDispatcher("/WEB-INF/jsp/locataire/dashboard.jsp").forward(req, resp);
            } else {
                req.getRequestDispatcher("/WEB-INF/jsp/locataire/paiements.jsp").forward(req, resp);
            }

        } else if (statutParam != null) {
            // 📌 Paiements filtrés par statut
            Paiement.StatutPaiement statut = Paiement.StatutPaiement.valueOf(statutParam.toUpperCase());
            List<Paiement> paiements = paiementDAO.findByStatut(statut);
            req.setAttribute("paiements", paiements);

            if ("admin".equalsIgnoreCase(role)) {
                req.getRequestDispatcher("/WEB-INF/jsp/admin/rapports.jsp").forward(req, resp);
            } else {
                req.getRequestDispatcher("/WEB-INF/jsp/locataire/paiements.jsp").forward(req, resp);
            }

        } else {
            // 📌 Tous les paiements
            List<Paiement> paiements = paiementDAO.findAll();
            req.setAttribute("paiements", paiements);

            if ("admin".equalsIgnoreCase(role)) {
                req.getRequestDispatcher("/WEB-INF/jsp/admin/rapports.jsp").forward(req, resp);
            } else if ("locataire".equalsIgnoreCase(role)) {
                req.getRequestDispatcher("/WEB-INF/jsp/locataire/dashboard.jsp").forward(req, resp);
            } else {
                req.getRequestDispatcher("/WEB-INF/jsp/locataire/paiements.jsp").forward(req, resp);
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        // ➕ Création d’un paiement
        Paiement paiement = new Paiement();
        paiement.setMontant(Double.parseDouble(req.getParameter("montant")));
        paiement.setDatePaiement(LocalDate.parse(req.getParameter("datePaiement")));
        paiement.setDateEcheance(LocalDate.parse(req.getParameter("dateEcheance")));
        paiement.setStatut(Paiement.StatutPaiement.valueOf(req.getParameter("statut")));

        // Associer contrat
        Long contratId = Long.parseLong(req.getParameter("contratId"));
        Contrat contrat = contratDAO.findById(contratId);
        paiement.setContrat(contrat);

        paiementDAO.save(paiement);

        resp.sendRedirect(req.getContextPath() + "/paiements?role=locataire");
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        // ✏️ Mise à jour d’un paiement
        Long id = Long.parseLong(req.getParameter("id"));
        Paiement paiement = paiementDAO.findById(id);

        if (paiement != null) {
            paiement.setMontant(Double.parseDouble(req.getParameter("montant")));
            paiement.setDatePaiement(LocalDate.parse(req.getParameter("datePaiement")));
            paiement.setDateEcheance(LocalDate.parse(req.getParameter("dateEcheance")));
            paiement.setStatut(Paiement.StatutPaiement.valueOf(req.getParameter("statut")));

            paiementDAO.update(paiement);
        }

        resp.sendRedirect(req.getContextPath() + "/paiements?role=locataire");
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Long id = Long.parseLong(req.getParameter("id"));
        paiementDAO.delete(id);
        resp.sendRedirect(req.getContextPath() + "/paiements?role=locataire");
    }
}
