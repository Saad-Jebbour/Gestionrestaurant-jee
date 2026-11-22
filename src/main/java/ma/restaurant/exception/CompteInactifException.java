package ma.restaurant.exception;

/**
 * Exception levée lorsqu'un utilisateur tente de se connecter alors que son compte est désactivé.
 * Hérite de {@link AuthentificationException}.
 */
public class CompteInactifException extends AuthentificationException {

    public CompteInactifException(String message) {
        super(message);
    }

    public CompteInactifException(String message, Throwable cause) {
        super(message, cause);
    }
}
