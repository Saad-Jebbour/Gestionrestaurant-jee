package ma.restaurant.service;

import java.util.List;
import java.util.Optional;

import ma.restaurant.model.Utilisateur;

public interface UtilisateurService {

    /** @throws ma.restaurant.exception.EntiteIntrouvableException si l'utilisateur n'existe pas */
    Utilisateur findById(Long id);

    List<Utilisateur> findAll();

    /** Vide si le nom n'existe pas (sert a la connexion, etape 9). */
    Optional<Utilisateur> findByNomUtilisateur(String nomUtilisateur);
}
