package tlog16rs.resources.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import java.io.IOException;
import tlog16rs.entities.Task;

/**
 *
 * Maps the {@link Task Task} class.
 * <br> Made by using Jackson API.
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
            jgen.writeNumberField("MinPerTask", task.getMinPerTask());
        }
        jgen.writeEndObject();        
    }
    
}
    

