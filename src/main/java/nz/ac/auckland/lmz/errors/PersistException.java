package nz.ac.auckland.lmz.errors;

import nz.ac.auckland.lmz.errors.ExpectedErrorException;

import java.util.Map;

/**
 * Exceptions thrown during persistence checks or operations.
 */
public class PersistException extends ExpectedErrorException {

    /** @see Exception#Exception(String) */
    public PersistException(String message, Map context) {
        super(message, context);
    }

    /** @see Exception#Exception(String, Throwable) */
    public PersistException(String message, Map context, Throwable cause) {
        super(message, context, cause);
    }

}
