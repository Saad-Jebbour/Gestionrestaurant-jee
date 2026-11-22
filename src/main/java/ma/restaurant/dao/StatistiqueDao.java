package ma.restaurant.dao;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import jakarta.persistence.EntityManager;
import ma.restaurant.model.StatutCommande;

public class StatistiqueDao {

    /** Chiffre d'affaires par jour : uniquement les commandes PAYEE. */
    public List<Object[]> chiffreAffairesParJour(EntityManager em, LocalDate depuis) {
        return em.createQuery(
                        "SELECT cast(c.dateCommande as LocalDate), SUM(c.total) FROM Commande c "
                                + "WHERE c.statut = :s AND c.dateCommande >= :debut "
                                + "GROUP BY cast(c.dateCommande as LocalDate) "
                                + "ORDER BY cast(c.dateCommande as LocalDate) DESC", Object[].class)
                .setParameter("s", StatutCommande.PAYEE)
                .setParameter("debut", depuis.atStartOfDay())
                .getResultList();
    }

    /** Plats les plus commandes : somme des quantites, commandes annulees exclues. */
    public List<Object[]> topPlats(EntityManager em, int limite) {
        return em.createQuery(
                        "SELECT p.nom, SUM(d.quantite) FROM DetailCommande d "
                                + "JOIN d.plat p JOIN d.commande c "
                                + "WHERE c.statut <> :annulee "
                                + "GROUP BY p.nom ORDER BY SUM(d.quantite) DESC", Object[].class)
                .setParameter("annulee", StatutCommande.ANNULEE)
                .setMaxResults(limite)
                .getResultList();
    }
}
