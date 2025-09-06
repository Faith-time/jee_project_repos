package sn.team.gestion_loc_immeubles.Controllers;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import sn.team.gestion_loc_immeubles.Entities.Role;
import sn.team.gestion_loc_immeubles.Entities.Utilisateur;
import sn.team.gestion_loc_immeubles.Services.UtilisateurService;
import sn.team.gestion_loc_immeubles.Services.ImmeubleService;
import sn.team.gestion_loc_immeubles.Services.ContratService;
import sn.team.gestion_loc_immeubles.Services.PaiementService;

import java.io.IOException;

@WebServlet(name = "AdminDashboardServlet", urlPatterns = {"/admin/dashboard"})
public class AdminDashboardServlet extends HttpServlet {

    private final UtilisateurService utilisateurService = new UtilisateurService();
    private final ImmeubleService immeubleService = new ImmeubleService();
    private final ContratService contratService = new ContratService();
    private final PaiementService paiementService = new PaiementService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        // Vérifier l'authentification et les permissions
        if (!verifierAccesAdmin(req, resp)) {
            return;
        }

        // Récupérer l'utilisateur connecté depuis la session
        HttpSession session = req.getSession(false);
        Utilisateur admin = (Utilisateur) session.getAttribute("utilisateur");

        // Ajouter les données nécessaires pour le dashboard admin
        req.setAttribute("admin", admin);

        try {
            // Charger les statistiques pour le dashboard
            chargerStatistiques(req);

            // Afficher message de bienvenue si présent
            String welcomeMessage = (String) session.getAttribute("welcomeMessage");
            if (welcomeMessage != null) {
                req.setAttribute("success", welcomeMessage);
                session.removeAttribute("welcomeMessage");
            }

        } catch (Exception e) {
            System.err.println("Erreur lors du chargement des statistiques: " + e.getMessage());
            e.printStackTrace();
            req.setAttribute("error", "Erreur lors du chargement des données du dashboard");
        }

        // Rediriger vers la page JSP du dashboard admin
        req.getRequestDispatcher("/WEB-INF/jsp/admin/dashboard.jsp").forward(req, resp);
    }

    /**
     * Charge toutes les statistiques nécessaires pour le dashboard admin
     */
    private void chargerStatistiques(HttpServletRequest req) {
        try {
            // Statistiques des utilisateurs
            long nbUtilisateurs = utilisateurService.compterTousUtilisateurs();
            long nbProprietaires = utilisateurService.compterParRole(Role.PROPRIETAIRE);
            long nbLocataires = utilisateurService.compterParRole(Role.LOCATAIRE);
            long nbAdmins = utilisateurService.compterParRole(Role.ADMIN);

            // Statistiques des biens
            long nbImmeubles = immeubleService.compterTousImmeubles();

            // Statistiques des contrats
            long nbContrats = contratService.compterTousContrats();

            // Statistiques des paiements
            long nbPaiements = paiementService.compterTousPaiements();

            // Ajouter aux attributs de la requête
            req.setAttribute("nbUtilisateurs", nbUtilisateurs);
            req.setAttribute("nbProprietaires", nbProprietaires);
            req.setAttribute("nbLocataires", nbLocataires);
            req.setAttribute("nbAdmins", nbAdmins);
            req.setAttribute("nbImmeubles", nbImmeubles);
            req.setAttribute("nbContrats", nbContrats);
            req.setAttribute("nbPaiements", nbPaiements);

            // Log pour debugging
            System.out.println("=== Statistiques Dashboard Admin ===");
            System.out.println("Utilisateurs: " + nbUtilisateurs);
            System.out.println("Propriétaires: " + nbProprietaires);
            System.out.println("Locataires: " + nbLocataires);
            System.out.println("Admins: " + nbAdmins);
            System.out.println("Immeubles: " + nbImmeubles);
            System.out.println("Contrats: " + nbContrats);
            System.out.println("Paiements: " + nbPaiements);

        } catch (Exception e) {
            System.err.println("Erreur lors du chargement des statistiques: " + e.getMessage());
            e.printStackTrace();

            // Valeurs par défaut en cas d'erreur
            req.setAttribute("nbUtilisateurs", 0L);
            req.setAttribute("nbProprietaires", 0L);
            req.setAttribute("nbLocataires", 0L);
            req.setAttribute("nbAdmins", 0L);
            req.setAttribute("nbImmeubles", 0L);
            req.setAttribute("nbContrats", 0L);
            req.setAttribute("nbPaiements", 0L);
        }
    }

    /**
     * Vérifie l'accès admin et redirige si nécessaire
     */
    private boolean verifierAccesAdmin(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        HttpSession session = req.getSession(false);

        // Vérifier si l'utilisateur est connecté
        if (session == null || session.getAttribute("utilisateur") == null) {
            System.out.println("Accès refusé: utilisateur non connecté");
            resp.sendRedirect(req.getContextPath() + "/auth?action=login");
            return false;
        }

        Utilisateur utilisateur = (Utilisateur) session.getAttribute("utilisateur");

        // Vérifier le rôle de l'utilisateur
        if (!utilisateur.isAdmin()) {
            System.out.println("Accès refusé: rôle " + utilisateur.getRole() + " (ADMIN requis)");
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Accès non autorisé - Droits administrateur requis");
            return false;
        }

        System.out.println("Accès accordé pour l'admin: " + utilisateur.getEmail());
        return true;
    }
}
