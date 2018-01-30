package tlog16rs.entities;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import tlog16rs.resources.utilities.Util;
import tlog16rs.exceptions.EmptyTimeFieldException;
import tlog16rs.exceptions.InvalidTaskIdException;
import tlog16rs.exceptions.NoTaskIdException;
import tlog16rs.exceptions.NotExpectedTimeOrderException;
import java.time.LocalTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import tlog16rs.resources.serializers.TaskSerializer;

/**
 *
 * The {@link Task Task} class is the most basic object of this application. 
 * <br>It contains most of the parameters, which are displayed in the UI of the application.
 * <br>
 * <br>{@link #taskId taskId} : A {@link String String} variable which contains The ID of the {@link Task Task}.
 * <br>{@link #startTime startTime} : A {@link LocalTime LocalTime} variable 
 * wich defines the start time of the {@link Task Task} (Hour,Minute)
 * <br>{@link #endTime endTime} : A {@link LocalTime LocalTime} variable wich 
 * defines the end time of the {@link Task Task} (Hour,Minute)
 * <br>{@link #comment comment} : A {@link String String} variable, wich contains a
 * user set comment to the {@link Task Task}
 * <br>
 * <br> The getters are generated through Lombok
 * <br> @see <a href="https://projectlombok.org/">https://projectlombok.org/</a>
 * <br> Serialized trough the {@link TaskSerializer TaskSerializer} class
 * @author Gyapi 
 */
@Entity
@lombok.Getter
@JsonSerialize(using = TaskSerializer.class)
public class Task {
    
    @Id
    @GeneratedValue
    private int id;
    @Column(name = "task_id")
    private String taskId;
    @Column(name = "start_time")
    private LocalTime startTime;
    @Column(name = "end_time")
    private LocalTime endTime;
    @Column(name = "comment")
    private String comment;
    //TODO: UPDATE-kor nézzél már rá
    @Column (name = "min_per_task")
    private long minPerTask;
    
    /**
     * 
     * Creates a new {@link Task Task} object from the required parameters.
     * <br> Uses the 
     * {@link #fillWithValidValues(java.lang.String, java.lang.String, java.time.LocalTime, java.time.LocalTime) 
     * fillWithValidValues}, {@link #timeConvert(java.lang.String) timeConvert},
     * {@link #timeConvert(int, int) timeConvert}, {@link #taskIDCheck(java.lang.String) taskIDCheck} methods
     * 
     * @param taskId : {@link String String} with one of the following formats: LT-(4 digit number) || 4 digit number
     * Validated trough: {@link #isValidTaskId(String taskID) isValidTaskId}
     * @param comment : {@link String String} with user comments. No validation necessary
     * @param startHour : {@link Integer Integer} between 0 and 23
     * @param startMin : {@link Integer Integer} between 0 and 59
     * @param endHour : {@link Integer Integer} between 0 and 23
     * @param endMin : {@link Integer Integer} between 0 and 59
     * 
     * @throws tlog16rs.exceptions.InvalidTaskIdException 
     * @throws tlog16rs.exceptions.NotExpectedTimeOrderException 
     * @throws tlog16rs.exceptions.NoTaskIdException 
     * @throws tlog16rs.exceptions.EmptyTimeFieldException 
     */
    public Task(String taskId, String comment, int startHour, int startMin, int endHour, int endMin) 
            throws InvalidTaskIdException, NotExpectedTimeOrderException, 
                NoTaskIdException, EmptyTimeFieldException {
        
        if(taskIDCheck(taskId)){
                    
            LocalTime startCheck = timeConvert(startHour, startMin);        
            LocalTime endCheck = timeConvert(endHour, endMin);
            
            if (!startCheck.isBefore(endCheck)){
                throw new NotExpectedTimeOrderException("Wrong time order. Please try again.");
            }
            else{
                fillWithValidValues(taskId, comment, startCheck, endCheck);
            }
        }
    }

    /**
     * 
     * Creates a new Task object from the required parameters.
     * <br> Uses the 
     * {@link #fillWithValidValues(java.lang.String, java.lang.String, java.time.LocalTime, java.time.LocalTime) 
     * fillWithValidValues}, {@link #timeConvert(java.lang.String) timeConvert},
     * {@link #timeConvert(int, int) timeConvert}, {@link #taskIDCheck(java.lang.String) taskIDCheck} methods
     * 
     * @param taskId : {@link String String} with one of the following formats: LT-(4 digit number) || 4 digit number
     * Validated trough: {@link #isValidTaskId(String taskID) isValidTaskId}
     * @param comment : {@link String String} with user comments. No validation necessary
     * @param startTime : {@link String String} wich defines the starting time of the task. Has to be in the following format: 
     * HH:mm
     * @param endTime  : {@link String String} wich defines the ending time of the task. Has to be in the following format: 
     * HH:mm
     * 
     * @throws tlog16rs.exceptions.InvalidTaskIdException 
     * @throws tlog16rs.exceptions.NotExpectedTimeOrderException 
     * @throws tlog16rs.exceptions.EmptyTimeFieldException 
     * @throws tlog16rs.exceptions.NoTaskIdException 
     */
    public Task(String taskId, String comment, String startTime, String endTime) 
            throws InvalidTaskIdException, NotExpectedTimeOrderException, 
            EmptyTimeFieldException, NoTaskIdException { 
        
        if(taskIDCheck(taskId)){            
            if (startTime.equals("") || startTime.equals(" ")){
                throw new EmptyTimeFieldException("Start time missing. Please try again.");
            }
            if (endTime.equals("") || endTime.equals(" ")){
                throw new EmptyTimeFieldException("End time missing. Please try again.");            
            }

            LocalTime startCheck = timeConvert(startTime);
            LocalTime endCheck = timeConvert(endTime);

            if (!startCheck.isBefore(endCheck)){
                throw new NotExpectedTimeOrderException("Wrong time order. Please try again.");
            }
            else{
                fillWithValidValues(taskId, comment, startCheck, endCheck);
            }
        }
    }

    /**
     * Creates a new {@link Task Task} object from the required parameter.
     * <br>Uses the {@link #taskIDCheck(java.lang.String) method.
     * 
     * @param taskId : {@link String String} with one of the following formats: LT-(4 digit number) || 4 digit number
     * Validated trough: {@link #isValidTaskId(String taskID) isValidTaskId}
     * 
     * @throws tlog16rs.exceptions.InvalidTaskIdException 
     * @throws tlog16rs.exceptions.NoTaskIdException 
     */
    public Task(String taskId) 
            throws InvalidTaskIdException, NoTaskIdException {
        
        if (taskIDCheck(taskId)){
            this.taskId = taskId; 
            this.minPerTask = 0;
        }
    } 

    /**
     * 
     * Checks that the TaskId field is not empty, then validates it trought 2 sub methods,
     * that it had the accepted formats.
     * <br> Uses the {@link #isValidLTTaskId(java.lang.String) isValidLTTaskId}, 
     * {@link #isValidRedmineTaskId(java.lang.String) isValidRedmineTaskId} methods.
     * 
     * @param : {@ink String String} TaskID the id of the {@link task task}. Must be in
     * one of the following formats: LT-(4 digit number) || 4 digit number.
     * 
     * @return true if the id passes all criteria
     * 
     * @throws InvalidTaskIdException
     * @throws NoTaskIdException 
     */    
    private boolean taskIDCheck(String taskID)
            throws InvalidTaskIdException, NoTaskIdException{
        
        if (taskID.equals("") || taskID.equals(" ")){
            throw new NoTaskIdException("Missing TaskID. Please try again.");
        }
        if (!isValidLTTaskId(taskID) && !isValidRedmineTaskId(taskID)) {
            throw new InvalidTaskIdException("Invalid TaskId. Please try again.");
        }
        return true;
    }
    
     /**
     * 
     * Sub Method of the {@link #taskIDCheck(java.lang.String) taskIDCheck}
     * <br>Checks if the Task ID is in the correct LT format (LT-(4 digit number))
     * 
     * @param taskID : {@link String String
     * 
     * @return boolean depending on the result of the validation 
     */
    private boolean isValidLTTaskId(String taskID) {
        return taskID.matches("LT-\\d{4}");
    }
    
    /**
     * 
     * Sub Method of the {@link #taskIDCheck(java.lang.String) taskIDCheck}.
     * <br>Checks if the Task ID is in the correct Redmine format (4 digit number))
     * 
     * @param taskID : {@link String String}
     * 
     * @return {@link Boolean Boolean} depending on the result of the validation
     */
    private boolean isValidRedmineTaskId(String taskID) {
        return taskID.matches("\\d{4}");
    }
    
    /**
     * 
     * Converts the given parameters to {@link LocalTime LocalTime}.
     * <br>Used by the constructors and the setter methods of the {@link #startTime startTime}, 
     * {@link #endTime endTime} fields
     * 
     * @param hour : {@link Integer Integer} between 0 and 23
     * @param min : {@link Integer Integer} between 0 and 59
     * 
     * @return {@link LocalTime LocalTime} variable created from the two parameters
     */
    private LocalTime timeConvert(int hour, int min){        
        return LocalTime.of(hour, min);
    }
    
    /**
     * 
     * Converts the given String parameter to LocalTime.
     * <br>Used by the constructors and the setter methods of the {@link #startTime startTime}, 
     * {@link #endTime endTime} fields
     * 
     * @param time : {@link String String}, must be in the following format: HH:mm
     * 
     * @return  {@link LocalTime LocalTime} variable created from the given String
     */
    private LocalTime timeConvert(String time){
        return LocalTime.parse(time);
    }
    
    /**
     * 
     * Fills the class's fields with valid values. 
     * <br>Called by the constructor, after careful validation of the given parameters.
     * <br>Required to shorten the lenght of the code.
     * <br>Uses the {@link #minPerTask() minpertask} method
     * 
     * @param taskID : {@link String String} with one of the following formats: LT-(4 digit number) || 4 digit number
     * @param comment : {@link String String} with user comments. No validation necessary
     * @param startTime : {@link LocalTime LocalTime} that defines the starting time of the task.
     * @param endTime : {@link LocalTime LocalTime} that defines the end time of the task.
     * 
     * @throws EmptyTimeFieldException
     * @throws NotExpectedTimeOrderException 
     */
    private void fillWithValidValues(String taskID, String comment, LocalTime startTime, LocalTime endTime) 
            throws EmptyTimeFieldException, NotExpectedTimeOrderException{

        this.minPerTask = 0;
        this.taskId = taskID;
        this.comment = comment;
        this.startTime = startTime;
        if (!Util.isMultipleQuarterHour(this.startTime, endTime)){                
            this.endTime = Util.roundToMultipleQuarterHour(this.startTime, endTime);
        }
        else{
            this.endTime = endTime;
        }
        minPerTask();
    }
    
    /**
     * 
     * Calculates the lenght of a task in minutes.
     * <br> Uses the objects {@link #startTime startTime}, {@link #endTime endTime} fields
     * If one of them is empty, it throws an exception.
     * 
     * @throws tlog16rs.exceptions.EmptyTimeFieldException 
     */
    public void minPerTask() 
            throws EmptyTimeFieldException{
        
        if (startTime == null){
            throw new EmptyTimeFieldException("Start time missing. Please try again."); 
        }
        if (endTime == null){ 
            throw new EmptyTimeFieldException("End time missing. Please try again."); 
        }
        else{
            minPerTask = 0;
            minPerTask = ((endTime.getHour() - startTime.getHour())*60) + 
                    (endTime.getMinute()- startTime.getMinute());
        }
    }
       
    /**
     * 
     * Sets values to the {@link #startTime startTime} trough the method
     * {@link #setStartTime(java.time.LocalTime) setStartTime}.
     * <br> It's only real job is to convert the given {@link Integer integers} to 
     * {@link LocalTime LocalTime} trough the {@link #timeConvert(int, int) timeConvert} method, 
     * then to give it to it to the {@link #setStartTime(java.time.LocalTime) setStartTime} method.
     * 
     * @param startHour : {@link Integer Integer} between 0 and 23
     * @param startMin : {@link Integer Integer} between 0 and 59
     * 
     * @throws tlog16rs.exceptions.NotExpectedTimeOrderException 
     * @throws tlog16rs.exceptions.EmptyTimeFieldException 
     */
    public void setStartTime(int startHour, int startMin) 
            throws NotExpectedTimeOrderException, EmptyTimeFieldException {
        
        LocalTime startCheck = timeConvert(startHour, startMin);
        setStartTime(startCheck);
    }
    
    /**
     * 
     * Sets values to the {@link #startTime startTime} trough the method
     * {@link #setStartTime(java.time.LocalTime) setStartTime}.
     * <br> It's only real job is to convert the given {@link String String} to 
     * {@link LocalTime LocalTime} trough the {@link #timeConvert(java.lang.String) timeConvert} method, 
     * then to give it to it to the {@link #setStartTime(java.time.LocalTime) setStartTime} method.
     * 
     * @param startTime : {@link String String} which must be in the following format: HH:mm
     * 
     * @throws tlog16rs.exceptions.EmptyTimeFieldException 
     * @throws tlog16rs.exceptions.NotExpectedTimeOrderException 
     */
    public void setStartTime(String startTime) 
            throws EmptyTimeFieldException, NotExpectedTimeOrderException { 
        
        LocalTime startCheck = timeConvert(startTime);
        setStartTime(startCheck);
    }
    
    /**
     * 
     * Sets value to the {@link #startTime startTime}.
     * <br>If there is an {@link #endTime endTime} already set, it will check if it is not before the
     * {@link #startTime startTime}.
     * <br> Uses the {@link Util#isMultipleQuarterHour(java.time.LocalTime, java.time.LocalTime)
     * isMultipleQuarterHour}, {@link Util#roundToMultipleQuarterHour(java.time.LocalTime, java.time.LocalTime)
     *  roundToMultipleQuarterHour} {@link #minPerTask() minPerTask} methods.
     * 
     * @param startTime : {@link LocalTime LocalTime} which contains the {@link Task task's} starting time.
     * 
     * @throws tlog16rs.exceptions.EmptyTimeFieldException 
     * @throws tlog16rs.exceptions.NotExpectedTimeOrderException 
     */
    public void setStartTime(LocalTime startTime) 
            throws EmptyTimeFieldException, NotExpectedTimeOrderException {        

        if (this.endTime != null && !startTime.isBefore(this.endTime)){
            throw new NotExpectedTimeOrderException("Wrong time order. Please try again.");
        }
        else{
            this.startTime = startTime;
            if (this.endTime != null && !Util.isMultipleQuarterHour(startTime, this.endTime)){
                this.endTime = Util.roundToMultipleQuarterHour(startTime, this.endTime);
                minPerTask();
            }
        }  
    }

    /**
     *
     * Sets values to the {@link #endTime endTime} trough the method
     * {@link #setEndTime(java.time.LocalTime) setEndTime}.
     * <br> It's only real job is to convert the given {@link String String} to 
     * {@link LocalTime LocalTime} trough the {@link #timeConvert(java.lang.String) timeConvert} method, 
     * then to give it to it to the {@link #setEndTime(java.time.LocalTime) setEndTime} method.
     * 
     * @param endHour : {@link Integer Integer} between 0 and 23
     * @param endMin : {@link Integer Integer} between 0 and 59 
     * 
     * @throws tlog16rs.exceptions.NotExpectedTimeOrderException 
     * @throws tlog16rs.exceptions.EmptyTimeFieldException 
     */
    public void setEndTime (int endHour, int endMin)
            throws NotExpectedTimeOrderException, EmptyTimeFieldException {         
        
        LocalTime endCheck = timeConvert(endHour, endMin);         
        setEndTime(endCheck);
    }
    
    /**
     * Sets values to the {@link #endTime endTime} trough the method 
     * {@link #setEndTime(java.time.LocalTime) setEndTime}.
     * <br> It's only real job is to convert the given {@link String String} to 
     * {@link LocalTime LocalTime} trough the {@link #timeConvert(java.lang.String) timeConvert} method, 
     * then to give it to it to the {@link #setEndTime(java.time.LocalTime) setEndTime} method.
     * 
     * @param endTime : {@link String String} which must be in the following format: HH:mm 
     * 
     * @throws tlog16rs.exceptions.NotExpectedTimeOrderException 
     * @throws tlog16rs.exceptions.EmptyTimeFieldException 
     */
    public void setEndTime(String endTime) 
            throws NotExpectedTimeOrderException, EmptyTimeFieldException {
        
        LocalTime endCheck = timeConvert(endTime);        
        setEndTime(endCheck);
    }
    
     /**
     * Sets values to the {@link #endTime endTime}.
     * <br>Checks if the {@link #startTime startTime} and the {@link #endTime endTime} is in the correct order
     * <br> Uses the {@link Util#isMultipleQuarterHour(java.time.LocalTime, java.time.LocalTime)
     * isMultipleQuarterHour}, {@link Util#roundToMultipleQuarterHour(java.time.LocalTime, java.time.LocalTime) 
     * roundToMultipleQuarterHour} {@link #minPerTask() minPerTask} methods.
     * 
     * @param endTime : {@link LocalTime LocalTime} which contains the {@link Task task's} end time.
     * 
     * @throws tlog16rs.exceptions.NotExpectedTimeOrderException 
     * @throws tlog16rs.exceptions.EmptyTimeFieldException 
     */
    public void setEndTime(LocalTime endTime) 
            throws NotExpectedTimeOrderException, EmptyTimeFieldException{
        
        if (!this.startTime.isBefore(endTime)){
            throw new NotExpectedTimeOrderException("Wrong time order. Please try again.");            
        }
        else {
            if (!Util.isMultipleQuarterHour(this.startTime, endTime)){                
                this.endTime = Util.roundToMultipleQuarterHour(this.startTime, endTime);
            }
            else {
               this.endTime = endTime; 
            }
            minPerTask();
        }    
    }    
    
    /**
     * 
     * Sets the {@link Task Task} object's {@link #taskId taksId} field's value, after validation.
     * <br>Uses the {@link #taskIDCheck(java.lang.String) method.
     * 
     * @param taskID : {@link String String} with one of the following formats: LT-(4 digit number) || 4 digit number
     * Validated trough: {@link #isValidTaskId(String taskID) isValidTaskId}
     * 
     * @throws tlog16rs.exceptions.InvalidTaskIdException
     * @throws tlog16rs.exceptions.NoTaskIdException 
     */
    public void setTaskId(String taskID) 
            throws InvalidTaskIdException, NoTaskIdException{
        
        if (taskIDCheck(taskID)){             
            this.taskId = taskID; 
        }
    }
    
    /**
     * 
     * Sets the user set {@link #comment comment}.
     * 
     * @param comment {@link String String} which contains a comment about the task
     */
    public void setComment (String comment){
        this.comment = comment;
    }
}
