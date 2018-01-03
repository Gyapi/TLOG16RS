package tlog16rs.core.Entities;

import tlog16rs.core.Util.Util;
import tlog16rs.core.Exceptions.EmptyTimeFieldException;
import tlog16rs.core.Exceptions.FutureWorkException;
import tlog16rs.core.Exceptions.NegativeMinutesOfWorkException;
import tlog16rs.core.Exceptions.NotSeparatedTimesException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * The WorkDay class collects and sorts the {@link Task Task}s,
 * so the application later can display, which day these were done.
 * <br>
 * <br>{@link #tasks tasks} : An {@link java.util.ArrayList ArrayList} which stores the 
 * Tasks, which belongs to this day. Adding new Tasks happens trough the method:
 * <br>{@link #requiredMinPerDay requiredMinPerDay} : long variable, it is important to the 
 * {@link #getExtraMinPerDay() getExtraMinPerDay} method.
 * <br>{@link #actualDay actualDay} : LocalDate variable, contains the date of the WorkDay. Set trough the
 * constructor, setter method
 * <br>{@link #sumPerDay sumPerDay} : The lenght of all tasks of the day. Calculated trough the
 * {@link #sumPerDay() sumPerDay} method.
 * <br>
 * <br> The getters and setters, which does not require special code, are generated through Lombok
 * <br> @see <a href="https://projectlombok.org/">https://projectlombok.org/</a>
 * @author Gyapi
 */
@lombok.Getter
@lombok.Setter
public class WorkDay {
    
    //Fields
    private final List<Task> tasks = new ArrayList<>();
    private long requiredMinPerDay;
    private LocalDate actualDay;
    private long sumPerDay;

    //Constructors
    /**
     * 
     * Creates a new WorkDay object with the default parameters.
     * {@link #requiredMinPerDay requiredMinPerDay} = 450
     * {@link #actualDay actualDay} = the actual day
     */
    public WorkDay() {
        this.requiredMinPerDay = 450;
        this.actualDay = LocalDate.now();
    }
    
    /**
     * 
     * Creates a new WorkDay object with the provided parameters.
     * {@link #requiredMinPerDay requiredMinPerDay} will have the default value (450)
     * @param year : {@link Integer Integer}, must be 4 digits long and has to have a value that is a valid year. 
     * Future years throw {@link Exceptions.FutureWorkException FutureWorkException}.
     * @param month : {@link Integer Integer} has to be between 1 and 12
     * @param day : {@link Integer Integer} has to be between 1 and 31, be mindful about the length of the month
     * @throws tlog16rs.core.Exceptions.FutureWorkException 
     */
    public WorkDay(int year, int month, int day) 
            throws FutureWorkException {

        LocalDate checkDate = LocalDate.of(year, month, day); 
        
        if (checkDate.isAfter(LocalDate.now())){
            throw new FutureWorkException("You cant add a date from the future. Please try again.");
        }
        else{
            this.requiredMinPerDay = 450;
            this.actualDay = checkDate;
        }
    }
    
    /**
     * 
     * Creates a new WorkDay object with the provided parameters.
     * @param requiredMinPerDay : {@link Long Long}, the required worktime for the day (in minutes). Can't be negative
     * @param year : {@link Integer Integer}, must be 4 digits long and has to have a value that is a valid year.
     * @param month : {@link Integer Integer} has to be between 1 and 12
     * @param day : {@link Integer Integer} has to be between 1 and 31, be mindful about the length of the month 
     * @throws tlog16rs.core.Exceptions.NegativeMinutesOfWorkException 
     * @throws tlog16rs.core.Exceptions.FutureWorkException 
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
        }
    }    

    /**
     * 
     * Creates a new WorkDay object with the provided parameters.
     * @param requiredMinPerDay : {@link Long Long}, the required worktime for the day (in minutes). Can't be negative 
     * @param actualDay : {@link LocalDate LocalDate} the date of the WorkDay
     * @throws tlog16rs.core.Exceptions.NegativeMinutesOfWorkException 
     * @throws tlog16rs.core.Exceptions.FutureWorkException 
     */
    public WorkDay(long requiredMinPerDay, LocalDate actualDay) 
            throws NegativeMinutesOfWorkException, FutureWorkException {
        
        if (requiredMinPerDay < 0){
            throw new NegativeMinutesOfWorkException("The requiredMinPerDay cannot be negative. Please try again.");
        }
        if (actualDay.isAfter(LocalDate.now())){
            throw new FutureWorkException("You cant add a date from the future. Please try again.");
        }
        else{
            this.requiredMinPerDay = requiredMinPerDay;
            this.actualDay = actualDay;
        }
        
        
    }

    /**
     * 
     * Creates a new WorkDay object with the provided parameters.
     * The {@link #actualDay actualDay} will have the default value 8the actual day)
     * @param requiredMinPerDay : {@link Long Long}, the required worktime for the day (in minutes). Can't be negative 
     * @throws NegativeMinutesOfWorkException 
     */
    public WorkDay(long requiredMinPerDay) 
            throws NegativeMinutesOfWorkException {
        
        if (requiredMinPerDay < 0){
            throw new NegativeMinutesOfWorkException("The requiredMinPerDay cannot be negative. Please try again.");
        }
        else{
            this.requiredMinPerDay = requiredMinPerDay;
            this.actualDay = LocalDate.now();
        }
    }
    
    //Methods
    /**
     * 
     * The toString method of the WorkDay class.
     * Returns the values of the object with a string
     * @return {@link #actualDay actualDay}yyyy-HH-dd  
     * Number of tasks: {@link #tasks tasks.size()} 
     * Required work: {@link #requiredMinPerDay requiredMinPerDay} 
     * Work done: {@link #sumPerDay sumPerDay} Extra work: {@link #getExtraMinPerDay() getExtraMinPerDay}
     */
    @Override
    public String toString() {
        return actualDay.toString() + " Number of tasks:" + tasks.size() + "Required work: " + requiredMinPerDay + 
                "Work done: " + sumPerDay + "Extra work:" + getExtraMinPerDay();
    } 
    
    /**
     * 
     * Add a task to the list of tasks, if task time intervals have no collision.
     * <br>Uses the {@link Util Util}'s {@link Util#isSeparatedTime(Entities.Task, java.util.List) isSeparatedTime
     * method.
     * <br>In the event of sucessful addin, calls the {@link #sumPerDay() sumPerDay} method, to refresh the
     * values of the {@link #sumPerDay sumPerDay} field.
     * @param t : {@link Task Task} object
     * @throws tlog16rs.core.Exceptions.NotSeparatedTimesException 
     * @throws tlog16rs.core.Exceptions.EmptyTimeFieldException 
     */
    public void addTask(Task t) 
            throws NotSeparatedTimesException, EmptyTimeFieldException{
        
        if (Util.isSeparatedTime(t, tasks) || tasks.isEmpty()){    
            tasks.add(t);
            sumPerDay();
        }
        else {
            throw new NotSeparatedTimesException("The task overlaps with another. Please try again.");
        }
    }   
    
    /**
     * Calculates, the sum of the {@link #tasks tasks} list members {@link Task#getMinPerTask() getMinPerTask}
     * If a {@link Task Task} is started, but has no end time, the method will skip it.
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
     * Calculates the difference between the requiredMinPerDay and the sumPerDay
     * @return {@link Long Long}
     */
    public long getExtraMinPerDay(){
        return sumPerDay - requiredMinPerDay;
    }
    
    /**
     * 
     * First sorts the list of Tasks, in the ascending order of the {@link Task#startTime starTime}s, then returns
     * the endTime of the last Task. If there is/are (a) Task(s) with no ent time, it will skip it/them.
     * @return the last {@link Task Task}'s {@link Task#endTime endTime}
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
    
    //Setters
    /**
     * 
     * Sets the value of the {@link #requiredMinPerDay requiredMinPerDay}. 
     * @param requiredMinPerDay : long, the required worktime for the day (in minutes). Can't be negative
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
     * Sets the {@link #actualDay actualDay} field's value.
     * @param year : {@link Integer Integer}, must be 4 digits long and has to have a value that is a valid year.
     * @param month : {@link Integer Integer} has to be between 1 and 12
     * @param day : {@link Integer Integer} has to be between 1 and 31, be mindful about the length of the month 
     * @throws tlog16rs.core.Exceptions.FutureWorkException 
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
