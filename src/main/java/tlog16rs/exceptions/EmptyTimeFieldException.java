package tlog16rs.exceptions;

/**
 *
 * Custom exception.
 * <br> Mostly thrown by the {@link Task Task} class
 * 
 * @author Gyapi
 */
public class EmptyTimeFieldException extends Exception{

    /**
     * 
     * @param message : {@link String String} custom error message
     */
    public EmptyTimeFieldException(String message) {
        super(message);
    }
    
}
