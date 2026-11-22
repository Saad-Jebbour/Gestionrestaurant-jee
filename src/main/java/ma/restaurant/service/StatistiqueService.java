package ma.restaurant.service;

import java.time.LocalDate;
import java.util.List;

import ma.restaurant.dao.StatistiqueDao;
import ma.restaurant.util.JPAUtil;

public class StatistiqueService {

    private final StatistiqueDao dao = new StatistiqueDao();

    public List<Object[]> chiffreAffairesParJour(int nbJours) {
        LocalDate depuis = LocalDate.now().minusDays(nbJours - 1L);
        return JPAUtil.executeRead(em -> dao.chiffreAffairesParJour(em, depuis));
    }

    public List<Object[]> topPlats(int limite) {
        return JPAUtil.executeRead(em -> dao.topPlats(em, limite));
    }
}