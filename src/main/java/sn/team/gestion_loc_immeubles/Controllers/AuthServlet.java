package sn.team.gestion_loc_immeubles.Controllers;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import sn.team.gestion_loc_immeubles.Services.AuthService;
import sn.team.gestion_loc_immeubles.Entities.Utilisateur;
import sn.team.gestion_loc_immeubles.Entities.Role;

import java.io.IOException;

@WebServlet(name = "AuthServlet", urlPatterns = {"/auth"})
public class AuthServlet extends HttpServlet {

    private final AuthService authService = new AuthService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String action = req.getParameter("action");

        if (action == null || action.trim().isEmpty()) {
            resp.sendRedirect(req.getContextPath() + "/auth?action=login");
            return;
        }

        switch (action.toLowerCase()) {
            case "login":
            case "showlogin":
                afficherPageLogin(req, resp);
                break;
            case "register":
            case "showregister":
                afficherPageRegister(req, resp);
                break;
            case "logout":
                gererDeconnexion(req, resp);
                break;
            default:
                resp.sendRedirect(req.getContextPath() + "/auth?action=login");
                break;
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException, ServletException {
        String action = req.getParameter("action");

        if (action == null || action.trim().isEmpty()) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Action manquante");
            return;
        }

        switch (action.toLowerCase()) {
            case "login":
                traiterConnexion(req, resp);
                break;
            case "register":
                traiterInscription(req, resp);
                break;
            default:
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Action non reconnue: " + action);
                break;
        }
    }

    /**
     * Affiche la page de connexion
     */
    private void afficherPageLogin(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        if (estUtilisateurConnecte(req)) {
            redirigerSelonRole(req, resp);
            return;
        }

        HttpSession session = req.getSession(false);
        if (session != null) {
            gererMessagesSession(req, session);
        }

        req.getRequestDispatcher("/WEB-INF/jsp/shared/login.jsp").forward(req, resp);
    }

    /**
     * Affiche la page d'inscription
     */
    private void afficherPageRegister(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        if (estUtilisateurConnecte(req)) {
            redirigerSelonRole(req, resp);
            return;
        }

        req.getRequestDispatcher("/WEB-INF/jsp/shared/register.jsp").forward(req, resp);
    }

    /**
     * Traite la connexion de l'utilisateur
     */
    private void traiterConnexion(HttpServletRequest req, HttpServletResponse resp)
            throws IOException, ServletException {

        String email = req.getParameter("email");
        String motDePasse = req.getParameter("motDePasse");

        if (!validerDonneesConnexion(email, motDePasse)) {
            req.setAttribute("error", "Veuillez saisir votre email et mot de passe");
            req.setAttribute("email", email);
            req.getRequestDispatcher("/WEB-INF/jsp/shared/login.jsp").forward(req, resp);
            return;
        }

        try {
            Utilisateur utilisateur = authService.connexion(email.trim(), motDePasse);

            if (utilisateur != null) {
                creerSessionUtilisateur(req, utilisateur);
                redirigerSelonRole(req, resp);
                System.out.println("DEBUG utilisateur connecté: " + utilisateur.getEmail()
                        + " - Rôle: " + utilisateur.getRole());
            } else {
                gererEchecConnexion(req, resp, email);
            }

        } catch (Exception e) {
            gererErreurConnexion(req, resp, email, e);
        }
    }

    /**
     * Traite l'inscription d'un nouvel utilisateur
     * Tous les nouveaux utilisateurs sont automatiquement des VISITEURS
     */
    private void traiterInscription(HttpServletRequest req, HttpServletResponse resp)
            throws IOException, ServletException {

        DonneesInscription donnees = extraireDonneesInscription(req);

        String erreurValidation = validerDonneesInscription(donnees);
        if (erreurValidation != null) {
            req.setAttribute("error", erreurValidation);
            preserverDonneesFormulaire(req, donnees);
            req.getRequestDispatcher("/WEB-INF/jsp/shared/register.jsp").forward(req, resp);
            return;
        }

        try {
            if (authService.existeDeja(donnees.email.trim())) {
                req.setAttribute("error", "Cet email est déjà utilisé");
                preserverDonneesFormulaire(req, donnees);
                req.getRequestDispatcher("/WEB-INF/jsp/shared/register.jsp").forward(req, resp);
                return;
            }

            Utilisateur utilisateur = creerUtilisateur(donnees);
            authService.inscription(utilisateur);

            creerSessionUtilisateur(req, utilisateur);
            ajouterMessageBienvenue(req, utilisateur, true);
            redirigerSelonRole(req, resp);

        } catch (Exception e) {
            gererErreurInscription(req, resp, donnees, e);
        }
    }

    /**
     * Gère la déconnexion de l'utilisateur
     */
    private void gererDeconnexion(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        HttpSession session = req.getSession(false);
        String nomUtilisateur = null;

        if (session != null) {
            nomUtilisateur = recupererNomUtilisateur(session);
            session.invalidate();
        }

        HttpSession nouvelleSession = req.getSession(true);
        String messageAuRevoir = (nomUtilisateur != null && !nomUtilisateur.trim().isEmpty())
                ? "Au revoir " + nomUtilisateur + " !"
                : "Vous avez été déconnecté avec succès";

        nouvelleSession.setAttribute("logoutMessage", messageAuRevoir);
        resp.sendRedirect(req.getContextPath() + "/auth?action=login");
    }

    /**
     * Crée une session pour l'utilisateur connecté
     */
    private void creerSessionUtilisateur(HttpServletRequest req, Utilisateur utilisateur) {
        HttpSession session = req.getSession(true);

        // Informations de base de l'utilisateur
        session.setAttribute("utilisateur", utilisateur);
        session.setAttribute("userId", utilisateur.getId());
        session.setAttribute("userEmail", utilisateur.getEmail());
        session.setAttribute("userName", utilisateur.getNomComplet());
        session.setAttribute("role", utilisateur.getRole().name());

        // Pour compatibilité avec l'ancien code
        session.setAttribute("typeUtilisateur", utilisateur.getRole().name());

        ajouterMessageBienvenue(req, utilisateur, false);
    }

    /**
     * Redirige l'utilisateur selon son rôle
     */
    private void redirigerSelonRole(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        HttpSession session = req.getSession(false);
        String contextPath = req.getContextPath();

        // Récupérer l'URL de redirection sauvegardée
        String redirectURL = recupererURLRedirection(session);

        System.out.println("DEBUG redirection - URL sauvegardée: " + redirectURL);
        System.out.println("DEBUG redirection - Context path: " + contextPath);

        // Si une URL spécifique était demandée ET ce n'est pas la page d'accueil, y rediriger
        if (redirectURL != null &&
                !redirectURL.equals(contextPath + "/") &&
                !redirectURL.equals(contextPath) &&
                !redirectURL.endsWith("/") && // éviter les URLs qui se terminent juste par /
                !redirectURL.contains("/auth")) { // éviter de rediriger vers les pages d'auth

            System.out.println("DEBUG redirection - Redirection vers URL sauvegardée: " + redirectURL);
            resp.sendRedirect(redirectURL);
            return;
        }

        // Sinon, rediriger vers le dashboard approprié selon le rôle
        String roleString = session != null ? (String) session.getAttribute("role") : null;
        Role role = Role.fromString(roleString);

        String urlRedirection = determinerURLRedirection(contextPath, role);

        System.out.println("DEBUG redirection - Utilisateur " + role + " redirigé vers: " + urlRedirection);
        resp.sendRedirect(urlRedirection);
    }

    /**
     * Détermine l'URL de redirection selon le rôle
     */
    private String determinerURLRedirection(String contextPath, Role role) {
        System.out.println("DEBUG redirection: role=" + role);

        return switch (role) {
            case ADMIN -> contextPath + "/admin/dashboard";
            case PROPRIETAIRE -> contextPath + "/proprietaire/dashboard";
            case LOCATAIRE -> contextPath + "/locataire/dashboard";
            case VISITEUR -> contextPath + "/"; // Les visiteurs vont à la page d'accueil avec navigation
        };
    }

    /**
     * Crée un utilisateur avec le rôle VISITEUR par défaut
     */
    private Utilisateur creerUtilisateur(DonneesInscription donnees) {
        // Tous les nouveaux utilisateurs sont des VISITEURS
        Utilisateur utilisateur = new Utilisateur(
                donnees.nom.trim(),
                donnees.prenom.trim(),
                donnees.telephone.trim(),
                donnees.email.trim().toLowerCase(),
                donnees.motDePasse,
                Role.VISITEUR // Forcer le rôle VISITEUR
        );

        return utilisateur;
    }

    // ========== MÉTHODES UTILITAIRES ==========

    private boolean estUtilisateurConnecte(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        return session != null && session.getAttribute("utilisateur") != null;
    }

    private boolean validerDonneesConnexion(String email, String motDePasse) {
        return email != null && !email.trim().isEmpty()
                && motDePasse != null && !motDePasse.trim().isEmpty();
    }

    private String validerDonneesInscription(DonneesInscription donnees) {
        if (donnees.nom == null || donnees.nom.trim().isEmpty() ||
                donnees.prenom == null || donnees.prenom.trim().isEmpty() ||
                donnees.email == null || donnees.email.trim().isEmpty() ||
                donnees.motDePasse == null || donnees.motDePasse.trim().isEmpty()) {
            return "Veuillez remplir tous les champs obligatoires";
        }

        if (donnees.confirmMotDePasse != null &&
                !donnees.motDePasse.equals(donnees.confirmMotDePasse)) {
            return "Les mots de passe ne correspondent pas";
        }

        if (donnees.motDePasse.length() < 6) {
            return "Le mot de passe doit contenir au moins 6 caractères";
        }

        if (!donnees.email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
            return "Format d'email invalide";
        }

        return null;
    }

    private DonneesInscription extraireDonneesInscription(HttpServletRequest req) {
        DonneesInscription donnees = new DonneesInscription();
        donnees.nom = req.getParameter("nom");
        donnees.prenom = req.getParameter("prenom");
        donnees.email = req.getParameter("email");
        donnees.motDePasse = req.getParameter("motDePasse");
        donnees.confirmMotDePasse = req.getParameter("confirmMotDePasse");
        // Le rôle n'est plus pris en compte, tous les nouveaux utilisateurs sont des VISITEURS
        donnees.roleString = "VISITEUR";

        return donnees;
    }

    private void preserverDonneesFormulaire(HttpServletRequest req, DonneesInscription donnees) {
        if (donnees.nom != null) req.setAttribute("nom", donnees.nom.trim());
        if (donnees.prenom != null) req.setAttribute("prenom", donnees.prenom.trim());
        if (donnees.email != null) req.setAttribute("email", donnees.email.trim());
        // Pas besoin de préserver le rôle car il est fixe
    }

    private String recupererNomUtilisateur(HttpSession session) {
        String nomUtilisateur = (String) session.getAttribute("userName");
        if (nomUtilisateur == null) {
            Utilisateur utilisateur = (Utilisateur) session.getAttribute("utilisateur");
            if (utilisateur != null) {
                nomUtilisateur = utilisateur.getPrenom();
            }
        }
        return nomUtilisateur;
    }

    private String recupererURLRedirection(HttpSession session) {
        if (session == null) return null;

        String redirectURL = (String) session.getAttribute("redirectAfterLogin");
        if (redirectURL != null) {
            session.removeAttribute("redirectAfterLogin");
        }
        return redirectURL;
    }

    private void gererMessagesSession(HttpServletRequest req, HttpSession session) {
        String logoutMessage = (String) session.getAttribute("logoutMessage");
        if (logoutMessage != null) {
            req.setAttribute("success", logoutMessage);
            session.removeAttribute("logoutMessage");
        }
    }

    private void ajouterMessageBienvenue(HttpServletRequest req, Utilisateur utilisateur, boolean inscription) {
        HttpSession session = req.getSession();
        String message = inscription
                ? "Bienvenue " + utilisateur.getPrenom() + " ! Votre compte visiteur a été créé avec succès. Parcourez nos immeubles disponibles !"
                : "Bienvenue " + utilisateur.getPrenom() + " !";
        session.setAttribute("welcomeMessage", message);
    }

    private void gererEchecConnexion(HttpServletRequest req, HttpServletResponse resp, String email)
            throws ServletException, IOException {
        req.setAttribute("error", "Email ou mot de passe incorrect");
        req.setAttribute("email", email);
        req.getRequestDispatcher("/WEB-INF/jsp/shared/login.jsp").forward(req, resp);
    }

    private void gererErreurConnexion(HttpServletRequest req, HttpServletResponse resp,
                                      String email, Exception e)
            throws ServletException, IOException {
        req.setAttribute("error", "Une erreur s'est produite lors de la connexion");
        req.setAttribute("email", email);
        req.getRequestDispatcher("/WEB-INF/jsp/shared/login.jsp").forward(req, resp);

        System.err.println("Erreur lors de la connexion pour l'email: " + email);
        e.printStackTrace();
    }

    private void gererErreurInscription(HttpServletRequest req, HttpServletResponse resp,
                                        DonneesInscription donnees, Exception e)
            throws ServletException, IOException {
        req.setAttribute("error", "Une erreur s'est produite lors de l'inscription: " + e.getMessage());
        preserverDonneesFormulaire(req, donnees);
        req.getRequestDispatcher("/WEB-INF/jsp/shared/register.jsp").forward(req, resp);

        System.err.println("Erreur lors de l'inscription pour l'email: " + donnees.email);
        e.printStackTrace();
    }

    private static class DonneesInscription {
        String nom;
        String prenom;
        String telephone;
        String email;
        String motDePasse;
        String confirmMotDePasse;
        String roleString;
    }
}