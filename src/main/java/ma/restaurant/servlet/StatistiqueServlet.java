package ma.restaurant.servlet;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ma.restaurant.service.StatistiqueService;

@WebServlet("/statistiques")
public class StatistiqueServlet extends HttpServlet {

    private StatistiqueService service;

    @Override
    public void init() {
        service = new StatistiqueService();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setAttribute("caParJour", service.chiffreAffairesParJour(7));
        req.setAttribute("topPlats", service.topPlats(5));
        req.getRequestDispatcher("/WEB-INF/views/statistiques.jsp").forward(req, resp);
    }
}
