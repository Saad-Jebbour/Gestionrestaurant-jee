package ma.restaurant.exception;

/**
 * Exception levée lorsqu'un utilisateur tente de se connecter sur un compte verrouillé
 * (après avoir atteint le nombre maximal d'échecs, ex. 5).
 * Hérite de {@link AuthentificationException}.
 */
public class CompteVerrouilleException extends AuthentificationException {

    public CompteVerrouilleException(String message) {
        super(message);
    }

    public CompteVerrouilleException(String message, Throwable cause) {
        super(message, cause);
    }
}
