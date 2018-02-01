package tlog16rs.exceptions;

/**
 * Custom exception.
 * <br> Mostly thrown by the {@link WorkDay WorkDay} class
 * 
 * @author Gyapi
 */
public class NotSeparatedTimesException extends Exception{
    
    /**
     * 
     * @param message : {@link String String} custom error message
     */
    public NotSeparatedTimesException(String message) {
        super(message);
    }
    
}
