/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tlog16rs.resources.RBobjects;

/**
 *
 * @author Gyapi
 */
@lombok.NoArgsConstructor
@lombok.Getter
@lombok.Setter
public class TaskRB {
    
    int year;
    int month;
    int day;
    String taskId;
    String startTime;
    String comment;
    
}
