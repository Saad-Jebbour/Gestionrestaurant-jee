package ma.restaurant.servlet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

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
import ma.restaurant.util.SessionUser;

/** Ecran de la cuisine : commandes a preparer (EN_ATTENTE et EN_PREPARATION). */
@WebServlet("/cuisine")
public class CuisineServlet extends HttpServlet {

    private CommandeService commandeService;

    @Override
    public void init() {
        commandeService = new CommandeServiceImpl();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        List<Commande> aPreparer = new ArrayList<>();
        try {
            for (StatutCommande statut : List.of(StatutCommande.EN_ATTENTE, StatutCommande.EN_PREPARATION)) {
                for (Commande c : commandeService.rechercher(null, statut, null)) {
                    // findById charge les lignes et leurs plats (rechercher ne le fait pas)
                    aPreparer.add(commandeService.findById(c.getIdCommande()));
                }
            }
        } catch (BusinessException e) {
            req.getSession().setAttribute("erreur", e.getMessage());
        }
        aPreparer.sort(Comparator.comparing(Commande::getDateCommande));   // la plus ancienne d'abord
        req.setAttribute("commandes", aPreparer);
        req.getRequestDispatcher("/WEB-INF/views/cuisine.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            SessionUser user = (SessionUser) req.getSession().getAttribute("user");
            if (user == null) {
                throw new BusinessException("Vous devez etre connecte.");
            }
            Long id = Long.valueOf(req.getParameter("idCommande"));
            StatutCommande nouveau = StatutCommande.valueOf(req.getParameter("nouveau"));
            commandeService.changerStatut(id, nouveau, user.getRole());   // le service verifie la transition et le role
            req.getSession().setAttribute("succes", "Statut mis a jour.");
        } catch (BusinessException e) {
            req.getSession().setAttribute("erreur", e.getMessage());
        } catch (IllegalArgumentException e) {
            req.getSession().setAttribute("erreur", "Parametres invalides.");
        }
        resp.sendRedirect(req.getContextPath() + "/cuisine");
    }
}
