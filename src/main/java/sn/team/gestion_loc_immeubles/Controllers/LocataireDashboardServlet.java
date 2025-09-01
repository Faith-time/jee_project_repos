package sn.team.gestion_loc_immeubles.Controllers;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import sn.team.gestion_loc_immeubles.Entities.Utilisateur;

import java.io.IOException;

@WebServlet("/locataire/dashboard")
public class LocataireDashboardServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        // Vérifier la session et le rôle
        HttpSession session = req.getSession(false);
        if (session == null || !"LOCATAIRE".equals(session.getAttribute("role"))) {
            resp.sendRedirect(req.getContextPath() + "/auth?action=login");
            return;
        }

        // Récupérer l'utilisateur connecté
        Utilisateur utilisateur = (Utilisateur) session.getAttribute("utilisateur");

        // TODO: Récupérer les données réelles depuis la base de données
        // List<Contrat> contrats = contratDAO.findByLocataireId(utilisateur.getId());
        // List<Paiement> paiements = paiementDAO.findByLocataireId(utilisateur.getId());

        // Statistiques pour le dashboard selon votre JSP existante
        long nbContrats = 0; // contrats.size();
        long nbPaiementsPayes = 0; // paiements.stream().filter(p -> "PAYE".equals(p.getStatut())).count();
        long nbPaiementsAttente = 0; // paiements.stream().filter(p -> "ATTENTE".equals(p.getStatut())).count();
        double soldeRestant = 0.0; // Calculer selon vos règles métier

        // Ajouter les attributs requis par votre JSP
        req.setAttribute("nbContrats", nbContrats);
        req.setAttribute("nbPaiementsPayes", nbPaiementsPayes);
        req.setAttribute("nbPaiementsAttente", nbPaiementsAttente);
        req.setAttribute("soldeRestant", soldeRestant);

        // Forward vers votre JSP existante
        req.getRequestDispatcher("/WEB-INF/jsp/locataire/dashboard.jsp").forward(req, resp);
    }
}