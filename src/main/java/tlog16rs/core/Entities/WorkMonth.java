package tlog16rs.core.Entities;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import tlog16rs.core.Exceptions.NotNewDateException;
import tlog16rs.core.Exceptions.NotTheSameMonthException;
import tlog16rs.core.Exceptions.WeekendNotEnabledException;
import tlog16rs.core.Util.Util;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import tlog16rs.core.Serializers.WorkMonthSerializer;

/**
 *
 * The WorkMonth class handles the collection of {@link WorkDay WorkDay}s
 * <br>
 * <br>{@link #days days} : {@link ArrayList ArrayList} that collects the {@link WorkDay WorkDay}s
 * <br>{@link #date date} : {@link YearMonth YearMonth} field, it contains the identity of the month (year, month)
 * <br>{@link #sumPerMonth sumPerMonth} : {@link Long Long}, the sum of the workhours in the month (in minutes)
 * <br>{@link #requiredMinPerMonth requiredMinPerMonth} : {@link Long Long}, the required workhours of the month (in minutes)
 * <br>
 * <br> The getters and setters, which does not require special code, are generated through Lombok
 * <br> @see <a href="https://projectlombok.org/">https://projectlombok.org/</a>
 * @author Gyapi
 */
@lombok.Getter
@lombok.Setter
@JsonSerialize(using = WorkMonthSerializer.class)
public class WorkMonth {
    
    //Fields
    private final List<WorkDay> days = new ArrayList<>();
    private final YearMonth date;
    private long sumPerMonth;
    private long requiredMinPerMonth;

    //Consturctors
    /**
     * 
     * Creates a new WorkMonth object with the given parameters
     * @param year : Integer, must be a valid year (4 digits long)
     * @param month : Integer, must be between 1 and 12
     */
    public WorkMonth(int year, int month) {
        this.date = YearMonth.of(year, month);
    }
    
    //Methods
    /**
     * 
     * Adds a {@link WorkDay WorkDay} to the list: {@link #days days}
     * Uses the {@link #sumsPerMonth sumsPerMonth} method
     * @param wd : {@link WorkDay WorkDay}
     * @throws tlog16rs.core.Exceptions.NotTheSameMonthException
     * @throws tlog16rs.core.Exceptions.NotNewDateException
     * @throws tlog16rs.core.Exceptions.WeekendNotEnabledException
     */
    public void addWorkDay(WorkDay wd)
            throws NotTheSameMonthException, NotNewDateException,
            WeekendNotEnabledException{ 
        
        if (!isSameMonth(wd)) {
            throw new NotTheSameMonthException("You can't add days from a different moth, "
                    + "than the one you work with. Please try again.");
        }
        if (!isNewDate(wd)){
            throw new NotNewDateException("This day already exists in the month. Please try again.");
        }
        if (!Util.isWeekday(wd.getActualDay())){
            throw new WeekendNotEnabledException("Working on weekends not enabled. Please try again.");
        }
        else{
            days.add(wd);  
            sumsPerMonth();
        }  
    }
    
    /**
     *
     * Adds a {@link WorkDay WorkDay} to the list: {@link #days days}
     * Overloads the {@link #addWorkDay(Entities.WorkDay) addWorkDay} method, so weekend can be enabled
     * Do not add the day to the list of days, if actualDay is on the weekend and isWeekendEnabled=false.
     * The isWeekendEnabled boolean parameter has default value=false
     * Uses the {@link #sumsPerMonth sumsPerMonth} method
     * @param wd : {@link WorkDay WorkDay}
     * @param isWeekendEnabled : {@link Boolean Boolean} through this, work on weekend can be enabled
     * @throws tlog16rs.core.Exceptions.NotTheSameMonthException 
     * @throws tlog16rs.core.Exceptions.NotNewDateException 
     * @throws tlog16rs.core.Exceptions.WeekendNotEnabledException 
     */
    public void addWorkDay(WorkDay wd, boolean isWeekendEnabled) 
            throws NotTheSameMonthException, NotNewDateException,
            WeekendNotEnabledException{   
        
        if (!isSameMonth(wd)) {
            throw new NotTheSameMonthException("You can't add days from a different moth, "
                    + "than the one you work with. Please try again.");
        }
        if (!isNewDate(wd)){
            throw new NotNewDateException("This day already exists in the month. Please try again.");
        }
        if (isWeekendEnabled == false && !Util.isWeekday(wd.getActualDay())){
            throw new WeekendNotEnabledException("Working on weekends not enabled. Plase try again.");
        }
        else{
            days.add(wd);
            sumsPerMonth();
        }      
    }
            
    /**
     * 
     * Decides, if this {@link WorkDay WorkDay} should be in this {@link WorkMonth WorkMonth}
     * or it fits into an other month by date
     * @param d : {@link WorkDay WorkDay}
     * @return {@link Boolean Boolean}
     */
    private boolean isSameMonth(WorkDay d){
        return d.getActualDay().getMonth().equals(date.getMonth());
    }
    
    /**
     * 
     * Decides if this {@link WorkDay WorkDay} is already exists in the list 
     * {@link #days days} or not
     * @param d
     * @return 
     */
    private boolean isNewDate(WorkDay d){
        
        List<WorkDay> filteredDay;
        
        if (days.isEmpty()){
            return true;
        }
        else{        
            filteredDay = days.stream()
                    .filter(day -> d.getActualDay().equals(day.getActualDay()))
                    .collect(Collectors.toList());

            return filteredDay.isEmpty();
        }
    }  
    
    /**
     * 
     * Calculates the sum of the workHours({@link WorkDay#sumPerDay sumPerDay})
     * and the required work hours({@link WorkDay#requiredMinPerDay requiredMinPerDay}) of the month
     */
    private void sumsPerMonth(){
        
        sumPerMonth = 0;        
        requiredMinPerMonth = 0;
    
        days.forEach((day) -> {            
            sumPerMonth += day.getSumPerDay();
            requiredMinPerMonth += day.getRequiredMinPerDay();
        });
    }

    /**
     * 
     * Calculates, how many extra minutes did the employee work in the actual month
     * @return 
     */
    public long getExtraMinPerMonth(){        
        return requiredMinPerMonth - sumPerMonth;
    }   
}
