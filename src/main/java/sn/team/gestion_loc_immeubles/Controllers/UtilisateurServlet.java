package sn.team.gestion_loc_immeubles.Controllers;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import sn.team.gestion_loc_immeubles.Services.AuthService;
import sn.team.gestion_loc_immeubles.Services.LocataireService;
import sn.team.gestion_loc_immeubles.Services.UtilisateurService;
import sn.team.gestion_loc_immeubles.Entities.Utilisateur;
import sn.team.gestion_loc_immeubles.Entities.Role;

import java.io.IOException;
import java.util.List;

/**
 * Servlet de gestion des utilisateurs
 * Gère l'affichage, la création, la modification et la suppression des utilisateurs
 */
@WebServlet(name = "UtilisateurServlet", urlPatterns = {
        "/utilisateurs",
        "/proprietaires",
        "/locataires",
        "/visiteurs"
})
public class UtilisateurServlet extends HttpServlet {

    private UtilisateurService utilisateurService;
    private AuthService authService;
    private LocataireService locataireService;

    @Override
    public void init() throws ServletException {
        // Initialiser les services
        this.utilisateurService = new UtilisateurService();
        this.authService = new AuthService();
        this.locataireService = new LocataireService();

    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        // Vérifier l'authentification
        if (!verifierAuthentification(req, resp)) {
            return;
        }

        String requestURI = req.getRequestURI();
        String contextPath = req.getContextPath();
        String action = req.getParameter("action");

        System.out.println("=== DEBUG doGet ===");
        System.out.println("RequestURI: " + requestURI);
        System.out.println("ContextPath: " + contextPath);
        System.out.println("Action: " + action);

        // Gestion des actions spécifiques
        if (action != null) {
            switch (action.toLowerCase()) {
                case "form":
                    String id = req.getParameter("id");
                    afficherFormulaireUtilisateur(req, resp, id);
                    return;
                case "delete":
                    String deleteId = req.getParameter("id");
                    supprimerUtilisateur(req, resp, deleteId);
                    return;
                case "promote":
                    String promoteId = req.getParameter("id");
                    promouvoirUtilisateur(req, resp, promoteId);
                    return;
                default:
                    resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Action inconnue: " + action);
                    return;
            }
        }

        // Déterminer l'action selon l'URL
        if (requestURI.equals(contextPath + "/utilisateurs")) {
            afficherListeUtilisateurs(req, resp);
        } else if (requestURI.equals(contextPath + "/proprietaires")) {
            afficherListeProprietaires(req, resp);
        } else if (requestURI.equals(contextPath + "/locataires")) {
            afficherListeLocataires(req, resp);
        } else if (requestURI.equals(contextPath + "/visiteurs")) {
            afficherListeVisiteurs(req, resp);
        } else {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        System.out.println("=== DEBUG doPost ===");
        System.out.println("Content-Type: " + req.getContentType());

        if (!verifierAuthentification(req, resp)) return;

        String action = req.getParameter("action");
        System.out.println("Action reçue: " + action);

        if (action == null || action.trim().isEmpty()) {
            System.out.println("ERREUR: Action manquante");
            req.setAttribute("error", "Action manquante");
            afficherListeUtilisateurs(req, resp);
            return;
        }

        switch (action.toLowerCase().trim()) {
            case "create":
                System.out.println("Traitement de la création d'utilisateur");
                creerUtilisateur(req, resp);
                break;
            case "update":
                System.out.println("Traitement de la modification d'utilisateur");
                modifierUtilisateur(req, resp);
                break;
            default:
                System.out.println("Action inconnue: " + action);
                req.setAttribute("error", "Action inconnue: " + action);
                afficherListeUtilisateurs(req, resp);
        }
    }

    /**
     * Affiche la liste de tous les utilisateurs
     */
    private void afficherListeUtilisateurs(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        try {
            List<Utilisateur> utilisateurs = utilisateurService.getTousLesUtilisateurs();

            // Statistiques pour les cartes du dashboard
            long nbUtilisateurs = utilisateurs.size();
            long nbAdmins = utilisateurs.stream().filter(u -> u.getRole() == Role.ADMIN).count();
            long nbProprietaires = utilisateurs.stream().filter(u -> u.getRole() == Role.PROPRIETAIRE).count();
            long nbLocataires = utilisateurs.stream().filter(u -> u.getRole() == Role.LOCATAIRE).count();
            long nbVisiteurs = utilisateurs.stream().filter(u -> u.getRole() == Role.VISITEUR).count();

            req.setAttribute("utilisateurs", utilisateurs);
            req.setAttribute("nbUtilisateurs", nbUtilisateurs);
            req.setAttribute("nbAdmins", nbAdmins);
            req.setAttribute("nbProprietaires", nbProprietaires);
            req.setAttribute("nbLocataires", nbLocataires);
            req.setAttribute("nbVisiteurs", nbVisiteurs);
            req.setAttribute("pageTitle", "Gestion des Utilisateurs");

            req.getRequestDispatcher("/WEB-INF/jsp/admin/gestion_utilisateurs.jsp").forward(req, resp);

        } catch (Exception e) {
            System.err.println("Erreur dans afficherListeUtilisateurs: " + e.getMessage());
            e.printStackTrace();
            req.setAttribute("error", "Erreur lors du chargement des utilisateurs: " + e.getMessage());
            req.getRequestDispatcher("/WEB-INF/jsp/admin/gestion_utilisateurs.jsp").forward(req, resp);
        }
    }

    /**
     * Affiche la liste des propriétaires uniquement
     */
    private void afficherListeProprietaires(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        try {
            List<Utilisateur> proprietaires = utilisateurService.getUtilisateursByRole(Role.PROPRIETAIRE);

            req.setAttribute("utilisateurs", proprietaires);
            req.setAttribute("nbProprietaires", proprietaires.size());
            req.setAttribute("pageTitle", "Gestion des Propriétaires");
            req.setAttribute("filtreRole", "PROPRIETAIRE");

            req.getRequestDispatcher("/WEB-INF/jsp/admin/Liste_Proprietaires.jsp").forward(req, resp);

        } catch (Exception e) {
            req.setAttribute("error", "Erreur lors du chargement des propriétaires: " + e.getMessage());
            req.getRequestDispatcher("/WEB-INF/jsp/admin/gestion_utilisateurs.jsp").forward(req, resp);
        }
    }

    /**
     * Affiche la liste des locataires uniquement
     */
    private void afficherListeLocataires(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        try {
            HttpSession session = req.getSession();
            String role = (String) session.getAttribute("role");
            Utilisateur utilisateurConnecte = (Utilisateur) session.getAttribute("utilisateur");

            List<Utilisateur> locataires;

            if ("ADMIN".equals(role)) {
                // L'admin voit tous les locataires
                locataires = locataireService.getAllLocataires();
                req.setAttribute("isAdminView", true);
            } else if ("PROPRIETAIRE".equals(role)) {
                // Le propriétaire ne voit que ses locataires
                locataires = locataireService.getLocatairesDuProprietaire(utilisateurConnecte.getId());
                req.setAttribute("isProprietaireView", true);
                req.setAttribute("proprietaireId", utilisateurConnecte.getId());
            } else {
                // Autres rôles n'ont pas accès
                resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Accès non autorisé");
                return;
            }

            req.setAttribute("utilisateurs", locataires); // Pour compatibilité avec JSP existante
            req.setAttribute("locataires", locataires);   // Pour nouvelles JSP
            req.setAttribute("nbLocataires", locataires.size());
            req.setAttribute("pageTitle", "PROPRIETAIRE".equals(role) ? "Mes Locataires" : "Gestion des Locataires");
            req.setAttribute("filtreRole", "LOCATAIRE");

            // Utiliser la JSP du propriétaire si elle existe, sinon celle de l'admin
            String jspPath = "PROPRIETAIRE".equals(role) ?
                    "/WEB-INF/jsp/proprietaire/Liste_Locataires.jsp" :
                    "/WEB-INF/jsp/admin/Liste_Locataires.jsp";

            req.getRequestDispatcher(jspPath).forward(req, resp);

        } catch (Exception e) {
            System.err.println("Erreur dans afficherListeLocataires: " + e.getMessage());
            e.printStackTrace();
            req.setAttribute("error", "Erreur lors du chargement des locataires: " + e.getMessage());
            req.getRequestDispatcher("/WEB-INF/jsp/admin/gestion_utilisateurs.jsp").forward(req, resp);
        }
    }

    /**
     * Affiche la liste des visiteurs uniquement
     */
    private void afficherListeVisiteurs(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        try {
            List<Utilisateur> visiteurs = utilisateurService.getUtilisateursByRole(Role.VISITEUR);

            req.setAttribute("utilisateurs", visiteurs);
            req.setAttribute("nbVisiteurs", visiteurs.size());
            req.setAttribute("pageTitle", "Gestion des Visiteurs");
            req.setAttribute("filtreRole", "VISITEUR");

            req.getRequestDispatcher("/WEB-INF/jsp/admin/gestion_utilisateurs.jsp").forward(req, resp);

        } catch (Exception e) {
            req.setAttribute("error", "Erreur lors du chargement des visiteurs: " + e.getMessage());
            req.getRequestDispatcher("/WEB-INF/jsp/admin/gestion_utilisateurs.jsp").forward(req, resp);
        }
    }

    /**
     * Affiche le formulaire de création ou modification d'un utilisateur
     */
    private void afficherFormulaireUtilisateur(HttpServletRequest req, HttpServletResponse resp, String idStr)
            throws ServletException, IOException {

        System.out.println("=== afficherFormulaireUtilisateur ===");
        System.out.println("ID reçu: " + idStr);

        Utilisateur u = null;
        if (idStr != null && !idStr.trim().isEmpty()) {
            try {
                Long id = Long.parseLong(idStr.trim());
                u = utilisateurService.trouverParId(id);
                System.out.println("Utilisateur trouvé: " + (u != null ? u.getNomComplet() : "null"));

                if (u == null) {
                    req.setAttribute("error", "Utilisateur non trouvé avec l'ID: " + id);
                    afficherListeUtilisateurs(req, resp);
                    return;
                }
            } catch (NumberFormatException e) {
                System.err.println("ID invalide: " + idStr);
                req.setAttribute("error", "ID utilisateur invalide: " + idStr);
                afficherListeUtilisateurs(req, resp);
                return;
            } catch (Exception e) {
                System.err.println("Erreur lors de la recherche de l'utilisateur: " + e.getMessage());
                req.setAttribute("error", "Erreur lors de la recherche de l'utilisateur: " + e.getMessage());
                afficherListeUtilisateurs(req, resp);
                return;
            }
        }

        req.setAttribute("utilisateur", u);
        req.setAttribute("action", u == null ? "create" : "update");
        req.setAttribute("pageTitle", u == null ? "Ajouter un Utilisateur" : "Modifier l'Utilisateur");

        System.out.println("Redirection vers le formulaire avec action: " + (u == null ? "create" : "update"));
        req.getRequestDispatcher("/WEB-INF/jsp/admin/utilisateur-form.jsp").forward(req, resp);
    }

    /**
     * Créer un nouvel utilisateur
     */
    private void creerUtilisateur(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        System.out.println("=== DÉBUT CRÉATION UTILISATEUR ===");

        String nom = req.getParameter("nom");
        String prenom = req.getParameter("prenom");
        String email = req.getParameter("email");
        String motDePasse = req.getParameter("motDePasse");
        String roleStr = req.getParameter("role");

        System.out.println("Paramètres reçus:");
        System.out.println("- Nom: " + nom);
        System.out.println("- Prénom: " + prenom);
        System.out.println("- Email: " + email);
        System.out.println("- Mot de passe: " + (motDePasse != null ? "[MASQUÉ]" : "null"));
        System.out.println("- Rôle: " + roleStr);

        // Validation des champs obligatoires
        if (nom == null || prenom == null || email == null || motDePasse == null || roleStr == null
                || nom.trim().isEmpty() || prenom.trim().isEmpty() || email.trim().isEmpty()
                || motDePasse.trim().isEmpty() || roleStr.trim().isEmpty()) {

            System.out.println("ERREUR: Champs manquants ou vides");
            req.setAttribute("error", "Tous les champs sont obligatoires");
            afficherFormulaireUtilisateur(req, resp, null);
            return;
        }

        try {
            // Conversion du rôle
            Role role = Role.fromString(roleStr.trim());
            System.out.println("Rôle converti: " + role);

            // Vérifier si l'email existe déjà
            Utilisateur existant = utilisateurService.trouverParEmail(email.trim());
            if (existant != null) {
                System.out.println("ERREUR: Email déjà existant");
                req.setAttribute("error", "Un utilisateur avec cet email existe déjà");
                afficherFormulaireUtilisateur(req, resp, null);
                return;
            }

            // Création de l'utilisateur
            Utilisateur nouvelUtilisateur = new Utilisateur();
            nouvelUtilisateur.setNom(nom.trim());
            nouvelUtilisateur.setPrenom(prenom.trim());
            nouvelUtilisateur.setEmail(email.trim());
            nouvelUtilisateur.setMotDePasse(motDePasse.trim());
            nouvelUtilisateur.setRole(role);

            System.out.println("Utilisateur créé: " + nouvelUtilisateur);

            // Sauvegarde
            utilisateurService.creerUtilisateur(nouvelUtilisateur);
            System.out.println("Utilisateur sauvegardé avec succès");

            // Message de succès et redirection
            HttpSession session = req.getSession();
            session.setAttribute("successMessage",
                    "Utilisateur " + nouvelUtilisateur.getNomComplet() + " créé avec succès");

            System.out.println("Redirection vers: " + req.getContextPath() + "/utilisateurs");
            resp.sendRedirect(req.getContextPath() + "/utilisateurs");

        } catch (Exception e) {
            System.err.println("ERREUR lors de la création de l'utilisateur:");
            e.printStackTrace();
            req.setAttribute("error", "Erreur lors de la création: " + e.getMessage());
            afficherFormulaireUtilisateur(req, resp, null);
        }

        System.out.println("=== FIN CRÉATION UTILISATEUR ===");
    }

    /**
     * Modifier un utilisateur existant
     */
    private void modifierUtilisateur(HttpServletRequest req, HttpServletResponse resp)
            throws IOException, ServletException {

        System.out.println("=== DÉBUT MODIFICATION UTILISATEUR ===");

        try {
            String idStr = req.getParameter("id");
            System.out.println("ID utilisateur à modifier: " + idStr);

            if (idStr == null || idStr.trim().isEmpty()) {
                req.setAttribute("error", "ID utilisateur manquant");
                afficherListeUtilisateurs(req, resp);
                return;
            }

            Long id = Long.parseLong(idStr.trim());
            Utilisateur u = utilisateurService.trouverParId(id);

            if (u == null) {
                req.setAttribute("error", "Utilisateur non trouvé");
                afficherListeUtilisateurs(req, resp);
                return;
            }

            System.out.println("Utilisateur trouvé: " + u.getNomComplet());

            // Récupération des nouveaux paramètres
            String nom = req.getParameter("nom");
            String prenom = req.getParameter("prenom");
            String email = req.getParameter("email");
            String roleStr = req.getParameter("role");
            String motDePasse = req.getParameter("motDePasse");

            System.out.println("Nouveaux paramètres:");
            System.out.println("- Nom: " + nom);
            System.out.println("- Prénom: " + prenom);
            System.out.println("- Email: " + email);
            System.out.println("- Rôle: " + roleStr);
            System.out.println("- Nouveau mot de passe: " + (motDePasse != null && !motDePasse.trim().isEmpty() ? "[OUI]" : "[NON]"));

            // Validation des champs obligatoires
            if (nom == null || prenom == null || email == null || roleStr == null
                    || nom.trim().isEmpty() || prenom.trim().isEmpty() || email.trim().isEmpty() || roleStr.trim().isEmpty()) {
                req.setAttribute("error", "Les champs nom, prénom, email et rôle sont obligatoires");
                afficherFormulaireUtilisateur(req, resp, idStr);
                return;
            }

            // Mise à jour des propriétés
            u.setNom(nom.trim());
            u.setPrenom(prenom.trim());
            u.setEmail(email.trim());
            u.setRole(Role.fromString(roleStr.trim()));

            // Mise à jour du mot de passe seulement s'il est fourni
            if (motDePasse != null && !motDePasse.trim().isEmpty()) {
                u.setMotDePasse(motDePasse.trim());
                System.out.println("Mot de passe mis à jour");
            }

            utilisateurService.mettreAJourUtilisateur(u);

            HttpSession session = req.getSession();
            session.setAttribute("successMessage",
                    "Utilisateur " + u.getNomComplet() + " modifié avec succès");

            System.out.println("Utilisateur mis à jour avec succès");
            resp.sendRedirect(req.getContextPath() + "/utilisateurs");

        } catch (NumberFormatException e) {
            System.err.println("ID invalide: " + e.getMessage());
            req.setAttribute("error", "ID utilisateur invalide");
            afficherListeUtilisateurs(req, resp);
        } catch (Exception e) {
            System.err.println("Erreur lors de la modification: " + e.getMessage());
            e.printStackTrace();
            req.setAttribute("error", "Erreur lors de la modification: " + e.getMessage());
            afficherListeUtilisateurs(req, resp);
        }

        System.out.println("=== FIN MODIFICATION UTILISATEUR ===");
    }

    /**
     * Supprimer un utilisateur
     */
    private void supprimerUtilisateur(HttpServletRequest req, HttpServletResponse resp, String idStr)
            throws IOException {
        try {
            if (idStr == null || idStr.trim().isEmpty()) {
                req.getSession().setAttribute("errorMessage", "ID utilisateur manquant");
                resp.sendRedirect(req.getContextPath() + "/utilisateurs");
                return;
            }

            Long id = Long.parseLong(idStr.trim());
            Utilisateur utilisateur = utilisateurService.trouverParId(id);

            if (utilisateur != null) {
                utilisateurService.supprimerUtilisateur(id);
                req.getSession().setAttribute("successMessage",
                        "Utilisateur " + utilisateur.getNomComplet() + " supprimé avec succès");
            } else {
                req.getSession().setAttribute("errorMessage", "Utilisateur non trouvé");
            }
        } catch (NumberFormatException e) {
            req.getSession().setAttribute("errorMessage", "ID utilisateur invalide");
        } catch (Exception e) {
            req.getSession().setAttribute("errorMessage", "Erreur: " + e.getMessage());
        }
        resp.sendRedirect(req.getContextPath() + "/utilisateurs");
    }

    /**
     * Promouvoir un visiteur en locataire
     */
    private void promouvoirUtilisateur(HttpServletRequest req, HttpServletResponse resp, String idStr)
            throws ServletException, IOException {

        try {
            if (idStr == null || idStr.trim().isEmpty()) {
                req.getSession().setAttribute("errorMessage", "ID utilisateur manquant");
                resp.sendRedirect(req.getContextPath() + "/utilisateurs");
                return;
            }

            Long id = Long.parseLong(idStr.trim());
            Utilisateur utilisateur = utilisateurService.trouverParId(id);

            if (utilisateur == null) {
                req.getSession().setAttribute("errorMessage", "Utilisateur non trouvé");
                resp.sendRedirect(req.getContextPath() + "/utilisateurs");
                return;
            }

            if (utilisateur.getRole() != Role.VISITEUR) {
                req.getSession().setAttribute("errorMessage",
                        "Seuls les visiteurs peuvent être promus en locataires");
                resp.sendRedirect(req.getContextPath() + "/utilisateurs");
                return;
            }

            // Promotion
            utilisateur.setRole(Role.LOCATAIRE);
            utilisateurService.mettreAJourUtilisateur(utilisateur);

            req.getSession().setAttribute("successMessage",
                    utilisateur.getNomComplet() + " a été promu(e) au rang de locataire");

            resp.sendRedirect(req.getContextPath() + "/utilisateurs");

        } catch (NumberFormatException e) {
            req.getSession().setAttribute("errorMessage", "ID utilisateur invalide");
            resp.sendRedirect(req.getContextPath() + "/utilisateurs");
        } catch (Exception e) {
            req.getSession().setAttribute("errorMessage",
                    "Erreur lors de la promotion: " + e.getMessage());
            resp.sendRedirect(req.getContextPath() + "/utilisateurs");
        }
    }

    /**
     * Vérifier l'authentification et les autorisations
     */
    private boolean verifierAuthentification(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        HttpSession session = req.getSession(false);

        if (session == null || session.getAttribute("utilisateur") == null) {
            resp.sendRedirect(req.getContextPath() + "/auth?action=login");
            return false;
        }

        String role = (String) session.getAttribute("role");
        String requestURI = req.getRequestURI();
        String contextPath = req.getContextPath();

        // Vérifier les permissions selon l'URL demandée
        if (requestURI.equals(contextPath + "/locataires")) {
            // Les propriétaires peuvent voir leurs locataires, les admins peuvent tout voir
            if (!"ADMIN".equals(role) && !"PROPRIETAIRE".equals(role)) {
                resp.sendError(HttpServletResponse.SC_FORBIDDEN,
                        "Accès refusé. Seuls les administrateurs et propriétaires peuvent voir les locataires.");
                return false;
            }
        } else {
            // Pour les autres routes (/utilisateurs, /proprietaires, /visiteurs), seuls les admins
            if (!"ADMIN".equals(role)) {
                resp.sendError(HttpServletResponse.SC_FORBIDDEN,
                        "Accès refusé. Seuls les administrateurs peuvent gérer les utilisateurs.");
                return false;
            }
        }

        return true;
    }

}