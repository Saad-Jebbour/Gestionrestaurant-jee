package ma.restaurant.filter;

import java.io.IOException;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * Filtre d'authentification :
 * Intercepte toutes les requêtes (/*), à l'exception de la page de connexion (/login, /logout)
 * et des ressources statiques (CSS, JS, images...).
 * Si l'utilisateur n'est pas connecté, il est redirigé vers /login.
 */
@WebFilter(filterName = "AuthentificationFilter")
public class AuthentificationFilter extends HttpFilter {

    @Override
    protected void doFilter(HttpServletRequest req, HttpServletResponse resp, FilterChain chain)
            throws IOException, ServletException {

        String uri = req.getRequestURI();
        String contextPath = req.getContextPath();
        String path = uri.substring(contextPath.length());

        // 1. Autoriser les chemins publics (connexion et ressources statiques)
        if (isRessourcePublique(path)) {
            chain.doFilter(req, resp);
            return;
        }

        // 2. Vérifier la présence d'un utilisateur en session
        HttpSession session = req.getSession(false);
        boolean estConnecte = (session != null && session.getAttribute("user") != null);

        if (estConnecte) {
            chain.doFilter(req, resp);
        } else {
            // Utilisateur non connecté : redirection vers /login
            // (ne pas afficher de message d'erreur s'il visite simplement l'accueil)
            if (!path.isEmpty() && !"/".equals(path) && !"/accueil".equalsIgnoreCase(path)) {
                HttpSession nouvelleSession = req.getSession(true);
                nouvelleSession.setAttribute("erreur", "Veuillez vous connecter pour accéder à cette page.");
            }
            resp.sendRedirect(contextPath + "/login");
        }
    }

    /**
     * Vérifie si le chemin demandé correspond à une ressource accessible sans authentification.
     */
    private boolean isRessourcePublique(String path) {
        if (path == null || path.isEmpty()) {
            return false;
        }

        String pathLower = path.toLowerCase();

        // Pages d'authentification
        if (pathLower.equals("/login") || pathLower.equals("/logout")) {
            return true;
        }

        // Dossiers de ressources statiques
        if (pathLower.startsWith("/css/") || pathLower.startsWith("/js/")
                || pathLower.startsWith("/images/") || pathLower.startsWith("/img/")
                || pathLower.startsWith("/assets/") || pathLower.startsWith("/webjars/")) {
            return true;
        }

        // Fichiers statiques par extension
        return pathLower.endsWith(".css")
                || pathLower.endsWith(".js")
                || pathLower.endsWith(".png")
                || pathLower.endsWith(".jpg")
                || pathLower.endsWith(".jpeg")
                || pathLower.endsWith(".gif")
                || pathLower.endsWith(".svg")
                || pathLower.endsWith(".ico")
                || pathLower.endsWith(".woff")
                || pathLower.endsWith(".woff2")
                || pathLower.endsWith(".ttf");
    }
}
