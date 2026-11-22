package ma.restaurant.service;

import java.util.List;
import java.util.Optional;

import ma.restaurant.model.Utilisateur;

public interface UtilisateurService {

    /**
     * Nombre maximal de tentatives d'authentification avant verrouillage du compte.
     */
    int MAX_TENTATIVES_ECHEC = 5;

    /** Durée du verrouillage temporaire en secondes (30 secondes). */
    int DUREE_VERROUILLAGE_SECONDES = 30;

    /**
     * @throws ma.restaurant.exception.EntiteIntrouvableException si l'utilisateur
     *                                                            n'existe pas
     */
    Utilisateur findById(Long id);

    List<Utilisateur> findAll();

    /** Vide si le nom n'existe pas. */
    Optional<Utilisateur> findByNomUtilisateur(String nomUtilisateur);

    /**
     * Authentifie un utilisateur avec son identifiant et mot de passe en clair.
     * - Si le mot de passe est faux, incrémente tentativesEchec.
     * - À 5 échecs consécutifs, verrouille le compte (verrouille = true).
     * - Si le mot de passe est correct, réinitialise tentativesEchec à 0.
     *
     * @param nomUtilisateur nom de l'utilisateur
     * @param motDePasse     mot de passe en clair
     * @return l'utilisateur authentifié
     * @throws ma.restaurant.exception.CompteVerrouilleException si le compte est
     *                                                           verrouillé
     * @throws ma.restaurant.exception.CompteInactifException    si le compte est
     *                                                           inactif
     * @throws ma.restaurant.exception.AuthentificationException si les identifiants
     *                                                           sont invalides
     */
    Utilisateur authentifier(String nomUtilisateur, String motDePasse);

    /**
     * Déverrouille un compte et remet à zéro son compteur d'échecs.
     *
     * @param id identifiant de l'utilisateur
     * @throws ma.restaurant.exception.EntiteIntrouvableException si l'utilisateur
     *                                                            n'existe pas
     */
    void deverrouiller(Long id);
}
