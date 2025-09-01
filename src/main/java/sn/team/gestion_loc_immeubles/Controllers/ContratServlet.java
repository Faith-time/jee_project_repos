package sn.team.gestion_loc_immeubles.Controllers;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import sn.team.gestion_loc_immeubles.DAO.ContratDAO;
import sn.team.gestion_loc_immeubles.DAO.ContratDAOImpl;
import sn.team.gestion_loc_immeubles.DAO.UtilisateurDAO;
import sn.team.gestion_loc_immeubles.DAO.UtilisateurDAOImpl;
import sn.team.gestion_loc_immeubles.DAO.UniteDAO;
import sn.team.gestion_loc_immeubles.DAO.UniteDAOImpl;
import sn.team.gestion_loc_immeubles.Entities.Contrat;
import sn.team.gestion_loc_immeubles.Entities.Locataire;
import sn.team.gestion_loc_immeubles.Entities.Unite;
import sn.team.gestion_loc_immeubles.Entities.Utilisateur;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@WebServlet("/contrats")
public class ContratServlet extends HttpServlet {

    private ContratDAO contratDAO;
    private UtilisateurDAO utilisateurDAO;
    private UniteDAO uniteDAO;

    @Override
    public void init() {
        this.contratDAO = new ContratDAOImpl();
        this.utilisateurDAO = new UtilisateurDAOImpl();
        this.uniteDAO = new UniteDAOImpl();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String typeParam = req.getParameter("type_utilisateur"); // 🔥 remplacé par type_utilisateur

        if ("admin".equalsIgnoreCase(typeParam)) {
            List<Contrat> contrats = contratDAO.findAll();
            req.setAttribute("contrats", contrats);
            req.getRequestDispatcher("/WEB-INF/jsp/admin/gestions-contrats.jsp").forward(req, resp);

        } else if ("locataire".equalsIgnoreCase(typeParam)) {
            String locataireIdParam = req.getParameter("locataireId");

            if (locataireIdParam != null) {
                try {
                    Long locataireId = Long.parseLong(locataireIdParam);

                    // Récupérer les contrats associés à ce locataire
                    List<Contrat> contrats = contratDAO.findByLocataire(locataireId);

                    req.setAttribute("contrats", contrats);
                    req.getRequestDispatcher("/WEB-INF/jsp/locataire/mes_contrats.jsp").forward(req, resp);

                } catch (NumberFormatException e) {
                    resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID locataire invalide");
                }
            } else {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Paramètre locataireId requis");
            }

        } else {
            // Si le type_utilisateur n’est pas reconnu → erreur
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Paramètre type_utilisateur manquant ou invalide");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        // ➕ Création d’un contrat
        Contrat contrat = new Contrat();

        contrat.setDateDebut(LocalDate.parse(req.getParameter("dateDebut")));
        String dateFinParam = req.getParameter("dateFin");
        if (dateFinParam != null && !dateFinParam.isBlank()) {
            contrat.setDateFin(LocalDate.parse(dateFinParam));
        }
        contrat.setMontant(Double.parseDouble(req.getParameter("montant")));

        // Associer locataire
        Long locataireId = Long.parseLong(req.getParameter("locataireId"));
        Utilisateur user = utilisateurDAO.findById(locataireId);
        if (user instanceof Locataire locataire) {
            contrat.setLocataire(locataire);
        }

        // Associer unité
        Long uniteId = Long.parseLong(req.getParameter("uniteId"));
        Unite unite = uniteDAO.findById(uniteId);
        contrat.setUnite(unite);

        contratDAO.save(contrat);

        resp.sendRedirect(req.getContextPath() + "/contrats?type_utilisateur=locataire"); // 🔥 modifié
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        // ✏️ Mise à jour d’un contrat
        Long id = Long.parseLong(req.getParameter("id"));
        Contrat contrat = contratDAO.findById(id);

        if (contrat != null) {
            contrat.setDateDebut(LocalDate.parse(req.getParameter("dateDebut")));
            String dateFinParam = req.getParameter("dateFin");
            if (dateFinParam != null && !dateFinParam.isBlank()) {
                contrat.setDateFin(LocalDate.parse(dateFinParam));
            }
            contrat.setMontant(Double.parseDouble(req.getParameter("montant")));

            // Mise à jour locataire
            Long locataireId = Long.parseLong(req.getParameter("locataireId"));
            Utilisateur user = utilisateurDAO.findById(locataireId);
            if (user instanceof Locataire locataire) {
                contrat.setLocataire(locataire);
            }

            // Mise à jour unité
            Long uniteId = Long.parseLong(req.getParameter("uniteId"));
            Unite unite = uniteDAO.findById(uniteId);
            contrat.setUnite(unite);

            contratDAO.update(contrat);
        }

        resp.sendRedirect(req.getContextPath() + "/contrats?type_utilisateur=locataire"); // 🔥 modifié
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Long id = Long.parseLong(req.getParameter("id"));
        contratDAO.delete(id);
        resp.sendRedirect(req.getContextPath() + "/contrats?type_utilisateur=locataire"); // 🔥 modifié
    }
}
