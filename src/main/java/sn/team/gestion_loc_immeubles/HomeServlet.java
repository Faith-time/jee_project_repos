package sn.team.gestion_loc_immeubles;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import sn.team.gestion_loc_immeubles.Entities.Utilisateur;

import java.io.IOException;

/**
 * Servlet pour gérer la page d'accueil
 */
@WebServlet({"/", "/home"})
public class HomeServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        HttpSession session = req.getSession(false);

        // Vérifier si l'utilisateur est connecté
        if (session == null || session.getAttribute("utilisateur") == null) {
            // Rediriger vers la page de connexion
            resp.sendRedirect(req.getContextPath() + "/auth?action=login");
            return;
        }

        // Utilisateur connecté, afficher le dashboard approprié selon le rôle
        String role = (String) session.getAttribute("role");
        Utilisateur utilisateur = (Utilisateur) session.getAttribute("utilisateur");

        // Ajouter des informations pour la page d'accueil
        req.setAttribute("utilisateur", utilisateur);
        req.setAttribute("role", role);

        // Message de bienvenue s'il existe
        String welcomeMessage = (String) session.getAttribute("welcomeMessage");
        if (welcomeMessage != null) {
            req.setAttribute("welcomeMessage", welcomeMessage);
            session.removeAttribute("welcomeMessage");
        }

        // Rediriger vers le dashboard approprié selon le rôle
        switch (role != null ? role : "UTILISATEUR") {
            case "ADMIN":
                // Pour l'admin, rediriger vers la gestion des utilisateurs
                resp.sendRedirect(req.getContextPath() + "/admin/dashboard");
                break;

            case "PROPRIETAIRE":
                // Pour le propriétaire, rediriger vers la gestion des immeubles
                resp.sendRedirect(req.getContextPath() + "/proprietaire/dashboard");
                break;

            case "LOCATAIRE":
                // Pour le locataire, rediriger vers ses contrats
                resp.sendRedirect(req.getContextPath() + "/locataire/dashboard");
                break;

            default:
                // Page d'accueil générique - chemin corrigé selon votre structure
                req.getRequestDispatcher("/index.jsp").forward(req, resp);
                break;
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        doGet(req, resp);
    }
}