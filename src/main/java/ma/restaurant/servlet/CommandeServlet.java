package ma.restaurant.servlet;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ma.restaurant.exception.BusinessException;
import ma.restaurant.model.Commande;
import ma.restaurant.model.StatutCommande;
import ma.restaurant.service.CommandeService;
import ma.restaurant.service.CommandeServiceImpl;
import ma.restaurant.service.PlatService;
import ma.restaurant.service.PlatServiceImpl;
import ma.restaurant.util.SessionUser;

/** Controleur des commandes : /commandes/*. Il ne contient aucune regle metier (elles sont dans le service). */
@WebServlet("/commandes/*")
public class CommandeServlet extends HttpServlet {

    private static final String VUES = "/WEB-INF/views/commandes/";

    private CommandeService commandeService;
    private PlatService platService;

    @Override
    public void init() {
        commandeService = new CommandeServiceImpl();
        platService = new PlatServiceImpl();
    }

    // ===================== GET : afficher =====================

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        try {
            switch (chemin(req)) {
                case "/":
                    afficherListe(req, resp);
                    break;
                case "/nouvelle":
                    req.getRequestDispatcher(VUES + "nouvelle.jsp").forward(req, resp);
                    break;
                case "/detail":
                    afficherDetail(req, resp);
                    break;
                default:
                    resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (BusinessException e) {
            req.getSession().setAttribute("erreur", e.getMessage());
            resp.sendRedirect(req.getContextPath() + "/commandes");
        }
    }

    private void afficherListe(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        LocalDate date = dateParam(req.getParameter("date"));
        StatutCommande statut = statutParam(req.getParameter("statut"));
        Integer table = tableParam(req.getParameter("numeroTable"));

        req.setAttribute("commandes", commandeService.rechercher(date, statut, table));
        req.setAttribute("statuts", StatutCommande.values());
        req.getRequestDispatcher(VUES + "liste.jsp").forward(req, resp);
    }

    private void afficherDetail(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        Long id = idParam(req, "id");
        req.setAttribute("commande", commandeService.findById(id));
        req.setAttribute("plats", platService.findDisponibles());
        req.getRequestDispatcher(VUES + "detail.jsp").forward(req, resp);
    }

    // ===================== POST : agir, puis rediriger (PRG) =====================

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        req.setCharacterEncoding("UTF-8");
        String retour = req.getContextPath() + "/commandes";   // ou la page a afficher apres l'action
        try {
            SessionUser user = utilisateurConnecte(req);
            switch (chemin(req)) {
                case "/creer": {
                    retour = req.getContextPath() + "/commandes/nouvelle";   // si la creation echoue
                    Commande c = commandeService.creer(user.getId(), intParam(req, "numeroTable"));
                    succes(req, "Commande creee.");
                    retour = detailUrl(req, c.getIdCommande());
                    break;
                }
                case "/ajouter": {
                    Long id = idParam(req, "idCommande");
                    retour = detailUrl(req, id);
                    commandeService.ajouterPlat(id, idParam(req, "idPlat"), intParam(req, "quantite"));
                    succes(req, "Plat ajoute.");
                    break;
                }
                case "/retirer": {
                    Long id = idParam(req, "idCommande");
                    retour = detailUrl(req, id);
                    commandeService.retirerDetail(id, idParam(req, "idDetail"));
                    succes(req, "Ligne retiree.");
                    break;
                }
                case "/statut": {
                    Long id = idParam(req, "idCommande");
                    retour = detailUrl(req, id);
                    commandeService.changerStatut(id, statutParam(req.getParameter("nouveau")), user.getRole());
                    succes(req, "Statut mis a jour.");
                    break;
                }
                case "/encaisser": {
                    Long id = idParam(req, "idCommande");
                    retour = detailUrl(req, id);
                    commandeService.encaisser(id);
                    succes(req, "Commande encaissee.");
                    break;
                }
                case "/annuler": {
                    Long id = idParam(req, "idCommande");
                    retour = detailUrl(req, id);
                    commandeService.annuler(id, user.getRole());
                    succes(req, "Commande annulee.");
                    break;
                }
                default:
                    resp.sendError(HttpServletResponse.SC_NOT_FOUND);
                    return;
            }
        } catch (BusinessException e) {
            req.getSession().setAttribute("erreur", e.getMessage());   // affiche en rouge par header.jspf
        } catch (Exception e) {
            req.getSession().setAttribute("erreur", "Erreur lors de l'operation : " + e.getMessage());
        }
        resp.sendRedirect(retour);
    }

    // ===================== Outils =====================

    private static String chemin(HttpServletRequest req) {
        String p = req.getPathInfo();
        return (p == null || p.isEmpty()) ? "/" : p;
    }

    private static SessionUser utilisateurConnecte(HttpServletRequest req) {
        SessionUser user = (SessionUser) req.getSession().getAttribute("user");
        if (user == null) {
            throw new BusinessException("Vous devez etre connecte.");
        }
        return user;
    }

    private static String detailUrl(HttpServletRequest req, Long id) {
        return req.getContextPath() + "/commandes/detail?id=" + id;
    }

    private static void succes(HttpServletRequest req, String message) {
        req.getSession().setAttribute("succes", message);
    }

    private static Long idParam(HttpServletRequest req, String nom) {
        try {
            return Long.valueOf(req.getParameter(nom));
        } catch (NumberFormatException e) {
            throw new BusinessException("Parametre invalide : " + nom);
        }
    }

    private static int intParam(HttpServletRequest req, String nom) {
        try {
            return Integer.parseInt(req.getParameter(nom));
        } catch (NumberFormatException e) {
            throw new BusinessException("Valeur invalide : " + nom);
        }
    }

    private static LocalDate dateParam(String v) {
        if (v == null || v.isBlank()) return null;
        try {
            return LocalDate.parse(v);
        } catch (DateTimeParseException e) {
            throw new BusinessException("Date invalide.");
        }
    }

    private static StatutCommande statutParam(String v) {
        if (v == null || v.isBlank()) return null;
        try {
            return StatutCommande.valueOf(v);
        } catch (IllegalArgumentException e) {
            throw new BusinessException("Statut invalide.");
        }
    }

    private static Integer tableParam(String v) {
        if (v == null || v.isBlank()) return null;
        try {
            return Integer.valueOf(v);
        } catch (NumberFormatException e) {
            throw new BusinessException("Numero de table invalide.");
        }
    }
}
