package tlog16rs.exceptions;

/**
 * Custom exception.
 * <br> Mostly thrown by the {@link Task Task} class
 * @author Gyapi
 */
public class NotExpectedTimeOrderException extends Exception{

    /**
     * 
     * @param message : {@link String String} custom error message
     */
    public NotExpectedTimeOrderException(String message) {
        super(message);
    }
    
}
