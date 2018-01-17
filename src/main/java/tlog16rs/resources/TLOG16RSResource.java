package tlog16rs.resources;

import com.fasterxml.jackson.core.JsonProcessingException;
import java.time.LocalDate;
import javax.ws.rs.Consumes;
import javax.ws.rs.Path;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import lombok.extern.slf4j.Slf4j;
import tlog16rs.core.Entities.Task;
import tlog16rs.core.Entities.WorkDay;
import tlog16rs.core.Entities.WorkMonth;
import tlog16rs.core.Exceptions.EmptyTimeFieldException;
import tlog16rs.core.Exceptions.FutureWorkException;
import tlog16rs.core.Exceptions.InvalidTaskIdException;
import tlog16rs.core.Exceptions.NegativeMinutesOfWorkException;
import tlog16rs.core.Exceptions.NoTaskIdException;
import tlog16rs.core.Exceptions.NotExpectedTimeOrderException;
import tlog16rs.core.Exceptions.NotNewDateException;
import tlog16rs.core.Exceptions.NotNewMonthException;
import tlog16rs.core.Exceptions.NotSeparatedTimesException;
import tlog16rs.core.Exceptions.NotTheSameMonthException;
import tlog16rs.core.Exceptions.WeekendNotEnabledException;
import tlog16rs.core.Util.DeleteTaskRB;
import tlog16rs.core.Util.FinishTaskRB;
import tlog16rs.core.Util.ModifyTaskRB;
import tlog16rs.core.Util.Services;
import tlog16rs.core.Util.TaskRB;
import tlog16rs.core.Util.WorkDayRB;
import tlog16rs.core.Util.WorkMonthRB;

/**
 * 
 * Containt the endpoints od the application
 * <br> Responsible for logging the errors
 * @author Gyapi
 */
@Path("/timelogger")
@Produces(MediaType.TEXT_PLAIN)
@Slf4j
public class TLOG16RSResource {
    
    private final Services services = new Services();
    
    @GET
    @Path("/workmonths")    
    public String getAllMonths(){
        
        return services.getMonths();
        
    }
    
    @GET
    @Path("/workmonths/{year}/{month}")
    public String getSelectedMonth(@PathParam(value = "year") String year,
        @PathParam(value = "month") String month){
        
        String me = "";
        
        try {           
            me = services.getSelectedMonth(year, month);            
        } 
        catch (NumberFormatException | JsonProcessingException ex) {
            log.error("GET, getSelectedMonth : {}.{} : {}", year, month, ex.toString());
        }
        
        return me;
    }    
    
    @POST
    @Path("/workmonths")    
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public WorkMonth addNewMonth(WorkMonthRB month) {
        
        WorkMonth workMonth = new WorkMonth(month.getYear(), month.getMonth());
        try {
            services.getTimelogger().addMonth(workMonth);
        } 
        catch (NotNewMonthException ex) {
            log.error("POST, addNewMonth : {} : {} : {}", workMonth.getDate(), LocalDate.now(), ex.toString());
        }
        
        return workMonth;
    }

    @GET
    @Path("/workmonths/workdays")
    public String getAllDays(){
        
        return services.getDays();
        
    }
    
    @GET
    @Path("/workmonths/{year}/{month}/{day}")
    public String getSelectedDay(@PathParam(value = "year") String year,
        @PathParam(value = "month") String month, @PathParam(value = "day") String day){
        
        String me = "";
        
        try {        
            me = services.getSelectedDay(year, month, day);     
        } 
        catch (NumberFormatException | JsonProcessingException| FutureWorkException ex) {
            log.error("GET, getSelectedMonth : {}.{} : {}", year, month, ex.toString());
        }
        
        return me;
    }
    
    @PUT
    @Path("/workmonths/deleteall")
    public String deleteAllMonths(){
        
        String returnMe = "There is nothing here to delete";
        
        if (services.deleteAllMonths()){
            returnMe = "All clear";
        }
        
        return returnMe;
    }
    
    @POST
    @Path("/workmonths/workdays")    
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public WorkDay addNewWorkDay(WorkDayRB day){
        
        WorkDay returned = null;
        
        try {
            returned =  services.createDay(day);
        } 
        catch (NotNewMonthException | FutureWorkException | NotTheSameMonthException | 
                NotNewDateException | WeekendNotEnabledException | NegativeMinutesOfWorkException ex) {
            log.error("POST, addNewDay : {}.{}.{} : {} : {} : {}", day.getYear(), day.getMonth(), day.getDay(), 
                    day.isWeekEnd(), LocalDate.now(), ex.toString());
        }
        
        return returned;
    }
    
    @GET
    @Path("/workmonths/workdays/tasks")
    public String getAllTasks(){
        
        return services.getTasks();
        
    }
    
    @POST
    @Path("/workmonths/workdays/tasks/start")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)    
    public Task startTask(TaskRB task){
        
        Task returned = null;
        
        try {
            returned = services.starTask(task);
        } 
        catch (NotNewMonthException | FutureWorkException | NotTheSameMonthException |
                NotNewDateException | WeekendNotEnabledException | InvalidTaskIdException |
                NoTaskIdException | EmptyTimeFieldException | NotExpectedTimeOrderException |
                NotSeparatedTimesException ex) {
            log.error("POST, addNewTask : {} - {}.{}.{} - {} '{}' {}", task.getTaskId(), task.getYear(),
                    task.getMonth(), task.getDay(), task.getStartTime(), task.getComment(), ex.toString());
        }
        
        return returned;
    }
    
    @PUT    
    @Path("/workmonths/workdays/tasks/finish")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON) 
    public Task finishTask(FinishTaskRB task){        
                
        Task returned = null;
        
        try {        
            returned = services.finishThatThask(task);
        } 
        catch (NotNewMonthException | FutureWorkException | NotTheSameMonthException |
                NotNewDateException | WeekendNotEnabledException | InvalidTaskIdException | 
                NotExpectedTimeOrderException | EmptyTimeFieldException | NoTaskIdException | 
                NotSeparatedTimesException ex) {
            log.error("PUT, FinishTask : {} - {}.{}.{} - {} - {}  {}", task.getTaskId(), task.getYear(),
                    task.getMonth(), task.getDay(), task.getStartTime(), task.getEndTime(), ex.toString());
        }
        
        return returned;        
    }
    
    @PUT    
    @Path("/workmonths/workdays/tasks/modify")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON) 
    public Task modifyTask(ModifyTaskRB task){
        
        Task modifyMe = null;
        
        try {            
            modifyMe = services.modifyTask(task);            
        } 
        catch (NotNewMonthException | FutureWorkException | NotTheSameMonthException |
                NotNewDateException | WeekendNotEnabledException | InvalidTaskIdException | 
                NotExpectedTimeOrderException | EmptyTimeFieldException | NoTaskIdException | 
                NotSeparatedTimesException ex) {
            log.error("PUT, ModifyTask : {} - {}.{}.{} - {}  {}", task.getTaskId(), task.getYear(),
                    task.getMonth(), task.getDay(), task.getStartTime(), ex.toString());
        }
        
        return modifyMe;
    }
    
    @PUT    
    @Path("/workmonths/workdays/tasks/delete")
    @Consumes(MediaType.APPLICATION_JSON)
    public String removeTask(DeleteTaskRB task){
        
        String returnMe = "No such Task";
        
        try {            
            if(services.deleteThisTask(task)){
                returnMe = "Task deleted";
            }            
        } catch (FutureWorkException | InvalidTaskIdException | NoTaskIdException | 
                EmptyTimeFieldException |  NotExpectedTimeOrderException ex) {
            log.error("PUT, DeleteTask : {} - {}.{}.{} - {}  {}", task.getTaskId(), task.getYear(),
                    task.getMonth(), task.getDay(), task.getStartTime(), ex.toString());
        }
        
        return returnMe;
    }
}