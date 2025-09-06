package sn.team.gestion_loc_immeubles.Controllers;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import sn.team.gestion_loc_immeubles.Entities.Utilisateur;
import sn.team.gestion_loc_immeubles.Entities.Role;
import sn.team.gestion_loc_immeubles.Services.ImmeubleService;
import sn.team.gestion_loc_immeubles.Services.UniteService;
import sn.team.gestion_loc_immeubles.Services.LocataireService;
import sn.team.gestion_loc_immeubles.Services.PaiementService;

import java.io.IOException;

/**
 * Servlet responsable de l'affichage du dashboard propriétaire
 * Gère l'affichage des statistiques et informations principales pour un propriétaire
 */
@WebServlet(name = "ProprietaireDashboardServlet", urlPatterns = {"/proprietaire/dashboard"})
public class ProprietaireDashboardServlet extends HttpServlet {

    // Services pour récupérer les données
    private final ImmeubleService immeubleService = new ImmeubleService();
    private final UniteService uniteService = new UniteService();
    private final LocataireService locataireService = new LocataireService();
    private final PaiementService paiementService = new PaiementService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        // Vérifier si l'utilisateur est connecté
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("utilisateur") == null) {
            System.out.println("DEBUG: Utilisateur non connecté, redirection vers login");
            resp.sendRedirect(req.getContextPath() + "/auth?action=login");
            return;
        }

        // Récupérer l'utilisateur de la session
        Utilisateur utilisateur = (Utilisateur) session.getAttribute("utilisateur");
        System.out.println("DEBUG: Utilisateur connecté: " + utilisateur.getEmail()
                + " - Rôle: " + utilisateur.getRole());

        // Vérifier que l'utilisateur est bien un propriétaire
        if (utilisateur.getRole() != Role.PROPRIETAIRE) {
            System.out.println("DEBUG: Utilisateur non autorisé (rôle: " + utilisateur.getRole() + ")");
            resp.sendError(HttpServletResponse.SC_FORBIDDEN,
                    "Accès réservé aux propriétaires");
            return;
        }

        try {
            // Charger les statistiques du propriétaire
            long proprietaireId = utilisateur.getId();

            // Compter les immeubles du propriétaire
            int nbImmeubles = immeubleService.compterImmeublesDuProprietaire(proprietaireId);

            // Compter les unités du propriétaire
            int nbUnites = uniteService.compterUnitesDuProprietaire(proprietaireId);

            // Compter les locataires du propriétaire
            int nbLocataires = locataireService.compterLocatairesDuProprietaire(proprietaireId);

            // Compter les paiements reçus par le propriétaire

            // Ajouter les statistiques comme attributs de la requête
            req.setAttribute("nbImmeubles", nbImmeubles);
            req.setAttribute("nbUnites", nbUnites);
            req.setAttribute("nbLocataires", nbLocataires);

            System.out.println("DEBUG: Statistiques chargées - Immeubles: " + nbImmeubles
                    + ", Unités: " + nbUnites
                    + ", Locataires: " + nbLocataires
                   );

            // Gérer le message de bienvenue s'il existe
            String welcomeMessage = (String) session.getAttribute("welcomeMessage");
            if (welcomeMessage != null) {
                req.setAttribute("welcomeMessage", welcomeMessage);
                session.removeAttribute("welcomeMessage");
            }

        } catch (Exception e) {
            System.err.println("Erreur lors du chargement des statistiques pour le propriétaire "
                    + utilisateur.getId() + ": " + e.getMessage());
            e.printStackTrace();

            // En cas d'erreur, mettre des valeurs par défaut
            req.setAttribute("nbImmeubles", 0);
            req.setAttribute("nbUnites", 0);
            req.setAttribute("nbLocataires", 0);
            req.setAttribute("nbPaiements", 0);

            // Optionnel: ajouter un message d'erreur
            req.setAttribute("errorMessage",
                    "Erreur lors du chargement des statistiques. Veuillez réessayer.");
        }

        // Rediriger vers la page JSP du dashboard
        System.out.println("DEBUG: Redirection vers /WEB-INF/jsp/proprietaire/dashboard.jsp");
        req.getRequestDispatcher("/WEB-INF/jsp/proprietaire/dashboard.jsp")
                .forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        // Pour l'instant, rediriger vers GET
        // Vous pouvez ajouter ici la logique pour traiter des actions POST si nécessaire
        doGet(req, resp);
    }

    /**
     * Méthode utilitaire pour vérifier l'autorisation
     */
    private boolean estProprietaireAutorise(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        if (session == null) return false;

        Utilisateur utilisateur = (Utilisateur) session.getAttribute("utilisateur");
        return utilisateur != null && utilisateur.getRole() == Role.PROPRIETAIRE;
    }
}