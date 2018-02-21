package tlog16rs.entities;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import tlog16rs.exceptions.NotNewDateException;
import tlog16rs.exceptions.NotTheSameMonthException;
import tlog16rs.exceptions.WeekendNotEnabledException;
import tlog16rs.resources.utilities.Util;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
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
import javax.persistence.Transient;
import tlog16rs.resources.serializers.WorkMonthSerializer;

/**
 *
 * The {@link WorkMonth WorkMonth} class handles the collection of {@link WorkDay WorkDays}.
 * <br>
 * <br>{@link #days days} : {@link ArrayList ArrayList} that collects the {@link WorkDay WorkDay}s
 * <br>{@link #date date} : {@link YearMonth YearMonth} field, it contains the identity of the month (year, month)
 * <br>{@link #sumPerMonth sumPerMonth} : {@link Long Long}, the sum of the workhours in the month (in minutes)
 * <br>{@link #requiredMinPerMonth requiredMinPerMonth} : {@link Long Long},
 * the required workhours of the month (in minutes)
 * <br>{@link #extraMinPerMonth extraMinPerMonth} : {@link Long Long}, the difference between the 
 * {@link #sumPerMonth sumPerMonth} and {@link #requiredMinPerMonth requiredMinPerMonth} fields.
 * <br>
 * <br> The getters are generated through Lombok
 * <br> @see <a href="https://projectlombok.org/">https://projectlombok.org/</a>
 * <br> Serialized trough the {@link WorkMonthSerializer WorkMonthSerializer} class
 * @author Gyapi
 */
@Entity
@lombok.Getter
@JsonSerialize(using = WorkMonthSerializer.class)
public class WorkMonth {
    
    @Id    
    @GeneratedValue
    private int Id;
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<WorkDay> days;
    @Transient
    private YearMonth date;
    @Column(name = "date")    
    private String monthDate;
    @Column(name = "sum_per_month")     
    private long sumPerMonth;
    @Column(name = "required_min_per_month") 
    private long requiredMinPerMonth;
    @Column(name = "extra_min_per_month")
    private long extraMinPerMonth;
    @Transient
    private static final DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy.MM");

    /** 
     * 
     * Creates a new WorkMonth object with the given parameters
     * 
     * @param year : Integer, must be a valid year (4 digits long)
     * @param month : Integer, must be between 1 and 12
     */
    public WorkMonth(int year, int month) {
        this.days = new ArrayList<>();
        this.sumPerMonth = 0;
        this.requiredMinPerMonth = 0;
        this.extraMinPerMonth = 0;
        this.date = YearMonth.of(year, month);
        setMonthDate();
    }
     
    /**
     * 
     * Formats the {@link YearMonth YearMonth} {@link #date date} field into 
     * a {@link String String} with the next format: YYYY.MM
     * <br>The target field is the {@link #monthDate monhtDate}
     */
    private void setMonthDate(){
        this.monthDate = date.format(format);
        
    }
    
    public void convertItBack(){
        this.date = YearMonth.parse(monthDate, format);
    }
    
    /**
     *
     * Adds a {@link WorkDay WorkDay} to the {@link ArrayList list}: {@link #days days}, 
     * after validating it.
     * <br>if sucessful calls the {@link #extraMinPerMonth() extraMinPerMonth}, {@link #requiredPerMonth() requiredPerMonth}
     * methods
     * 
     * @param wd : the {@link WorkDay WorkDay} object the method will validate, and add to the list
     * @param isWeekendEnabled : {@link Boolean boolean} objec. If the employee wants to work on the weekend,
     * it must get 'true' value
     * 
     * @throws tlog16rs.exceptions.NotTheSameMonthException 
     * @throws tlog16rs.exceptions.NotNewDateException 
     * @throws tlog16rs.exceptions.WeekendNotEnabledException 
     */
    public void addWorkDay(WorkDay wd, boolean isWeekendEnabled) 
            throws NotTheSameMonthException, NotNewDateException, WeekendNotEnabledException{   
        
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
            extraMinPerMonth();
        }      
    }
    
    /**
     * 
     * Overloads the {@link #addWorkDay(tlog16rs.entities.WorkDay, boolean) addWorkDay} method
     * with default 'false' value .
     * <br>Uses the {@link #addWorkDay(tlog16rs.entities.WorkDay, boolean) addWorkDay} method 
     * 
     * @param wd : the {@link WorkDay WorkDay} object the method will validate, and add to the list
     * 
     * @throws tlog16rs.exceptions.NotTheSameMonthException
     * @throws tlog16rs.exceptions.NotNewDateException
     * @throws tlog16rs.exceptions.WeekendNotEnabledException
     */
    public void addWorkDay(WorkDay wd)
            throws NotTheSameMonthException, NotNewDateException, WeekendNotEnabledException{ 
        addWorkDay(wd, false);
    }
            
    /**
     * 
     * Decides, if this {@link WorkDay WorkDay} should be in this {@link WorkMonth WorkMonth}
     * or it fits into an other month by date.
     * 
     * @param d : {@link WorkDay WorkDay}
     * @return {@link Boolean Boolean}
     */
    private boolean isSameMonth(WorkDay d){
        return d.getActualDay().getMonth().equals(date.getMonth());
    }
    
    /**
     * 
     * Decides if this {@link WorkDay WorkDay} already exists in the list {@link #days days} or not.
     * 
     * @param d : {@link WorkDay WorkDay} object, wich the method will work with
     * 
     * @return 'true' if it is a new date, 'false' if not
     */
    public boolean isNewDate(WorkDay d){
        
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
     * Calculates the required workhours ({@link #requiredMinPerMonth requiredMinPerMonth} 
     * of the {@link WorkMonth WorkMonth}, from the {@link ArrayList list} of {@link WorkDay WorkDays}
     * ({@link #days days}).
     */
    private void requiredPerMonth(){
        
        requiredMinPerMonth = 0;
        
        days.forEach((day) -> {            
            requiredMinPerMonth += day.getRequiredMinPerDay();
        });
    }
    
    /**
     * 
     * Calculates the sum of the workHours({@link WorkDay#sumPerDay sumPerDay}) .
     * of the {@link WorkMonth WorkMonth}, from the {@link ArrayList list} of {@link WorkDay WorkDays}
     * ({@link #days days})
     */
    private void sumPerMonth(){
        
        sumPerMonth = 0;
    
        days.forEach((day) -> {            
            sumPerMonth += day.getSumPerDay();
        });
    }

    /**
     * 
     * Calculates, how many extra minutes did the employee work in this {@link WorkMonth WorkMonth}.
     * <br>Uses the {@link #sumPerMonth() sumperMonth) and {@link #requiredPerMonth() requiredPerMont} methods
     */
    @Column(name = "extra_min_per_month")
    public void extraMinPerMonth(){ 
        requiredPerMonth();
        sumPerMonth();
        this.extraMinPerMonth = sumPerMonth - requiredMinPerMonth;
    }   
}
