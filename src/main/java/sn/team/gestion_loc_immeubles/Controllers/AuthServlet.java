package sn.team.gestion_loc_immeubles.Controllers;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import sn.team.gestion_loc_immeubles.Services.AuthService;
import sn.team.gestion_loc_immeubles.Entities.Utilisateur;
import sn.team.gestion_loc_immeubles.Entities.Administrateur;
import sn.team.gestion_loc_immeubles.Entities.Proprietaire;
import sn.team.gestion_loc_immeubles.Entities.Locataire;

import java.io.IOException;

public class AuthServlet extends HttpServlet {

    private final AuthService authService = new AuthService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getParameter("action");

        switch (action != null ? action.toLowerCase() : "") {
            case "login":
            case "showlogin":
                handleLoginPage(req, resp);
                break;

            case "register":
            case "showregister":
                handleRegisterPage(req, resp);
                break;

            case "logout":
                handleLogout(req, resp);
                break;

            default:
                // Action non reconnue, rediriger vers login
                resp.sendRedirect(req.getContextPath() + "/auth?action=login");
                break;
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        String action = req.getParameter("action");

        switch (action != null ? action.toLowerCase() : "") {
            case "login":
                handleLogin(req, resp);
                break;

            case "register":
                handleRegister(req, resp);
                break;

            default:
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Action non reconnue");
                break;
        }
    }

    /**
     * Afficher la page de connexion
     */
    private void handleLoginPage(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        // Vérifier si l'utilisateur est déjà connecté
        if (isUserLoggedIn(req)) {
            redirectAfterLogin(req, resp);
        } else {
            // Afficher un message de déconnexion s'il existe
            HttpSession session = req.getSession(false);
            if (session != null) {
                String logoutMessage = (String) session.getAttribute("logoutMessage");
                if (logoutMessage != null) {
                    req.setAttribute("success", logoutMessage);
                    session.removeAttribute("logoutMessage");
                }
            }

            // CORRECTION: Utiliser le bon chemin JSP selon votre structure
            req.getRequestDispatcher("/WEB-INF/jsp/shared/login.jsp").forward(req, resp);
        }
    }

    /**
     * Afficher la page d'inscription
     */
    private void handleRegisterPage(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        // Vérifier si l'utilisateur est déjà connecté
        if (isUserLoggedIn(req)) {
            redirectAfterLogin(req, resp);
        } else {
            // CORRECTION: Utiliser le bon chemin JSP selon votre structure
            req.getRequestDispatcher("/WEB-INF/jsp/shared/register.jsp").forward(req, resp);
        }
    }

    /**
     * Gestion de la connexion utilisateur
     */
    private void handleLogin(HttpServletRequest req, HttpServletResponse resp)
            throws IOException, ServletException {
        String email = req.getParameter("email");
        String motDePasse = req.getParameter("motDePasse");

        // Validation des paramètres
        if (email == null || email.trim().isEmpty() ||
                motDePasse == null || motDePasse.trim().isEmpty()) {
            req.setAttribute("error", "Veuillez remplir tous les champs");
            req.setAttribute("email", email); // Préserver l'email
            req.getRequestDispatcher("/WEB-INF/jsp/shared/login.jsp").forward(req, resp);
            return;
        }

        try {
            // Tentative de connexion
            Utilisateur utilisateur = authService.connexion(email.trim(), motDePasse);

            if (utilisateur != null) {
                // Connexion réussie - Créer la session
                HttpSession session = req.getSession(true);

                // CORRECTION: Utiliser "utilisateur" au lieu de "user" pour cohérence avec le filtre
                session.setAttribute("utilisateur", utilisateur);
                session.setAttribute("userId", utilisateur.getId());
                session.setAttribute("userEmail", utilisateur.getEmail());
                session.setAttribute("userName", utilisateur.getPrenom() + " " + utilisateur.getNom());

                // Déterminer et stocker le rôle
                String role = determinerRoleUtilisateur(utilisateur);
                session.setAttribute("role", role);

                // Message de bienvenue
                session.setAttribute("welcomeMessage",
                        "Bienvenue " + utilisateur.getPrenom() + " !");

                // Redirection après connexion
                redirectAfterLogin(req, resp);

            } else {
                // Échec de connexion
                req.setAttribute("error", "Email ou mot de passe incorrect");
                req.setAttribute("email", email); // Préserver l'email pour l'UX
                req.getRequestDispatcher("/WEB-INF/jsp/shared/login.jsp").forward(req, resp);
            }

        } catch (Exception e) {
            // Erreur système
            req.setAttribute("error", "Une erreur s'est produite lors de la connexion");
            req.setAttribute("email", email);
            req.getRequestDispatcher("/WEB-INF/jsp/shared/login.jsp").forward(req, resp);

            // Log de l'erreur
            System.err.println("Erreur lors de la connexion: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Gestion de l'inscription utilisateur
     */
    private void handleRegister(HttpServletRequest req, HttpServletResponse resp)
            throws IOException, ServletException {
        String nom = req.getParameter("nom");
        String prenom = req.getParameter("prenom");
        String email = req.getParameter("email");
        String motDePasse = req.getParameter("motDePasse");
        String confirmMotDePasse = req.getParameter("confirmMotDePasse");
        String typeUtilisateur = req.getParameter("type");

        // Validation des paramètres
        if (nom == null || nom.trim().isEmpty() ||
                prenom == null || prenom.trim().isEmpty() ||
                email == null || email.trim().isEmpty() ||
                motDePasse == null || motDePasse.trim().isEmpty()) {

            req.setAttribute("error", "Veuillez remplir tous les champs obligatoires");
            preserveFormData(req, nom, prenom, email, typeUtilisateur);
            req.getRequestDispatcher("/WEB-INF/jsp/shared/register.jsp").forward(req, resp);
            return;
        }

        // Validation de la confirmation du mot de passe
        if (confirmMotDePasse != null && !motDePasse.equals(confirmMotDePasse)) {
            req.setAttribute("error", "Les mots de passe ne correspondent pas");
            preserveFormData(req, nom, prenom, email, typeUtilisateur);
            req.getRequestDispatcher("/WEB-INF/jsp/shared/register.jsp").forward(req, resp);
            return;
        }

        // Validation de la longueur du mot de passe
        if (motDePasse.length() < 6) {
            req.setAttribute("error", "Le mot de passe doit contenir au moins 6 caractères");
            preserveFormData(req, nom, prenom, email, typeUtilisateur);
            req.getRequestDispatcher("/WEB-INF/jsp/shared/register.jsp").forward(req, resp);
            return;
        }

        try {
            // Vérifier si l'email existe déjà
            if (authService.existeDeja(email.trim())) {
                req.setAttribute("error", "Cet email est déjà utilisé");
                preserveFormData(req, nom, prenom, email, typeUtilisateur);
                req.getRequestDispatcher("/shared/register.jsp").forward(req, resp);
                return;
            }

            // Créer le nouvel utilisateur selon le type sélectionné
            Utilisateur utilisateur = createUserByType(typeUtilisateur);
            utilisateur.setNom(nom.trim());
            utilisateur.setPrenom(prenom.trim());
            utilisateur.setEmail(email.trim().toLowerCase());
            utilisateur.setMotDePasse(motDePasse);

            // Inscription via le service
            authService.inscription(utilisateur);

            // Connexion automatique après inscription réussie
            HttpSession session = req.getSession(true);

            // CORRECTION: Utiliser "utilisateur" au lieu de "user"
            session.setAttribute("utilisateur", utilisateur);
            session.setAttribute("userId", utilisateur.getId());
            session.setAttribute("userEmail", utilisateur.getEmail());
            session.setAttribute("userName", utilisateur.getPrenom() + " " + utilisateur.getNom());

            String role = determinerRoleUtilisateur(utilisateur);
            session.setAttribute("role", role);
            session.setAttribute("welcomeMessage",
                    "Bienvenue " + utilisateur.getPrenom() + " ! Votre compte a été créé avec succès.");

            // Redirection après inscription
            redirectAfterLogin(req, resp);

        } catch (Exception e) {
            // Erreur système
            req.setAttribute("error", "Une erreur s'est produite lors de l'inscription: " + e.getMessage());
            preserveFormData(req, nom, prenom, email, typeUtilisateur);
            req.getRequestDispatcher("/shared/register.jsp").forward(req, resp);

            // Log de l'erreur
            System.err.println("Erreur lors de l'inscription: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Gestion de la déconnexion
     */
    private void handleLogout(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession(false);
        String userName = "";

        if (session != null) {
            // Récupérer le nom pour le message d'au revoir
            userName = (String) session.getAttribute("userName");
            if (userName == null) {
                // CORRECTION: Utiliser "utilisateur" au lieu de "user"
                Utilisateur user = (Utilisateur) session.getAttribute("utilisateur");
                if (user != null) {
                    userName = user.getPrenom();
                }
            }

            // Invalider la session
            session.invalidate();
        }

        // Créer une nouvelle session pour le message de déconnexion
        HttpSession newSession = req.getSession(true);
        if (userName != null && !userName.isEmpty()) {
            newSession.setAttribute("logoutMessage", "Au revoir " + userName + " !");
        } else {
            newSession.setAttribute("logoutMessage", "Vous avez été déconnecté avec succès");
        }

        resp.sendRedirect(req.getContextPath() + "/auth?action=login");
    }

    /**
     * Créer un utilisateur selon son type
     */
    private Utilisateur createUserByType(String type) {
        if (type == null) {
            return new Locataire(); // Type par défaut changé de Utilisateur à Locataire
        }

        return switch (type.toUpperCase()) {
            case "ADMIN" -> new Administrateur();
            case "PROPRIETAIRE" -> new Proprietaire();
            case "LOCATAIRE" -> new Locataire();
            default -> new Locataire(); // Par défaut : Locataire
        };
    }

    /**
     * Déterminer le rôle d'un utilisateur basé sur son type de classe
     */
    private String determinerRoleUtilisateur(Utilisateur utilisateur) {
        if (utilisateur instanceof Administrateur) {
            return "ADMIN";
        } else if (utilisateur instanceof Proprietaire) {
            return "PROPRIETAIRE";
        } else if (utilisateur instanceof Locataire) {
            return "LOCATAIRE";
        } else {
            return "UTILISATEUR";
        }
    }

    /**
     * Vérifier si un utilisateur est connecté
     */
    private boolean isUserLoggedIn(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        // CORRECTION: Utiliser "utilisateur" au lieu de "user"
        return session != null && session.getAttribute("utilisateur") != null;
    }

    /**
     * Redirection intelligente après connexion
     */
    private void redirectAfterLogin(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession(false);
        String redirectURL = null;

        // Vérifier s'il y a une URL de redirection sauvegardée
        if (session != null) {
            redirectURL = (String) session.getAttribute("redirectAfterLogin");
            if (redirectURL != null) {
                session.removeAttribute("redirectAfterLogin");
                resp.sendRedirect(redirectURL);
                return;
            }
        }

        // Redirection par défaut selon le rôle
        String contextPath = req.getContextPath();
        String role = session != null ? (String) session.getAttribute("role") : "UTILISATEUR";

        switch (role) {
            case "ADMIN":
                resp.sendRedirect(contextPath + "/utilisateurs");
                break;
            case "PROPRIETAIRE":
                resp.sendRedirect(contextPath + "/immeubles");
                break;
            case "LOCATAIRE":
                resp.sendRedirect(contextPath + "/contrats");
                break;
            default:
                resp.sendRedirect(contextPath + "/");
                break;
        }
    }

    /**
     * Préserver les données du formulaire en cas d'erreur
     */
    private void preserveFormData(HttpServletRequest req, String nom, String prenom,
                                  String email, String type) {
        if (nom != null) req.setAttribute("nom", nom.trim());
        if (prenom != null) req.setAttribute("prenom", prenom.trim());
        if (email != null) req.setAttribute("email", email.trim());
        if (type != null) req.setAttribute("type", type);
    }
}