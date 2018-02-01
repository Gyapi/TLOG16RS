package tlog16rs.exceptions;

/**
 * Custom exception.
 * <br> Mostly thrown by the {@link Task Task} class
 * 
 * @author Gyapi
 */
public class NoTaskIdException extends Exception{
    
    /**
     * 
     * @param message : {@link String String} custom error message
     */
    public NoTaskIdException(String message) {
        super(message);
    }
    
}
