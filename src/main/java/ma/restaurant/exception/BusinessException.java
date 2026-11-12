package ma.restaurant.exception;

/**
 * Exception racine pour les erreurs et violations de règles métier.
 * Hérite de {@link RuntimeException} pour être non vérifiée (unchecked).
 */
public class BusinessException extends RuntimeException {

    public BusinessException(String message) {
        super(message);
    }

    public BusinessException(String message, Throwable cause) {
        super(message, cause);
    }
}

