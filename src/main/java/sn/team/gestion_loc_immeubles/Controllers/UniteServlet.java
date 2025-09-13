package sn.team.gestion_loc_immeubles.Controllers;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import sn.team.gestion_loc_immeubles.DAO.*;
import sn.team.gestion_loc_immeubles.Entities.*;
import sn.team.gestion_loc_immeubles.Services.UniteService;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet("/unites")
public class UniteServlet extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(UniteServlet.class.getName());
    private UniteDAO uniteDAO;
    private ImmeubleDAO immeubleDAO;
    private UniteService uniteService;
    private ContratDAO contratDAO;
    private PaiementDAO paiementDAO;

    @Override
    public void init() {
        this.uniteDAO = new UniteDAOImpl();
        this.immeubleDAO = new ImmeubleDAOImpl();
        this.uniteService = new UniteService();
        this.contratDAO = new ContratDAOImpl();
        this.paiementDAO = new PaiementDAOImpl();

    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession();
        Utilisateur utilisateurConnecte = (Utilisateur) session.getAttribute("utilisateur");

        if (utilisateurConnecte == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        String action = req.getParameter("action");
        String idParam = req.getParameter("id");
        String immeubleIdParam = req.getParameter("immeubleId");
        String role = req.getParameter("role");

        // Log de débogage
        LOGGER.info("UniteServlet - Utilisateur connecté: ID=" + utilisateurConnecte.getId() +
                ", Rôle=" + utilisateurConnecte.getRole() + ", Action=" + action);

        try {
            // --- Liste des unités disponibles pour le locataire ---
            if ("disponibles".equals(action)) {
                List<Unite> unitesDisponibles = uniteService.listerUnitesDisponibles();
                req.setAttribute("unites", unitesDisponibles);
                req.getRequestDispatcher("/WEB-INF/jsp/locataire/Liste_unites.jsp").forward(req, resp);
                return;
            }

            // --- Louer une unité ---
            if ("louer".equals(action) && idParam != null) {
                handleLouerUnite(req, resp, Long.parseLong(idParam));
                return;
            }

            // --- Libérer une unité ---
            if ("liberer".equals(action) && idParam != null) {
                handleLibererUnite(req, resp, Long.parseLong(idParam));
                return;
            }

            // --- Supprimer une unité ---
            if ("delete".equals(action) && idParam != null) {
                handleDelete(req, resp, Long.parseLong(idParam));
                return;
            }

            // --- Modifier / Détail d'une unité ---
            if (idParam != null) {
                Long id = Long.parseLong(idParam);
                Unite unite = uniteDAO.findById(id);
                if (unite == null) {
                    session.setAttribute("errorMessage", "Unité introuvable.");
                    resp.sendRedirect(req.getContextPath() + "/unites");
                    return;
                }
                List<Immeuble> immeubles = immeubleDAO.findAll();
                req.setAttribute("immeubles", immeubles);
                req.setAttribute("unite", unite);

                // Calcul des statistiques
                long totalUnites = 0;
                long unitesDisponibles = 0;

                if (utilisateurConnecte.getRole() == Role.PROPRIETAIRE) {
                    totalUnites = uniteService.compterUnitesByProprietaire(utilisateurConnecte.getId());
                    unitesDisponibles = uniteService.compterUnitesDisponiblesDuProprietaire(utilisateurConnecte.getId());
                    LOGGER.info("Statistiques propriétaire - Total: " + totalUnites + ", Disponibles: " + unitesDisponibles);
                } else {
                    totalUnites = uniteService.compterToutesUnites();
                    unitesDisponibles = uniteService.compterUnitesDisponibles();
                    LOGGER.info("Statistiques globales - Total: " + totalUnites + ", Disponibles: " + unitesDisponibles);
                }

                req.setAttribute("totalUnites", totalUnites);
                req.setAttribute("unitesDisponibles", unitesDisponibles);
                req.setAttribute("totalUnitesImmeuble", unite.getImmeuble() != null ?
                        uniteService.compterUnitesByImmeuble(unite.getImmeuble().getId()) : 0L);

                req.getRequestDispatcher("/WEB-INF/jsp/proprietaire/unites-form.jsp").forward(req, resp);
                return;
            }

            // --- Liste des unités par immeuble ---
            if (immeubleIdParam != null) {
                Long immeubleId = Long.parseLong(immeubleIdParam);
                Immeuble immeuble = immeubleDAO.findById(immeubleId);
                if (immeuble == null) {
                    session.setAttribute("errorMessage", "Immeuble introuvable.");
                    resp.sendRedirect(req.getContextPath() + "/immeubles");
                    return;
                }
                List<Unite> unites = uniteDAO.findByImmeuble(immeubleId);
                long totalUnitesImmeuble = uniteService.compterUnitesByImmeuble(immeubleId);

                req.setAttribute("unites", unites);
                req.setAttribute("immeuble", immeuble);
                req.setAttribute("totalUnitesImmeuble", totalUnitesImmeuble);

                // Calcul des statistiques
                long totalUnites = 0;
                long unitesDisponibles = 0;

                if (utilisateurConnecte.getRole() == Role.PROPRIETAIRE) {
                    totalUnites = uniteService.compterUnitesByProprietaire(utilisateurConnecte.getId());
                    unitesDisponibles = uniteService.compterUnitesDisponiblesDuProprietaire(utilisateurConnecte.getId());
                    LOGGER.info("Stats propriétaire (immeuble) - Total: " + totalUnites + ", Disponibles: " + unitesDisponibles);
                } else {
                    totalUnites = uniteService.compterToutesUnites();
                    unitesDisponibles = uniteService.compterUnitesDisponibles();
                }

                req.setAttribute("totalUnites", totalUnites);
                req.setAttribute("unitesDisponibles", unitesDisponibles);

                req.getRequestDispatcher("/WEB-INF/jsp/proprietaire/gestion_unites.jsp").forward(req, resp);
                return;
            }

            // --- Dashboard général selon le rôle ---
            List<Unite> unites;
            List<Immeuble> tousImmeubles;
            long totalUnites = 0;
            long unitesDisponibles = 0;

            switch (utilisateurConnecte.getRole()) {
                case ADMIN:
                    unites = uniteDAO.findAll();
                    tousImmeubles = immeubleDAO.findAll();
                    totalUnites = uniteService.compterToutesUnites();
                    unitesDisponibles = uniteService.compterUnitesDisponibles();
                    LOGGER.info("Dashboard ADMIN - Total: " + totalUnites + ", Disponibles: " + unitesDisponibles);
                    break;

                case PROPRIETAIRE:
                    // Récupérer les unités du propriétaire
                    unites = uniteService.getUnitesDuProprietaire(utilisateurConnecte.getId());

                    // Récupérer seulement les immeubles du propriétaire
                    tousImmeubles = immeubleDAO.findByProprietaire(utilisateurConnecte.getId());

                    // Calculer les statistiques spécifiques au propriétaire
                    totalUnites = uniteService.compterUnitesByProprietaire(utilisateurConnecte.getId());
                    unitesDisponibles = uniteService.compterUnitesDisponiblesDuProprietaire(utilisateurConnecte.getId());

                    LOGGER.info("Dashboard PROPRIETAIRE - ID: " + utilisateurConnecte.getId() +
                            ", Total: " + totalUnites + ", Disponibles: " + unitesDisponibles +
                            ", Unités récupérées: " + unites.size());
                    break;

                default: // LOCATAIRE
                    unites = uniteService.listerUnitesDisponibles();
                    tousImmeubles = immeubleDAO.findAll();
                    totalUnites = uniteService.compterToutesUnites();
                    unitesDisponibles = uniteService.compterUnitesDisponibles();
                    LOGGER.info("Dashboard LOCATAIRE - Total: " + totalUnites + ", Disponibles: " + unitesDisponibles);
                    break;
            }

            // Définir les attributs pour la vue
            req.setAttribute("immeubles", tousImmeubles);
            req.setAttribute("totalUnites", totalUnites);
            req.setAttribute("unitesDisponibles", unitesDisponibles);
            req.setAttribute("totalUnitesImmeuble", 0L);
            req.setAttribute("unites", unites);

            // Log final des attributs
            LOGGER.info("Attributs envoyés à la vue - totalUnites: " + totalUnites +
                    ", unitesDisponibles: " + unitesDisponibles +
                    ", nombre unités: " + unites.size());

            // Choix de la vue
            if ("locataire".equalsIgnoreCase(role) || utilisateurConnecte.getRole() == Role.LOCATAIRE) {
                req.getRequestDispatcher("/WEB-INF/jsp/locataire/Liste_unites.jsp").forward(req, resp);
            } else {
                req.getRequestDispatcher("/WEB-INF/jsp/proprietaire/gestion_unites.jsp").forward(req, resp);
            }

        } catch (NumberFormatException e) {
            LOGGER.log(Level.WARNING, "Paramètre ID invalide", e);
            session.setAttribute("errorMessage", "Paramètre invalide.");
            resp.sendRedirect(req.getContextPath() + "/unites");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur lors du traitement de la requête GET", e);
            session.setAttribute("errorMessage", "Erreur interne du serveur.");
            resp.sendRedirect(req.getContextPath() + "/unites");
        }
    }    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession();
        Utilisateur utilisateurConnecte = (Utilisateur) session.getAttribute("utilisateur");

        if (utilisateurConnecte == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        String action = req.getParameter("action");
        String idParam = req.getParameter("id");
        String method = req.getParameter("_method");

        try {
            if ("DELETE".equals(method) && idParam != null) {
                handleDelete(req, resp, Long.parseLong(idParam));
                return;
            }

            if ("louer".equals(action)) {
                Long uniteId = Long.parseLong(req.getParameter("uniteId"));
                Unite unite = uniteDAO.findById(uniteId);

                if (unite != null && unite.getStatut() == Unite.StatutUnite.DISPONIBLE) {
                    // 1. Mise à jour du statut
                    unite.setStatut(Unite.StatutUnite.LOUEE);
                    uniteDAO.update(unite);

                    // 2. Création du contrat
                    Contrat contrat = new Contrat();
                    contrat.setDateDebut(LocalDate.now());
                    contrat.setDateFin(LocalDate.now().plusYears(1)); // +1 an
                    contrat.setMontant(unite.getLoyer());
                    contrat.setLocataire(utilisateurConnecte); // le locataire connecté
                    contrat.setUnite(unite);

                    // 3. Sauvegarde en BDD
                    contratDAO.save(contrat);

                    // 4. NOUVEAU : Création automatique du premier paiement
                    Paiement premierPaiement = new Paiement();
                    premierPaiement.setMontant(unite.getLoyer());
                    premierPaiement.setDateEcheance(LocalDate.now().plusMonths(1)); // Échéance dans 1 mois
                    premierPaiement.setStatut(Paiement.StatutPaiement.EN_ATTENTE);
                    premierPaiement.setContrat(contrat);

                    // Générer un transactionId unique
                    String transactionId = "PAI-" + contrat.getId() + "-" + System.currentTimeMillis();
                    premierPaiement.setTransactionId(transactionId);

                    // 5. Sauvegarde du paiement en BDD
                    paiementDAO.save(premierPaiement);

                    LOGGER.info("Paiement automatique créé pour le contrat ID: " + contrat.getId() +
                            " avec transaction ID: " + transactionId);
                }

                resp.sendRedirect(req.getContextPath() + "/unites"); // retour liste
                return;
            }

            if (idParam != null && !idParam.trim().isEmpty()) {
                handleUpdate(req, resp, session);
            } else {
                handleCreate(req, resp, session);
            }

        } catch (NumberFormatException e) {
            LOGGER.log(Level.WARNING, "Paramètre ID invalide", e);
            session.setAttribute("errorMessage", "Paramètre invalide.");
            resp.sendRedirect(req.getContextPath() + "/unites");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur lors du traitement de la requête POST", e);
            session.setAttribute("errorMessage", "Erreur lors de l'opération. Veuillez réessayer.");
            resp.sendRedirect(req.getContextPath() + "/unites");
        }
    }


    private void handleLouerUnite(HttpServletRequest req, HttpServletResponse resp, Long uniteId) throws IOException {
        HttpSession session = req.getSession();

        try {
            boolean success = uniteService.louerUnite(uniteId);

            if (success) {
                session.setAttribute("successMessage", "L'unité a été louée avec succès!");
            } else {
                session.setAttribute("errorMessage", "Erreur lors de la location. L'unité pourrait déjà être louée.");
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la location de l'unité", e);
            session.setAttribute("errorMessage", "Erreur lors de la location de l'unité.");
        }

        resp.sendRedirect(req.getContextPath() + "/unites?action=disponibles");
    }

    private void handleLibererUnite(HttpServletRequest req, HttpServletResponse resp, Long uniteId) throws IOException {
        HttpSession session = req.getSession();

        try {
            boolean success = uniteService.libererUnite(uniteId);

            if (success) {
                session.setAttribute("successMessage", "L'unité a été libérée avec succès!");
            } else {
                session.setAttribute("errorMessage", "Erreur lors de la libération de l'unité.");
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la libération de l'unité", e);
            session.setAttribute("errorMessage", "Erreur lors de la libération de l'unité.");
        }

        resp.sendRedirect(req.getContextPath() + "/unites");
    }

    private void handleCreate(HttpServletRequest req, HttpServletResponse resp, HttpSession session) throws IOException {
        // Validation des données
        String numeroStr = req.getParameter("numero");
        String nbPiecesStr = req.getParameter("nbPieces");
        String superficieStr = req.getParameter("superficie");
        String loyerStr = req.getParameter("loyer");
        String statutStr = req.getParameter("statut");
        String immeubleIdStr = req.getParameter("immeubleId");

        if (numeroStr == null || numeroStr.trim().isEmpty() ||
                nbPiecesStr == null || nbPiecesStr.trim().isEmpty() ||
                superficieStr == null || superficieStr.trim().isEmpty() ||
                loyerStr == null || loyerStr.trim().isEmpty() ||
                immeubleIdStr == null || immeubleIdStr.trim().isEmpty()) {

            session.setAttribute("errorMessage", "Tous les champs obligatoires doivent être renseignés.");
            resp.sendRedirect(req.getContextPath() + "/unites");
            return;
        }

        try {
            Integer numero = Integer.parseInt(numeroStr);
            Integer nbPieces = Integer.parseInt(nbPiecesStr);
            Double superficie = Double.parseDouble(superficieStr);
            Double loyer = Double.parseDouble(loyerStr);
            Long immeubleId = Long.parseLong(immeubleIdStr);

            // Validations métier
            if (nbPieces < 1) {
                session.setAttribute("errorMessage", "Le nombre de pièces doit être au minimum 1.");
                resp.sendRedirect(req.getContextPath() + "/unites");
                return;
            }

            if (superficie < 10) {
                session.setAttribute("errorMessage", "La superficie doit être au minimum 10 m².");
                resp.sendRedirect(req.getContextPath() + "/unites");
                return;
            }

            if (loyer < 0) {
                session.setAttribute("errorMessage", "Le loyer ne peut pas être négatif.");
                resp.sendRedirect(req.getContextPath() + "/unites");
                return;
            }

            // Vérifier que l'immeuble existe
            Immeuble immeuble = immeubleDAO.findById(immeubleId);
            if (immeuble == null) {
                session.setAttribute("errorMessage", "Immeuble introuvable.");
                resp.sendRedirect(req.getContextPath() + "/unites");
                return;
            }

            // Créer l'unité
            Unite unite = new Unite();
            unite.setNumero(numero);
            unite.setNbPieces(nbPieces);
            unite.setSuperficie(superficie);
            unite.setLoyer(loyer);
            unite.setImmeuble(immeuble);

            // Définir le statut
            if (statutStr != null && !statutStr.trim().isEmpty()) {
                try {
                    unite.setStatut(Unite.StatutUnite.valueOf(statutStr));
                } catch (IllegalArgumentException e) {
                    unite.setStatut(Unite.StatutUnite.DISPONIBLE);
                }
            } else {
                unite.setStatut(Unite.StatutUnite.DISPONIBLE);
            }

            uniteDAO.save(unite);
            session.setAttribute("successMessage", "Unité #" + numero + " ajoutée avec succès!");

        } catch (NumberFormatException e) {
            session.setAttribute("errorMessage", "Données numériques invalides.");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la création de l'unité", e);
            session.setAttribute("errorMessage", "Erreur lors de la création de l'unité.");
        }

        resp.sendRedirect(req.getContextPath() + "/unites?immeubleId=" + immeubleIdStr);
    }

    private void handleUpdate(HttpServletRequest req, HttpServletResponse resp, HttpSession session) throws IOException {
        String idStr = req.getParameter("id");
        Long id = Long.parseLong(idStr);

        Unite unite = uniteDAO.findById(id);
        if (unite == null) {
            session.setAttribute("errorMessage", "Unité introuvable.");
            resp.sendRedirect(req.getContextPath() + "/unites");
            return;
        }

        // Validation des données
        String numeroStr = req.getParameter("numero");
        String nbPiecesStr = req.getParameter("nbPieces");
        String superficieStr = req.getParameter("superficie");
        String loyerStr = req.getParameter("loyer");
        String statutStr = req.getParameter("statut");
        String immeubleIdStr = req.getParameter("immeubleId");

        if (numeroStr == null || numeroStr.trim().isEmpty() ||
                nbPiecesStr == null || nbPiecesStr.trim().isEmpty() ||
                superficieStr == null || superficieStr.trim().isEmpty() ||
                loyerStr == null || loyerStr.trim().isEmpty() ||
                immeubleIdStr == null || immeubleIdStr.trim().isEmpty()) {

            session.setAttribute("errorMessage", "Tous les champs obligatoires doivent être renseignés.");
            resp.sendRedirect(req.getContextPath() + "/unites");
            return;
        }

        try {
            Integer numero = Integer.parseInt(numeroStr);
            Integer nbPieces = Integer.parseInt(nbPiecesStr);
            Double superficie = Double.parseDouble(superficieStr);
            Double loyer = Double.parseDouble(loyerStr);
            Long immeubleId = Long.parseLong(immeubleIdStr);

            // Validations métier
            if (nbPieces < 1) {
                session.setAttribute("errorMessage", "Le nombre de pièces doit être au minimum 1.");
                resp.sendRedirect(req.getContextPath() + "/unites");
                return;
            }

            if (superficie < 10) {
                session.setAttribute("errorMessage", "La superficie doit être au minimum 10 m².");
                resp.sendRedirect(req.getContextPath() + "/unites");
                return;
            }

            if (loyer < 0) {
                session.setAttribute("errorMessage", "Le loyer ne peut pas être négatif.");
                resp.sendRedirect(req.getContextPath() + "/unites");
                return;
            }

            // Vérifier que l'immeuble existe
            Immeuble immeuble = immeubleDAO.findById(immeubleId);
            if (immeuble == null) {
                session.setAttribute("errorMessage", "Immeuble introuvable.");
                resp.sendRedirect(req.getContextPath() + "/unites");
                return;
            }

            // Mettre à jour l'unité
            unite.setNumero(numero);
            unite.setNbPieces(nbPieces);
            unite.setSuperficie(superficie);
            unite.setLoyer(loyer);
            unite.setImmeuble(immeuble);

            // Définir le statut
            if (statutStr != null && !statutStr.trim().isEmpty()) {
                try {
                    unite.setStatut(Unite.StatutUnite.valueOf(statutStr));
                } catch (IllegalArgumentException e) {
                    // Garder le statut actuel si invalide
                }
            }

            uniteDAO.update(unite);
            session.setAttribute("successMessage", "Unité #" + numero + " modifiée avec succès!");

        } catch (NumberFormatException e) {
            session.setAttribute("errorMessage", "Données numériques invalides.");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la modification de l'unité", e);
            session.setAttribute("errorMessage", "Erreur lors de la modification de l'unité.");
        }

        resp.sendRedirect(req.getContextPath() + "/unites?immeubleId=" + immeubleIdStr);
    }

    private void handleDelete(HttpServletRequest req, HttpServletResponse resp, Long id) throws IOException {
        HttpSession session = req.getSession();

        try {
            Unite unite = uniteDAO.findById(id);

            if (unite == null) {
                session.setAttribute("errorMessage", "Unité introuvable.");
            } else {
                // Vérifier si l'unité est actuellement louée
                if (unite.getStatut() == Unite.StatutUnite.LOUEE) {
                    session.setAttribute("errorMessage",
                            "Impossible de supprimer l'unité #" + unite.getNumero() +
                                    " car elle est actuellement louée. Libérez-la d'abord.");
                } else {
                    Integer numeroUnite = unite.getNumero();
                    uniteDAO.delete(id);
                    session.setAttribute("successMessage", "Unité #" + numeroUnite + " supprimée avec succès!");
                }
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la suppression de l'unité", e);
            session.setAttribute("errorMessage", "Erreur lors de la suppression. L'unité pourrait être liée à des contrats.");
        }

        resp.sendRedirect(req.getContextPath() + "/unites");
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
                resp.sendRedirect(req.getContextPath() + "/unites");
            }
        }
    }
}