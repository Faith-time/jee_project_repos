package sn.team.gestion_loc_immeubles.Controllers;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import sn.team.gestion_loc_immeubles.DAO.UtilisateurDAO;
import sn.team.gestion_loc_immeubles.DAO.UtilisateurDAOImpl;
import sn.team.gestion_loc_immeubles.Entities.Administrateur;
import sn.team.gestion_loc_immeubles.Entities.Locataire;
import sn.team.gestion_loc_immeubles.Entities.Proprietaire;
import sn.team.gestion_loc_immeubles.Entities.Utilisateur;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@WebServlet("/utilisateurs")
public class UtilisateurServlet extends HttpServlet {

    private final UtilisateurDAO utilisateurDAO = new UtilisateurDAOImpl();

    /**
     * GET → Liste complète, recherche par id/email ou filtrage par type
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getParameter("action");
        String idParam = req.getParameter("id");
        String emailParam = req.getParameter("email");
        String typeParam = req.getParameter("type_utilisateur");

        // Gestion de l'affichage du formulaire
        if ("form".equals(action)) {
            if (idParam != null && !idParam.trim().isEmpty()) {
                // Mode édition - charger l'utilisateur existant
                try {
                    Long id = Long.parseLong(idParam);
                    Utilisateur utilisateur = utilisateurDAO.findById(id);
                    req.setAttribute("utilisateur", utilisateur);
                } catch (NumberFormatException e) {
                    // Si l'ID n'est pas valide, on continue en mode création
                }
            }
            // Mode création ou édition - rediriger vers le formulaire
            req.getRequestDispatcher("/WEB-INF/jsp/admin/utilisateur-form.jsp").forward(req, resp);
            return;
        }

        // Gestion de la suppression
        if ("delete".equals(action) && idParam != null) {
            try {
                Long id = Long.parseLong(idParam);
                utilisateurDAO.delete(id);
                resp.sendRedirect(req.getContextPath() + "/utilisateurs");
                return;
            } catch (NumberFormatException e) {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID utilisateur invalide");
                return;
            }
        }

        // Recherche par ID spécifique (pour affichage détaillé)
        if (idParam != null && !"form".equals(action) && !"delete".equals(action)) {
            try {
                Long id = Long.parseLong(idParam);
                Utilisateur utilisateur = utilisateurDAO.findById(id);

                if (utilisateur != null) {
                    utilisateur.setClassName(utilisateur.getClass().getSimpleName());
                }
                req.setAttribute("utilisateur", utilisateur);
                req.getRequestDispatcher("/WEB-INF/jsp/admin/gestion_utilisateurs.jsp").forward(req, resp);
                return;
            } catch (NumberFormatException e) {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID utilisateur invalide");
                return;
            }
        }

        // Recherche par email
        if (emailParam != null && !emailParam.trim().isEmpty()) {
            Utilisateur utilisateur = utilisateurDAO.findByEmail(emailParam);

            if (utilisateur != null) {
                utilisateur.setClassName(utilisateur.getClass().getSimpleName());
            }
            req.setAttribute("utilisateur", utilisateur);
            req.getRequestDispatcher("/WEB-INF/jsp/admin/gestion_utilisateurs.jsp").forward(req, resp);
            return;
        }

        // Filtrage par type d'utilisateur
        if (typeParam != null && !typeParam.trim().isEmpty()) {
            List<Utilisateur> allUsers = utilisateurDAO.findAll();

            switch (typeParam.toLowerCase()) {
                case "locataire" -> {
                    List<Locataire> locataires = allUsers.stream()
                            .filter(u -> u instanceof Locataire)
                            .map(u -> {
                                u.setClassName(u.getClass().getSimpleName());
                                return (Locataire) u;
                            })
                            .collect(Collectors.toList());
                    req.setAttribute("locataires", locataires);
                    req.getRequestDispatcher("/WEB-INF/jsp/locataire/dashboard.jsp").forward(req, resp);
                }
                case "proprietaire" -> {
                    List<Proprietaire> proprietaires = allUsers.stream()
                            .filter(u -> u instanceof Proprietaire)
                            .map(u -> {
                                u.setClassName(u.getClass().getSimpleName());
                                return (Proprietaire) u;
                            })
                            .collect(Collectors.toList());
                    req.setAttribute("proprietaires", proprietaires);
                    req.getRequestDispatcher("/WEB-INF/jsp/proprietaire/dashboard.jsp").forward(req, resp);
                }
                default -> resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Type d'utilisateur inconnu: " + typeParam);
            }
            return;
        }

        // Liste complète pour Admin (cas par défaut)
        List<Utilisateur> utilisateurs = utilisateurDAO.findAll();

        // Préparer le className pour chaque utilisateur
        utilisateurs.forEach(u -> u.setClassName(u.getClass().getSimpleName()));

        req.setAttribute("utilisateurs", utilisateurs);
        req.getRequestDispatcher("/WEB-INF/jsp/admin/gestion_utilisateurs.jsp").forward(req, resp);
    }

    /**
     * POST → Création d’un nouvel utilisateur
     */
    /**
     * POST → Création ou mise à jour d'un utilisateur
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String idParam = req.getParameter("id");
        String nom = req.getParameter("nom");
        String prenom = req.getParameter("prenom");
        String email = req.getParameter("email");
        String motDePasse = req.getParameter("motDePasse");
        String type = req.getParameter("type"); // Corrigé: "type" au lieu de "type_utilisateur"

        try {
            if (idParam != null && !idParam.trim().isEmpty()) {
                // **MODE ÉDITION** - Mise à jour d'un utilisateur existant
                Long id = Long.parseLong(idParam);
                Utilisateur utilisateur = utilisateurDAO.findById(id);

                if (utilisateur != null) {
                    // Mettre à jour les informations de base
                    utilisateur.setNom(nom);
                    utilisateur.setPrenom(prenom);
                    utilisateur.setEmail(email);

                    // Mettre à jour le mot de passe seulement s'il est fourni
                    if (motDePasse != null && !motDePasse.trim().isEmpty()) {
                        utilisateur.setMotDePasse(motDePasse);
                    }

                    // Note: Le type ne change pas lors de l'édition
                    // car cela pourrait causer des problèmes de cohérence des données

                    utilisateurDAO.update(utilisateur);
                } else {
                    resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Utilisateur non trouvé");
                    return;
                }

            } else {
                // **MODE CRÉATION** - Création d'un nouvel utilisateur
                Utilisateur utilisateur;

                // Créer l'instance selon le type sélectionné
                if ("ADMIN".equalsIgnoreCase(type)) {
                    utilisateur = new Administrateur();
                } else if ("LOCATAIRE".equalsIgnoreCase(type)) {
                    utilisateur = new Locataire();
                } else if ("PROPRIETAIRE".equalsIgnoreCase(type)) {
                    utilisateur = new Proprietaire();
                } else {
                    resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Type d'utilisateur invalide: " + type);
                    return;
                }

                // Définir les propriétés
                utilisateur.setNom(nom);
                utilisateur.setPrenom(prenom);
                utilisateur.setEmail(email);
                utilisateur.setMotDePasse(motDePasse);

                utilisateurDAO.save(utilisateur);
            }

            // Redirection vers la liste des utilisateurs après succès
            resp.sendRedirect(req.getContextPath() + "/utilisateurs");

        } catch (NumberFormatException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID utilisateur invalide");
        } catch (Exception e) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Erreur lors de la sauvegarde: " + e.getMessage());
        }
    }

    /**
     * PUT → Mise à jour d’un utilisateur
     */
    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Long id = Long.parseLong(req.getParameter("id"));
        Utilisateur utilisateur = utilisateurDAO.findById(id);

        if (utilisateur != null) {
            String nom = req.getParameter("nom");
            String prenom = req.getParameter("prenom");
            String email = req.getParameter("email");
            String motDePasse = req.getParameter("motDePasse");

            if (nom != null) utilisateur.setNom(nom);
            if (prenom != null) utilisateur.setPrenom(prenom);
            if (email != null) utilisateur.setEmail(email);
            if (motDePasse != null) utilisateur.setMotDePasse(motDePasse);

            utilisateurDAO.update(utilisateur);
            resp.getWriter().write("✅ Utilisateur mis à jour avec succès");
        } else {
            resp.getWriter().write("❌ Utilisateur non trouvé");
        }
    }

    /**
     * DELETE → Suppression d’un utilisateur
     */
    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Long id = Long.parseLong(req.getParameter("id"));
        utilisateurDAO.delete(id);
        resp.getWriter().write("✅ Utilisateur supprimé avec succès");
    }
}
