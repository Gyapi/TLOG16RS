package tlog16rs.entities;

import tlog16rs.exceptions.NotNewMonthException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;

/**
 *
 * The "main" class of the application. 
 * It handles the collection of {@link WorkMonth WorkMonth} classes
 * <br>
 * <br> The getters and setters, which does not require special code, are generated through Lombok
 * <br> @see <a href="https://projectlombok.org/">https://projectlombok.org/</a>
 * @author Gyapi
 */
@Entity
@lombok.Getter
@lombok.Setter
public class TimeLogger {
    
    @Id    
    @GeneratedValue
    private int id;
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private final List<WorkMonth> months = new ArrayList<>();
    @Column (name = "name")
    private String name;

    public TimeLogger(String name) {
        this.name = name;
    }
    
    /**
     * 
     * Adds a new {@link WorkMonth WorkMonth} to the list {@link #months months}
     * after checking, trough the  {@link #isNewMonth(Entities.WorkMonth) isNewMonth} method if it already exsits.
     * 
     * @param m : {@link WorkMonth WorkMonth} object we want to add
     * 
     * @throws tlog16rs.exceptions.NotNewMonthException 
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
     * Decides, if the given {@link WorkMonth WorkMonth} already exists in the list {@link #months months} or not.
     * 
     * @param m : {@link WorkMonth WorkMonth} object we inspect
     * 
     * @return true if it is a new {@link WorkMonth month} in the list
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
