/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
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
 * A class containing the actual methods of the endpoints
 * <br> The setters, which does not require special code, are generated through Lombok
 * <br> @see <a href="https://projectlombok.org/">https://projectlombok.org/</a>
 * @author Gyapi
 */
@lombok.Getter
@Slf4j
public class Services {
    
    private final TimeLogger timelogger;

    public Services() {
        timelogger = Ebean.find(TimeLogger.class, 1);
        //createContent();
        timelogger.getMonths().forEach((month) -> {
            month.convertItBack();
        });
    }
    
    private void createContent(){
        //-----------------Test code-----------------
        //TODO : DELETE after obsolete
        Task testTask1, testTask2, testTask3, testTask4, testTask5, 
                testTask6, testTask7, testTask8, testTask9, testTask10, testTask11,
                testTask12, testTask13;
        WorkMonth testMonth1, testMonth2;
        WorkDay testDay1, testDay2, testDay3, testDay4, testDay5;
        
        try {
            testMonth1 = new WorkMonth(2017, 10);
            testTask1 = new Task("LT-0001", "Exception Test", "07:30", "07:45");
            testTask2 = new Task("LT-0002", "Exception Test", "08:30", "08:45");
            testDay1 = new WorkDay(120, 2017, 10, 10);
            testDay1.addTask(testTask1);
            testDay1.addTask(testTask2);
            testMonth1.addWorkDay(testDay1);
            testTask3 = new Task("LT-0003", "Exception Test", "07:30", "07:45");
            testTask4 = new Task("LT-0004", "Exception Test", "08:30", "08:45");
            testDay2 = new WorkDay(2017, 10, 11);
            testDay2.addTask(testTask3);
            testDay2.addTask(testTask4);
            testMonth1.addWorkDay(testDay2);   

            timelogger.addMonth(testMonth1);
            
            testMonth2 = new WorkMonth(2017, 9);
            testTask5 = new Task("LT-0005", "Exception Test", "07:30", "07:45");
            testTask6 = new Task("LT-0006", "Exception Test", "08:30", "08:45");
            testDay3 = new WorkDay(120, 2017, 9, 8);
            testDay3.addTask(testTask5);
            testDay3.addTask(testTask6);
            testMonth2.addWorkDay(testDay3);

            testTask7 = new Task("LT-0007", "Exception Test", "07:30", "07:45");
            testTask8 = new Task("LT-0008", "Exception Test", "08:30", "08:45");
            testDay4 = new WorkDay(2017, 9, 11);
            testDay4.addTask(testTask7);
            testDay4.addTask(testTask8);
            testMonth2.addWorkDay(testDay4);

            testTask9 = new Task("LT-0009", "Exception Test", "07:30", "07:45");
            testTask10 = new Task("LT-0010", "Exception Test", "08:30", "08:45");
            testTask11 = new Task("LT-0011");
            testTask11.setStartTime("08:45");
            testTask11.setComment("Unfinished Test");  
            testTask12 = new Task("LT-0012", "Exception Test", "10:30", "10:45"); 
            testTask13 = new Task("LT-0013");
            testTask13.setStartTime("07:15");
            testTask13.setComment("Unfinished Test"); 
            testDay5 = new WorkDay(2017, 9, 12);
            testDay5.addTask(testTask9);
            testDay5.addTask(testTask10);
            testDay5.addTask(testTask11);
            testDay5.addTask(testTask12);
            testDay5.addTask(testTask13);
            testMonth2.addWorkDay(testDay5);

            timelogger.addMonth(testMonth2);
            Ebean.save(timelogger);
        } 
        catch (EmptyTimeFieldException | FutureWorkException | InvalidTaskIdException |
                NegativeMinutesOfWorkException | NoTaskIdException | NotExpectedTimeOrderException | 
                NotNewDateException | NotNewMonthException | NotSeparatedTimesException | 
                NotTheSameMonthException | WeekendNotEnabledException exception) {
            System.out.println(exception);
        }
        //---------------------------------------------
    }
    
    
    /**
     * 
     * Returns all the {@link WorkMonth WorkMonths} from the designated {@link TimeLogger TimeLogger} object 
     * as a serialized {@link String String}.
     * Uses the {@link tlog16rs.core.Serializers.WorkMonthSerializer WorkMonthSerializer} class
     * @return String
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
     * Uses the {@link WorkMonthSerializer WorkMonthSerializer} class
     * Uses the {@link #monthSelector(tlog16rs.core.Entities.WorkMonth) monthSelector} method
     * @param wantedYear
     * @param wantedMonth
     * @return String
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
     * Deletes all {@link WorkMonth WorkMonth} object from the {@link Timelogger Timelogger's} list
     * @return String
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
     * as a serialized {@link String String}
     * Uses the {@link WorkDaySerializer WorkDaySerializer} class
     * @return String
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
     * Returns the selected {@link WorkDay WorkDay} as a serialized {@link String String}
     * Uses {@link WorkDaySerializer WorkDaySerializer} as serializer
     * @param wantedYear
     * @param wantedMonth
     * @param wantedDay
     * @return String
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
     * Created a new {@link WorkDay WorkDay} object from the given {@link WorkDayRB workDayRB} object
     * Uses the {@link #monthSelector(tlog16rs.core.Entities.WorkMonth) monthSelector} method
     * @param day
     * @return
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
     * as a serialized {@link String String}
     * Uses the {@link TaskSerializer TaskSerializer} class
     * @return String
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
     * Starts a new {@link Task Task} object from the given {@link TaskRB TaskRB} object
     * Uses the {@link #monthSelector(tlog16rs.core.Entities.WorkMonth) monthSelector, 
     * {@link #daySelector(tlog16rs.core.Entities.WorkMonth, tlog16rs.core.Entities.WorkDay)  dayselector} methods
     * @param task
     * @return Task
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
     * Finishes the selected {@link Task task} with the given {@link TaskRB TaskRB's} properties
     * Uses the {@link #monthSelector(tlog16rs.core.Entities.WorkMonth)  monthSelector},
     * {@link #daySelector(tlog16rs.core.Entities.WorkMonth, tlog16rs.core.Entities.WorkDay)  daySelector}, 
     * {@link #taskSelector(tlog16rs.core.Entities.WorkDay, tlog16rs.core.Entities.Task)  taskSelector} methods
     * @param task
     * @return Task
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
     * Modifies the selected {@link Task Task}
     * Uses the {@link #monthSelector(tlog16rs.core.Entities.WorkMonth) nthSelector monthSelector},
     * {@link #daySelector(tlog16rs.core.Entities.WorkMonth, tlog16rs.core.Entities.WorkDay) daySelector}, 
     * {@link #taskSelector taskSelector}, {@link #createTask(tlog16rs.core.Util.ModifyTaskRB) createTask},
     * {@link #modifyThisTask(tlog16rs.core.Entities.Task, tlog16rs.core.Util.ModifyTaskRB) modifyThisTask} methods
     * @param task
     * @return Task
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
     * {@link DeleteTaskRB DeleteTaskRB}
     * Uses the {@link #monthSelector(tlog16rs.core.Entities.WorkMonth) nthSelector monthSelector},
     * {@link #daySelector(tlog16rs.core.Entities.WorkMonth, tlog16rs.core.Entities.WorkDay) daySelector}, 
     * {@link #taskSelector taskSelector}, {@link #createTask(tlog16rs.core.Util.ModifyTaskRB) createTask} methods
     * @param task
     * @return boolean
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
     * month list and gives it back
     * @param month
     * @return WorkMonth 
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
     * month list and gives it back
     * @param month
     * @param day
     * @return WorkDay
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
     * {@link WorkDay WorkDay's} task list
     * @param day
     * @param task
     * @return Task
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
     * Creates a new {@link Task Task} object from the given {@link ModifyTaskRB ModifyTaskRB}
     * @param task
     * @return Task
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
     * @param selected
     * @param modifier
     * @return
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
