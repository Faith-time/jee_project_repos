package sn.team.gestion_loc_immeubles.Controllers;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import sn.team.gestion_loc_immeubles.DAO.UtilisateurDAO;
import sn.team.gestion_loc_immeubles.DAO.UtilisateurDAOImpl;
import sn.team.gestion_loc_immeubles.Entities.Utilisateur;

import java.io.IOException;
import java.util.List;

@WebServlet("/admin/dashboard")
public class AdminDashboardServlet extends HttpServlet {

    private final UtilisateurDAO utilisateurDAO = new UtilisateurDAOImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        // Vérifier la session et le rôle
        HttpSession session = req.getSession(false);
        if (session == null || !"ADMIN".equals(session.getAttribute("role"))) {
            resp.sendRedirect(req.getContextPath() + "/auth?action=login");
            return;
        }

        // Récupérer les données pour le dashboard admin
        List<Utilisateur> utilisateurs = utilisateurDAO.findAll();
        utilisateurs.forEach(u -> u.setClassName(u.getClass().getSimpleName()));

        // Calcul des statistiques selon votre JSP existante
        long nbUtilisateurs = utilisateurs.size();
        long nbProprietaires = utilisateurs.stream()
                .filter(u -> "Proprietaire".equals(u.getClassName()))
                .count();
        long nbLocataires = utilisateurs.stream()
                .filter(u -> "Locataire".equals(u.getClassName()))
                .count();

        // TODO: Récupérer le nombre d'immeubles depuis la base de données
        // long nbImmeubles = immeubleDAO.count();
        long nbImmeubles = 0; // À remplacer par la vraie valeur

        // Ajouter les attributs requis par votre JSP
        req.setAttribute("nbUtilisateurs", nbUtilisateurs);
        req.setAttribute("nbProprietaires", nbProprietaires);
        req.setAttribute("nbLocataires", nbLocataires);
        req.setAttribute("nbImmeubles", nbImmeubles);

        // Forward vers votre JSP existante
        req.getRequestDispatcher("/WEB-INF/jsp/admin/dashboard.jsp").forward(req, resp);
    }
}