package ma.restaurant.servlet;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ma.restaurant.exception.BusinessException;
import ma.restaurant.model.Plat;
import ma.restaurant.service.PlatService;
import ma.restaurant.service.PlatServiceImpl;

/**
 * Contrôleur CRUD des plats : /plats/*
 * - GET  /plats/          : Liste de tous les plats
 * - GET  /plats/nouveau   : Formulaire d'ajout d'un plat
 * - GET  /plats/modifier  : Formulaire de modification d'un plat existant
 * - POST /plats/enregistrer : Création ou mise à jour
 * - POST /plats/supprimer   : Suppression d'un plat
 * - POST /plats/basculer-disponibilite : Active/Désactive la disponibilité d'un plat
 */
@WebServlet("/plats/*")
public class PlatsServlet extends HttpServlet {

    private static final String VUES = "/WEB-INF/views/plats/";

    private PlatService platService;

    @Override
    public void init() {
        platService = new PlatServiceImpl();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        try {
            switch (chemin(req)) {
                case "/":
                    afficherListe(req, resp);
                    break;
                case "/nouveau":
                    afficherFormulaireCreation(req, resp);
                    break;
                case "/modifier":
                    afficherFormulaireModification(req, resp);
                    break;
                default:
                    resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (BusinessException e) {
            erreur(req, e.getMessage());
            resp.sendRedirect(req.getContextPath() + "/plats");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        try {
            switch (chemin(req)) {
                case "/enregistrer":
                    enregistrer(req, resp);
                    break;
                case "/supprimer":
                    supprimer(req, resp);
                    break;
                case "/basculer-disponibilite":
                    basculerDisponibilite(req, resp);
                    break;
                default:
                    resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (BusinessException e) {
            erreur(req, e.getMessage());
            resp.sendRedirect(req.getContextPath() + "/plats");
        } catch (Exception e) {
            erreur(req, "Une erreur inattendue est survenue : " + e.getMessage());
            resp.sendRedirect(req.getContextPath() + "/plats");
        }
    }

    private void afficherListe(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        List<Plat> plats = platService.findAll();
        req.setAttribute("plats", plats);
        req.getRequestDispatcher(VUES + "liste.jsp").forward(req, resp);
    }

    private void afficherFormulaireCreation(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        Plat plat = new Plat();
        plat.setDisponible(true);
        req.setAttribute("plat", plat);
        req.setAttribute("edition", false);
        req.getRequestDispatcher(VUES + "form.jsp").forward(req, resp);
    }

    private void afficherFormulaireModification(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        Long id = Long.valueOf(req.getParameter("id"));
        Plat plat = platService.findById(id);
        req.setAttribute("plat", plat);
        req.setAttribute("edition", true);
        req.getRequestDispatcher(VUES + "form.jsp").forward(req, resp);
    }

    private void enregistrer(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String idStr = req.getParameter("idPlat");
        String nom = req.getParameter("nom");
        String description = req.getParameter("description");
        String prixStr = req.getParameter("prix");
        boolean disponible = "true".equalsIgnoreCase(req.getParameter("disponible"))
                || "on".equalsIgnoreCase(req.getParameter("disponible"));

        if (nom == null || nom.isBlank()) {
            throw new BusinessException("Le nom du plat est obligatoire.");
        }
        if (prixStr == null || prixStr.isBlank()) {
            throw new BusinessException("Le prix du plat est obligatoire.");
        }

        BigDecimal prix;
        try {
            prix = new BigDecimal(prixStr.trim().replace(",", "."));
        } catch (NumberFormatException e) {
            throw new BusinessException("Le format du prix est invalide.");
        }

        if (idStr == null || idStr.isBlank()) {
            // Création d'un nouveau plat
            Plat plat = new Plat(null, nom.trim(), description != null ? description.trim() : null, prix, disponible);
            platService.ajouterPlat(plat);
            succes(req, "Le plat « " + plat.getNom() + " » a été ajouté avec succès.");
        } else {
            // Modification d'un plat existant
            Long id = Long.valueOf(idStr.trim());
            Plat plat = platService.findById(id);
            plat.setNom(nom.trim());
            plat.setDescription(description != null ? description.trim() : null);
            plat.setPrix(prix);
            plat.setDisponible(disponible);
            platService.modifierPlat(plat);
            succes(req, "Le plat « " + plat.getNom() + " » a été modifié avec succès.");
        }

        resp.sendRedirect(req.getContextPath() + "/plats");
    }

    private void supprimer(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Long id = Long.valueOf(req.getParameter("id"));
        platService.supprimerPlat(id);
        succes(req, "Le plat a été supprimé avec succès.");
        resp.sendRedirect(req.getContextPath() + "/plats");
    }

    private void basculerDisponibilite(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Long id = Long.valueOf(req.getParameter("id"));
        Plat plat = platService.findById(id);
        plat.setDisponible(!plat.isDisponible());
        platService.modifierPlat(plat);
        succes(req, "La disponibilité du plat « " + plat.getNom() + " » a été modifiée.");
        resp.sendRedirect(req.getContextPath() + "/plats");
    }

    private static String chemin(HttpServletRequest req) {
        String p = req.getPathInfo();
        return (p == null || p.isEmpty()) ? "/" : p;
    }

    private static void succes(HttpServletRequest req, String message) {
        req.getSession().setAttribute("succes", message);
    }

    private static void erreur(HttpServletRequest req, String message) {
        req.getSession().setAttribute("erreur", message);
    }
}
