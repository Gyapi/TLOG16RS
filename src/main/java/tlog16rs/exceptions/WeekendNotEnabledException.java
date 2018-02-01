package tlog16rs.exceptions;

/**
 * Custom exception.
 * <br> Mostly thrown by the {@link WorkMonth WorkMonth} class
 * 
 * @author Gyapi
 */
public class WeekendNotEnabledException extends Exception{
    
    /**
     * 
     * @param message : {@link String String} custom error message
     */
    public WeekendNotEnabledException(String message) {
        super(message);
    }
    
}
