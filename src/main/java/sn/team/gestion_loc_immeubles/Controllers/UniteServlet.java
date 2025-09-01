package sn.team.gestion_loc_immeubles.Controllers;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import sn.team.gestion_loc_immeubles.DAO.UniteDAO;
import sn.team.gestion_loc_immeubles.DAO.UniteDAOImpl;
import sn.team.gestion_loc_immeubles.DAO.ImmeubleDAO;
import sn.team.gestion_loc_immeubles.DAO.ImmeubleDAOImpl;
import sn.team.gestion_loc_immeubles.Entities.Unite;
import sn.team.gestion_loc_immeubles.Entities.Immeuble;

import java.io.IOException;
import java.util.List;

@WebServlet("/unites")
public class UniteServlet extends HttpServlet {

    private UniteDAO uniteDAO;
    private ImmeubleDAO immeubleDAO;

    @Override
    public void init() {
        this.uniteDAO = new UniteDAOImpl();
        this.immeubleDAO = new ImmeubleDAOImpl();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String idParam = req.getParameter("id");
        String immeubleIdParam = req.getParameter("immeubleId");
        String role = req.getParameter("role"); // "admin", "proprietaire", "locataire"

        if (idParam != null) {
            // 🔎 Détail d’une unité
            Long id = Long.parseLong(idParam);
            Unite unite = uniteDAO.findById(id);
            req.setAttribute("unite", unite);

            req.getRequestDispatcher("/WEB-INF/jsp/proprietaire/dashboard.jsp").forward(req, resp);

        } else if (immeubleIdParam != null) {
            // 📌 Liste des unités d’un immeuble
            Long immeubleId = Long.parseLong(immeubleIdParam);
            List<Unite> unites = uniteDAO.findByImmeuble(immeubleId);
            req.setAttribute("unites", unites);

            req.getRequestDispatcher("/WEB-INF/jsp/proprietaire/gestion_unites.jsp").forward(req, resp);

        } else {
            // 📌 Liste générale (selon rôle)
            List<Unite> unites = uniteDAO.findAll();
            req.setAttribute("unites", unites);

            if ("admin".equalsIgnoreCase(role)) {
                req.getRequestDispatcher("/WEB-INF/jsp/admin/rapports.jsp").forward(req, resp);
            } else if ("locataire".equalsIgnoreCase(role)) {
                req.getRequestDispatcher("/WEB-INF/jsp/proprietaire/gestion_unites.jsp").forward(req, resp);
            } else {
                req.getRequestDispatcher("/WEB-INF/jsp/proprietaire/gestion_unites.jsp").forward(req, resp);
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        // ➕ Création d’une nouvelle unité
        Unite unite = new Unite();
        unite.setNumero(Integer.parseInt(req.getParameter("numero")));
        unite.setNbPieces(Integer.parseInt(req.getParameter("nbPieces")));
        unite.setSuperficie(Double.parseDouble(req.getParameter("superficie")));
        unite.setLoyer(Double.parseDouble(req.getParameter("loyer")));

        // Associer à un immeuble
        Long immeubleId = Long.parseLong(req.getParameter("immeubleId"));
        Immeuble immeuble = immeubleDAO.findById(immeubleId);
        if (immeuble != null) {
            unite.setImmeuble(immeuble);
            uniteDAO.save(unite);
        }

        resp.sendRedirect(req.getContextPath() + "/unites?immeubleId=" + immeubleId);
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        // ✏️ Mise à jour d’une unité
        Long id = Long.parseLong(req.getParameter("id"));
        Unite unite = uniteDAO.findById(id);

        if (unite != null) {
            unite.setNumero(Integer.parseInt(req.getParameter("numero")));
            unite.setNbPieces(Integer.parseInt(req.getParameter("nbPieces")));
            unite.setSuperficie(Double.parseDouble(req.getParameter("superficie")));
            unite.setLoyer(Double.parseDouble(req.getParameter("loyer")));

            // Mettre à jour l’immeuble associé
            Long immeubleId = Long.parseLong(req.getParameter("immeubleId"));
            Immeuble immeuble = immeubleDAO.findById(immeubleId);
            if (immeuble != null) {
                unite.setImmeuble(immeuble);
                uniteDAO.update(unite);
            }
        }

        resp.sendRedirect(req.getContextPath() + "/unites?immeubleId=" + req.getParameter("immeubleId"));
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Long id = Long.parseLong(req.getParameter("id"));
        uniteDAO.delete(id);
        resp.sendRedirect(req.getContextPath() + "/unites");
    }
}
