package ma.restaurant.dao;

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
