package ma.restaurant.service;

import java.math.BigDecimal;
import java.util.List;

import jakarta.persistence.PersistenceException;
import ma.restaurant.dao.PlatDao;
import ma.restaurant.exception.BusinessException;
import ma.restaurant.exception.EntiteIntrouvableException;
import ma.restaurant.model.Plat;
import ma.restaurant.util.JPAUtil;

public class PlatServiceImpl implements PlatService {

    private final PlatDao platDao = new PlatDao();

    @Override
    public Plat findById(Long id) {
        return JPAUtil.executeRead(em -> {
            Plat plat = platDao.findById(em, id);
            if (plat == null) {
                throw new EntiteIntrouvableException("Plat introuvable : " + id);
            }
            return plat;
        });
    }

    @Override
    public List<Plat> findAll() {
        return JPAUtil.executeRead(platDao::findAll);
    }

    @Override
    public List<Plat> findDisponibles() {
        return JPAUtil.executeRead(platDao::findDisponibles);
    }

    @Override
    public Plat ajouterPlat(Plat plat) {
        valider(plat);
        return JPAUtil.executeInTransaction(em -> {
            plat.setIdPlat(null); // la base attribue l'identifiant
            platDao.save(em, plat);
            return plat;
        });
    }

    @Override
    public Plat modifierPlat(Plat plat) {
        valider(plat);
        if (plat.getIdPlat() == null) {
            throw new BusinessException("Impossible de modifier un plat sans identifiant.");
        }
        return JPAUtil.executeInTransaction(em -> {
            if (platDao.findById(em, plat.getIdPlat()) == null) {
                throw new EntiteIntrouvableException("Plat introuvable : " + plat.getIdPlat());
            }
            return platDao.update(em, plat);
        });
    }

    @Override
    public void supprimerPlat(Long id) {
        try {
            JPAUtil.executeInTransaction(em -> {
                Plat plat = platDao.findById(em, id);
                if (plat == null) {
                    throw new EntiteIntrouvableException("Plat introuvable : " + id);
                }
                platDao.delete(em, plat);
                return null;
            });
        } catch (PersistenceException e) {
            // La cle etrangere DETAIL_COMMANDE -> PLAT (ON DELETE RESTRICT) empeche la suppression.
            throw new BusinessException("Ce plat figure dans des commandes : "
                    + "rendez-le indisponible au lieu de le supprimer.");
        }
    }

    private void valider(Plat plat) {
        if (plat == null) {
            throw new BusinessException("Plat obligatoire.");
        }
        if (plat.getNom() == null || plat.getNom().isBlank()) {
            throw new BusinessException("Le nom du plat est obligatoire.");
        }
        if (plat.getPrix() == null || plat.getPrix().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("Le prix doit etre superieur a 0.");
        }
    }
}
