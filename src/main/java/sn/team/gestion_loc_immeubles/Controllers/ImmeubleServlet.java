package sn.team.gestion_loc_immeubles.Controllers;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import sn.team.gestion_loc_immeubles.DAO.ImmeubleDAO;
import sn.team.gestion_loc_immeubles.DAO.ImmeubleDAOImpl;
import sn.team.gestion_loc_immeubles.DAO.UtilisateurDAO;
import sn.team.gestion_loc_immeubles.DAO.UtilisateurDAOImpl;
import sn.team.gestion_loc_immeubles.Entities.Immeuble;
import sn.team.gestion_loc_immeubles.Entities.Role;
import sn.team.gestion_loc_immeubles.Entities.Utilisateur;

import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet("/immeubles")
public class ImmeubleServlet extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(ImmeubleServlet.class.getName());
    private ImmeubleDAO immeubleDAO;
    private UtilisateurDAO utilisateurDAO;

    @Override
    public void init() {
        this.immeubleDAO = new ImmeubleDAOImpl();
        this.utilisateurDAO = new UtilisateurDAOImpl();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession();
        Utilisateur utilisateurConnecte = (Utilisateur) session.getAttribute("utilisateur");

        // Vérifier si l'utilisateur est connecté
        if (utilisateurConnecte == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        String idParam = req.getParameter("id");
        String proprietaireIdParam = req.getParameter("proprietaireId");
        String action = req.getParameter("action");

        try {
            if ("delete".equals(action) && idParam != null) {
                // Action de suppression via GET (pour les liens directs)
                handleDelete(req, resp, Long.parseLong(idParam));
                return;
            }

            if (idParam != null) {
                // Détail d'un immeuble
                Long id = Long.parseLong(idParam);
                Immeuble immeuble = immeubleDAO.findById(id);

                if (immeuble == null) {
                    session.setAttribute("errorMessage", "Immeuble introuvable.");
                    resp.sendRedirect(req.getContextPath() + "/immeubles");
                    return;
                }

                req.setAttribute("immeuble", immeuble);
                req.getRequestDispatcher("/WEB-INF/jsp/proprietaire/detail_immeuble.jsp").forward(req, resp);

            } else if (proprietaireIdParam != null) {
                // Liste des immeubles d'un propriétaire spécifique
                Long proprietaireId = Long.parseLong(proprietaireIdParam);
                List<Immeuble> immeubles = immeubleDAO.findByProprietaire(proprietaireId);
                req.setAttribute("immeubles", immeubles);
                req.getRequestDispatcher("/WEB-INF/jsp/proprietaire/gestion_immeubles.jsp").forward(req, resp);

            } else {
                // Liste selon le rôle de l'utilisateur connecté
                List<Immeuble> immeubles;

                if (utilisateurConnecte.getRole() == Role.ADMIN) {
                    // Admin : voir tous les immeubles
                    immeubles = immeubleDAO.findAll();
                } else if (utilisateurConnecte.getRole() == Role.PROPRIETAIRE) {
                    // Propriétaire : voir ses propres immeubles
                    immeubles = immeubleDAO.findByProprietaire(utilisateurConnecte.getId());
                } else {
                    // Autres rôles : rediriger vers le dashboard
                    resp.sendRedirect(req.getContextPath() + "/dashboard");
                    return;
                }

                req.setAttribute("immeubles", immeubles);
                req.getRequestDispatcher("/WEB-INF/jsp/proprietaire/gestion_immeubles.jsp").forward(req, resp);
            }

        } catch (NumberFormatException e) {
            LOGGER.log(Level.WARNING, "Paramètre ID invalide", e);
            session.setAttribute("errorMessage", "Paramètre invalide.");
            resp.sendRedirect(req.getContextPath() + "/immeubles");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur lors du traitement de la requête GET", e);
            session.setAttribute("errorMessage", "Erreur interne du serveur.");
            resp.sendRedirect(req.getContextPath() + "/immeubles");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession();
        Utilisateur utilisateurConnecte = (Utilisateur) session.getAttribute("utilisateur");

        if (utilisateurConnecte == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        String idParam = req.getParameter("id");
        String method = req.getParameter("_method");

        try {
            if ("DELETE".equals(method) && idParam != null) {
                // Suppression via POST avec _method=DELETE
                handleDelete(req, resp, Long.parseLong(idParam));
                return;
            }

            if (idParam != null && !idParam.trim().isEmpty()) {
                // Modification d'un immeuble existant
                handleUpdate(req, resp, session);
            } else {
                // Création d'un nouvel immeuble
                handleCreate(req, resp, session, utilisateurConnecte);
            }

        } catch (NumberFormatException e) {
            LOGGER.log(Level.WARNING, "Paramètre ID invalide", e);
            session.setAttribute("errorMessage", "Paramètre invalide.");
            resp.sendRedirect(req.getContextPath() + "/immeubles");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur lors du traitement de la requête POST", e);
            session.setAttribute("errorMessage", "Erreur lors de l'opération. Veuillez réessayer.");
            resp.sendRedirect(req.getContextPath() + "/immeubles");
        }
    }

    private void handleCreate(HttpServletRequest req, HttpServletResponse resp, HttpSession session, Utilisateur utilisateurConnecte) throws IOException {
        // Validation des données
        String nom = req.getParameter("nom");
        String adresse = req.getParameter("adresse");
        String description = req.getParameter("description");
        String proprietaireIdParam = req.getParameter("proprietaireId");

        if (nom == null || nom.trim().isEmpty() || nom.length() < 3 || nom.length() > 100) {
            session.setAttribute("errorMessage", "Le nom de l'immeuble doit contenir entre 3 et 100 caractères.");
            resp.sendRedirect(req.getContextPath() + "/immeubles");
            return;
        }

        if (adresse == null || adresse.trim().isEmpty()) {
            session.setAttribute("errorMessage", "L'adresse est obligatoire.");
            resp.sendRedirect(req.getContextPath() + "/immeubles");
            return;
        }

        if (description != null && description.length() > 255) {
            session.setAttribute("errorMessage", "La description ne peut pas dépasser 255 caractères.");
            resp.sendRedirect(req.getContextPath() + "/immeubles");
            return;
        }

        // Création de l'immeuble
        Immeuble immeuble = new Immeuble();
        immeuble.setNom(nom.trim());
        immeuble.setAdresse(adresse.trim());
        immeuble.setDescription(description != null ? description.trim() : null);

        // Déterminer le propriétaire
        Long proprietaireId;
        if (proprietaireIdParam != null && !proprietaireIdParam.trim().isEmpty()) {
            proprietaireId = Long.parseLong(proprietaireIdParam);
        } else {
            proprietaireId = utilisateurConnecte.getId();
        }

        Utilisateur proprietaire = utilisateurDAO.findById(proprietaireId);

        if (proprietaire == null || proprietaire.getRole() != Role.PROPRIETAIRE) {
            session.setAttribute("errorMessage", "Le propriétaire spécifié est invalide.");
            resp.sendRedirect(req.getContextPath() + "/immeubles");
            return;
        }

        // Vérifier les autorisations
        if (utilisateurConnecte.getRole() != Role.ADMIN &&
                !utilisateurConnecte.getId().equals(proprietaireId)) {
            session.setAttribute("errorMessage", "Vous n'êtes pas autorisé à créer un immeuble pour ce propriétaire.");
            resp.sendRedirect(req.getContextPath() + "/immeubles");
            return;
        }

        immeuble.setProprietaire(proprietaire);

        try {
            immeubleDAO.save(immeuble);
            session.setAttribute("successMessage", "Immeuble '" + nom + "' ajouté avec succès!");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la création de l'immeuble", e);
            session.setAttribute("errorMessage", "Erreur lors de la création de l'immeuble. Veuillez réessayer.");
        }

        resp.sendRedirect(req.getContextPath() + "/immeubles");
    }

    private void handleUpdate(HttpServletRequest req, HttpServletResponse resp, HttpSession session) throws IOException {
        Long id = Long.parseLong(req.getParameter("id"));
        Immeuble immeuble = immeubleDAO.findById(id);

        if (immeuble == null) {
            session.setAttribute("errorMessage", "Immeuble introuvable.");
            resp.sendRedirect(req.getContextPath() + "/immeubles");
            return;
        }

        // Validation des données
        String nom = req.getParameter("nom");
        String adresse = req.getParameter("adresse");
        String description = req.getParameter("description");
        String proprietaireIdParam = req.getParameter("proprietaireId");

        if (nom == null || nom.trim().isEmpty() || nom.length() < 3 || nom.length() > 100) {
            session.setAttribute("errorMessage", "Le nom de l'immeuble doit contenir entre 3 et 100 caractères.");
            resp.sendRedirect(req.getContextPath() + "/immeubles");
            return;
        }

        if (adresse == null || adresse.trim().isEmpty()) {
            session.setAttribute("errorMessage", "L'adresse est obligatoire.");
            resp.sendRedirect(req.getContextPath() + "/immeubles");
            return;
        }

        if (description != null && description.length() > 255) {
            session.setAttribute("errorMessage", "La description ne peut pas dépasser 255 caractères.");
            resp.sendRedirect(req.getContextPath() + "/immeubles");
            return;
        }

        // Mise à jour des propriétés
        immeuble.setNom(nom.trim());
        immeuble.setAdresse(adresse.trim());
        immeuble.setDescription(description != null ? description.trim() : null);

        // Mise à jour du propriétaire si nécessaire
        if (proprietaireIdParam != null && !proprietaireIdParam.trim().isEmpty()) {
            Long proprietaireId = Long.parseLong(proprietaireIdParam);
            Utilisateur proprietaire = utilisateurDAO.findById(proprietaireId);

            if (proprietaire != null && proprietaire.getRole() == Role.PROPRIETAIRE) {
                immeuble.setProprietaire(proprietaire);
            } else {
                session.setAttribute("errorMessage", "Le propriétaire spécifié est invalide.");
                resp.sendRedirect(req.getContextPath() + "/immeubles");
                return;
            }
        }

        try {
            immeubleDAO.update(immeuble);
            session.setAttribute("successMessage", "Immeuble '" + nom + "' modifié avec succès!");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la modification de l'immeuble", e);
            session.setAttribute("errorMessage", "Erreur lors de la modification de l'immeuble. Veuillez réessayer.");
        }

        resp.sendRedirect(req.getContextPath() + "/immeubles");
    }

    private void handleDelete(HttpServletRequest req, HttpServletResponse resp, Long id) throws IOException {
        HttpSession session = req.getSession();

        try {
            Immeuble immeuble = immeubleDAO.findById(id);

            if (immeuble == null) {
                session.setAttribute("errorMessage", "Immeuble introuvable.");
            } else {
                // Vérifier s'il y a des unités associées
                if (immeuble.getUnites() != null && !immeuble.getUnites().isEmpty()) {
                    session.setAttribute("errorMessage",
                            "Impossible de supprimer l'immeuble '" + immeuble.getNom() +
                                    "' car il contient " + immeuble.getUnites().size() + " unité(s). " +
                                    "Supprimez d'abord toutes les unités.");
                } else {
                    String nomImmeuble = immeuble.getNom();
                    immeubleDAO.delete(id);
                    session.setAttribute("successMessage", "Immeuble '" + nomImmeuble + "' supprimé avec succès!");
                }
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la suppression de l'immeuble", e);
            session.setAttribute("errorMessage", "Erreur lors de la suppression. L'immeuble pourrait contenir des unités associées.");
        }

        resp.sendRedirect(req.getContextPath() + "/immeubles");
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        // Rediriger vers doPost pour gérer la mise à jour
        doPost(req, resp);
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String idParam = req.getParameter("id");
        if (idParam != null) {
            try {
                handleDelete(req, resp, Long.parseLong(idParam));
            } catch (NumberFormatException e) {
                HttpSession session = req.getSession();
                session.setAttribute("errorMessage", "Paramètre ID invalide.");
                resp.sendRedirect(req.getContextPath() + "/immeubles");
            }
        }
    }
}