package ma.restaurant.util;

import java.util.regex.Pattern;

import org.mindrot.jbcrypt.BCrypt;

import ma.restaurant.exception.MotDePasseInvalideException;

/**
 * Utilitaire de gestion des mots de passe :
 * - Validation de la politique de sécurité (8 caractères min, 1 majuscule, 1 chiffre, 1 caractère spécial).
 * - Hachage sécurisé avec jBCrypt (facteur de coût = 10).
 * - Vérification des empreintes lors de l'authentification.
 */
public final class PasswordUtil {

    /** Facteur de coût (log_rounds) pour l'algorithme BCrypt : 2^10 = 1024 itérations. */
    public static final int COUT_HACHAGE = 10;

    /** Longueur minimale imposée par la politique. */
    public static final int LONGUEUR_MINIMALE = 8;

    private static final Pattern MAJUSCULE_PATTERN = Pattern.compile(".*[A-Z].*");
    private static final Pattern CHIFFRE_PATTERN = Pattern.compile(".*[0-9].*");
    private static final Pattern SPECIAL_PATTERN = Pattern.compile(".*[^a-zA-Z0-9].*");

    private PasswordUtil() {
        // Classe utilitaire statique non instanciable
    }

    /**
     * Valide un mot de passe par rapport aux règles de la politique.
     *
     * @param motDePasse le mot de passe en clair à vérifier
     * @throws MotDePasseInvalideException si le mot de passe ne respecte pas l'un des critères
     */
    public static void validerPolitique(String motDePasse) {
        if (motDePasse == null || motDePasse.isBlank()) {
            throw new MotDePasseInvalideException("Le mot de passe ne peut pas être vide.");
        }
        if (motDePasse.length() < LONGUEUR_MINIMALE) {
            throw new MotDePasseInvalideException(
                    "Le mot de passe doit comporter au moins " + LONGUEUR_MINIMALE + " caractères.");
        }
        if (!MAJUSCULE_PATTERN.matcher(motDePasse).matches()) {
            throw new MotDePasseInvalideException(
                    "Le mot de passe doit comporter au moins une lettre majuscule.");
        }
        if (!CHIFFRE_PATTERN.matcher(motDePasse).matches()) {
            throw new MotDePasseInvalideException(
                    "Le mot de passe doit comporter au moins un chiffre.");
        }
        if (!SPECIAL_PATTERN.matcher(motDePasse).matches()) {
            throw new MotDePasseInvalideException(
                    "Le mot de passe doit comporter au moins un caractère spécial (ex: @, #, $, %, etc.).");
        }
    }

    /**
     * Indique si un mot de passe respecte la politique de sécurité.
     *
     * @param motDePasse le mot de passe en clair
     * @return true s'il est conforme, false sinon
     */
    public static boolean isConformePolitique(String motDePasse) {
        try {
            validerPolitique(motDePasse);
            return true;
        } catch (MotDePasseInvalideException e) {
            return false;
        }
    }

    /**
     * Valide le mot de passe selon la politique puis génère son hash BCrypt.
     *
     * @param motDePasseClair le mot de passe en clair
     * @return le hash BCrypt incluant le sel et le facteur de coût
     * @throws MotDePasseInvalideException si le mot de passe ne respecte pas la politique
     */
    public static String hacher(String motDePasseClair) {
        validerPolitique(motDePasseClair);
        return BCrypt.hashpw(motDePasseClair, BCrypt.gensalt(COUT_HACHAGE));
    }

    /**
     * Vérifie si un mot de passe en clair correspond à un hash BCrypt stocké.
     *
     * @param motDePasseClair le mot de passe saisi par l'utilisateur
     * @param motDePasseHache le hash stocké en base de données
     * @return true si le mot de passe correspond, false sinon
     */
    public static boolean verifier(String motDePasseClair, String motDePasseHache) {
        if (motDePasseClair == null || motDePasseHache == null || motDePasseHache.isBlank()) {
            return false;
        }
        try {
            return BCrypt.checkpw(motDePasseClair, motDePasseHache);
        } catch (IllegalArgumentException e) {
            // Dans le cas où le hash stocké n'est pas un hash BCrypt valide
            return false;
        }
    }
}
