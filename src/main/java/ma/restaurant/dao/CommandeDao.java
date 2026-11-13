package ma.restaurant.dao;
import java.time.LocalDate;
import java.util.List;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import ma.restaurant.model.Commande;
import ma.restaurant.model.StatutCommande;

/** Acces aux tables COMMANDE et DETAIL_COMMANDE. Ne contient AUCUNE regle metier. */
public class CommandeDao {

    public void save(EntityManager em, Commande commande) {
        em.persist(commande);
    }

    public Commande update(EntityManager em, Commande commande) {
        return em.merge(commande);
    }

    /** Charge la commande AVEC ses lignes et leurs plats (pas d'erreur de chargement dans les JSP). */
    public Commande findById(EntityManager em, Long id) {
        return em.createQuery(
                        "SELECT DISTINCT c FROM Commande c "
                                + "LEFT JOIN FETCH c.details d "
                                + "LEFT JOIN FETCH d.plat "
                                + "WHERE c.idCommande = :id", Commande.class)
                .setParameter("id", id)
                .getResultStream()
                .findFirst()
                .orElse(null);
    }

    /** Un filtre a null signifie "pas de filtre". */
    public List<Commande> rechercher(EntityManager em, LocalDate date, StatutCommande statut, Integer numeroTable) {
        StringBuilder jpql = new StringBuilder(
                "SELECT c FROM Commande c JOIN FETCH c.utilisateur WHERE c.idCommande IS NOT NULL");
        if (date != null) {
            jpql.append(" AND c.dateCommande >= :debut AND c.dateCommande < :fin");
        }
        if (statut != null) {
            jpql.append(" AND c.statut = :statut");
        }
        if (numeroTable != null) {
            jpql.append(" AND c.numeroTable = :numeroTable");
        }
        jpql.append(" ORDER BY c.dateCommande DESC");

        TypedQuery<Commande> query = em.createQuery(jpql.toString(), Commande.class);
        if (date != null) {
            query.setParameter("debut", date.atStartOfDay());
            query.setParameter("fin", date.plusDays(1).atStartOfDay());
        }
        if (statut != null) {
            query.setParameter("statut", statut);
        }
        if (numeroTable != null) {
            query.setParameter("numeroTable", numeroTable);
        }
        return query.getResultList();
    }
}