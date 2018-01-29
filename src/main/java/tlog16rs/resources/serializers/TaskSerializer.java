/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tlog16rs.resources.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import tlog16rs.entities.Task;
import tlog16rs.exceptions.EmptyTimeFieldException;

/**
 *
 * @author Gyapi
 */
public class TaskSerializer extends StdSerializer<Task>{
    
    public TaskSerializer() {
        this(null);
    }
   
    public TaskSerializer(Class<Task> t) {
        super(t);
    }

    @Override
    public void serialize(Task task, JsonGenerator jgen, SerializerProvider provider) 
            throws IOException, JsonProcessingException {
        
        jgen.writeStartObject();
        jgen.writeStringField("ID", task.getTaskId());
        jgen.writeStringField("Comment", task.getComment());
        jgen.writeStringField("Start Time", task.getStartTime().toString());
        if (task.getEndTime() != null){
            jgen.writeStringField("End Time", task.getEndTime().toString());
            try {
                jgen.writeNumberField("MinPerTask", task.getMinPerTask());
            } 
            catch (EmptyTimeFieldException ex) {
                Logger.getLogger(TaskSerializer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        jgen.writeEndObject();        
    }
    
}
    

