package ma.restaurant.service;

import java.util.List;
import java.util.Optional;

import ma.restaurant.dao.UtilisateurDao;
import ma.restaurant.exception.EntiteIntrouvableException;
import ma.restaurant.model.Utilisateur;
import ma.restaurant.util.JPAUtil;

public class UtilisateurServiceImpl implements UtilisateurService {

    private final UtilisateurDao utilisateurDao = new UtilisateurDao();

    @Override
    public Utilisateur findById(Long id) {
        return JPAUtil.executeRead(em -> {
            Utilisateur utilisateur = utilisateurDao.findById(em, id);
            if (utilisateur == null) {
                throw new EntiteIntrouvableException("Utilisateur introuvable : " + id);
            }
            return utilisateur;
        });
    }

    @Override
    public List<Utilisateur> findAll() {
        return JPAUtil.executeRead(utilisateurDao::findAll);
    }

    @Override
    public Optional<Utilisateur> findByNomUtilisateur(String nomUtilisateur) {
        return JPAUtil.executeRead(em -> utilisateurDao.findByNomUtilisateur(em, nomUtilisateur));
    }
}
