package tlog16rs.core.Entities;

import tlog16rs.core.Exceptions.NotNewMonthException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * The "main" class of the application. It collects and handles all functions regarding to the 
 * {@link WorkMonth WorkMonth} classes
 * <br>
 * <br> The getters and setters, which does not require special code, are generated through Lombok
 * <br> @see <a href="https://projectlombok.org/">https://projectlombok.org/</a>
 * @author Gyapi
 */
@lombok.Getter
@lombok.Setter
public class TimeLogger {
    
    //Fields
    private final List<WorkMonth> months = new ArrayList<>();
    
    //Methods
    /**
     * 
     * Adds a new {@link WorkMonth WorkMonth} to the list {@link #months months}
     * after checking, trough a {@link #isNewMonth(Entities.WorkMonth) submethod} if it already exsits
     * @param m : {@link WorkMonth WorkMonth}
     * @throws tlog16rs.core.Exceptions.NotNewMonthException 
     */
    public void addMonth(WorkMonth m) 
            throws NotNewMonthException{
        if (!isNewMonth(m)){
            throw new NotNewMonthException("This month already exists. Please try again.");
        }
        else{
            months.add(m);
        }
    }

    /**
     * 
     * Decides, if the given {@link WorkMonth WorkMonth} already exists in the list {@link #months months} or not
     * @param m : {@link WorkMonth WorkMonth}
     * @return {@link Boolean Boolean}
     */
    public boolean isNewMonth(WorkMonth  m){
        
        List<WorkMonth> filteredMonth;
        
        if (months.isEmpty()){
            return true;
        }
        else {
            filteredMonth = months.stream()
                    .filter(month -> month.getDate().equals(m.getDate()))
                    .collect(Collectors.toList()); 

            return filteredMonth.isEmpty();
        }
    }
}
