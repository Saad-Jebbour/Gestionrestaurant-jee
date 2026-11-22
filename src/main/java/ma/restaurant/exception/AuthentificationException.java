package ma.restaurant.exception;

/**
 * Exception levée en cas d'échec d'authentification (identifiant ou mot de passe invalide).
 * Hérite de {@link BusinessException}.
 */
public class AuthentificationException extends BusinessException {

    public AuthentificationException(String message) {
        super(message);
    }

    public AuthentificationException(String message, Throwable cause) {
        super(message, cause);
    }
}
