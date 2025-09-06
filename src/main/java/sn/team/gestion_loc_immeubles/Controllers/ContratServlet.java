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

        if ("mes-contrats".equals(action)) {
            // Afficher les contrats de l'utilisateur connecté
            List<Contrat> contrats = contratService.listerParLocataire(utilisateurConnecte.getId());
            req.setAttribute("contrats", contrats);
            req.getRequestDispatcher("/WEB-INF/jsp/locataire/mes_contrats.jsp").forward(req, resp);

        } else if ("detail".equals(action) && idParam != null) {
            // Afficher le détail d'un contrat
            Long contratId = Long.parseLong(idParam);
            Contrat contrat = contratService.trouverParId(contratId);

            // Vérifier que le contrat appartient à l'utilisateur connecté (sécurité)
            if (contrat != null && contrat.getLocataire().getId().equals(utilisateurConnecte.getId())) {
                req.setAttribute("contrat", contrat);
                req.getRequestDispatcher("/WEB-INF/jsp/locataire/detail_contrat.jsp").forward(req, resp);
            } else {
                req.getSession().setAttribute("errorMessage", "Contrat non trouvé ou accès non autorisé.");
                resp.sendRedirect(req.getContextPath() + "/contrats?action=mes-contrats");
            }

        } else if ("tous".equals(action) && utilisateurConnecte.isAdmin()) {
            // Liste de tous les contrats (admin seulement)
            List<Contrat> contrats = contratService.listerContrats();
            req.setAttribute("contrats", contrats);
            req.getRequestDispatcher("/WEB-INF/jsp/admin/tous_contrats.jsp").forward(req, resp);

        } else {
            // Par défaut, rediriger vers les contrats de l'utilisateur
            resp.sendRedirect(req.getContextPath() + "/contrats?action=mes-contrats");
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