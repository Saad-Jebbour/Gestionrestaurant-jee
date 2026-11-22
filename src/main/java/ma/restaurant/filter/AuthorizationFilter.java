package ma.restaurant.filter;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import ma.restaurant.model.Role;
import ma.restaurant.util.SessionUser;

/**
 * Filtre d'autorisation basé sur les préfixes d'URL et les rôles :
 * - /statistiques et /admin/* -> ADMIN uniquement
 * - /cuisine/*               -> CUISINIER et ADMIN
 * - /commandes/*             -> SERVEUR et ADMIN
 * 
 * En cas de rôle non autorisé, renvoie un statut HTTP 403 (Forbidden).
 */
@WebFilter(filterName = "AuthorizationFilter")
public class AuthorizationFilter extends HttpFilter {

    /** Table de correspondance : Préfixe d'URL -> Ensemble des rôles autorisés. */
    private final Map<String, Set<Role>> autorisationsParPrefixe = new LinkedHashMap<>();

    @Override
    public void init() {
        // Administration : indicateurs et gestion des plats
        autorisationsParPrefixe.put("/statistiques", Set.of(Role.ADMIN));
        autorisationsParPrefixe.put("/plats", Set.of(Role.ADMIN));
        autorisationsParPrefixe.put("/admin", Set.of(Role.ADMIN));

        // Espace cuisine
        autorisationsParPrefixe.put("/cuisine", Set.of(Role.CUISINIER, Role.ADMIN));

        // Prise et gestion des commandes en salle
        autorisationsParPrefixe.put("/commandes", Set.of(Role.SERVEUR, Role.ADMIN));
    }

    @Override
    protected void doFilter(HttpServletRequest req, HttpServletResponse resp, FilterChain chain)
            throws IOException, ServletException {

        String uri = req.getRequestURI();
        String contextPath = req.getContextPath();
        String path = uri.substring(contextPath.length());

        // 1. Rechercher si l'URL courante correspond à un préfixe restreint
        Set<Role> rolesAutorises = trouverRolesAutorises(path);

        // Si l'URL n'a pas de restriction de rôle spécifique, laisser passer
        if (rolesAutorises == null) {
            chain.doFilter(req, resp);
            return;
        }

        // 2. Récupérer l'utilisateur connecté en session
        HttpSession session = req.getSession(false);
        SessionUser user = (session != null) ? (SessionUser) session.getAttribute("user") : null;

        if (user == null) {
            // Sécurité additionnelle : si la session est expirée ou inexistante
            resp.sendRedirect(contextPath + "/login");
            return;
        }

        // 3. Vérifier si le rôle de l'utilisateur fait partie des rôles autorisés
        if (rolesAutorises.contains(user.getRole())) {
            chain.doFilter(req, resp);
        } else {
            // Rôle insuffisant : HTTP 403 Forbidden
            resp.sendError(HttpServletResponse.SC_FORBIDDEN,
                    "Accès refusé : votre rôle (" + user.getRole() + ") n'a pas les droits requis pour accéder à cette page.");
        }
    }

    /**
     * Recherche les rôles autorisés pour un chemin donné par comparaison de préfixe.
     */
    private Set<Role> trouverRolesAutorises(String path) {
        if (path == null) {
            return null;
        }
        for (Map.Entry<String, Set<Role>> entry : autorisationsParPrefixe.entrySet()) {
            String prefixe = entry.getKey();
            if (path.equals(prefixe) || path.startsWith(prefixe + "/")) {
                return entry.getValue();
            }
        }
        return null;
    }
}
