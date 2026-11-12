package ma.restaurant.dao;

import java.util.List;
import java.util.Optional;

import jakarta.persistence.EntityManager;
import ma.restaurant.model.Utilisateur;

/** Accès à la table UTILISATEUR. Ne contient AUCUNE regle metier. */
public class UtilisateurDao {

    public Utilisateur findById(EntityManager em, Long id) {
        return em.find(Utilisateur.class, id);
    }

    public List<Utilisateur> findAll(EntityManager em) {
        return em.createQuery("SELECT u FROM Utilisateur u ORDER BY u.nomUtilisateur", Utilisateur.class)
                .getResultList();
    }

    public Optional<Utilisateur> findByNomUtilisateur(EntityManager em, String nomUtilisateur) {
        return em.createQuery("SELECT u FROM Utilisateur u WHERE u.nomUtilisateur = :nom", Utilisateur.class)
                .setParameter("nom", nomUtilisateur)
                .getResultStream()
                .findFirst();
    }

    public void save(EntityManager em, Utilisateur utilisateur) {
        em.persist(utilisateur);
    }

    public Utilisateur update(EntityManager em, Utilisateur utilisateur) {
        return em.merge(utilisateur);
    }
}
