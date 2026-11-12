package ma.restaurant.service;

import java.util.List;

import ma.restaurant.model.Plat;

public interface PlatService {

    /** @throws ma.restaurant.exception.EntiteIntrouvableException si le plat n'existe pas */
    Plat findById(Long id);

    List<Plat> findAll();

    /** Uniquement les plats avec disponible = true. */
    List<Plat> findDisponibles();

    /** Ajoute un plat (nom obligatoire, prix superieur a 0). */
    Plat ajouterPlat(Plat plat);

    /** Modifie un plat existant. */
    Plat modifierPlat(Plat plat);

    /** Supprime un plat. Refuse s'il est deja utilise dans une commande. */
    void supprimerPlat(Long id);
}
