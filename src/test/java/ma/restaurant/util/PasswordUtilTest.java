package ma.restaurant.util;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import ma.restaurant.exception.MotDePasseInvalideException;

class PasswordUtilTest {

    @Test
    @DisplayName("Mot de passe valide respectant tous les critères")
    void testMotDePasseValide() {
        assertDoesNotThrow(() -> PasswordUtil.validerPolitique("Admin@2026"));
        assertTrue(PasswordUtil.isConformePolitique("Admin@2026"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "   "})
    @DisplayName("Mot de passe vide ou blanc doit échouer")
    void testMotDePasseVide(String mdp) {
        assertThrows(MotDePasseInvalideException.class, () -> PasswordUtil.validerPolitique(mdp));
        assertFalse(PasswordUtil.isConformePolitique(mdp));
    }

    @Test
    @DisplayName("Mot de passe null doit échouer")
    void testMotDePasseNull() {
        assertThrows(MotDePasseInvalideException.class, () -> PasswordUtil.validerPolitique(null));
        assertFalse(PasswordUtil.isConformePolitique(null));
    }

    @Test
    @DisplayName("Mot de passe trop court (< 8 caractères)")
    void testMotDePasseTropCourt() {
        assertThrows(MotDePasseInvalideException.class, () -> PasswordUtil.validerPolitique("Ab1@xyz"));
        assertFalse(PasswordUtil.isConformePolitique("Ab1@xyz"));
    }

    @Test
    @DisplayName("Mot de passe sans majuscule")
    void testMotDePasseSansMajuscule() {
        assertThrows(MotDePasseInvalideException.class, () -> PasswordUtil.validerPolitique("admin@2026"));
        assertFalse(PasswordUtil.isConformePolitique("admin@2026"));
    }

    @Test
    @DisplayName("Mot de passe sans chiffre")
    void testMotDePasseSansChiffre() {
        assertThrows(MotDePasseInvalideException.class, () -> PasswordUtil.validerPolitique("Admin@Secret"));
        assertFalse(PasswordUtil.isConformePolitique("Admin@Secret"));
    }

    @Test
    @DisplayName("Mot de passe sans caractère spécial")
    void testMotDePasseSansSpecial() {
        assertThrows(MotDePasseInvalideException.class, () -> PasswordUtil.validerPolitique("Admin2026"));
        assertFalse(PasswordUtil.isConformePolitique("Admin2026"));
    }

    @Test
    @DisplayName("Hachage et vérification BCrypt réussie")
    void testHachageEtVerification() {
        String clair = "Serveur@2026";
        String hash = PasswordUtil.hacher(clair);

        assertTrue(hash.startsWith("$2a$") || hash.startsWith("$2b$") || hash.startsWith("$2y$"));
        assertTrue(PasswordUtil.verifier(clair, hash));
        assertFalse(PasswordUtil.verifier("MauvaisMotDePasse@1", hash));
        assertFalse(PasswordUtil.verifier(null, hash));
        assertFalse(PasswordUtil.verifier(clair, null));
        assertFalse(PasswordUtil.verifier(clair, "hash_invalide"));
    }
}
