package ma.restaurant.dao;

import java.math.BigDecimal;
import java.util.List;

import jakarta.persistence.EntityManager;
import ma.restaurant.model.Plat;

/** Acces à la table PLAT. Ne contient AUCUNE regle metier. */
public class PlatDao {

    public Plat findById(EntityManager em, Long id) {
        return em.find(Plat.class, id);
    }

    public List<Plat> findAll(EntityManager em) {
        return em.createQuery("SELECT p FROM Plat p ORDER BY p.nom", Plat.class).getResultList();
    }

    public List<Plat> findDisponibles(EntityManager em) {
        return em.createQuery("SELECT p FROM Plat p WHERE p.disponible = true ORDER BY p.nom", Plat.class)
                .getResultList();
    }

    /**
     * Recherche avec filtres dynamiques (mot-clé, disponibilité, prix) et pagination.
     */
    public List<Plat> rechercher(EntityManager em, String motCle, Boolean disponible,
                                 BigDecimal prixMin, BigDecimal prixMax,
                                 int page, int taille) {
        StringBuilder jpql = new StringBuilder("SELECT p FROM Plat p WHERE 1=1");

        if (motCle != null && !motCle.isBlank()) {
            jpql.append(" AND (LOWER(p.nom) LIKE :motCle OR LOWER(p.description) LIKE :motCle)");
        }
        if (disponible != null) {
            jpql.append(" AND p.disponible = :disponible");
        }
        if (prixMin != null) {
            jpql.append(" AND p.prix >= :prixMin");
        }
        if (prixMax != null) {
            jpql.append(" AND p.prix <= :prixMax");
        }
        jpql.append(" ORDER BY p.nom ASC");

        jakarta.persistence.TypedQuery<Plat> query = em.createQuery(jpql.toString(), Plat.class);

        if (motCle != null && !motCle.isBlank()) {
            query.setParameter("motCle", "%" + motCle.trim().toLowerCase() + "%");
        }
        if (disponible != null) {
            query.setParameter("disponible", disponible);
        }
        if (prixMin != null) {
            query.setParameter("prixMin", prixMin);
        }
        if (prixMax != null) {
            query.setParameter("prixMax", prixMax);
        }

        if (page > 0 && taille > 0) {
            query.setFirstResult((page - 1) * taille);
            query.setMaxResults(taille);
        }

        return query.getResultList();
    }

    /**
     * Compte le nombre total de plats correspondant aux filtres pour la pagination.
     */
    public long compter(EntityManager em, String motCle, Boolean disponible,
                       BigDecimal prixMin, BigDecimal prixMax) {
        StringBuilder jpql = new StringBuilder("SELECT COUNT(p) FROM Plat p WHERE 1=1");

        if (motCle != null && !motCle.isBlank()) {
            jpql.append(" AND (LOWER(p.nom) LIKE :motCle OR LOWER(p.description) LIKE :motCle)");
        }
        if (disponible != null) {
            jpql.append(" AND p.disponible = :disponible");
        }
        if (prixMin != null) {
            jpql.append(" AND p.prix >= :prixMin");
        }
        if (prixMax != null) {
            jpql.append(" AND p.prix <= :prixMax");
        }

        jakarta.persistence.TypedQuery<Long> query = em.createQuery(jpql.toString(), Long.class);

        if (motCle != null && !motCle.isBlank()) {
            query.setParameter("motCle", "%" + motCle.trim().toLowerCase() + "%");
        }
        if (disponible != null) {
            query.setParameter("disponible", disponible);
        }
        if (prixMin != null) {
            query.setParameter("prixMin", prixMin);
        }
        if (prixMax != null) {
            query.setParameter("prixMax", prixMax);
        }

        return query.getSingleResult();
    }

    public void save(EntityManager em, Plat plat) {
        em.persist(plat);
    }

    public Plat update(EntityManager em, Plat plat) {
        return em.merge(plat);
    }

    public void delete(EntityManager em, Plat plat) {
        em.remove(em.contains(plat) ? plat : em.merge(plat));
    }
}
