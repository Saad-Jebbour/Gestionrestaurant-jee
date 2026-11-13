package ma.restaurant.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import ma.restaurant.dao.CommandeDao;
import ma.restaurant.exception.BusinessException;
import ma.restaurant.exception.EntiteIntrouvableException;
import ma.restaurant.model.Commande;
import ma.restaurant.model.Role;
import ma.restaurant.model.StatutCommande;
import ma.restaurant.model.Utilisateur;
import ma.restaurant.util.JPAUtil;

public class CommandeServiceImpl implements CommandeService {

    private static final String A_FAIRE_ETAPE_10 = "A implementer a l'etape 10";

    private final CommandeDao commandeDao = new CommandeDao();

    // ===== Etape 8 : creation et lecture =====

    @Override
    public Commande creer(Long idUtilisateur, int numeroTable) {
        if (idUtilisateur == null) {
            throw new BusinessException("Utilisateur obligatoire.");
        }
        if (numeroTable <= 0) {
            throw new BusinessException("Le numero de table doit etre superieur a 0.");
        }
        return JPAUtil.executeInTransaction(em -> {
            // On lit l'utilisateur dans la MEME transaction : plus simple et plus sur.
            Utilisateur utilisateur = em.find(Utilisateur.class, idUtilisateur);
            if (utilisateur == null) {
                throw new EntiteIntrouvableException("Utilisateur introuvable : " + idUtilisateur);
            }
            Commande commande = new Commande();
            commande.setUtilisateur(utilisateur);
            commande.setNumeroTable(numeroTable);
            commande.setDateCommande(LocalDateTime.now());
            commandeDao.save(em, commande);
            return commande;
        });
    }

    @Override
    public Commande findById(Long idCommande) {
        return JPAUtil.executeRead(em -> {
            Commande commande = commandeDao.findById(em, idCommande);
            if (commande == null) {
                throw new EntiteIntrouvableException("Commande introuvable : " + idCommande);
            }
            return commande;
        });
    }

    @Override
    public List<Commande> rechercher(LocalDate date, StatutCommande statut, Integer numeroTable) {
        return JPAUtil.executeRead(em -> commandeDao.rechercher(em, date, statut, numeroTable));
    }

    // ===== Etape 10 : regles metier (pas encore faites) =====

    @Override
    public Commande ajouterPlat(Long idCommande, Long idPlat, int quantite) {
        throw new UnsupportedOperationException(A_FAIRE_ETAPE_10);
    }

    @Override
    public Commande retirerDetail(Long idCommande, Long idDetail) {
        throw new UnsupportedOperationException(A_FAIRE_ETAPE_10);
    }

    @Override
    public Commande changerStatut(Long idCommande, StatutCommande nouveau, Role roleActeur) {
        throw new UnsupportedOperationException(A_FAIRE_ETAPE_10);
    }

    @Override
    public Commande encaisser(Long idCommande) {
        throw new UnsupportedOperationException(A_FAIRE_ETAPE_10);
    }

    @Override
    public Commande annuler(Long idCommande, Role roleActeur) {
        return changerStatut(idCommande, StatutCommande.ANNULEE, roleActeur);
    }
}