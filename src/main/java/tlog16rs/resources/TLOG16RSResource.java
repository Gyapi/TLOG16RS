package tlog16rs.resources;

import com.avaje.ebean.Ebean;
import com.fasterxml.jackson.core.JsonProcessingException;
import java.time.DateTimeException;
import java.time.LocalDate;
import javax.ws.rs.Consumes;
import javax.ws.rs.Path;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;
import tlog16rs.core.Entities.Task;
import tlog16rs.core.Entities.TestEntity;
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
        
        try {        
            return services.getMonths();
        } 
        catch (JsonProcessingException ex) {
            log.error("{} : @GET, getAllMonths : {}", LocalDate.now(), ex.toString());
            return LocalDate.now() + "\n@GET, getAllMonths\n" + ex.toString();
        }
    }
    
    @GET
    @Path("/workmonths/{year}/{month}")
    public String getSelectedMonth(@PathParam(value = "year") String year,
        @PathParam(value = "month") String month){
        
        try {           
            return services.getSelectedMonth(year, month);            
        } 
        catch (NumberFormatException | JsonProcessingException | DateTimeException ex) {
            log.error("{} : @GET, getSelectedMonth : {}.{} : {}", LocalDate.now(), year, month, ex.toString());
           return LocalDate.now() + "\n@GET getSelectedMonth : " + year + "." + month + "\n" + ex.toString();
        }
    }    
    
    @POST
    @Path("/workmonths")    
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addNewMonth(WorkMonthRB month) {
        
        WorkMonth workMonth = new WorkMonth(month.getYear(), month.getMonth());
        
        try {
            services.getTimelogger().addMonth(workMonth);
            return Response.ok(workMonth).build();
        } 
        catch (NotNewMonthException  | DateTimeException ex) {
            log.error("{} : @POST, addNewMonth : {} : {}", LocalDate.now(), workMonth.getDate(), ex.toString());
            return Response.status(409).entity("Status: 409\nConflict\n" + LocalDate.now().toString() + "\n" + 
                    "@POST, addNewMonth : " + workMonth.getDate().toString() + "\n" + ex.toString()).build();
        }
    }
    
    @PUT
    @Path("/workmonths/deleteall")
    public String deleteAllMonths(){
        
        return services.deleteAllMonths(); 
    }

    @GET
    @Path("/workmonths/workdays")
    public String getAllDays(){
        
        try {
            return services.getDays();
        }
        catch (JsonProcessingException ex) {
            log.error("{} : @GET, getAllDays : {}", LocalDate.now(), ex.toString());
            return LocalDate.now() + "\n@GET, getAllDays\n" + ex.toString();
        }        
    }
    
    @GET
    @Path("/workmonths/{year}/{month}/{day}")
    public String getSelectedDay(@PathParam(value = "year") String year,
        @PathParam(value = "month") String month, @PathParam(value = "day") String day){
        
        try {        
            return services.getSelectedDay(year, month, day);     
        } 
        catch (NumberFormatException | JsonProcessingException| FutureWorkException | DateTimeException ex) {
            log.error( "{} : @GET, getSelectedDay : {}.{}.{} : {}",LocalDate.now(), year, month, day, ex.toString());
            return LocalDate.now() + "\n@GET getSelectedDay : " + year + "." + month + "." + day + 
                    "\n" + ex.toString();
        }
    }
    
    @POST
    @Path("/workmonths/workdays")    
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addNewWorkDay(WorkDayRB day){
        
        try {
            WorkDay workDay =  services.createDay(day);
            return Response.ok(workDay).build();
        } 
        catch (NotNewMonthException | FutureWorkException | NotTheSameMonthException | 
                NotNewDateException | WeekendNotEnabledException | NegativeMinutesOfWorkException  
                | DateTimeException ex) {
            log.error("{} : @POST, addNewDay : {}.{}.{} : {} : {}", LocalDate.now(),
                    day.getYear(), day.getMonth(), day.getDay(), day.isWeekEnd(), ex.toString());
            return Response.status(409).entity("Status: 409\nConflict\n" + LocalDate.now().toString() + "\n" + 
                    "@POST, addNewMonth : " + day.getYear() + "." + day.getMonth() + "." + day.getDay() + 
                    "\n" + ex.toString()).build();
        }
    }
    
    @GET
    @Path("/workmonths/workdays/tasks")
    public String getAllTasks(){
        
        try {
            return services.getTasks();
        } 
        catch (JsonProcessingException ex) {
            log.error("{} : @GET, getAllTasks : {}", LocalDate.now(), ex.toString());
            return LocalDate.now() + "\n@GET, getAllTasks\n" + ex.toString();
        }        
    }
    
    @POST
    @Path("/workmonths/workdays/tasks/start")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)    
    public Response startTask(TaskRB task){
        
        try {
           Task startedTask = services.starTask(task);
           return Response.ok(startedTask).build();
        } 
        catch (NotNewMonthException | FutureWorkException | NotTheSameMonthException |
                NotNewDateException | WeekendNotEnabledException | InvalidTaskIdException |
                NoTaskIdException | EmptyTimeFieldException | NotExpectedTimeOrderException |
                NotSeparatedTimesException ex) {
            log.error("{} : @POST, startTask : {} - {}.{}.{} - {} '{}' {}", LocalDate.now(), 
                    task.getTaskId(), task.getYear(), task.getMonth(), task.getDay(), task.getStartTime(), 
                    task.getComment(), ex.toString());
            return Response.status(409).entity("Status: 409\nConflict\n" + LocalDate.now().toString() + "\n" + 
                    "@POST, startTask : " + task.getTaskId() + " : "  + task.getYear() + "." + task.getMonth() +
                    "." + task.getDay() + " : " +  task.getStartTime() + " : '" + task.getComment() + "'\n" +
                    ex.toString()).build();
        }
    }
    
    @PUT    
    @Path("/workmonths/workdays/tasks/finish")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON) 
    public Response finishTask(FinishTaskRB task){        
        
        try {        
            Task finishedTask = services.finishThatThask(task);
            return Response.ok(finishedTask).build();
        } 
        catch (NotNewMonthException | FutureWorkException | NotTheSameMonthException |
                NotNewDateException | WeekendNotEnabledException | InvalidTaskIdException | 
                NotExpectedTimeOrderException | EmptyTimeFieldException | NoTaskIdException | 
                NotSeparatedTimesException ex) {
            log.error("{} : @PUT, startTask : {} - {}.{}.{} - {} - {} : {}", LocalDate.now(), 
                    task.getTaskId(), task.getYear(), task.getMonth(), task.getDay(), task.getStartTime(), 
                    task.getEndTime(), ex.toString());
            return Response.status(409).entity("Status: 409\nConflict\n" + LocalDate.now().toString() + "\n" + 
                    "@PUT, startTask : " + task.getTaskId() + " : "  + task.getYear() + "." + task.getMonth() +
                    "." + task.getDay() + " : " +  task.getStartTime() + " - " + task.getEndTime() + "\n" +
                    ex.toString()).build();
        }  
    }
    
    @PUT    
    @Path("/workmonths/workdays/tasks/modify")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON) 
    public Response modifyTask(ModifyTaskRB task){
        
        try {            
            Task modifiedTask = services.modifyTask(task);    
            return Response.ok(modifiedTask).build();
        } 
        catch (NotNewMonthException | FutureWorkException | NotTheSameMonthException |
                NotNewDateException | WeekendNotEnabledException | InvalidTaskIdException | 
                NotExpectedTimeOrderException | EmptyTimeFieldException | NoTaskIdException | 
                NotSeparatedTimesException ex) {
            log.error("{} : @PUT, modifyTask : {} - {}.{}.{} - {} : {}", LocalDate.now(), 
                    task.getTaskId(), task.getYear(), task.getMonth(), task.getDay(), task.getStartTime(), 
                    ex.toString());
            return Response.status(409).entity("Status: 409\nConflict\n" + LocalDate.now().toString() + "\n" + 
                    "@PUT, modifyTask : " + task.getTaskId() + " : "  + task.getYear() + "." + task.getMonth() +
                    "." + task.getDay() + " : " +  task.getStartTime() + "\n" + ex.toString()).build();
        }
    }
    
    @PUT    
    @Path("/workmonths/workdays/tasks/delete")
    @Consumes(MediaType.APPLICATION_JSON)
    public String removeTask(DeleteTaskRB task){
        
        try {            
            if(services.deleteThisTask(task)){
                return "Task deleted";
            }   
            else{
                return "No such Task";
            }
        } catch (FutureWorkException | InvalidTaskIdException | NoTaskIdException | 
                EmptyTimeFieldException |  NotExpectedTimeOrderException ex) {
            log.error("{} : @PUT, removeTask : {} - {}.{}.{} - {} : {}", LocalDate.now(), task.getTaskId(),
                    task.getYear(), task.getMonth(), task.getDay(), task.getStartTime(), ex.toString());
            return "Status: 409\nConflict\n" + LocalDate.now().toString() + "\n" +  "@PUT, removeTask : " + 
                    task.getTaskId() + " : " + task.getStartTime() + "\n" + ex.toString();
            
        }
    }
    
    @POST
    @Path("/save/test")
    @Consumes(MediaType.TEXT_PLAIN)
    public String newTestEntity(String text){
        
        TestEntity test = new TestEntity(text);
        Ebean.save(test);
        
        return text;
    }
    
}