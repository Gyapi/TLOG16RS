package tlog16rs.resources.utilities;

import com.avaje.ebean.Ebean;
import tlog16rs.resources.RBobjects.TaskRB;
import tlog16rs.resources.RBobjects.DeleteTaskRB;
import tlog16rs.resources.RBobjects.ModifyTaskRB;
import tlog16rs.resources.RBobjects.FinishTaskRB;
import tlog16rs.resources.RBobjects.WorkDayRB;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalTime;
import lombok.extern.slf4j.Slf4j;
import tlog16rs.entities.Task;
import tlog16rs.entities.TimeLogger;
import tlog16rs.entities.WorkDay;
import tlog16rs.entities.WorkMonth;
import tlog16rs.exceptions.EmptyTimeFieldException;
import tlog16rs.exceptions.FutureWorkException;
import tlog16rs.exceptions.InvalidTaskIdException;
import tlog16rs.exceptions.NegativeMinutesOfWorkException;
import tlog16rs.exceptions.NoTaskIdException;
import tlog16rs.exceptions.NotExpectedTimeOrderException;
import tlog16rs.exceptions.NotNewDateException;
import tlog16rs.exceptions.NotNewMonthException;
import tlog16rs.exceptions.NotSeparatedTimesException;
import tlog16rs.exceptions.NotTheSameMonthException;
import tlog16rs.exceptions.WeekendNotEnabledException;

/**
 *
 * A class containing the actual methods behind the endpoints.
 * <br> The setters, which does not require special code, are generated through Lombok
 * <br> @see <a href="https://projectlombok.org/">https://projectlombok.org/</a>
 * @author Gyapi
 */
@lombok.Getter
public class Services {
    
    private final TimeLogger timelogger;

    /**
     * 
     * Constructor of the object.
     * Gets all necessary information from the Database
     */
    public Services() {
        timelogger = Ebean.find(TimeLogger.class, 2);
        timelogger.getMonths().forEach((month) -> {
            month.convertItBack();
        });
    }    
    
    /**
     * 
     * Returns all the {@link WorkMonth WorkMonths} from the designated {@link TimeLogger TimeLogger} object 
     * as a serialized {@link String String}.
     * <br>Uses the {@link tlog16rs.core.Serializers.WorkMonthSerializer WorkMonthSerializer} class
     * 
     * @return all the {@link WorkMonth WorkMonths} (linked to this timelogger) as a serialized {@link String String}
     * 
     * @throws com.fasterxml.jackson.core.JsonProcessingException
     */    
    public String getMonths() 
            throws JsonProcessingException{
        
        String returnMe = "";
        ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();
        
        if (timelogger.getMonths().isEmpty()){
            return "Nothing is here";
        }
        else{            
            for (WorkMonth month : timelogger.getMonths()){
                returnMe += objectMapper.writeValueAsString(month) + "\n\n";
            }            
            return returnMe;
        }
    }
    
    /**
     * 
     * Gives back the selected {@link WorkMonth WorkMonth} as a serialized {@link String String}. 
     * <br>Uses the {@link WorkMonthSerializer WorkMonthSerializer} class
     * <br>Uses the {@link #monthSelector(tlog16rs.core.Entities.WorkMonth) monthSelector} method
     * 
     * @param wantedYear : The selected month's year as a {@link String String}
     * @param wantedMonth : The selected month's month of year value as a {@link String String}
     * 
     * @return String : the selected {@link WorkMonth WorkMonths} as a serialized {@link String String}
     * 
     * @throws NumberFormatException
     * @throws JsonProcessingException 
     */
    public String getSelectedMonth(String wantedYear, String wantedMonth)
        throws NumberFormatException, JsonProcessingException{
        
        String returnMe = "No such month exists";
        ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();
        
        WorkMonth selectedMonth = new WorkMonth(Integer.parseInt(wantedYear),
                Integer.parseInt(wantedMonth));

        if (!timelogger.isNewMonth(selectedMonth)){
            returnMe = objectMapper.writeValueAsString(monthSelector(selectedMonth));
        }

        return returnMe;
    }
    
    /**
     * 
     * Deletes all {@link WorkMonth WorkMonth} object from the {@link Timelogger Timelogger's} list, 
     * as well as from the database.
     * 
     * @return String : Message about the sucsess of the deletion
     */
    public String deleteAllMonths(){
                
        Ebean.deleteAll(timelogger.getMonths());
        if (!timelogger.getMonths().isEmpty()){
            timelogger.getMonths().forEach((month) -> {
                month = null;
            });
            timelogger.getMonths().clear();
            return "Deletetion of WorkMonths: SUCCESSFUL";
        }
        return "There is nothing to delete here.";
    }
    
    /**
     * 
     * Returns all the {@link WorkDay WorkDays} from the designated {@link TimeLogger TimeLogger} object 
     * as a serialized {@link String String}.
     * <br>Uses the {@link WorkDaySerializer WorkDaySerializer} class
     * 
     * @return the {@link WorkDay WorkDays} as a serialized {@link String String}
     * 
     * @throws com.fasterxml.jackson.core.JsonProcessingException
     */  
    public String getDays() 
            throws JsonProcessingException {

        String returnMe = "";
        ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

        if (timelogger.getMonths().isEmpty()){
            return returnMe;
        }
        else{
            for (WorkMonth month : timelogger.getMonths()){
                if (!month.getDays().isEmpty()){
                    for (WorkDay day : month.getDays()){      
                        returnMe += objectMapper.writeValueAsString(day) + "\n\n";    
                    }
                    returnMe += "\n";
                }
            }
            if (returnMe.equals("")){
                returnMe = "Nothing is here";
            }
            return returnMe; 
        } 
    }
         
    
    /**
     * 
     * Returns the selected {@link WorkDay WorkDay} as a serialized {@link String String}.
     * <br>Uses {@link WorkDaySerializer WorkDaySerializer} as serializer
     * 
     * @param wantedYear the date's year value as {@link String String}
     * @param wantedMonth the date's month of year value as {@link String String}
     * @param wantedDay the date's day of month value as {@link String String}
     * 
     * @return String the selected {@link WorkDay WorkDay} as a serialized {@link String String}
     * 
     * @throws NumberFormatException
     * @throws JsonProcessingException
     * @throws FutureWorkException 
     * @throws tlog16rs.exceptions.NegativeMinutesOfWorkException 
     */
    public String getSelectedDay(String wantedYear, String wantedMonth, String wantedDay)
        throws NumberFormatException, JsonProcessingException, FutureWorkException,
            NegativeMinutesOfWorkException{
        
        String returnMe = "No such day exists";
        ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();
        
        WorkMonth selectedMonth = new WorkMonth(Integer.parseInt(wantedYear),
                Integer.parseInt(wantedMonth));
        if (!timelogger.isNewMonth(selectedMonth)){
            selectedMonth = monthSelector(selectedMonth);
        }
        else{
            return returnMe;
        }
        
        WorkDay selectedDay = new WorkDay(Integer.parseInt(wantedYear), Integer.parseInt(wantedMonth),
                Integer.parseInt(wantedDay));
        if(selectedMonth.isNewDate(selectedDay)){
            return returnMe;
        }
        for (WorkDay day : selectedMonth.getDays()){
            if (day.getActualDay().equals(selectedDay.getActualDay())){
                return objectMapper.writeValueAsString(day);                
            }
        }
        
        return returnMe;
    }
    
    /**
     * 
     * Creates a new {@link WorkDay WorkDay} object from the given {@link WorkDayRB workDayRB} object.
     * <br>Uses the {@link #monthSelector(tlog16rs.core.Entities.WorkMonth) monthSelector} method
     * 
     * @param day the {@link WorkDayRB WorkDayRB} object we want to work with
     * 
     * @return the created {@link WorkDay WorkDay} object
     * 
     * @throws NotNewMonthException
     * @throws FutureWorkException
     * @throws NotTheSameMonthException
     * @throws NotNewDateException
     * @throws WeekendNotEnabledException
     * @throws NegativeMinutesOfWorkException 
     */
    public WorkDay createDay(WorkDayRB day) 
            throws NotNewMonthException, FutureWorkException, NotTheSameMonthException,
            NotNewDateException, WeekendNotEnabledException, NegativeMinutesOfWorkException {
        
        WorkMonth month = new WorkMonth(day.getYear(), day.getMonth());   
        if (timelogger.isNewMonth(month)){
            timelogger.addMonth(month);
        }
        else{
            month = monthSelector(month);
        }
        
        WorkDay newDay = null;        
        if (day.getRequiredHours() == 0){
            newDay = new WorkDay(day.getYear(), day.getMonth(), day.getDay());            
        }
        else{
            newDay = new WorkDay(day.getRequiredHours(), day.getYear(), day.getMonth(), day.getDay());
        }
        
        if (day.isWeekEnd()){
            month.addWorkDay(newDay, true);
        }
        else{
            month.addWorkDay(newDay);
        }
        
        return newDay;
    }
    
    /**
     * 
     * Returns all the {@link Task Tasks} from the designated {@link TimeLogger TimeLogger} object 
     * as a serialized {@link String String}.
     * <br>Uses the {@link TaskSerializer TaskSerializer} class
     * 
     * @return the {@link Task Tasks} as a serialized {@link String String}
     * 
     * @throws com.fasterxml.jackson.core.JsonProcessingException
     */  
    public String getTasks() 
            throws JsonProcessingException{

        String returnMe = "Nothing is here";
        ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

        if (timelogger.getMonths().isEmpty()){
            return returnMe;
        }
        else{
            for (WorkMonth month : timelogger.getMonths()){
                if (!month.getDays().isEmpty()){
                    for (WorkDay day : month.getDays()){
                        if (!day.getTasks().isEmpty()){
                            returnMe = "";
                            for (Task task: day.getTasks()){
                                returnMe += objectMapper.writeValueAsString(task) + "\n";
                            }
                            returnMe += "\n";  
                        }
                    }
                    returnMe += "\n";
                }
            }
            return returnMe;  
        }
    }
    
    /**
     * Starts a new {@link Task Task} object from the given {@link TaskRB TaskRB} object.
     * <br>Uses the {@link #monthSelector(tlog16rs.core.Entities.WorkMonth) monthSelector, 
     * {@link #daySelector(tlog16rs.core.Entities.WorkMonth, tlog16rs.core.Entities.WorkDay)  dayselector} methods
     * 
     * @param task the {@link TaskRB TaskRB} object we work with
     * 
     * @return the created {@link Task task} object
     * 
     * @throws NotNewMonthException
     * @throws FutureWorkException
     * @throws NotTheSameMonthException
     * @throws NotNewDateException
     * @throws WeekendNotEnabledException
     * @throws InvalidTaskIdException
     * @throws NoTaskIdException
     * @throws EmptyTimeFieldException
     * @throws NotExpectedTimeOrderException
     * @throws NotSeparatedTimesException 
     * @throws tlog16rs.exceptions.NegativeMinutesOfWorkException 
     */
    public Task starTask(TaskRB task) 
            throws NotNewMonthException, FutureWorkException, NotTheSameMonthException, 
            NotNewDateException, WeekendNotEnabledException, InvalidTaskIdException,
            NoTaskIdException, EmptyTimeFieldException, NotExpectedTimeOrderException,
            NotSeparatedTimesException, NegativeMinutesOfWorkException{
        
        WorkMonth month = new WorkMonth(task.getYear(), task.getMonth()); 
        if (timelogger.isNewMonth(month)){
            timelogger.addMonth(month);
        }
        else{
            month = monthSelector(month);
        }
        
        WorkDay day = new WorkDay(task.getYear(), task.getMonth(), task.getDay());
        if (month.isNewDate(day)){
            month.addWorkDay(day);
        }
        else{
            day = daySelector(month, day);            
        }
        
        Task newTask = new Task(task.getTaskId());        
        newTask.setComment(task.getComment());
        newTask.setStartTime(task.getStartTime());        
        day.addTask(newTask);
        
        return newTask;        
    }  
    
     /**
     * 
     * Finishes the selected {@link Task task} with the given {@link TaskRB TaskRB's} properties.
     * <br>Uses the {@link #monthSelector(tlog16rs.core.Entities.WorkMonth)  monthSelector},
     * {@link #daySelector(tlog16rs.core.Entities.WorkMonth, tlog16rs.core.Entities.WorkDay)  daySelector}, 
     * {@link #taskSelector(tlog16rs.core.Entities.WorkDay, tlog16rs.core.Entities.Task)  taskSelector} methods
     * 
     * @param task the {@link FinishTaskRB FinishTaskRB} object we work with
     * 
     * @return the finished {@link Task Task}
     * 
     * @throws NotNewMonthException
     * @throws FutureWorkException
     * @throws NotTheSameMonthException
     * @throws NotNewDateException
     * @throws WeekendNotEnabledException
     * @throws InvalidTaskIdException
     * @throws NotExpectedTimeOrderException
     * @throws EmptyTimeFieldException
     * @throws NoTaskIdException
     * @throws NotSeparatedTimesException 
     * @throws tlog16rs.exceptions.NegativeMinutesOfWorkException 
     */
    public Task finishThatThask(FinishTaskRB task) 
            throws NotNewMonthException, FutureWorkException, NotTheSameMonthException,
            NotNewDateException, WeekendNotEnabledException, InvalidTaskIdException,
            NotExpectedTimeOrderException, EmptyTimeFieldException, NoTaskIdException, NotSeparatedTimesException, NegativeMinutesOfWorkException {        
        
        WorkMonth month = new WorkMonth(task.getYear(), task.getMonth());
        
        if (timelogger.isNewMonth(month)){
            timelogger.addMonth(month);
        }
        else {
            month = monthSelector(month);
        }
        
        WorkDay day = new WorkDay(task.getYear(), task.getMonth(), task.getDay());
        if (month.isNewDate(day)){
            month.addWorkDay(day);
        }
        else{
            day = daySelector(month, day);
        }
        
        Task workWithMe = new Task(task.getTaskId(), "", task.getStartTime(), task.getEndTime());  
        Task modifyThisTask;
        if (day.getTasks().isEmpty()){
            day.addTask(workWithMe);
            modifyThisTask = workWithMe;
        }
        else {
            modifyThisTask = taskSelector(day, workWithMe);
            if (modifyThisTask == null){
                day.addTask(workWithMe);
                modifyThisTask = workWithMe;           
            }
            else{
                modifyThisTask.setEndTime(workWithMe.getEndTime());
            }
        }     
        
        day.extraMinPerDay();
        month.extraMinPerMonth();
        return modifyThisTask;
    }    

    /**
     * 
     * Modifies the selected {@link Task Task}.
     * <br>Uses the {@link #monthSelector(tlog16rs.core.Entities.WorkMonth) nthSelector monthSelector},
     * {@link #daySelector(tlog16rs.core.Entities.WorkMonth, tlog16rs.core.Entities.WorkDay) daySelector}, 
     * {@link #taskSelector taskSelector}, {@link #createTask(tlog16rs.core.Util.ModifyTaskRB) createTask},
     * {@link #modifyThisTask(tlog16rs.core.Entities.Task, tlog16rs.core.Util.ModifyTaskRB) modifyThisTask} methods
     * 
     * @param task the {@link ModifyTaskRB ModifyTaskRB} object we work with
     * 
     * @return the modified {@link Task Task}
     * 
     * @throws NotNewMonthException
     * @throws FutureWorkException
     * @throws NotTheSameMonthException
     * @throws NotNewDateException
     * @throws WeekendNotEnabledException
     * @throws InvalidTaskIdException
     * @throws NotExpectedTimeOrderException
     * @throws EmptyTimeFieldException
     * @throws NoTaskIdException
     * @throws NotSeparatedTimesException 
     * @throws tlog16rs.exceptions.NegativeMinutesOfWorkException 
     */
    public Task modifyTask(ModifyTaskRB task) 
            throws NotNewMonthException, FutureWorkException, NotTheSameMonthException, 
            NotNewDateException, WeekendNotEnabledException, InvalidTaskIdException, 
            NotExpectedTimeOrderException, EmptyTimeFieldException, NoTaskIdException,
            NotSeparatedTimesException, NegativeMinutesOfWorkException {
        
        WorkMonth month = new WorkMonth(task.getYear(), task.getMonth());
        
        if (timelogger.isNewMonth(month)){
            timelogger.addMonth(month);
        }
        else {
            month = monthSelector(month);
        }
        
        WorkDay day = new WorkDay(task.getYear(), task.getMonth(), task.getDay());
        if (month.isNewDate(day)){
            month.addWorkDay(day);            
        }
        else{
            day = daySelector(month, day);
        }
        
        Task modified;            
        if (day.getTasks().isEmpty()){
            modified = createTask(task);
            day.addTask(modified);
        }
        else {
            modified = taskSelector(day, createTask(task));
            if (modified == null){
                modified = createTask(task);
                day.addTask(modified);
            }
            
           modified = modifyThisTask(modified, task); 
        }
        
        day.extraMinPerDay();
        month.extraMinPerMonth();
        return modified;        
    }
    
        /**
     * 
     * Deletes the chosen {@link Task Task} based on the parameters of the given 
     * {@link DeleteTaskRB DeleteTaskRB}.
     * <br>Uses the {@link #monthSelector(tlog16rs.core.Entities.WorkMonth) nthSelector monthSelector},
     * {@link #daySelector(tlog16rs.core.Entities.WorkMonth, tlog16rs.core.Entities.WorkDay) daySelector}, 
     * {@link #taskSelector taskSelector}, {@link #createTask(tlog16rs.core.Util.ModifyTaskRB) createTask} methods
     * 
     * @param task the {@link DeleteTaskRB DeleteTaskRB} object we work with
     * 
     * @return boolean true if the {@link Task task} is deleted
     * 
     * @throws FutureWorkException
     * @throws InvalidTaskIdException
     * @throws NoTaskIdException
     * @throws EmptyTimeFieldException
     * @throws NotExpectedTimeOrderException 
     * @throws tlog16rs.exceptions.NegativeMinutesOfWorkException 
     */
    public boolean deleteThisTask(DeleteTaskRB task) 
            throws FutureWorkException, InvalidTaskIdException, NoTaskIdException, 
            EmptyTimeFieldException, NotExpectedTimeOrderException, NegativeMinutesOfWorkException{
        
        WorkMonth month = new WorkMonth(task.getYear(), task.getMonth());
        
        if (timelogger.isNewMonth(month)){
            return false;
        }
        else {
            month = monthSelector(month);
        }
        
        WorkDay day = new WorkDay(task.getYear(), task.getMonth(), task.getDay());
        if (month.isNewDate(day)){
            return false;
        }
        else{
            day = daySelector(month, day);
        }
        
        Task deleteThis = new Task(task.getTaskId());        
        LocalTime originalStart = LocalTime.parse(task.getStartTime());
        deleteThis.setStartTime(originalStart);
        deleteThis = taskSelector(day, deleteThis);
        if (deleteThis == null){
            return false;
        }
        else{
            Ebean.delete(deleteThis);
            day.getTasks().remove(deleteThis);
            day.extraMinPerDay();
            month.extraMinPerMonth();
            deleteThis = null;
            return true;
            }      
    }
    
    /**
     * 
     * Based on the given {@link WorkMonth WorkMonth's} date, finds it in the {@link TimeLogger Timelogger's}
     * month list and gives it back.
     * 
     * @param month the {@link WorkMonth WorkMonth} we are looking for
     * 
     * @return the found {@link WorkMonth WorkMonth} from the list
     */
    private WorkMonth monthSelector(WorkMonth month){
        
        WorkMonth returnMe = null;
        
        for (WorkMonth wmonth : timelogger.getMonths()){
            if (wmonth.getDate().equals(month.getDate())){
                return wmonth;
            }
        }
        
        return returnMe;
    }
    
    /**
     * 
     * Based on the given {@link WorkDay WorkDay's} date, finds it in the {@link WorkMonth WorkMonth's}
     * month list and gives it back.
     * 
     * @param month the {@link WorkMonth WorkMonth} where the searched day is
     * @param day the {@link WorkDay WorkDay} we are looking for
     * 
     * @return the found {@link WorkDay WorkDay}
     */
    private WorkDay daySelector(WorkMonth month, WorkDay day){
        
        WorkDay returnMe = null;
        
        for (WorkDay wday : month.getDays()){
            if (wday.getActualDay().equals(day.getActualDay())){
                return wday;
            }
        }
        
        return returnMe;
    }
    
    /**
     * 
     * Based on the given {@link Task Task's} propeties, selects it from the given
     * {@link WorkDay WorkDay's} task list.
     * 
     * @param day the {@link WorkDay WorkDay} the task is in
     * @param task the {@link Task Task} we are looking for
     * 
     * @return the found {@link Task Task}
     */
    private Task taskSelector(WorkDay day, Task task){
        
        Task returnMe = null;        
        for (Task selectedTask : day.getTasks()){
                if (selectedTask.getStartTime().equals(task.getStartTime()) && 
                        selectedTask.getTaskId().equals(task.getTaskId())){                    
                    return selectedTask;
                }
            }
        
        return returnMe;
    } 

    /**
     * 
     * Creates a new {@link Task Task} object from the given {@link ModifyTaskRB ModifyTaskRB}.
     * 
     * @param task {@link ModifyTaskRB ModifyTaskRB} we work with
     * 
     * @return created {@link Task Task}
     * 
     * @throws InvalidTaskIdException
     * @throws NoTaskIdException
     * @throws EmptyTimeFieldException
     * @throws NotExpectedTimeOrderException 
     */
    private Task createTask(ModifyTaskRB task) 
            throws InvalidTaskIdException, NoTaskIdException,
            EmptyTimeFieldException, NotExpectedTimeOrderException{
        Task newTask;
        
        if (task.getNewTaskId() == null){
            newTask = new Task(task.getTaskId());            
        }
        else{
            newTask = new Task(task.getNewTaskId());           
        }
        
        if (task.getNewStartTime() == null){
            newTask.setStartTime(task.getStartTime()); 
        }
        else{
            newTask.setStartTime(task.getNewStartTime());            
        }
        if (task.getNewEndTime() != null){
            newTask.setEndTime(task.getNewEndTime());
        }
        if (task.getNewComment() != null){
            newTask.setComment(task.getNewComment());
        }
        
        return newTask;        
    }
    
    /**
     * 
     * Modifies the given {@link Task Task} with the given {@link ModifyTaskRB ModifyTaskRB's} properties
     * 
     * @param selected the {@link Task Task} we want to modify
     * @param modifier the {@link ModifyTaskRB ModifyTaskRB} we work with
     * 
     * @return the modified {@link Task Task}
     * 
     * @throws EmptyTimeFieldException
     * @throws NotExpectedTimeOrderException 
     */
    private Task modifyThisTask(Task selected, ModifyTaskRB modifier)
            throws EmptyTimeFieldException, NotExpectedTimeOrderException,
            InvalidTaskIdException, NoTaskIdException{
        
        if (modifier.getNewTaskId()!= null){
            selected.setTaskId(modifier.getNewTaskId());            
        }
        if (modifier.getNewComment() != null){
            selected.setComment(modifier.getNewComment());
        }
        if (modifier.getNewStartTime() != null){
            selected.setStartTime(modifier.getNewStartTime());            
                    }
        if (modifier.getNewEndTime() != null){
            selected.setEndTime(modifier.getNewEndTime());
        }
        
        return selected;
    }
    
}
