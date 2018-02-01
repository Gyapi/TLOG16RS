package tlog16rs.exceptions;

/**
 * Custom exception.
 * <br> Mostly thrown by the {@link TimeLogger TimeLogger} class
 * 
 * @author Gyapi
 */
public class NotNewMonthException extends Exception{
    
    /**
     * 
     * @param message : {@link String String} custom error message
     */
    public NotNewMonthException(String message) {
        super(message);
    }
    
}
