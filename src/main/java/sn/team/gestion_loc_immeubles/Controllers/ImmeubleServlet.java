package sn.team.gestion_loc_immeubles.Controllers;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import sn.team.gestion_loc_immeubles.DAO.ImmeubleDAO;
import sn.team.gestion_loc_immeubles.DAO.ImmeubleDAOImpl;
import sn.team.gestion_loc_immeubles.DAO.UtilisateurDAO;
import sn.team.gestion_loc_immeubles.DAO.UtilisateurDAOImpl;
import sn.team.gestion_loc_immeubles.Entities.Immeuble;
import sn.team.gestion_loc_immeubles.Entities.Proprietaire;
import sn.team.gestion_loc_immeubles.Entities.Utilisateur;

import java.io.IOException;
import java.util.List;

@WebServlet("/immeubles")
public class ImmeubleServlet extends HttpServlet {

    private ImmeubleDAO immeubleDAO;
    private UtilisateurDAO utilisateurDAO;

    @Override
    public void init() {
        this.immeubleDAO = new ImmeubleDAOImpl();
        this.utilisateurDAO = new UtilisateurDAOImpl();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String idParam = req.getParameter("id");
        String proprietaireIdParam = req.getParameter("proprietaireId");

        if (idParam != null) {
            // 🔎 Détail d’un immeuble
            Long id = Long.parseLong(idParam);
            Immeuble immeuble = immeubleDAO.findById(id);
            req.setAttribute("immeuble", immeuble);

            req.getRequestDispatcher("/WEB-INF/jsp/proprietaire/dashboard.jsp").forward(req, resp);

        } else if (proprietaireIdParam != null) {
            // 📌 Liste des immeubles d’un propriétaire
            Long proprietaireId = Long.parseLong(proprietaireIdParam);
            List<Immeuble> immeubles = immeubleDAO.findByProprietaire(proprietaireId);
            req.setAttribute("immeubles", immeubles);

            req.getRequestDispatcher("/WEB-INF/jsp/proprietaire/gestion_immeubles.jsp").forward(req, resp);

        } else {
            // 📌 Liste complète des immeubles (admin)
            List<Immeuble> immeubles = immeubleDAO.findAll();
            req.setAttribute("immeubles", immeubles);

            req.getRequestDispatcher("/WEB-INF/jsp/admin/rapports.jsp").forward(req, resp);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        // ➕ Création d’un immeuble
        Immeuble immeuble = new Immeuble();
        immeuble.setNom(req.getParameter("nom"));
        immeuble.setAdresse(req.getParameter("adresse"));
        immeuble.setDescription(req.getParameter("description"));

        // Associer au propriétaire
        Long proprietaireId = Long.parseLong(req.getParameter("proprietaireId"));
        Utilisateur user = utilisateurDAO.findById(proprietaireId);

        if (user instanceof Proprietaire proprietaire) {
            immeuble.setProprietaire(proprietaire);
            immeubleDAO.save(immeuble);
        }

        resp.sendRedirect(req.getContextPath() + "/immeubles?proprietaireId=" + proprietaireId);
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        // ✏️ Mise à jour d’un immeuble
        Long id = Long.parseLong(req.getParameter("id"));
        Immeuble immeuble = immeubleDAO.findById(id);

        if (immeuble != null) {
            immeuble.setNom(req.getParameter("nom"));
            immeuble.setAdresse(req.getParameter("adresse"));
            immeuble.setDescription(req.getParameter("description"));

            Long proprietaireId = Long.parseLong(req.getParameter("proprietaireId"));
            Utilisateur user = utilisateurDAO.findById(proprietaireId);

            if (user instanceof Proprietaire proprietaire) {
                immeuble.setProprietaire(proprietaire);
                immeubleDAO.update(immeuble);
            }
        }

        resp.sendRedirect(req.getContextPath() + "/immeubles?proprietaireId=" + req.getParameter("proprietaireId"));
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Long id = Long.parseLong(req.getParameter("id"));
        immeubleDAO.delete(id);
        resp.sendRedirect(req.getContextPath() + "/immeubles");
    }
}
