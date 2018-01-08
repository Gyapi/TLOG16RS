/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tlog16rs.core.Util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;
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
    
}
