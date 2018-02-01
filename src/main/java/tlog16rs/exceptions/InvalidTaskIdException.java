package tlog16rs.exceptions;

/**
 * 
 * Custom exception.
 * <br> Mostly thrown by the {@link Task Task} class
 * 
 * @author Gyapi
 */
public class InvalidTaskIdException extends Exception{

    /**
     * 
     * 
     * @param message : {@link String String} custom error message
     */
    public InvalidTaskIdException(String message) {
        super(message);
    }
    
}
