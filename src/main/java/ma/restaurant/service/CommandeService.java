package ma.restaurant.service;

import java.time.LocalDate;
import java.util.List;

import ma.restaurant.model.Commande;
import ma.restaurant.model.Role;
import ma.restaurant.model.StatutCommande;

public interface CommandeService {

    /**
     * Crée une commande EN_ATTENTE pour une table, au nom du serveur donné.
     * Lance EntiteIntrouvableException si l'utilisateur n'existe pas.
     */
    Commande creer(Long idUtilisateur, int numeroTable);

    /**
     * Ajoute un plat à la commande en copiant son prix actuel, puis recalcule le total.
     * Lance EntiteIntrouvableException (commande ou plat inconnu),
     * PlatIndisponibleException (plat non disponible)
     * ou CommandeNonModifiableException (commande non EN_ATTENTE).
     */
    Commande ajouterPlat(Long idCommande, Long idPlat, int quantite);

    /**
     * Retire une ligne de la commande et recalcule le total.
     * Lance EntiteIntrouvableException ou CommandeNonModifiableException.
     */
    Commande retirerDetail(Long idCommande, Long idDetail);

    /**
     * Fait passer la commande au statut demandé si la transition est autorisée pour ce rôle.
     * Lance EntiteIntrouvableException ou TransitionInterditeException.
     */
    Commande changerStatut(Long idCommande, StatutCommande nouveauStatut, Role role);

    /**
     * Encaisse une commande SERVIE et la passe à PAYEE.
     * Lance EntiteIntrouvableException ou TransitionInterditeException.
     */
    Commande encaisser(Long idCommande);

    /**
     * Annule la commande si son statut actuel le permet pour ce rôle.
     * Lance EntiteIntrouvableException ou TransitionInterditeException.
     */
    Commande annuler(Long idCommande, Role role);

    /**
     * Retourne la commande avec ses lignes.
     * Lance EntiteIntrouvableException si elle n'existe pas.
     */
    Commande findById(Long idCommande);

    /**
     * Recherche des commandes ; un critère à null n'est pas utilisé comme filtre.
     */
    List<Commande> rechercher(LocalDate date, StatutCommande statut, Integer numeroTable);
}
