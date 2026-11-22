package ma.restaurant.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import ma.restaurant.dao.CommandeDao;
import ma.restaurant.exception.BusinessException;
import ma.restaurant.exception.CommandeNonModifiableException;
import ma.restaurant.exception.EntiteIntrouvableException;
import ma.restaurant.exception.PlatIndisponibleException;
import ma.restaurant.exception.TransitionInterditeException;
import ma.restaurant.model.Commande;
import ma.restaurant.model.DetailCommande;
import ma.restaurant.model.Plat;
import ma.restaurant.model.Role;
import ma.restaurant.model.StatutCommande;
import ma.restaurant.model.Utilisateur;
import ma.restaurant.util.JPAUtil;

public class CommandeServiceImpl implements CommandeService {

    private final CommandeDao commandeDao = new CommandeDao();

    // ===== Tableau des transitions (docs/contrats.md) : de -> vers -> roles autorises =====

    private static final Map<StatutCommande, Map<StatutCommande, Set<Role>>> TRANSITIONS =
            new EnumMap<>(StatutCommande.class);

    static {
        ajouterTransition(StatutCommande.EN_ATTENTE, StatutCommande.EN_PREPARATION, Role.CUISINIER);
        ajouterTransition(StatutCommande.EN_ATTENTE, StatutCommande.ANNULEE, Role.SERVEUR, Role.ADMIN);
        ajouterTransition(StatutCommande.EN_PREPARATION, StatutCommande.PRETE, Role.CUISINIER);
        ajouterTransition(StatutCommande.EN_PREPARATION, StatutCommande.ANNULEE, Role.ADMIN);
        ajouterTransition(StatutCommande.PRETE, StatutCommande.SERVIE, Role.SERVEUR);
        ajouterTransition(StatutCommande.SERVIE, StatutCommande.PAYEE, Role.SERVEUR);
        // PAYEE et ANNULEE sont finales : aucune entree, donc aucune sortie possible.
    }

    private static void ajouterTransition(StatutCommande de, StatutCommande vers, Role first, Role... others) {
        TRANSITIONS
                .computeIfAbsent(de, k -> new EnumMap<>(StatutCommande.class))
                .put(vers, EnumSet.of(first, others));
    }

    // ===== Creation et lecture =====

    @Override
    public Commande creer(Long idUtilisateur, int numeroTable) {
        if (idUtilisateur == null) {
            throw new BusinessException("Utilisateur obligatoire.");
        }
        if (numeroTable <= 0) {
            throw new BusinessException("Le numero de table doit etre superieur a 0.");
        }
        return JPAUtil.executeInTransaction(em -> {
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

    // ===== Etape 10 : regles metier =====

    @Override
    public Commande ajouterPlat(Long idCommande, Long idPlat, int quantite) {
        if (quantite <= 0) {
            throw new BusinessException("La quantite doit etre superieure a 0.");
        }
        return JPAUtil.executeInTransaction(em -> {
            Commande commande = chargerCommande(em, idCommande);
            verifierModifiable(commande);

            Plat plat = em.find(Plat.class, idPlat);
            if (plat == null) {
                throw new EntiteIntrouvableException("Plat introuvable : " + idPlat);
            }
            if (!plat.isDisponible()) {
                throw new PlatIndisponibleException(plat.getNom() + " n'est plus disponible.");
            }

            // Si le plat est deja dans la commande : on fusionne les quantites (UNIQUE idCommande+idPlat).
            DetailCommande existant = commande.getDetails().stream()
                    .filter(d -> d.getPlat().getIdPlat().equals(idPlat))
                    .findFirst()
                    .orElse(null);

            if (existant != null) {
                existant.mettreAJourQuantite(existant.getQuantite() + quantite); // prix deja fige : on n'y touche pas
            } else {
                DetailCommande detail = new DetailCommande();
                detail.setPlat(plat);
                detail.setQuantite(quantite);
                detail.setPrixUnitaire(plat.getPrix());                // PRIX FIGE : copie, une seule fois
                detail.setSousTotal(detail.calculerSousTotal());
                commande.ajouterDetail(detail);
            }

            commande.setTotal(commande.calculerTotal());               // TOTAL AUTOMATIQUE
            return commande;
        });
    }

    @Override
    public Commande retirerDetail(Long idCommande, Long idDetail) {
        return JPAUtil.executeInTransaction(em -> {
            Commande commande = chargerCommande(em, idCommande);
            verifierModifiable(commande);

            boolean existe = commande.getDetails().stream()
                    .anyMatch(d -> d.getIdDetail() != null && d.getIdDetail().equals(idDetail));
            if (!existe) {
                throw new EntiteIntrouvableException("Ligne introuvable : " + idDetail);
            }
            commande.retirerDetail(idDetail);                          // orphanRemoval supprime la ligne en base
            commande.setTotal(commande.calculerTotal());               // TOTAL AUTOMATIQUE
            return commande;
        });
    }

    @Override
    public Commande changerStatut(Long idCommande, StatutCommande nouveau, Role roleActeur) {
        if (nouveau == null) {
            throw new BusinessException("Nouveau statut obligatoire.");
        }
        return JPAUtil.executeInTransaction(em -> {
            Commande commande = chargerCommande(em, idCommande);
            verifierTransition(commande.getStatut(), nouveau, roleActeur);
            commande.setStatut(nouveau);
            return commande;
        });
    }

    @Override
    public Commande encaisser(Long idCommande) {
        // Seul le serveur encaisse (contrats.md) : SERVIE -> PAYEE.
        return changerStatut(idCommande, StatutCommande.PAYEE, Role.SERVEUR);
    }

    @Override
    public Commande annuler(Long idCommande, Role roleActeur) {
        return changerStatut(idCommande, StatutCommande.ANNULEE, roleActeur);
    }

    // ===== Outils internes =====

    private Commande chargerCommande(jakarta.persistence.EntityManager em, Long idCommande) {
        Commande commande = commandeDao.findById(em, idCommande);
        if (commande == null) {
            throw new EntiteIntrouvableException("Commande introuvable : " + idCommande);
        }
        return commande;
    }

    /** On ne modifie les lignes que tant que la commande est EN_ATTENTE. */
    static void verifierModifiable(Commande commande) {
        if (commande.getStatut() != StatutCommande.EN_ATTENTE) {
            throw new CommandeNonModifiableException(
                    "La commande n'est plus modifiable (statut " + commande.getStatut() + ").");
        }
    }

    /** Applique le tableau des transitions : le passage doit exister ET le role etre autorise. */
    static void verifierTransition(StatutCommande actuel, StatutCommande nouveau, Role role) {
        Map<StatutCommande, Set<Role>> cibles = TRANSITIONS.get(actuel);
        if (cibles == null || !cibles.containsKey(nouveau)) {
            throw new TransitionInterditeException("Transition interdite : " + actuel + " -> " + nouveau);
        }
        if (role == null || !cibles.get(nouveau).contains(role)) {
            throw new TransitionInterditeException(
                    "Le role " + role + " ne peut pas faire " + actuel + " -> " + nouveau);
        }
    }
}