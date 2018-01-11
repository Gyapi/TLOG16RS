/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tlog16rs.core.Util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;
import java.time.LocalTime;
import lombok.extern.slf4j.Slf4j;
import tlog16rs.core.Entities.Task;
import tlog16rs.core.Entities.TimeLogger;
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

/**
 *
 * @author Gyapi
 */
@lombok.Getter
@lombok.Setter
@Slf4j
public class Services {
    
    private final TimeLogger timelogger;

    public Services() {
        timelogger = new TimeLogger();
        createContent();
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
        } 
        catch (EmptyTimeFieldException | FutureWorkException | InvalidTaskIdException |
                NegativeMinutesOfWorkException | NoTaskIdException | NotExpectedTimeOrderException | 
                NotNewDateException | NotNewMonthException | NotSeparatedTimesException | 
                NotTheSameMonthException | WeekendNotEnabledException exception) {
            System.out.println(exception);
        }
        //---------------------------------------------
    }
    
    public String makeItString(){
        
        String returnMe = "";
        ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

        for (WorkMonth month : timelogger.getMonths()){
            try{
                returnMe += objectMapper.writeValueAsString(month) + "\n\n";
            }
            catch (JsonProcessingException ex){
                log.error("{} : {}", LocalDate.now(), ex.toString());
            }
        }

        if (returnMe.isEmpty()){
            returnMe = "Nothing is here";
        }

        return returnMe;
    }

    public String getDays() {

        String returnMe = "";
        ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

        for (WorkMonth month : timelogger.getMonths()){
            for (WorkDay day : month.getDays()){
                try {          
                    returnMe += objectMapper.writeValueAsString(day) + "\n\n";
                } 
                catch (JsonProcessingException ex) {
                    log.error("{} : {}", LocalDate.now(), ex.toString());
                }        
            }
            returnMe += "\n";
        }
            
        if (returnMe.isEmpty()){
            returnMe = "Nothing is here";
        }

        return returnMe;  
    }

    public WorkDay createDay(WorkDayRB day) 
            throws NotNewMonthException, FutureWorkException, NotTheSameMonthException,
            NotTheSameMonthException, NotNewDateException, NotNewDateException, WeekendNotEnabledException,
            NegativeMinutesOfWorkException {
        WorkMonth month = new WorkMonth(day.getYear(), day.getMonth());
        WorkDay newDay = null;
        
        if (timelogger.isNewMonth(month)){
            timelogger.addMonth(month);
        }
        
        if (day.getRequiredHours() == 0){
            newDay = new WorkDay(day.getYear(), day.getMonth(), day.getDay());            
        }
        else{
            newDay = new WorkDay(day.getRequiredHours(), day.getYear(), day.getMonth(), day.getDay());
        }
        
        for (WorkMonth selected : timelogger.getMonths()){
            if (selected.getDate().equals(month.getDate())){
                if (day.isWeekEnd()){
                    selected.addWorkDay(newDay, true);
                }
                else{
                    selected.addWorkDay(newDay);
                }
            }
        }
        
        return newDay;
    }
    
    public String getTasks(){

        String returnMe = "";
        ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

        for (WorkMonth month : timelogger.getMonths()){
            for (WorkDay day : month.getDays()){
                for (Task task: day.getTasks()){
                    try {
                        returnMe += objectMapper.writeValueAsString(task) + "\n\n";
                    }
                    catch (JsonProcessingException ex) {
                        log.error("{} : {}", LocalDate.now(), ex.toString());
                    }
                }
                returnMe += "\n";  
            }
            returnMe += "\n";
        }
            
        if (returnMe.isEmpty()){
            returnMe = "Nothing is here";
        }

        return returnMe;  
    }
    
    public Task starTask(TaskRB task) 
            throws NotNewMonthException, FutureWorkException, NotTheSameMonthException, 
            NotNewDateException, WeekendNotEnabledException, InvalidTaskIdException,
            NoTaskIdException, EmptyTimeFieldException, NotExpectedTimeOrderException,
            NotSeparatedTimesException{
        
        WorkMonth month = new WorkMonth(task.getYear(), task.getMonth());
        if (timelogger.isNewMonth(month)){
            timelogger.addMonth(month);
        }
        
        //TODO: Ezt mindeképp újnak veszi te fasz
        WorkDay day = new WorkDay(task.getYear(), task.getMonth(), task.getDay());
        if (month.isNewDate(day)){
            month.addWorkDay(day);            
        }
        
        Task newTask = new Task(task.getTaskId());        
        newTask.setComment(task.getComment());
        newTask.setStartTime(task.getStartTime());
        
        day.addTask(newTask);
        
        return newTask;        
    }
    
    public String getSelectedMonth(String wantedYear, String wantedMonth)
        throws NumberFormatException, JsonProcessingException{
        
        String returnMe = "No such month exists";
        ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();
        
        WorkMonth selectedMonth = new WorkMonth(Integer.parseInt(wantedYear),
                Integer.parseInt(wantedMonth));
        
        for (WorkMonth month : timelogger.getMonths()){
            if (month.getDate().equals(selectedMonth.getDate())){
                returnMe = objectMapper.writeValueAsString(month);
            }
        }
        
        return returnMe;
    }
    
    public String getSelectedDay(String wantedYear, String wantedMonth, String wantedDay)
        throws NumberFormatException, JsonProcessingException{
        
        String returnMe = "No such day exists";
        ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();
        
        WorkMonth selectedMonth = new WorkMonth(Integer.parseInt(wantedYear),
                Integer.parseInt(wantedMonth));
        
        for (WorkMonth month : timelogger.getMonths()){
            if (month.getDate().equals(selectedMonth.getDate())){
                for (WorkDay day : month.getDays()){
                    if (day.getActualDay().getDayOfMonth() == Integer.parseInt(wantedDay)){
                        returnMe = objectMapper.writeValueAsString(day);
                    }
                }
            }
        }
        
        return returnMe;
    }

    public Task finishThatThask(FinishTaskRB task) 
            throws NotNewMonthException, FutureWorkException, NotTheSameMonthException,
            NotNewDateException, WeekendNotEnabledException, InvalidTaskIdException,
            NotExpectedTimeOrderException, EmptyTimeFieldException, NoTaskIdException, NotSeparatedTimesException {        
        
        WorkMonth month = new WorkMonth(task.getYear(), task.getMonth());
        
        if (timelogger.isNewMonth(month)){
            timelogger.addMonth(month);
        }
        else {
            for (WorkMonth selectedMonth: timelogger.getMonths()){
                if (selectedMonth.getDate().equals(month.getDate())){
                    month = selectedMonth;
                    break;
                }
            }
        }
        
        WorkDay day = new WorkDay(task.getYear(), task.getMonth(), task.getDay());
        if (month.isNewDate(day)){
            month.addWorkDay(day);
        }
        else{
            for (WorkDay selectedDay : month.getDays()){
                if (selectedDay.getActualDay().equals(day.getActualDay())){
                    day = selectedDay;
                    break;
                }
            }
        }
        
        Task modifyThisTask = new Task(task.getTaskId(), "", task.getStartTime(), task.getEndTime());
        boolean isItNew = true;        
        if (day.getTasks().isEmpty()){
            day.addTask(modifyThisTask);
        }
        else {
            for (Task selectedTask : day.getTasks()){
                if (selectedTask.getStartTime().equals(modifyThisTask.getStartTime()) && 
                        selectedTask.getTaskId().equals(modifyThisTask.getTaskId())){
                    isItNew = false;
                    selectedTask.setEndTime(modifyThisTask.getEndTime());
                }
            }
        }
        
        if (isItNew){
           day.addTask(modifyThisTask); 
        }
        
        return modifyThisTask;
    }    

    public Task modifyTask(ModifyTaskRB task) 
            throws NotNewMonthException, FutureWorkException, NotTheSameMonthException, 
            NotNewDateException, WeekendNotEnabledException, InvalidTaskIdException, 
            NotExpectedTimeOrderException, EmptyTimeFieldException, NoTaskIdException,
            NotSeparatedTimesException {
        
        WorkMonth month = new WorkMonth(task.getYear(), task.getMonth());
        
        if (timelogger.isNewMonth(month)){
            timelogger.addMonth(month);
        }
        else {
            for (WorkMonth selectedMonth: timelogger.getMonths()){
                if (selectedMonth.getDate().equals(month.getDate())){
                    month = selectedMonth;
                    break;
                }
            }
        }
        
        WorkDay day = new WorkDay(task.getYear(), task.getMonth(), task.getDay());
        if (month.isNewDate(day)){
            month.addWorkDay(day);            
        }
        else{
            for (WorkDay selectedDay : month.getDays()){
                if (selectedDay.getActualDay().equals(day.getActualDay())){
                    day = selectedDay;
                    break;
                }
            }
        }
        
        Task modified = null;     
        LocalTime originalStart = LocalTime.parse(task.getStartTime());
        boolean isItNew = true;        
        if (day.getTasks().isEmpty()){
            modified = createTask(task);
            day.addTask(modified);
        }
        else {
            for (Task selectedTask : day.getTasks()){
                if (selectedTask.getStartTime().equals(originalStart) && 
                        selectedTask.getTaskId().equals(task.getTaskId())){
                    isItNew = false;
                    modified = modifyThisTask(selectedTask, task);
                    break;
                }
            }
        }
        
        if (isItNew){
            modified = createTask(task);
            day.addTask(modified);
        }
        
        return modified;        
    }
    
    private Task createTask(ModifyTaskRB task)
            throws InvalidTaskIdException, NoTaskIdException, 
            EmptyTimeFieldException, NotExpectedTimeOrderException {
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
    
    private Task modifyThisTask(Task selected, ModifyTaskRB modifier)
            throws EmptyTimeFieldException, NotExpectedTimeOrderException{
        
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
    
    public boolean deleteThisTask(DeleteTaskRB task) 
            throws FutureWorkException, InvalidTaskIdException, NoTaskIdException{
        
        WorkMonth month = new WorkMonth(task.getYear(), task.getMonth());
        
        if (timelogger.isNewMonth(month)){
            return false;
        }
        else {
            for (WorkMonth selectedMonth: timelogger.getMonths()){
                if (selectedMonth.getDate().equals(month.getDate())){
                    month = selectedMonth;
                    break;
                }
            }
        }
        
        WorkDay day = new WorkDay(task.getYear(), task.getMonth(), task.getDay());
        if (month.isNewDate(day)){
            return false;
        }
        else{
            for (WorkDay selectedDay : month.getDays()){
                if (selectedDay.getActualDay().equals(day.getActualDay())){
                    day = selectedDay;
                    break;
                }
            }
        }
        
        Task deleteThis = new Task(task.getTaskId());        
        LocalTime originalStart = LocalTime.parse(task.getStartTime());
        for (Task selected : day.getTasks()) {
            if (selected.getStartTime().equals(originalStart) && 
                        selected.getTaskId().equals(task.getTaskId())){
                day.getTasks().remove(selected);
                selected = null;
                return true;
            }
        }
        
        return false;       
    }
    
}
