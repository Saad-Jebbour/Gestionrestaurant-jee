package ma.restaurant.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import ma.restaurant.exception.TransitionInterditeException;
import ma.restaurant.model.Role;
import ma.restaurant.model.StatutCommande;

class CommandeTransitionTest {

    @Test
    @DisplayName("Cuisinier : EN_ATTENTE -> EN_PREPARATION autorise")
    void testCuisinierDemarrePreparation() {
        assertDoesNotThrow(() ->
                CommandeServiceImpl.verifierTransition(StatutCommande.EN_ATTENTE, StatutCommande.EN_PREPARATION, Role.CUISINIER));
    }

    @Test
    @DisplayName("Serveur ne peut pas demarrer une preparation")
    void testServeurNePeutPasDemarrerPreparation() {
        assertThrows(TransitionInterditeException.class, () ->
                CommandeServiceImpl.verifierTransition(StatutCommande.EN_ATTENTE, StatutCommande.EN_PREPARATION, Role.SERVEUR));
    }

    @Test
    @DisplayName("Cuisinier : EN_PREPARATION -> PRETE autorise")
    void testCuisinierTerminePreparation() {
        assertDoesNotThrow(() ->
                CommandeServiceImpl.verifierTransition(StatutCommande.EN_PREPARATION, StatutCommande.PRETE, Role.CUISINIER));
    }

    @Test
    @DisplayName("Serveur : PRETE -> SERVIE autorise")
    void testServeurMarqueServie() {
        assertDoesNotThrow(() ->
                CommandeServiceImpl.verifierTransition(StatutCommande.PRETE, StatutCommande.SERVIE, Role.SERVEUR));
    }

    @Test
    @DisplayName("Cuisinier ne peut pas marquer une commande comme servie")
    void testCuisinierNePeutPasServir() {
        assertThrows(TransitionInterditeException.class, () ->
                CommandeServiceImpl.verifierTransition(StatutCommande.PRETE, StatutCommande.SERVIE, Role.CUISINIER));
    }

    @Test
    @DisplayName("Serveur : SERVIE -> PAYEE autorise")
    void testServeurEncaisse() {
        assertDoesNotThrow(() ->
                CommandeServiceImpl.verifierTransition(StatutCommande.SERVIE, StatutCommande.PAYEE, Role.SERVEUR));
    }

    @Test
    @DisplayName("Admin : peut annuler EN_ATTENTE et EN_PREPARATION")
    void testAdminAnnulation() {
        assertDoesNotThrow(() ->
                CommandeServiceImpl.verifierTransition(StatutCommande.EN_ATTENTE, StatutCommande.ANNULEE, Role.ADMIN));
        assertDoesNotThrow(() ->
                CommandeServiceImpl.verifierTransition(StatutCommande.EN_PREPARATION, StatutCommande.ANNULEE, Role.ADMIN));
    }

    @Test
    @DisplayName("Serveur ne peut pas annuler une commande EN_PREPARATION")
    void testServeurNePeutPasAnnulerEnPreparation() {
        assertThrows(TransitionInterditeException.class, () ->
                CommandeServiceImpl.verifierTransition(StatutCommande.EN_PREPARATION, StatutCommande.ANNULEE, Role.SERVEUR));
    }

    @Test
    @DisplayName("Statuts finaux : aucune transition autorisee depuis PAYEE ou ANNULEE")
    void testStatutsFinaux() {
        assertThrows(TransitionInterditeException.class, () ->
                CommandeServiceImpl.verifierTransition(StatutCommande.PAYEE, StatutCommande.ANNULEE, Role.ADMIN));
        assertThrows(TransitionInterditeException.class, () ->
                CommandeServiceImpl.verifierTransition(StatutCommande.ANNULEE, StatutCommande.EN_ATTENTE, Role.ADMIN));
    }
}
