package ma.restaurant.servlet;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import ma.restaurant.model.Role;
import ma.restaurant.util.SessionUser;

/**
 * Contrôleur de la racine (/) et page d'accueil (/accueil) :
 * Redirige l'utilisateur connecté vers son espace de travail selon son rôle :
 * - SERVEUR   -> /commandes
 * - CUISINIER -> /cuisine
 * - ADMIN     -> /plats
 */
@WebServlet(name = "AccueilServlet", urlPatterns = {"", "/accueil"})
public class AccueilServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        HttpSession session = req.getSession(false);
        SessionUser user = (session != null) ? (SessionUser) session.getAttribute("user") : null;

        if (user == null || user.getRole() == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        redirigerSelonRole(req, resp, user.getRole());
    }

    /**
     * Redirige l'utilisateur vers son écran d'accueil dédié selon son rôle.
     */
    public static void redirigerSelonRole(HttpServletRequest req, HttpServletResponse resp, Role role) throws IOException {
        String ctx = req.getContextPath();
        if (role == null) {
            resp.sendRedirect(ctx + "/login");
            return;
        }

        switch (role) {
            case CUISINIER:
                resp.sendRedirect(ctx + "/cuisine");
                break;
            case ADMIN:
                resp.sendRedirect(ctx + "/plats");
                break;
            case SERVEUR:
            default:
                resp.sendRedirect(ctx + "/commandes");
                break;
        }
    }
}
