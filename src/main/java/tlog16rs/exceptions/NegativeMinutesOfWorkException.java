package tlog16rs.exceptions;

/**
 * Custom exception.
 * <br> Mostly thrown by the {@link WorkDay WorkDay} class
 * 
 * @author Gyapi
 */
public class NegativeMinutesOfWorkException extends Exception{

    /**
     * 
     * @param message : {@link String String} custom error message
     */
    public NegativeMinutesOfWorkException(String message) {
        super(message);
    }
}
