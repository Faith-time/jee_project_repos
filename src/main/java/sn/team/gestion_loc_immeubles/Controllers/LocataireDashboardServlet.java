package sn.team.gestion_loc_immeubles.Controllers;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import sn.team.gestion_loc_immeubles.Entities.Role;
import sn.team.gestion_loc_immeubles.Entities.Utilisateur;
import sn.team.gestion_loc_immeubles.Services.LocataireService;

import java.io.IOException;
import java.util.List;

@WebServlet(name = "LocataireDashboardServlet", urlPatterns = {"/locataire/dashboard"})
public class LocataireDashboardServlet extends HttpServlet {

    private LocataireService locataireService;

    @Override
    public void init() {
        this.locataireService = new LocataireService();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);

        // Vérifier si l'utilisateur est connecté
        if (session == null || session.getAttribute("utilisateur") == null) {
            resp.sendRedirect(req.getContextPath() + "/auth?action=login");
            return;
        }

        Utilisateur utilisateurConnecte = (Utilisateur) session.getAttribute("utilisateur");

        try {
            // Admin : voir tous les locataires
            if (utilisateurConnecte.isAdmin()) {
                List<Utilisateur> locataires = locataireService.getAllLocataires();
                req.setAttribute("locataires", locataires);
                req.setAttribute("isAdminView", true);
                req.getRequestDispatcher("/WEB-INF/jsp/proprietaire/Liste_Locataires.jsp").forward(req, resp);

                // Propriétaire : voir ses propres locataires
            } else if (utilisateurConnecte.isProprietaire()) {
                List<Utilisateur> locataires = locataireService.getLocatairesDuProprietaire(utilisateurConnecte.getId());
                req.setAttribute("locataires", locataires);
                req.setAttribute("proprietaireId", utilisateurConnecte.getId());
                req.setAttribute("isProprietaireView", true);
                req.getRequestDispatcher("/WEB-INF/jsp/proprietaire/Liste_Locataires.jsp").forward(req, resp);

                // Locataire : accéder à son dashboard
            } else if (utilisateurConnecte.isLocataire()) {
                // Exemple : récupérer les informations pertinentes pour le locataire
                // Ici tu peux appeler des services pour ses unités louées, ses paiements, etc.
                req.setAttribute("utilisateur", utilisateurConnecte);
                req.getRequestDispatcher("/WEB-INF/jsp/locataire/dashboard.jsp").forward(req, resp);

                // Tout autre rôle : accès interdit
            } else {
                resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Accès non autorisé");
            }

        } catch (Exception e) {
            System.err.println("Erreur dans LocataireDashboardServlet: " + e.getMessage());
            e.printStackTrace();
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Erreur interne du serveur");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // Pour de futures fonctionnalités (ex : modification du profil locataire)
        resp.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED, "Méthode non implémentée");
    }
}
