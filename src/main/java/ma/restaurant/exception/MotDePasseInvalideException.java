package ma.restaurant.exception;

/**
 * Exception levée lorsqu'un mot de passe ne respecte pas la politique de sécurité requise :
 * - Au moins 8 caractères
 * - Au moins une lettre majuscule
 * - Au moins un chiffre
 * - Au moins un caractère spécial
 */
public class MotDePasseInvalideException extends BusinessException {

    public MotDePasseInvalideException(String message) {
        super(message);
    }

    public MotDePasseInvalideException(String message, Throwable cause) {
        super(message, cause);
    }
}
