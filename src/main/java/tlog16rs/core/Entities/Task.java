package tlog16rs.core.Entities;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import tlog16rs.core.Util.Util;
import tlog16rs.core.Exceptions.EmptyTimeFieldException;
import tlog16rs.core.Exceptions.InvalidTaskIdException;
import tlog16rs.core.Exceptions.NoTaskIdException;
import tlog16rs.core.Exceptions.NotExpectedTimeOrderException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import tlog16rs.core.Serializers.TaskSerializer;

/**
 *
 * The Task class is the most basic object of this application. 
 * <br>It contains most of the parameters, which are displayed in the UI of the application.
 * <br>
 * <br>{@link #taskId taskId} : A {@link String String} variable which contains The ID of the task.
 * <br>{@link #startTime startTime} : A {@link LocalTime LocalTime} variable 
 * wich defines the start time of the task (Hour,Minute)
 * <br>{@link #endTime endTime} : A {@link LocalTime LocalTime} variable wich 
 * defines the end time of the task (Hour,Minute)
 * <br>{@link #comment comment} : A {@link String String} variable, wich contains a user set comment to the task
 * <br>
 * <br> The getters and setters, which does not require special code, are generated through Lombok
 * <br> @see <a href="https://projectlombok.org/">https://projectlombok.org/</a>
 * @author Gyapi
 */
@lombok.Getter
@lombok.Setter
@JsonSerialize(using = TaskSerializer.class)
public class Task {
    
    //Fields
    private final String taskId;
    private LocalTime startTime;
    private LocalTime endTime;
    private String comment;
    
    //Contructors
    /**
     * 
     * Creates a new Task object from the required parameters
     * @param taskId : String with one of the following formats: LT-(4 digit number) || 4 digit number
     * Validated trhough: {@link #isValidTaskId(String taskID) isValidTaskId}
     * @param comment : {@link String String} with user comments. No validation necessary
     * @param startHour : {@link Integer Integer} between 0 and 23
     * @param startMin : {@link Integer Integer} between 0 and 59
     * @param endHour : {@link Integer Integer} between 0 and 23
     * @param endMin : {@link Integer Integer} between 0 and 59
     * @throws tlog16rs.core.Exceptions.InvalidTaskIdException 
     * @throws tlog16rs.core.Exceptions.NotExpectedTimeOrderException 
     * @throws tlog16rs.core.Exceptions.NoTaskIdException 
     * @throws tlog16rs.core.Exceptions.EmptyTimeFieldException 
     */
    public Task(String taskId, String comment, int startHour, int startMin, int endHour, int endMin) 
            throws InvalidTaskIdException, NotExpectedTimeOrderException, 
            NoTaskIdException, EmptyTimeFieldException {
        
        LocalTime startCheck, endCheck;
        startCheck = timeConvert(startHour, startMin);        
        endCheck = timeConvert(endHour, endMin);
        
        if (taskId.equals("") || taskId.equals(" ")){
            throw new NoTaskIdException("Missing TaskID. Please try again.");
        }
        if (!isValidLTTaskId(taskId)) {
            throw new InvalidTaskIdException("Invalid TaskId. Please try again.");
        }
        if (!startCheck.isBefore(endCheck)){
            throw new NotExpectedTimeOrderException("Wrong time order. Please try again.");
        }
        else {
            this.taskId = taskId;
            this.comment = comment;
            this.startTime = startCheck; 
            if (!Util.isMultipleQuarterHour(this.startTime, endCheck)){
                this.endTime = Util.roundToMultipleQuarterHour(this.startTime, endCheck);
            } 
            else{
                this.endTime = endCheck;
            }
        }
    }

    /**
     * 
     * Creates a new Task object from the required parameters
     * @param taskId : String with one of the following formats: LT-(4 digit number) || 4 digit number
     * Validated trhough: {@link #isValidTaskId(String taskID) isValidTaskId}
     * @param comment : {@link String String} with user comments. No validation necessary
     * @param startTime : {@link String String} wich defines the starting time of the task. Has to be in the following format: 
     * HH:mm
     * @param endTime  : {@link String String} wich defines the ending time of the task. Has to be in the following format: 
     * HH:mm
     * @throws tlog16rs.core.Exceptions.InvalidTaskIdException 
     * @throws tlog16rs.core.Exceptions.NotExpectedTimeOrderException 
     * @throws tlog16rs.core.Exceptions.EmptyTimeFieldException 
     * @throws tlog16rs.core.Exceptions.NoTaskIdException 
     */
    public Task(String taskId, String comment, String startTime, String endTime) 
            throws InvalidTaskIdException, NotExpectedTimeOrderException, 
            EmptyTimeFieldException, NoTaskIdException { 
        
        LocalTime startCheck, endCheck;
        
        if (taskId.equals("") || taskId.equals(" ")){
            throw new NoTaskIdException("Missing TaskID. Please try again.");
        }
        if (!isValidTaskId(taskId)) {
            throw new InvalidTaskIdException("Invalid TaskId. Please try again.");
        }
        if (startTime.equals("") || startTime.equals(" ")){
            throw new EmptyTimeFieldException("Start time missing. Please try again.");
        }
        if (endTime.equals("") || endTime.equals(" ")){
            throw new EmptyTimeFieldException("End time missing. Please try again.");            
        }
        
        startCheck = timeConvert(startTime);
        endCheck = timeConvert(endTime);
        
        if (!startCheck.isBefore(endCheck)){
            throw new NotExpectedTimeOrderException("Wrong time order. Please try again.");
        }
        else{  
            this.taskId = taskId;
            this.comment = comment;
            this.startTime = startCheck;
            if (!Util.isMultipleQuarterHour(this.startTime, endCheck)){
                this.endTime = Util.roundToMultipleQuarterHour(this.startTime, endCheck);
            }
            else{
                this.endTime = endCheck;
            }
        }
    }

    /**
     * Creates a new Task object from the required parameter
     * @param taskId : {@link String String} with one of the following formats: LT-(4 digit number) || 4 digit number
     * Validated trhough: {@link #isValidTaskId(String taskID) isValidTaskId}
     * @throws tlog16rs.core.Exceptions.InvalidTaskIdException 
     * @throws tlog16rs.core.Exceptions.NoTaskIdException 
     */
    public Task(String taskId) 
            throws InvalidTaskIdException, NoTaskIdException {
        
        if (taskId.equals("") || taskId.equals(" ")){
            throw new NoTaskIdException("Missing TaskID. Please try again.");
        }
        if (!isValidTaskId(taskId)){ 
            throw new InvalidTaskIdException("Invalid TaskId. Please try again.");              
        }
        else{
            this.taskId = taskId; 
        }
    } 
     
    //Methods    
    /**
     * 
     * Converts the given parameters to LocalTime
     * Used by the constructors and the setter methods of the {@link #startTime startTime}, 
     * {@link #endTime endTime} fields
     * @param hour : {@link Integer Integer} between 0 and 23
     * @param min : {@link Integer Integer} between 0 and 59
     * @return {@link LocalTime LocalTime} variable created from the two parameters
     */
    private LocalTime timeConvert(int hour, int min){        
        return LocalTime.of(hour, min);
    }
    
    /**
     * 
     * Converts the given String parameter to LocalTime
     * Used by the constructors and the setter methods of the {@link #startTime startTime}, 
     * {@link #endTime endTime} fields
     * @param time : {@link String String}, must be in the following format: HH:mm
     * @return  {@link LocalTime LocalTime} variable created from the given String
     */
    private LocalTime timeConvert(String time){
        return LocalTime.parse(time);
    }
    
    /**
     * 
     * Validates the taskID through 2 different sub methods: 
     * {@link #isValidLTTaskId(java.lang.String) isValidLTTaskId}
     * {@link #isValidRedmineTaskId(java.lang.String) isValidRedmineTaskId}
     * Used by the constructors
     * @param taskID : {@link String String} with one of the following formats: LT-(4 digit number) || 4 digit number
     * @return {@link Boolean Boolean} depending on the result of the sub methods. If one of them results true,
     * this will return true value
     */
    private boolean isValidTaskId(String taskID){
        return isValidLTTaskId(taskID) || isValidRedmineTaskId(taskID);
    }
    
    /**
     * 
     * Sub Method of the isValidTaskId
     * Checks if the Task ID is in the correct LT format (LT-(4 digit number))
     * @param taskID : {@link String String
     * @return boolean depending on the result of the validation 
     */
    private boolean isValidLTTaskId(String taskID) {
        return taskID.matches("LT-\\d{4}");
    }
    
    /**
     * 
     * Sub Method of the isValidTaskId
     * Checks if the Task ID is in the correct Redmine format (4 digit number))
     * @param taskID : {@link String String}
     * @return {@link Boolean Boolean} depending on the result of the validation
     */
    private boolean isValidRedmineTaskId(String taskID) {
        return taskID.matches("\\d{4}");
    }
    
    /**
     * 
     * Calculates the lenght of a task in minutes
     * Uses the objects {@link #startTime startTime}, {@link #endTime endTime} fields
     * If one of them is empty, throws an exception
     * @return {@link Long Long} variable
     * @throws tlog16rs.core.Exceptions.EmptyTimeFieldException 
     */
    public long getMinPerTask() 
            throws EmptyTimeFieldException{
        
        if (startTime == null){
            throw new EmptyTimeFieldException("Start time missing. Please try again."); 
        }
        if (endTime == null){ 
            throw new EmptyTimeFieldException("End time missing. Please try again."); 
        }
        else{
            return ((endTime.getHour() - startTime.getHour())*60) + 
                    (endTime.getMinute()- startTime.getMinute());
        }
    }
    
    /**
     * 
     * The to string method of the Task class
     * Returns the values of the object with a string
     * @return TaskId. {@link #startTime startTime}(HH:mm) - 
     * {@link #endTime endTime}(HH:mm) {@link #comment comment}: comment
     */
    @Override
    public String toString() {        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        
        if (endTime == null){        
            return taskId + ". " + startTime.format(formatter) + " - " +
                    " Comment:" + comment;
        }
        return taskId + ". " + startTime.format(formatter) + " - " + endTime.format(formatter) +
                " Comment:" + comment;
    }
       
    //Setters  
    /**
     * 
     * Sets values to the {@link #startTime startTime}
     * If there is an {@link #endTime endTime} already set, it will check if it is not before the
     * {@link #startTime startTime}
     * <br> Uses the {@link Util#isMultipleQuarterHour(java.time.LocalTime, java.time.LocalTime)
     * isMultipleQuarterHour}, {@link Util#roundToMultipleQuarterHour(java.time.LocalTime, java.time.LocalTime) 
     * roundToMultipleQuarterHour} methods.
     * @param startHour : {@link Integer Integer} between 0 and 23
     * @param startMin : {@link Integer Integer} between 0 and 59
     * @throws tlog16rs.core.Exceptions.NotExpectedTimeOrderException 
     * @throws tlog16rs.core.Exceptions.EmptyTimeFieldException 
     */
    public void setStartTime(int startHour, int startMin) 
            throws NotExpectedTimeOrderException, EmptyTimeFieldException {
        
         LocalTime startCheck = timeConvert(startHour, startMin);
         
        if (endTime != null && !startCheck.isBefore(this.endTime)){
            throw new NotExpectedTimeOrderException("Wrong time order. Please try again.");
        }
        else{
            this.startTime = startCheck;
            if (endTime != null && !Util.isMultipleQuarterHour(startCheck, this.endTime)){
                this.endTime = Util.roundToMultipleQuarterHour(startCheck, this.endTime);
            }
        }   
    }
    
    /**
     * 
     * Sets values to the {@link #startTime startTime}
     * If there is an {@link #endTime endTime} already set, it will check if it is not before the
     * {@link #startTime startTime}
     * <br> Uses the {@link Util#isMultipleQuarterHour(java.time.LocalTime, java.time.LocalTime)
     * isMultipleQuarterHour}, {@link Util#roundToMultipleQuarterHour(java.time.LocalTime, java.time.LocalTime) 
     * roundToMultipleQuarterHour} methods.
     * @param startTime : {@link String String} which must be in the following format: HH:mm
     * @throws tlog16rs.core.Exceptions.EmptyTimeFieldException 
     * @throws tlog16rs.core.Exceptions.NotExpectedTimeOrderException 
     */
    public void setStartTime(String startTime) 
            throws EmptyTimeFieldException, NotExpectedTimeOrderException { 
        
        LocalTime startCheck = timeConvert(startTime);
        
        if (this.endTime != null && !startCheck.isBefore(this.endTime)){
            throw new NotExpectedTimeOrderException("Wrong time order. Please try again.");
        }
        else{
            this.startTime = startCheck;
            if (this.endTime != null && !Util.isMultipleQuarterHour(startCheck, this.endTime)){
                this.endTime = Util.roundToMultipleQuarterHour(startCheck, this.endTime);
            }
        }    
    }
    
    /**
     * 
     * Sets values to the {@link #startTime startTime}
     * If there is an {@link #endTime endTime} already set, it will check if it is not before the
     * {@link #startTime startTime}
     * <br> Uses the {@link Util#isMultipleQuarterHour(java.time.LocalTime, java.time.LocalTime)
     * isMultipleQuarterHour}, {@link Util#roundToMultipleQuarterHour(java.time.LocalTime, java.time.LocalTime) 
     * roundToMultipleQuarterHour} methods.
     * @param startTime : {@link LocalTime LocalTime} variable 
     * @throws tlog16rs.core.Exceptions.EmptyTimeFieldException 
     * @throws tlog16rs.core.Exceptions.NotExpectedTimeOrderException 
     */
    public void setStartTime(LocalTime startTime) 
            throws EmptyTimeFieldException, NotExpectedTimeOrderException {        

        if (this.endTime == null){
            throw new EmptyTimeFieldException("End time missing. Please try again.");            
        }
        if (this.endTime != null && !startTime.isBefore(this.endTime)){
            throw new NotExpectedTimeOrderException("Wrong time order. Please try again.");
        }
        else{
            this.startTime = startTime;
            if (this.endTime != null && !Util.isMultipleQuarterHour(startTime, this.endTime)){
                this.endTime = Util.roundToMultipleQuarterHour(startTime, this.endTime);
            }
        }  
    }

    /**
     *
     * Sets values to the {@link #endTime endTime}
     * Checks if the {@link #startTime startTime} and the {@link #endTime endTime} is in the correct order
     * <br> Uses the {@link Util#isMultipleQuarterHour(java.time.LocalTime, java.time.LocalTime)
     * isMultipleQuarterHour}, {@link Util#roundToMultipleQuarterHour(java.time.LocalTime, java.time.LocalTime) 
     * roundToMultipleQuarterHour} methods.
     * @param endHour : {@link Integer Integer} between 0 and 23
     * @param endMin : {@link Integer Integer} between 0 and 59 
     * @throws tlog16rs.core.Exceptions.NotExpectedTimeOrderException 
     * @throws tlog16rs.core.Exceptions.EmptyTimeFieldException 
     */
    public void setEndTime (int endHour, int endMin)
            throws NotExpectedTimeOrderException, EmptyTimeFieldException {         
        
        LocalTime endCheck = timeConvert(endHour, endMin);         
   
        if (!this.startTime.isBefore(endCheck)){
            throw new NotExpectedTimeOrderException("Wrong time order. Please try again.");
        }
        else {
            if (!Util.isMultipleQuarterHour(this.startTime, endCheck)){
                this.endTime = Util.roundToMultipleQuarterHour(this.startTime, endCheck);
            }
            else {
               this.endTime = endCheck; 
            }
        }       
    }
    
    /**
     * Sets values to the {@link #endTime endTime}
     * Checks if the {@link #startTime startTime} and the {@link #endTime endTime} is in the correct order
     * <br> Uses the {@link Util#isMultipleQuarterHour(java.time.LocalTime, java.time.LocalTime)
     * isMultipleQuarterHour}, {@link Util#roundToMultipleQuarterHour(java.time.LocalTime, java.time.LocalTime) 
     * roundToMultipleQuarterHour} methods.
     * @param endTime : {@link String String} which must be in the following format: HH:mm 
     * @throws tlog16rs.core.Exceptions.NotExpectedTimeOrderException 
     * @throws tlog16rs.core.Exceptions.EmptyTimeFieldException 
     */
    public void setEndTime(String endTime) 
            throws NotExpectedTimeOrderException, EmptyTimeFieldException {
        
        LocalTime endCheck = timeConvert(endTime);        
   
        if (!this.startTime.isBefore(endCheck)){
            throw new NotExpectedTimeOrderException("Wrong time order. Please try again.");
        }
        else {
            if (!Util.isMultipleQuarterHour(this.startTime, endCheck)){
                this.endTime = Util.roundToMultipleQuarterHour(this.startTime, endCheck);
            }
            else {
               this.endTime = endCheck; 
            }
        }    
    }
}
