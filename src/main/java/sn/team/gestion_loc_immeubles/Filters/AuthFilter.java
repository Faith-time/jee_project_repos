package sn.team.gestion_loc_immeubles.Filters;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * Filtre d'authentification qui contrôle l'accès à toutes les pages
 * Redirige vers la page de connexion si l'utilisateur n'est pas connecté
 */
@WebFilter("/*")
public class AuthFilter implements Filter {

    // URLs publiques accessibles sans authentification
    private static final List<String> PUBLIC_URLS = Arrays.asList(
            "/auth",           // Pages d'authentification
            "/shared/",        // Ressources partagées (login, register)
            "/assets/",        // CSS, JS, images
            "/favicon.ico"     // Icône du site
    );

    // Extensions de fichiers statiques autorisées
    private static final List<String> STATIC_EXTENSIONS = Arrays.asList(
            ".css", ".js", ".png", ".jpg", ".jpeg", ".gif", ".ico",
            ".woff", ".woff2", ".ttf", ".eot", ".svg"
    );

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // Initialisation du filtre si nécessaire
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String requestURI = httpRequest.getRequestURI();
        String contextPath = httpRequest.getContextPath();

        // Enlever le context path pour obtenir l'URI relative
        String relativePath = requestURI.substring(contextPath.length());

        // Vérifier si l'URL est publique ou statique
        if (isPublicResource(relativePath)) {
            chain.doFilter(request, response);
            return;
        }

        // Vérifier l'authentification - CORRECTION: utiliser "utilisateur" au lieu de "user"
        HttpSession session = httpRequest.getSession(false);
        boolean isLoggedIn = (session != null && session.getAttribute("utilisateur") != null);

        if (isLoggedIn) {
            // Utilisateur connecté, laisser passer
            chain.doFilter(request, response);
        } else {
            // Utilisateur non connecté, rediriger vers login

            // Sauvegarder l'URL demandée pour redirection après connexion
            String requestedURL = httpRequest.getRequestURL().toString();
            String queryString = httpRequest.getQueryString();

            if (queryString != null) {
                requestedURL += "?" + queryString;
            }

            // Stocker l'URL dans la session pour redirection après login
            HttpSession newSession = httpRequest.getSession(true);
            newSession.setAttribute("redirectAfterLogin", requestedURL);

            // Rediriger vers la page de connexion
            httpResponse.sendRedirect(contextPath + "/auth?action=login");
        }
    }

    /**
     * Vérifier si une ressource est publique (accessible sans authentification)
     */
    private boolean isPublicResource(String path) {
        // Vérifier les URLs publiques
        for (String publicUrl : PUBLIC_URLS) {
            if (path.startsWith(publicUrl)) {
                return true;
            }
        }

        // Vérifier les extensions de fichiers statiques
        for (String extension : STATIC_EXTENSIONS) {
            if (path.toLowerCase().endsWith(extension)) {
                return true;
            }
        }

        // CORRECTION: La racine "/" n'est plus publique - elle nécessite une authentification
        return false;
    }

    @Override
    public void destroy() {
        // Nettoyage si nécessaire
    }
}