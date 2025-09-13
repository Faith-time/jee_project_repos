package sn.team.gestion_loc_immeubles.Controllers;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import sn.team.gestion_loc_immeubles.Entities.Contrat;
import sn.team.gestion_loc_immeubles.Entities.Utilisateur;
import sn.team.gestion_loc_immeubles.Services.ContratService;

import java.io.IOException;
import java.util.List;

@WebServlet("/contrats")
public class ContratServlet extends HttpServlet {

    private ContratService contratService;

    @Override
    public void init() {
        this.contratService = new ContratService();
    }

// Modification de la partie doGet() dans ContratServlet
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getParameter("action");
        String idParam = req.getParameter("id");

        // Vérifier si l'utilisateur est connecté
        Utilisateur utilisateurConnecte = (Utilisateur) req.getSession().getAttribute("utilisateur");
        if (utilisateurConnecte == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        // Ajout de logs pour déboguer
        System.out.println("=== ContratServlet Debug ===");
        System.out.println("Action reçue: " + action);
        System.out.println("Utilisateur connecté ID: " + utilisateurConnecte.getId());
        System.out.println("Est locataire: " + utilisateurConnecte.isLocataire());

        if ("mes-contrats".equals(action) || action == null) { // <- Modification ici
            // Afficher les contrats de l'utilisateur connecté
            List<Contrat> contrats = contratService.listerParLocataire(utilisateurConnecte.getId());

            // Debug
            System.out.println("Nombre de contrats trouvés: " + contrats.size());
            for (Contrat c : contrats) {
                System.out.println("Contrat ID: " + c.getId() +
                        ", Locataire: " + c.getLocataire().getId() +
                        ", Unité: " + c.getUnite().getNumero());
            }

            req.setAttribute("contrats", contrats);

            // Redirection vers le bon fichier JSP
            req.getRequestDispatcher("/WEB-INF/jsp/locataire/mes_contrats.jsp").forward(req, resp); // <- Changé ici

        } else if ("detail".equals(action) && idParam != null) {
            // ... reste identique ...

        } else if ("tous".equals(action) && utilisateurConnecte.isAdmin()) {
            // ... reste identique ...

        } else {
            // Par défaut, afficher les contrats sans redirection
            List<Contrat> contrats = contratService.listerParLocataire(utilisateurConnecte.getId());
            req.setAttribute("contrats", contrats);
            req.getRequestDispatcher("/WEB-INF/jsp/locataire/mes_contrats.jsp").forward(req, resp);
        }
    }
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String action = req.getParameter("action");

        // Vérifier si l'utilisateur est connecté
        Utilisateur utilisateurConnecte = (Utilisateur) req.getSession().getAttribute("utilisateur");
        if (utilisateurConnecte == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        if ("creer-manuel".equals(action) && utilisateurConnecte.isAdmin()) {
            // Création manuelle d'un contrat (admin seulement)
            try {
                Long locataireId = Long.parseLong(req.getParameter("locataireId"));
                Long uniteId = Long.parseLong(req.getParameter("uniteId"));
                Double montant = Double.parseDouble(req.getParameter("montant"));

                Contrat contrat = contratService.creerContratLocation(locataireId, uniteId, montant);

                if (contrat != null) {
                    req.getSession().setAttribute("successMessage", "Contrat créé avec succès!");
                } else {
                    req.getSession().setAttribute("errorMessage", "Erreur lors de la création du contrat.");
                }

            } catch (Exception e) {
                req.getSession().setAttribute("errorMessage", "Erreur lors de la création du contrat: " + e.getMessage());
            }

            resp.sendRedirect(req.getContextPath() + "/contrats?action=tous");
        } else {
            resp.sendRedirect(req.getContextPath() + "/contrats?action=mes-contrats");
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String idParam = req.getParameter("id");

        // Vérifier si l'utilisateur est connecté et est admin
        Utilisateur utilisateurConnecte = (Utilisateur) req.getSession().getAttribute("utilisateur");
        if (utilisateurConnecte == null || !utilisateurConnecte.isAdmin()) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Accès non autorisé");
            return;
        }

        if (idParam != null) {
            try {
                Long contratId = Long.parseLong(idParam);
                contratService.supprimerContrat(contratId);
                req.getSession().setAttribute("successMessage", "Contrat supprimé avec succès!");
            } catch (Exception e) {
                req.getSession().setAttribute("errorMessage", "Erreur lors de la suppression du contrat: " + e.getMessage());
            }
        }

        resp.sendRedirect(req.getContextPath() + "/contrats?action=tous");
    }
}