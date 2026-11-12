package ma.restaurant.exception;

public class CommandeNonModifiableException extends BusinessException {
    public CommandeNonModifiableException(String message) { super(message); }
}
