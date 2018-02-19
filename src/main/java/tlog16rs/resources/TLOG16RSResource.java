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
import tlog16rs.entities.Task;
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
import tlog16rs.resources.RBobjects.DeleteTaskRB;
import tlog16rs.resources.RBobjects.FinishTaskRB;
import tlog16rs.resources.RBobjects.ModifyTaskRB;
import tlog16rs.resources.utilities.Services;
import tlog16rs.resources.RBobjects.TaskRB;
import tlog16rs.resources.RBobjects.WorkDayRB;
import tlog16rs.resources.RBobjects.WorkMonthRB;

/**
 * 
 * Contains the endpoints of the application.
 * <br>Responsible for logging the errors
 * <br> The error logging happens trough Lombok's @Slf4j
 * <br> @see <a href="https://projectlombok.org/">https://projectlombok.org/</a>
 * 
 * @author Gyapi
 */
@Path("/timelogger")
@Produces(MediaType.APPLICATION_JSON)
@Slf4j
public class TLOG16RSResource {
    
    private final Services services = new Services();
    
    @GET
    @Path("/workmonths")    
    public Response getAllMonths(){
        
        try {        
            return Response.ok(services.getMonths())
                    .header("Access-Control-Allow-Origin", "*")
                    .header("Access-Control-Allow-Methods", "GET")
                    .allow("OPTIONS")
                    .build();
        } 
        catch (JsonProcessingException ex) {
            log.error("{} : @GET, getAllMonths : {}", LocalDate.now(), ex.toString());
            return Response.status(500).entity("Status: 500\nInternal Server Error\n" + LocalDate.now().toString() + "\n" + 
                    "@GET, getAllMonths : " + "\n" + ex.toString())
                    .header("Access-Control-Allow-Origin", "*")
                    .header("Access-Control-Allow-Methods", "GET")
                    .allow("OPTIONS")
                    .build();
        }
    }
    
    @GET
    @Path("/workmonths/{year}/{month}")
    public Response getSelectedMonth(@PathParam(value = "year") String year,
        @PathParam(value = "month") String month){
        
        try {          
            return Response.ok(services.getSelectedMonth(year, month))
                    .header("Access-Control-Allow-Origin", "*")
                    .header("Access-Control-Allow-Methods", "GET")
                    .allow("OPTIONS")
                    .build();         
        } 
        catch (NumberFormatException | DateTimeException ex) {
            log.error("{} : @GET, getSelectedMonth : {}.{} : {}", LocalDate.now(), year, month, ex.toString());
           return Response.status(400).entity("Status: 400\nBad Request\n" + LocalDate.now().toString() + "\n" + 
                    "@GET getSelectedMonth : " + year + " " + month + "\n" + ex.toString())
                    .header("Access-Control-Allow-Origin", "*")
                    .header("Access-Control-Allow-Methods", "GET")
                    .allow("OPTIONS")
                    .build();
        }
        catch (JsonProcessingException ex) {
            log.error("{} : @GET, getSelectedMonth : {}.{} : {}", LocalDate.now(), year, month, ex.toString());
           return Response.status(500).entity("Status: 500\nInternal Server Error\n" + LocalDate.now().toString() 
                   + "\n@GET getSelectedMonth : " + year + " " + month + "\n" + ex.toString())
                    .header("Access-Control-Allow-Origin", "*")
                    .header("Access-Control-Allow-Methods", "GET")
                    .allow("OPTIONS")
                    .build();
        }
        
    }    
    
    @POST
    @Path("/workmonths")    
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addNewMonth(WorkMonthRB month) {
        
        WorkMonth workMonth = new WorkMonth(month.getYear(), month.getMonth());
        
        try {
            services.getTimelogger().addMonth(workMonth);
            Ebean.save(services.getTimelogger());
            return Response.ok(workMonth)
                    .header("Access-Control-Allow-Origin", "*")
                    .header("Access-Control-Allow-Methods", "POST")
                    .allow("OPTIONS")
                    .build();         
        } 
        catch (NotNewMonthException  ex) {
            log.error("{} : @POST, addNewMonth : {} : {}", LocalDate.now(), workMonth.getDate(), ex.toString());
            return Response.status(409).entity("Status: 409\nConflict\n" + LocalDate.now().toString() + "\n" + 
                    "@POST, addNewMonth : " + workMonth.getDate().toString() + "\n" + ex.toString()).build();
        }
        catch (DateTimeException ex) {
            log.error("{} : @POST, addNewMonth : {} : {}", LocalDate.now(), workMonth.getDate(), ex.toString());
            return Response.status(400).entity("Status: 400\nBad Rquest\n" + LocalDate.now().toString() + "\n" + 
                    "@POST, addNewMonth : " + workMonth.getDate().toString() + "\n" + ex.toString())
                    .header("Access-Control-Allow-Origin", "*")
                    .header("Access-Control-Allow-Methods", "POST")
                    .allow("OPTIONS")
                    .build();
        }
    }
    
    @PUT
    @Path("/workmonths/deleteall")
    public Response deleteAllMonths(){
        
        return Response.ok(services.deleteAllMonths())
                    .header("Access-Control-Allow-Origin", "*")
                    .header("Access-Control-Allow-Methods", "PUT")
                    .allow("OPTIONS")
                    .build();         
    }

    @GET
    @Path("/workmonths/workdays")
    public Response getAllDays(){        
        
        try {
            return Response.ok(services.getDays())
                    .header("Access-Control-Allow-Origin", "*")
                    .header("Access-Control-Allow-Methods", "GET")
                    .allow("OPTIONS")
                    .build();
        }
        catch (JsonProcessingException ex) {
            log.error("{} : @GET, getAllDays : {}", LocalDate.now(), ex.toString());
            return Response.status(500).entity("Status: 500\nInternal Server Error\n" + LocalDate.now().toString() +
                    "\n@GET, getAllDays\n" + ex.toString())
                    .header("Access-Control-Allow-Origin", "*")
                    .header("Access-Control-Allow-Methods", "GET")
                    .allow("OPTIONS")
                    .build(); 
        }        
    }
    
    @GET
    @Path("/workmonths/{year}/{month}/{day}")
    public Response getSelectedDay(@PathParam(value = "year") String year,
        @PathParam(value = "month") String month, @PathParam(value = "day") String day){
        
        try {    
            return Response.ok(services.getSelectedDay(year, month, day))
                    .header("Access-Control-Allow-Origin", "*")
                    .header("Access-Control-Allow-Methods", "GET")
                    .allow("OPTIONS")
                    .build();     
        } 
        catch (JsonProcessingException | NullPointerException ex) {
            log.error( "{} : @GET, getSelectedDay : {}.{}.{} : {}",LocalDate.now(), year, month, day, ex.toString());
            return Response.status(500).entity("Status: 500\nInternal Server Error\n" + LocalDate.now().toString() +
                    "\n@GET, getSelectedDay: " + year + "." + month + "." + day + "\n" + ex.toString())
                    .header("Access-Control-Allow-Origin", "*")
                    .header("Access-Control-Allow-Methods", "GET")
                    .allow("OPTIONS")
                    .build();
        }
        catch (NumberFormatException | FutureWorkException | DateTimeException | NegativeMinutesOfWorkException ex) {            
            log.error( "{} : @GET, getSelectedDay : {}.{}.{} : {}",LocalDate.now(), year, month, day, ex.toString());
            return Response.status(400).entity("Status: 400\nBad Request\n" + LocalDate.now().toString() +
                    "\n@GET, getSelectedDay: " + year + "." + month + "." + day + "\n" + ex.toString())
                    .header("Access-Control-Allow-Origin", "*")
                    .header("Access-Control-Allow-Methods", "GET")
                    .allow("OPTIONS")
                    .build();
        }
    }
    
    @POST
    @Path("/workmonths/workdays")    
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addNewWorkDay(WorkDayRB day){
        
        try {
            WorkDay workDay =  services.createDay(day);
            Ebean.save(services.getTimelogger());
            return Response.ok(workDay)
                    .header("Access-Control-Allow-Origin", "*")
                    .header("Access-Control-Allow-Methods", "POST")
                    .allow("OPTIONS")
                    .build(); 
        } 
        catch (NotNewMonthException | NotNewDateException ex) {
            log.error("{} : @POST, addNewDay : {}.{}.{} : {} : {}", LocalDate.now(),
                    day.getYear(), day.getMonth(), day.getDay(), day.isWeekEnd(), ex.toString());
            return Response.status(409).entity("Status: 409\nConflict\n" + LocalDate.now().toString() + "\n" + 
                    "@POST, addNewMonth : " + day.getYear() + "." + day.getMonth() + "." + day.getDay() + 
                    "\n" + ex.toString())
                    .header("Access-Control-Allow-Origin", "*")
                    .header("Access-Control-Allow-Methods", "POST")
                    .allow("OPTIONS")
                    .build(); 
        }
        catch (FutureWorkException | NotTheSameMonthException | WeekendNotEnabledException |
                NegativeMinutesOfWorkException | DateTimeException ex) {
            log.error("{} : @POST, addNewDay : {}.{}.{} : {} : {}", LocalDate.now(),
                    day.getYear(), day.getMonth(), day.getDay(), day.isWeekEnd(), ex.toString());
            return Response.status(400).entity("Status: 400\nBad Request\n" + LocalDate.now().toString() + "\n" + 
                    "@POST, addNewMonth : " + day.getYear() + "." + day.getMonth() + "." + day.getDay() + 
                    "\n" + ex.toString())
                    .header("Access-Control-Allow-Origin", "*")
                    .header("Access-Control-Allow-Methods", "POST")
                    .allow("OPTIONS")
                    .build(); 
        }
        catch (NullPointerException ex) {
            log.error("{} : @POST, addNewDay : {}.{}.{} : {} : {}", LocalDate.now(),
                    day.getYear(), day.getMonth(), day.getDay(), day.isWeekEnd(), ex.toString());
            return Response.status(500).entity("Status: 400\nInternal Server Error\n" + LocalDate.now().toString() +
                    "\n@POST, addNewMonth : " + day.getYear() + "." + day.getMonth() + "." + day.getDay() + 
                    "\n" + ex.toString())
                    .header("Access-Control-Allow-Origin", "*")
                    .header("Access-Control-Allow-Methods", "POST")
                    .allow("OPTIONS")
                    .build(); 
        }
    }
    
    @GET
    @Path("/workmonths/workdays/tasks")
    public Response getAllTasks(){
        
        try {
            return Response.ok(services.getTasks())
                    .header("Access-Control-Allow-Origin", "*")
                    .header("Access-Control-Allow-Methods", "GET")
                    .allow("OPTIONS")
                    .build();
        } 
        catch (JsonProcessingException ex) {
            log.error("{} : @GET, getAllTasks : {}", LocalDate.now(), ex.toString());
            return Response.status(500).entity("Status: 500\nInternal Server Error\n" + LocalDate.now() +
                    "\n@GET, getAllTasks\n" + ex.toString())
                    .header("Access-Control-Allow-Origin", "*")
                    .header("Access-Control-Allow-Methods", "GET")
                    .allow("OPTIONS")
                    .build();
        }        
    }
    
    @POST
    @Path("/workmonths/workdays/tasks/start")
    @Consumes(MediaType.APPLICATION_JSON)  
    public Response startTask(TaskRB task){
        
        try {
           Task startedTask = services.starTask(task);
           Ebean.save(services.getTimelogger());
           return Response.ok(startedTask)
                    .header("Access-Control-Allow-Origin", "*")
                    .header("Access-Control-Allow-Methods", "POST")
                    .allow("OPTIONS")
                    .build();
        } 
        catch (NotNewMonthException | NotNewDateException ex) {
            log.error("{} : @POST, startTask : {} - {}.{}.{} - {} '{}' {}", LocalDate.now(), 
                    task.getTaskId(), task.getYear(), task.getMonth(), task.getDay(), task.getStartTime(), 
                    task.getComment(), ex.toString());
            return Response.status(409).entity("Status: 409\nConflict\n" + LocalDate.now().toString() + "\n" + 
                    "@POST, startTask : " + task.getTaskId() + " : "  + task.getYear() + "." + task.getMonth() +
                    "." + task.getDay() + " : " +  task.getStartTime() + " : '" + task.getComment() + "'\n" +
                    ex.toString())
                    .header("Access-Control-Allow-Origin", "*")
                    .header("Access-Control-Allow-Methods", "POST")
                    .allow("OPTIONS")
                    .build();
        }
        catch (FutureWorkException | NotTheSameMonthException | WeekendNotEnabledException | 
                InvalidTaskIdException | NoTaskIdException | EmptyTimeFieldException | 
                NotExpectedTimeOrderException | NotSeparatedTimesException | NegativeMinutesOfWorkException ex) {
            log.error("{} : @POST, startTask : {} - {}.{}.{} - {} '{}' {}", LocalDate.now(), 
                    task.getTaskId(), task.getYear(), task.getMonth(), task.getDay(), task.getStartTime(), 
                    task.getComment(), ex.toString());
            return Response.status(409).entity("Status: 400\nBad Request\n" + LocalDate.now().toString() + "\n" + 
                    "@POST, startTask : " + task.getTaskId() + " : "  + task.getYear() + "." + task.getMonth() +
                    "." + task.getDay() + " : " +  task.getStartTime() + " : '" + task.getComment() + "'\n" +
                    ex.toString())
                    .header("Access-Control-Allow-Origin", "*")
                    .header("Access-Control-Allow-Methods", "POST")
                    .allow("OPTIONS")
                    .build();
        }
        catch (NullPointerException ex) {
            log.error("{} : @POST, startTask : {} - {}.{}.{} - {} '{}' {}", LocalDate.now(), 
                    task.getTaskId(), task.getYear(), task.getMonth(), task.getDay(), task.getStartTime(), 
                    task.getComment(), ex.toString());
            return Response.status(409).entity("Status: 500\nInternal Server Error\n" + LocalDate.now().toString() +
                    "\n@POST, startTask : " + task.getTaskId() + " : "  + task.getYear() + "." + task.getMonth() +
                    "." + task.getDay() + " : " +  task.getStartTime() + " : '" + task.getComment() + "'\n" +
                    ex.toString())
                    .header("Access-Control-Allow-Origin", "*")
                    .header("Access-Control-Allow-Methods", "POST")
                    .allow("OPTIONS")
                    .build();
        }
    }
    
    @PUT    
    @Path("/workmonths/workdays/tasks/finish")
    @Consumes(MediaType.APPLICATION_JSON) 
    public Response finishTask(FinishTaskRB task){        
        
        try {        
            Task finishedTask = services.finishThatThask(task);
            Ebean.update(services.getTimelogger());
            return Response.ok(finishedTask)
                    .header("Access-Control-Allow-Origin", "*")
                    .header("Access-Control-Allow-Methods", "PUT")
                    .allow("OPTIONS")
                    .build();
        } 
        catch (NotNewMonthException | NotNewDateException ex) {
            log.error("{} : @PUT, startTask : {} - {}.{}.{} - {} - {} : {}", LocalDate.now(), 
                    task.getTaskId(), task.getYear(), task.getMonth(), task.getDay(), task.getStartTime(), 
                    task.getEndTime(), ex.toString());
            return Response.status(409).entity("Status: 409\nConflict\n" + LocalDate.now().toString() + "\n" + 
                    "@PUT, startTask : " + task.getTaskId() + " : "  + task.getYear() + "." + task.getMonth() +
                    "." + task.getDay() + " : " +  task.getStartTime() + " - " + task.getEndTime() + "\n" +
                    ex.toString())
                    .header("Access-Control-Allow-Origin", "*")
                    .header("Access-Control-Allow-Methods", "PUT")
                    .allow("OPTIONS")
                    .build();
        }   
        catch (FutureWorkException | NotTheSameMonthException | WeekendNotEnabledException |
                InvalidTaskIdException | NotExpectedTimeOrderException | EmptyTimeFieldException |
                NoTaskIdException | NotSeparatedTimesException | NegativeMinutesOfWorkException ex) {
            log.error("{} : @PUT, startTask : {} - {}.{}.{} - {} - {} : {}", LocalDate.now(), 
                    task.getTaskId(), task.getYear(), task.getMonth(), task.getDay(), task.getStartTime(), 
                    task.getEndTime(), ex.toString());
            return Response.status(400).entity("Status: 400\nBad Request\n" + LocalDate.now().toString() + "\n" + 
                    "@PUT, startTask : " + task.getTaskId() + " : "  + task.getYear() + "." + task.getMonth() +
                    "." + task.getDay() + " : " +  task.getStartTime() + " - " + task.getEndTime() + "\n" +
                    ex.toString())
                    .header("Access-Control-Allow-Origin", "*")
                    .header("Access-Control-Allow-Methods", "PUT")
                    .allow("OPTIONS")
                    .build();
        }    
        catch (NullPointerException ex) {
            log.error("{} : @PUT, startTask : {} - {}.{}.{} - {} - {} : {}", LocalDate.now(), 
                    task.getTaskId(), task.getYear(), task.getMonth(), task.getDay(), task.getStartTime(), 
                    task.getEndTime(), ex.toString());
            return Response.status(400).entity("Status: 500\nInternal Server Error\n" + LocalDate.now().toString() +
                    "\n@PUT, startTask : " + task.getTaskId() + " : "  + task.getYear() + "." + task.getMonth() +
                    "." + task.getDay() + " : " +  task.getStartTime() + " - " + task.getEndTime() + "\n" +
                    ex.toString())
                    .header("Access-Control-Allow-Origin", "*")
                    .header("Access-Control-Allow-Methods", "PUT")
                    .allow("OPTIONS")
                    .build();
        } 
    }
    
    @PUT    
    @Path("/workmonths/workdays/tasks/modify")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response modifyTask(ModifyTaskRB task){
        
        try {            
            Task modifiedTask = services.modifyTask(task); 
            Ebean.update(services.getTimelogger());   
            return Response.ok(modifiedTask)
                    .header("Access-Control-Allow-Origin", "*")
                    .header("Access-Control-Allow-Methods", "PUT")
                    .allow("OPTIONS")
                    .build();
        } 
        catch (NotNewMonthException | NotNewDateException ex) {
            log.error("{} : @PUT, modifyTask : {} - {}.{}.{} - {} : {}", LocalDate.now(), 
                    task.getTaskId(), task.getYear(), task.getMonth(), task.getDay(), task.getStartTime(), 
                    ex.toString());
            return Response.status(409).entity("Status: 409\nConflict\n" + LocalDate.now().toString() + "\n" + 
                    "@PUT, modifyTask : " + task.getTaskId() + " : "  + task.getYear() + "." + task.getMonth() +
                    "." + task.getDay() + " : " +  task.getStartTime() + "\n" + ex.toString())
                    .header("Access-Control-Allow-Origin", "*")
                    .header("Access-Control-Allow-Methods", "PUT")
                    .allow("OPTIONS")
                    .build();
        } 
        catch (FutureWorkException | NotTheSameMonthException | WeekendNotEnabledException | 
                InvalidTaskIdException | NotExpectedTimeOrderException | EmptyTimeFieldException |
                NoTaskIdException | NotSeparatedTimesException | NegativeMinutesOfWorkException ex) {
            log.error("{} : @PUT, modifyTask : {} - {}.{}.{} - {} : {}", LocalDate.now(), 
                    task.getTaskId(), task.getYear(), task.getMonth(), task.getDay(), task.getStartTime(), 
                    ex.toString());
            return Response.status(400).entity("Status: 400\nBad Request\n" + LocalDate.now().toString() + "\n" + 
                    "@PUT, modifyTask : " + task.getTaskId() + " : "  + task.getYear() + "." + task.getMonth() +
                    "." + task.getDay() + " : " +  task.getStartTime() + "\n" + ex.toString())
                    .header("Access-Control-Allow-Origin", "*")
                    .header("Access-Control-Allow-Methods", "PUT")
                    .allow("OPTIONS")
                    .build();
        } 
        catch (NullPointerException ex) {
            log.error("{} : @PUT, modifyTask : {} - {}.{}.{} - {} : {}", LocalDate.now(), 
                    task.getTaskId(), task.getYear(), task.getMonth(), task.getDay(), task.getStartTime(), 
                    ex.toString());
            return Response.status(400).entity("Status: 500\nInternal Server Error\n" + LocalDate.now().toString() +
                    "\n@PUT, modifyTask : " + task.getTaskId() + " : "  + task.getYear() + "." + task.getMonth() +
                    "." + task.getDay() + " : " +  task.getStartTime() + "\n" + ex.toString())
                    .header("Access-Control-Allow-Origin", "*")
                    .header("Access-Control-Allow-Methods", "PUT")
                    .allow("OPTIONS")
                    .build();
        }
    }
    
    @PUT    
    @Path("/workmonths/workdays/tasks/delete")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response removeTask(DeleteTaskRB task){
        
        try {            
            if(services.deleteThisTask(task)){
                Ebean.update(services.getTimelogger());
                return Response.ok("Task deleted")
                    .header("Access-Control-Allow-Origin", "*")
                    .header("Access-Control-Allow-Methods", "PUT")
                    .allow("OPTIONS")
                    .build();
            }   
            else{
                return Response.ok("No such Task")
                    .header("Access-Control-Allow-Origin", "*")
                    .header("Access-Control-Allow-Methods", "PUT")
                    .allow("OPTIONS")
                    .build();
            }
        } 
        catch (FutureWorkException | InvalidTaskIdException | NoTaskIdException | 
                EmptyTimeFieldException |  NotExpectedTimeOrderException | NegativeMinutesOfWorkException ex) {
            log.error("{} : @PUT, removeTask : {} - {}.{}.{} - {} : {}", LocalDate.now(), task.getTaskId(),
                    task.getYear(), task.getMonth(), task.getDay(), task.getStartTime(), ex.toString());
            return Response.status(400).entity("Status: 400\nBad Request\n" + LocalDate.now().toString() + "\n"
                    +  "@PUT, removeTask : " + task.getTaskId() + " : " + task.getStartTime() +
                    "\n" + ex.toString())
                    .header("Access-Control-Allow-Origin", "*")
                    .header("Access-Control-Allow-Methods", "PUT")
                    .allow("OPTIONS")
                    .build();
            
        } 
        catch (NullPointerException ex) {
            log.error("{} : @PUT, removeTask : {} - {}.{}.{} - {} : {}", LocalDate.now(), task.getTaskId(),
                    task.getYear(), task.getMonth(), task.getDay(), task.getStartTime(), ex.toString());
            return Response.status(400).entity("Status: 500\nInternal Server Error\n" + LocalDate.now().toString() +
                    "\n@PUT, removeTask : " + task.getTaskId() + " : " + task.getStartTime() +
                    "\n" + ex.toString())
                    .header("Access-Control-Allow-Origin", "*")
                    .header("Access-Control-Allow-Methods", "PUT")
                    .allow("OPTIONS")
                    .build();
            
        }
    }    
}