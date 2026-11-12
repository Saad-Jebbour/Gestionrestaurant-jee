package ma.restaurant.exception;

/**
 * Exception levée lorsqu'une entité recherchée par son identifiant n'existe pas en base.
 * Hérite de {@link BusinessException}.
 */
public class EntiteIntrouvableException extends BusinessException {

    public EntiteIntrouvableException(String message) {
        super(message);
    }

    public EntiteIntrouvableException(String message, Throwable cause) {
        super(message, cause);
    }
}

