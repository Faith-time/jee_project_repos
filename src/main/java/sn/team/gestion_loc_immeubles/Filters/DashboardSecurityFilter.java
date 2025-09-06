package sn.team.gestion_loc_immeubles.Filters;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import sn.team.gestion_loc_immeubles.Entities.Role;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Filtre de sécurité pour protéger les dashboards selon le rôle d'utilisateur
 */
@WebFilter(filterName = "DashboardSecurityFilter",
        urlPatterns = {"/admin/*", "/proprietaire/*", "/locataire/*"})
public class DashboardSecurityFilter implements Filter {

    private Map<String, Role> urlRoleMapping;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // Initialiser le mapping des URLs et rôles
        urlRoleMapping = new HashMap<>();
        urlRoleMapping.put("/admin/", Role.ADMIN);
        urlRoleMapping.put("/proprietaire/", Role.PROPRIETAIRE);
        urlRoleMapping.put("/locataire/", Role.LOCATAIRE);
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;

        // Obtenir l'URI de la requête
        String requestURI = req.getRequestURI();
        String contextPath = req.getContextPath();
        String path = requestURI.substring(contextPath.length());

        // Déterminer le rôle requis pour cette URL
        Role roleRequis = determinerRoleRequis(path);

        if (roleRequis != null) {
            // Vérifier l'authentification et les autorisations
            if (!verifierAcces(req, resp, roleRequis)) {
                return; // L'accès a été refusé, arrêter le traitement
            }
        }

        // Continuer la chaîne de filtres si tout est OK
        chain.doFilter(request, response);
    }

    /**
     * Détermine le rôle requis selon l'URL
     */
    private Role determinerRoleRequis(String path) {
        for (Map.Entry<String, Role> entry : urlRoleMapping.entrySet()) {
            if (path.startsWith(entry.getKey())) {
                return entry.getValue();
            }
        }
        return null;
    }

    /**
     * Vérifie l'accès de l'utilisateur
     */
    private boolean verifierAcces(HttpServletRequest req, HttpServletResponse resp, Role roleRequis)
            throws IOException {

        HttpSession session = req.getSession(false);

        // Vérifier si l'utilisateur est connecté
        if (session == null || session.getAttribute("utilisateur") == null) {
            sauvegarderURLEtRediriger(req, resp);
            return false;
        }

        // Récupérer le rôle de l'utilisateur
        String roleString = (String) session.getAttribute("role");
        Role roleUtilisateur = Role.fromString(roleString);

        // Vérifier le rôle
        if (roleRequis != roleUtilisateur) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN,
                    "Accès non autorisé. Vous n'avez pas les permissions nécessaires pour accéder à cette section.");
            return false;
        }

        return true; // Accès autorisé
    }

    /**
     * Sauvegarde l'URL actuelle et redirige vers la page de connexion
     */
    private void sauvegarderURLEtRediriger(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        // Créer une nouvelle session pour sauvegarder l'URL de redirection
        HttpSession session = req.getSession(true);
        String fullURL = req.getRequestURL().toString();
        String queryString = req.getQueryString();

        if (queryString != null) {
            fullURL += "?" + queryString;
        }

        session.setAttribute("redirectAfterLogin", fullURL);

        // Redirige vers la page de connexion
        resp.sendRedirect(req.getContextPath() + "/auth?action=login");
    }

    @Override
    public void destroy() {
        urlRoleMapping = null;
    }
}