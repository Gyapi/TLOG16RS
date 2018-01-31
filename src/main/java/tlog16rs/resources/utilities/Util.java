/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tlog16rs.resources.utilities;

import tlog16rs.entities.Task;
import tlog16rs.exceptions.EmptyTimeFieldException;
import tlog16rs.exceptions.NotExpectedTimeOrderException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import static java.time.temporal.ChronoUnit.MINUTES;

/**
 *
 * This class is a Collection of methods, that are used trouhg various other classes.
 * <br>{@link #isMultipleQuarterHour(java.time.LocalTime, java.time.LocalTime) isMultipleQuarterHour}
 * <br>{@link #isSeparatedTime(Entities.Task, java.util.List) isSeparatedTime}
 * <br>{@link #isWeekday(java.time.LocalDate) isWeekday}
 * @author Gyapi
 */
public class Util {
    
    /**
     * Checks if the time interval's length is multiple of the quarter hour
     * @param startTime : {@link LocalTime LocalTime} startTime of the time interval
     * @param endTime : {@link LocalTime LocalTime} endTime of the time interval
     * @return : {@link Boolean Boolean} 
     * @throws tlog16rs.exceptions.EmptyTimeFieldException 
     * @throws tlog16rs.exceptions.NotExpectedTimeOrderException 
     */
    public static boolean isMultipleQuarterHour(LocalTime startTime, LocalTime endTime) 
            throws EmptyTimeFieldException, NotExpectedTimeOrderException{
        
        long timeInterval = 0;
        
        if (startTime == null){
            throw new EmptyTimeFieldException("Start time missing. Please try again."); 
        }
        if (endTime == null){ 
            throw new EmptyTimeFieldException("End time missing. Please try again."); 
        }
        if (startTime.isAfter(endTime)){
            throw new NotExpectedTimeOrderException("Wrong time order. Please try again.");}
        else{
            timeInterval = (((endTime.getHour() - startTime.getHour())*60) +
                    (endTime.getMinute()- startTime.getMinute()));
        }
        
        return timeInterval % 15 == 0;        
    } 
    
   /**
    * 
    * Rounds the given time interval to multiple of a quarter hour
    * @param startTime : {@link LocalTime LocalTime} startTime of the time interval
    * @param endTime : {@link LocalTime LocalTime} endTime of the time interval
    * @return : {@link LocalTime LocalTime} rounded endTime 
    */
    public static LocalTime roundToMultipleQuarterHour(LocalTime startTime, LocalTime endTime){
        
        long roundThis = (((endTime.getHour() - startTime.getHour())*60) +
                (endTime.getMinute()- startTime.getMinute()));
        
        if (roundThis < 15) {
            return startTime.plusMinutes(15);
        }
        if (roundThis >= 15 && roundThis % 15 < 15/2) {
            return endTime.minusMinutes(roundThis % 15);
        }
        else {
            return endTime.plusMinutes(15 - (roundThis % 15));
        }
    }
  
    /**
     * Decides if actual day is a weekday
     * @param actualDay
     * @return 
     */
    public static boolean isWeekday(LocalDate actualDay){
        return ((!actualDay.getDayOfWeek().equals(DayOfWeek.SUNDAY)) && 
                (!actualDay.getDayOfWeek().equals(DayOfWeek.SATURDAY)));        
    }
    
    
    /**
     * Decides if the given {@link Task Task} has a common time interval
     * with any existing {@link Task Task}'s time interval in the given list
     * @param t : {@link Task Task} the task we validate
     * @param tasks : {@link ArrayList ArrayList} of {@link Task Task}s
     * @return : {@link Boolean Boolean}
     */
    public static boolean isSeparatedTime(Task t, List<Task> tasks){
        
        List<Task> collected = new ArrayList<>();
        long dif;
        
        if (t.getEndTime() == null){
            for (Task task : tasks) { 
                if (task.getEndTime() == null){
                    dif = t.getStartTime().until(task.getStartTime(), MINUTES);
                   if (dif > -15 && dif < 15){
                        collected.add(task);                       
                   }
                }          
                else{
                    if (t.getStartTime().until(task.getStartTime(), MINUTES) < 15 
                            && !(t.getStartTime().compareTo(task.getEndTime()) >= 0)){
                        collected.add(task);
                    }
                }
            }
        }
        else{
            for (Task task : tasks) {
                if (task.getEndTime() == null){
                    if (task.getStartTime().until(t.getStartTime(), MINUTES) < 15 
                            && !(task.getStartTime().compareTo(t.getEndTime()) >= 0)){
                        collected.add(task);
                    }
                }
                else{
                    if (!(t.getStartTime().compareTo(task.getEndTime()) >= 0 ||
                        task.getStartTime().compareTo(t.getEndTime()) >= 0)){
                        collected.add(task);
                    }
                }
            }
        }
        return collected.isEmpty();        
    }
}
