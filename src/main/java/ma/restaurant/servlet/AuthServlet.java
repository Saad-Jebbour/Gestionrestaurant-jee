package ma.restaurant.servlet;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import ma.restaurant.exception.AuthentificationException;
import ma.restaurant.model.Role;
import ma.restaurant.model.Utilisateur;
import ma.restaurant.service.UtilisateurService;
import ma.restaurant.service.UtilisateurServiceImpl;
import ma.restaurant.util.SessionUser;

/**
 * Contrôleur d'authentification :
 * - /login et /Login : Affichage du formulaire (GET) et traitement de la connexion (POST)
 * - /logout et /Logout : Déconnexion et invalidation de la session
 */
@WebServlet(name = "AuthServlet", urlPatterns = {"/login", "/Login", "/logout", "/Logout"})
public class AuthServlet extends HttpServlet {

    private static final String VUE_LOGIN = "/WEB-INF/views/login.jsp";

    private UtilisateurService utilisateurService;

    @Override
    public void init() {
        utilisateurService = new UtilisateurServiceImpl();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String path = req.getServletPath();

        // Gestion de la déconnexion
        if ("/logout".equalsIgnoreCase(path)) {
            traiterDeconnexion(req, resp);
            return;
        }

        // Si l'utilisateur est déjà connecté, redirection vers son espace
        HttpSession session = req.getSession(false);
        if (session != null && session.getAttribute("user") != null) {
            SessionUser user = (SessionUser) session.getAttribute("user");
            redirigerSelonRole(req, resp, user.getRole());
            return;
        }

        req.getRequestDispatcher(VUE_LOGIN).forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String path = req.getServletPath();

        if ("/logout".equalsIgnoreCase(path)) {
            traiterDeconnexion(req, resp);
            return;
        }

        String nomUtilisateur = req.getParameter("nomUtilisateur");
        if (nomUtilisateur == null) {
            nomUtilisateur = req.getParameter("login");
        }

        String motDePasse = req.getParameter("motDePasse");
        if (motDePasse == null) {
            motDePasse = req.getParameter("password");
        }

        try {
            // Validation et authentification par le service métier
            Utilisateur utilisateur = utilisateurService.authentifier(
                    nomUtilisateur != null ? nomUtilisateur.trim() : null,
                    motDePasse
            );

            // Renouvellement de session (protection contre la fixation de session)
            HttpSession ancienneSession = req.getSession(false);
            if (ancienneSession != null) {
                ancienneSession.invalidate();
            }
            HttpSession session = req.getSession(true);

            // Stockage de l'utilisateur connecté en session
            SessionUser sessionUser = new SessionUser(
                    utilisateur.getIdUtilisateur(),
                    utilisateur.getNomUtilisateur(),
                    utilisateur.getRole()
            );
            session.setAttribute("user", sessionUser);

            // Redirection selon le rôle
            redirigerSelonRole(req, resp, utilisateur.getRole());

        } catch (AuthentificationException e) {
            req.setAttribute("erreur", e.getMessage());
            req.setAttribute("nomUtilisateur", nomUtilisateur);
            req.getRequestDispatcher(VUE_LOGIN).forward(req, resp);
        } catch (Exception e) {
            req.setAttribute("erreur", "Une erreur inattendue est survenue lors de la connexion.");
            req.setAttribute("nomUtilisateur", nomUtilisateur);
            req.getRequestDispatcher(VUE_LOGIN).forward(req, resp);
        }
    }

    private void traiterDeconnexion(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        resp.sendRedirect(req.getContextPath() + "/login");
    }

    private void redirigerSelonRole(HttpServletRequest req, HttpServletResponse resp, Role role) throws IOException {
        String ctx = req.getContextPath();
        if (role == null) {
            resp.sendRedirect(ctx + "/commandes");
            return;
        }

        switch (role) {
            case CUISINIER:
                resp.sendRedirect(ctx + "/cuisine");
                break;
            case ADMIN:
                resp.sendRedirect(ctx + "/statistiques");
                break;
            case SERVEUR:
            default:
                resp.sendRedirect(ctx + "/commandes");
                break;
        }
    }
}
