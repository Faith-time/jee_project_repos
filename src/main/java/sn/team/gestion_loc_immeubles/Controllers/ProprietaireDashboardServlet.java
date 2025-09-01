package sn.team.gestion_loc_immeubles.Controllers;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import sn.team.gestion_loc_immeubles.Entities.Utilisateur;

import java.io.IOException;

@WebServlet("/proprietaire/dashboard")
public class ProprietaireDashboardServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        // Vérifier la session et le rôle
        HttpSession session = req.getSession(false);
        if (session == null || !"PROPRIETAIRE".equals(session.getAttribute("role"))) {
            resp.sendRedirect(req.getContextPath() + "/auth?action=login");
            return;
        }

        // Récupérer l'utilisateur connecté
        Utilisateur utilisateur = (Utilisateur) session.getAttribute("utilisateur");

        // TODO: Récupérer les données réelles depuis la base de données
        // List<Immeuble> immeubles = immeubleDAO.findByProprietaireId(utilisateur.getId());
        // List<Unite> unites = uniteDAO.findByProprietaireId(utilisateur.getId());
        // List<Locataire> locataires = locataireDAO.findByProprietaireId(utilisateur.getId());
        // List<Paiement> paiements = paiementDAO.findByProprietaireId(utilisateur.getId());

        // Statistiques pour le dashboard selon votre JSP existante
        long nbImmeubles = 0; // immeubles.size();
        long nbUnites = 0; // unites.size();
        long nbLocataires = 0; // locataires.size();
        long nbPaiements = 0; // paiements.size();

        // Ajouter les attributs requis par votre JSP
        req.setAttribute("nbImmeubles", nbImmeubles);
        req.setAttribute("nbUnites", nbUnites);
        req.setAttribute("nbLocataires", nbLocataires);
        req.setAttribute("nbPaiements", nbPaiements);

        // Forward vers votre JSP existante
        req.getRequestDispatcher("/WEB-INF/jsp/proprietaire/dashboard.jsp").forward(req, resp);
    }
}