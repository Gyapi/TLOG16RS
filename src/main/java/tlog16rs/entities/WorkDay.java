package tlog16rs.entities;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import tlog16rs.resources.utilities.Util;
import tlog16rs.exceptions.EmptyTimeFieldException;
import tlog16rs.exceptions.FutureWorkException;
import tlog16rs.exceptions.NegativeMinutesOfWorkException;
import tlog16rs.exceptions.NotSeparatedTimesException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Transient;
import tlog16rs.resources.serializers.WorkDaySerializer;

/**
 *
 * The {@link WorkDay WorkDay} class handles the collection of {@link Task Tasks}.
 * <br>
 * <br>{@link #tasks tasks} : An {@link java.util.ArrayList ArrayList} which stores the 
 * {@link Task Tasks}, which belongs to this {@link WorkDay WorkDay}
 * Adding new Tasks happens trough the method: {@link #addTask(tlog16rs.entities.Task) addTask}
 * <br>{@link #requiredMinPerDay requiredMinPerDay} : {@link Long long} variable, it contains 
 * the required workhours (in minutes) for the {@link WorkDay WorkDay}
 * <br>{@link #actualDay actualDay} : {@link LocalDate LocalDate} variable, contains the date 
 * of the {@link WorkDay WorkDay}. Set trough the constructor, setter method
 * <br>{@link #sumPerDay sumPerDay} : The lenght of all {@link Task Tasks} of the day. Calculated trough the
 * {@link #sumPerDay() sumPerDay} method.
 * <br>{@link #extraMinPerDay extraMinPerDay} : the difference between the {@link #sumPerDay sumPerDay} and
 * {@link #requiredMinPerDay requiredMinPerDay} fields
 * <br>
 * <br> The getters are generated through Lombok
 * <br> @see <a href="https://projectlombok.org/">https://projectlombok.org/</a>
 * <br> Serialized trough the {@link WorkDaySerializer WorkDaySerializer} class
 * @author Gyapi
 */
@Entity
@lombok.Getter
@JsonSerialize(using = WorkDaySerializer.class)
public class WorkDay {
    
    @Id    
    @GeneratedValue
    private int Id;    
    @Transient
    private static final LocalDate now = LocalDate.now();
    @Transient
    private static final int defaultRequiredMin = 450;   
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Task> tasks = new ArrayList<>();
    @Column(name = "required_min_per_day")
    private long requiredMinPerDay;
    @Column(name = "actual_day")
    private LocalDate actualDay;
    @Column(name = "sum_per_day")
    private long sumPerDay; 
    @Column(name = "extra_min_per_day")
    private long extraMinPerDay;
    
    
    /**
     * 
     * Creates a new {@link WorkDay WorkDay} object with the provided parameters.
     * 
     * @param requiredMinPerDay : {@link Long Long}, the required worktime for the day (in minutes). Can't be negative
     * @param year : {@link Integer Integer}, must be 4 digits long and has to have a value that is a valid year.
     * @param month : {@link Integer Integer} has to be between 1 and 12
     * @param day : {@link Integer Integer} has to be between 1 and 31, be mindful about the length of the month 
     * 
     * @throws tlog16rs.exceptions.NegativeMinutesOfWorkException 
     * @throws tlog16rs.exceptions.FutureWorkException 
     */
    public WorkDay(long requiredMinPerDay, int year, int month, int day) 
            throws NegativeMinutesOfWorkException, FutureWorkException {
        
        LocalDate checkDate = LocalDate.of(year, month, day); 
        
        if (requiredMinPerDay < 0){
            throw new NegativeMinutesOfWorkException("The requiredMinPerDay cannot be negative. Please try again.");
        }
        if (checkDate.isAfter(LocalDate.now())){
            throw new FutureWorkException("You cant add a date from the future. Please try again.");
        }
        else{
            this.requiredMinPerDay = requiredMinPerDay;
            this.actualDay = LocalDate.of(year, month, day);
            this.sumPerDay = 0;
        }
    }    

    /**
     * 
     * Creates a new {@link WorkDay WorkDay} object with the default parameters.
     * <br>{@link #requiredMinPerDay requiredMinPerDay} = 450
     * <br>{@link #actualDay actualDay} = the actual day
     * 
     * @throws tlog16rs.exceptions.NegativeMinutesOfWorkException
     * @throws tlog16rs.exceptions.FutureWorkException
     */
    public WorkDay() 
            throws NegativeMinutesOfWorkException, FutureWorkException{
        this(defaultRequiredMin, now.getDayOfYear(), now.getMonthValue(), now.getDayOfMonth());        
    }
    
    /**
     * 
     * Creates a new {@link WorkDay WorkDay} object with the provided parameters.
     * <br>{@link #requiredMinPerDay requiredMinPerDay} will have the default value (450)
     * 
     * @param year : {@link Integer Integer}, must be 4 digits long and has to have a value that is a valid year. 
     * Future years throw {@link Exceptions.FutureWorkException FutureWorkException}.     * 
     * @param month : {@link Integer Integer} has to be between 1 and 12
     * @param day : {@link Integer Integer} has to be between 1 and 31, be mindful about the length of the month
     * 
     * @throws tlog16rs.exceptions.NegativeMinutesOfWorkException     * 
     * @throws tlog16rs.exceptions.FutureWorkException 
     */
    public WorkDay(int year, int month, int day) 
            throws NegativeMinutesOfWorkException, FutureWorkException{
        this(defaultRequiredMin, year, month, day);
    }

    /**
     * 
     * Creates a new {@link WorkDay WorkDay} object with the provided parameters.
     * 
     * @param requiredMinPerDay : {@link Long Long}, the required worktime for the day (in minutes). Can't be negative 
     * @param actualDay : {@link LocalDate LocalDate} the date of the {@link WorkDay WorkDay}
     * 
     * @throws tlog16rs.exceptions.NegativeMinutesOfWorkException 
     * @throws tlog16rs.exceptions.FutureWorkException 
     */
    public WorkDay(long requiredMinPerDay, LocalDate actualDay) 
            throws NegativeMinutesOfWorkException, FutureWorkException {                
        this(requiredMinPerDay, actualDay.getDayOfYear(), actualDay.getMonthValue(), actualDay.getDayOfMonth());
    }

    /**
     * 
     * Creates a new {@link WorkDay WorkDay} object with the provided parameters.
     * The {@link #actualDay actualDay} will have the default value of the actual day)
     * @param requiredMinPerDay : {@link Long Long}, the required worktime for the day (in minutes). Can't be negative
     * 
     * @throws tlog16rs.exceptions.NegativeMinutesOfWorkException 
     * @throws tlog16rs.exceptions.FutureWorkException 
     */
    public WorkDay(long requiredMinPerDay) 
            throws NegativeMinutesOfWorkException, FutureWorkException{
        this(requiredMinPerDay, now.getDayOfYear(), now.getMonthValue(), now.getDayOfMonth());
    }
    
    /**
     * 
     * Add a task to the list of tasks, if task time intervals have no collision.
     * <br>Uses the {@link Util Util}'s {@link Util#isSeparatedTime(Entities.Task, java.util.List) isSeparatedTime}
     * method.
     * <br>If sucessful, calls the {@link #extraMinPerDay() extraMinPerDay} method, to refresh the
     * values of the {@link #sumPerDay sumPerDay} and {@link #extraMinPerDay extraMinPerDay} fields.
     * 
     * @param t : the {@link Task Task} object the method validates 
     * 
     * @throws tlog16rs.exceptions.NotSeparatedTimesException 
     * @throws tlog16rs.exceptions.EmptyTimeFieldException 
     */
    public void addTask(Task t) 
            throws NotSeparatedTimesException, EmptyTimeFieldException{
        
        if (Util.isSeparatedTime(t, tasks) || tasks.isEmpty()){    
            tasks.add(t);
            extraMinPerDay();
        }
        else {
            throw new NotSeparatedTimesException("The task overlaps with another. Please try again.");
        }
    }   
    
    /**
     * 
     * Calculates, the sum of the {@link #tasks tasks} {@link ArrayList list} members
     * {@link Task#minPerTask() minPerTask}
     * <br>If a {@link Task Task} is started, but has no end time, the method will skip it.
     * 
     * @throws tlog16rs.exceptions.EmptyTimeFieldException
     */
    private void sumPerDay() 
            throws EmptyTimeFieldException{
        
        sumPerDay = 0;
            
        for (Task task : tasks) {
            if (task.getEndTime() != null){
                sumPerDay += task.getMinPerTask();
            }
        }
    }
    
    /**
     * 
     * Calculates the difference between the {@link #requiredMinPerDay requiredMinPerDay} and the 
     * {@link #sumPerDay sumPerDay}.
     * <br> Uses the {@link #sumPerDay() sumPerDay} method
     * 
     * @throws tlog16rs.exceptions.EmptyTimeFieldException
     */
    public void extraMinPerDay() 
            throws EmptyTimeFieldException{
        sumPerDay();
        this.extraMinPerDay = sumPerDay - requiredMinPerDay;
    }
    
    /**
     * 
     * First sorts the {@link ArrayList list} of {@link Task Tasks}, in the ascending order of the
     * {@link Task#startTime starTime}s, then returns the endTime of the last Task.
     * <br>If there is/are (a) Task(s) with no end time, it will skip it/them.
     * 
     * @return The last {@link Task Task}'s {@link Task#endTime endTime}
     */
    public LocalTime endTimeOfTheLastTask(){
        
        List<Task> sorted = tasks;
        Task lastTask = null;
        
        sorted.sort((t1, t2) -> t1.getStartTime().compareTo(t2.getStartTime()));
        
        while (lastTask == null){
            for (int i = sorted.size() - 1; i >= 0; i--){
                if (sorted.get(i).getEndTime() != null){
                    lastTask = sorted.get(i);
                    break;
                }
            }
        }
        
        return lastTask.getEndTime();
    }
    
    /**
     * 
     * Sets the value of the {@link #requiredMinPerDay requiredMinPerDay}.
     * 
     * @param requiredMinPerDay : long, the required worktime for the day (in minutes). Can't be negative
     * 
     * @throws NegativeMinutesOfWorkException 
     */
    public void setRequiredMinPerDay(long requiredMinPerDay) 
            throws NegativeMinutesOfWorkException {
        
        if (requiredMinPerDay < 0){
            throw new NegativeMinutesOfWorkException("The requiredMinPerDay cannot be negative. Please try again.");
        }
        else {
            this.requiredMinPerDay = requiredMinPerDay;
        }
    }

    /**
     * 
     * Sets the value of the {@link #actualDay actualDay}.
     * 
     * @param year : {@link Integer Integer}, must be 4 digits long and has to have a value that is a valid year.
     * @param month : {@link Integer Integer} has to be between 1 and 12
     * @param day : {@link Integer Integer} has to be between 1 and 31, be mindful about the length of the month 
     * 
     * @throws tlog16rs.exceptions.FutureWorkException 
     */
    public void setActualDay(int year, int month, int day) 
            throws FutureWorkException {
        
        LocalDate checkDate = LocalDate.of(year, month, day); 
        
        if (checkDate.isAfter(LocalDate.now())){
            throw new FutureWorkException("You cant add a date from the future. Please try again.");
        }
        this.actualDay = LocalDate.of(year, month, day);
    }
}
