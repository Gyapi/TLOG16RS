package tlog16rs.resources;

import com.fasterxml.jackson.core.JsonProcessingException;
import java.time.LocalDate;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.Consumes;
import javax.ws.rs.Path;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
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
import tlog16rs.core.Util.Services;
import tlog16rs.core.Util.TaskRB;
import tlog16rs.core.Util.WorkDayRB;
import tlog16rs.core.Util.WorkMonthRB;

@Path("/timelogger")
@Produces(MediaType.TEXT_PLAIN)
@Slf4j
public class TLOG16RSResource {
    
    private final Services services = new Services();
    
    @GET
    @Path("/workmonths")    
    public String getAllMonths(){
        
        return services.makeItString();
        
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
    @Path("/workdays")
    public String getAllDays(){
        
        return services.getDays();
        
    }
    
    @POST
    @Path("/workdays")    
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public WorkDay addNewWorkDay(WorkDayRB day){
        
        WorkDay returned = null;
        
        try {
            returned =  services.createDay(day);
        } 
        catch (NotNewMonthException | FutureWorkException | NotTheSameMonthException | 
                NotNewDateException | WeekendNotEnabledException ex) {
            log.error("POST, addNewDay : {}.{}.{} : {} : {} : {}", day.getYear(), day.getMonth(), day.getDay(), 
                    day.isWeekEnd(), LocalDate.now(), ex.toString());
        } catch (NegativeMinutesOfWorkException ex) {
            Logger.getLogger(TLOG16RSResource.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return returned;
    }
    
    @GET
    @Path("/workdays/tasks")
    public String getAllTasks(){
        
        return services.getTasks();
        
    }
    
    @POST
    @Path("/workdays/tasks/start")
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
    
    @GET
    @Path("/workmonths/{year}/{month}")
    public String getSelectedMonth(@PathParam(value = "year") String year,
        @PathParam(value = "month") String month){
        
        String me = "";
        
        try {           
            
            me = services.getSelectedMonth(year, month);
            
        } catch (NumberFormatException | JsonProcessingException ex) {
            log.error("GET, getSelectedMonth : {}.{} : {}", year, month, ex.toString());
        }
        
        return me;
    }
    
    @GET
    @Path("/workmonths/{year}/{month}/{day}")
    public String getSelectedDay(@PathParam(value = "year") String year,
        @PathParam(value = "month") String month, @PathParam(value = "day") String day){
        
        String me = "";
        
        try {           
            
            me = services.getSelectedDay(year, month, day);
            
        } 
        catch (NumberFormatException | JsonProcessingException ex) {
            log.error("GET, getSelectedMonth : {}.{} : {}", year, month, ex.toString());
        }
        
        return me;
    }
    
    //TODO: Javadoc, refactor hogy szebb legyen
    
    /*@GET
    public String getGreeting() {
        return "Hello world!";
    }    
    
    @Path("/{name}")
    @GET
    public String getNamedGreeting(@PathParam(value = "name") String name) {
        return "Hello " + name + "!";
    }
    
    @Path("/query_param")
    @GET
    public String getNamedStringWithParam(@DefaultValue("world") @QueryParam("name") String name) {
        return "Hello " + name;
    }    
        
    @Path("/hello_json")
    @GET    
    @Produces(MediaType.APPLICATION_JSON)
    public Greeting getJSONGreeting() {
        return new Greeting("Hello world!");
    }*/    
}