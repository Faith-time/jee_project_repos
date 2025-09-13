package sn.team.gestion_loc_immeubles.Controllers;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import sn.team.gestion_loc_immeubles.Entities.Paiement;
import sn.team.gestion_loc_immeubles.Entities.Unite;
import sn.team.gestion_loc_immeubles.Entities.Utilisateur;
import sn.team.gestion_loc_immeubles.Services.PaiementService;

import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

@WebServlet(name = "LocataireServlet",
        urlPatterns = {"/locataire/dashboard", "/locataire/paiements", "/locataire/payer"})
public class LocataireDashboardServlet extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(LocataireDashboardServlet.class.getName());
    private PaiementService paiementService;

    @Override
    public void init() {
        System.out.println("*** INIT LocataireServlet ***");
        this.paiementService = new PaiementService();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        System.out.println("=== DEBUG SERVLET ===");
        System.out.println("URL demandée: " + req.getRequestURL());
        System.out.println("Servlet Path: " + req.getServletPath());
        System.out.println("Context Path: " + req.getContextPath());

        HttpSession session = req.getSession(false);

        // Vérification de la session
        if (session == null) {
            System.out.println("ERREUR: Session nulle");
            resp.sendRedirect(req.getContextPath() + "/auth?action=login&error=session_expired");
            return;
        }

        Object utilisateurObj = session.getAttribute("utilisateur");
        if (utilisateurObj == null) {
            System.out.println("ERREUR: Pas d'utilisateur en session");
            resp.sendRedirect(req.getContextPath() + "/auth?action=login&error=no_user");
            return;
        }

        Utilisateur utilisateurConnecte = (Utilisateur) utilisateurObj;
        System.out.println("Utilisateur trouvé: " + utilisateurConnecte.getId());
        System.out.println("Est locataire: " + utilisateurConnecte.isLocataire());

        try {
            // Vérification du rôle locataire
            if (!utilisateurConnecte.isLocataire()) {
                System.out.println("ERREUR: Utilisateur n'est pas locataire");
                resp.sendRedirect(req.getContextPath() + "/auth?action=login&error=access_denied");
                return;
            }

            Long locataireId = utilisateurConnecte.getId();
            System.out.println("ID locataire: " + locataireId);

            // Routage selon le path
            switch (req.getServletPath()) {

                case "/locataire/dashboard": {
                    System.out.println("=== Route: Dashboard ===");

                    long totalPaiements = paiementService.listerPaiementsParLocataire(locataireId).size();

                    req.setAttribute("utilisateur", utilisateurConnecte);
                    req.setAttribute("totalPaiements", totalPaiements);

                    LOGGER.info("Dashboard locataire chargé - ID utilisateur: " + locataireId);
                    req.getRequestDispatcher("/WEB-INF/jsp/locataire/dashboard.jsp").forward(req, resp);
                    break;
                }

                case "/locataire/paiements": {
                    System.out.println("=== Route: Paiements ===");

                    try {
                        // Mise à jour des statuts en retard
                        paiementService.mettreAJourPaiementsEnRetard();

                        // Récupération des paiements
                        List<Paiement> listePaiements = paiementService.listerPaiementsParLocataire(locataireId);
                        System.out.println("Nombre de paiements trouvés: " + listePaiements.size());

                        // Calcul des statistiques
                        long totalPaiements = listePaiements.size();
                        long paiementsPayes = paiementService.compterPaiementsPayesByLocataire(locataireId);
                        long paiementsEnAttente = paiementService.compterPaiementsByLocataireEtStatut(locataireId, Paiement.StatutPaiement.EN_ATTENTE);
                        long paiementsEnRetard = paiementService.compterPaiementsByLocataireEtStatut(locataireId, Paiement.StatutPaiement.EN_RETARD);

                        // Définition des attributs
                        req.setAttribute("utilisateur", utilisateurConnecte);
                        req.setAttribute("listePaiements", listePaiements);
                        req.setAttribute("totalPaiements", totalPaiements);
                        req.setAttribute("paiementsPayes", paiementsPayes);
                        req.setAttribute("paiementsEnAttente", paiementsEnAttente);
                        req.setAttribute("paiementsEnRetard", paiementsEnRetard);

                        System.out.println("=== Statistiques ===");
                        System.out.println("Total: " + totalPaiements);
                        System.out.println("Payés: " + paiementsPayes);
                        System.out.println("En attente: " + paiementsEnAttente);
                        System.out.println("En retard: " + paiementsEnRetard);

                        LOGGER.info("Page paiements locataire chargée - ID utilisateur: " + locataireId);

                        // Forward vers la page JSP
                        String jspPath = "/WEB-INF/jsp/locataire/paiements.jsp";
                        System.out.println("Forward vers: " + jspPath);
                        req.getRequestDispatcher(jspPath).forward(req, resp);

                    } catch (Exception e) {
                        System.out.println("ERREUR dans le traitement des paiements: " + e.getMessage());
                        e.printStackTrace();

                        req.setAttribute("errorMessage", "Erreur lors du chargement des paiements: " + e.getMessage());
                        resp.sendRedirect(req.getContextPath() + "/locataire/dashboard?error=paiements_error");
                    }
                    break;
                }

                case "/locataire/payer": {
                    System.out.println("=== Route: Payer ===");

                    try {
                        // Mise à jour des statuts en retard
                        paiementService.mettreAJourPaiementsEnRetard();

                        // Récupération des unités louées par le locataire
                        List<Unite> listeUnitesLouees = paiementService.listerUnitesLoueesParLocataire(locataireId);
                        System.out.println("Nombre d'unités louées trouvées: " + listeUnitesLouees.size());

                        // Définition des attributs pour la page payer
                        req.setAttribute("utilisateur", utilisateurConnecte);
                        req.setAttribute("listeUnitesLouees", listeUnitesLouees);

                        LOGGER.info("Page payer locataire chargée - ID utilisateur: " + locataireId +
                                " - Unités louées: " + listeUnitesLouees.size());

                        // Forward vers la page JSP payer
                        String jspPath = "/WEB-INF/jsp/locataire/payer.jsp";
                        System.out.println("Forward vers: " + jspPath);
                        req.getRequestDispatcher(jspPath).forward(req, resp);

                    } catch (Exception e) {
                        System.out.println("ERREUR dans le chargement de la page payer: " + e.getMessage());
                        e.printStackTrace();

                        session.setAttribute("errorMessage",
                                "Erreur lors du chargement de la page de paiement: " + e.getMessage());
                        resp.sendRedirect(req.getContextPath() + "/locataire/paiements");
                    }
                    break;
                }

                default:
                    System.out.println("ERREUR: Page inconnue: " + req.getServletPath());
                    resp.sendRedirect(req.getContextPath() + "/locataire/dashboard?error=unknown_page");
            }

        } catch (Exception e) {
            System.out.println("=== EXCEPTION GÉNÉRALE ===");
            System.out.println("Message: " + e.getMessage());
            LOGGER.severe("Erreur dans LocataireServlet: " + e.getMessage());
            e.printStackTrace();

            resp.sendRedirect(req.getContextPath() + "/locataire/dashboard?error=technical_error");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        // Rediriger les POST vers GET pour toutes les routes
        doGet(req, resp);
    }
}