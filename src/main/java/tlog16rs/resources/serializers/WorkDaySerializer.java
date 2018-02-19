package tlog16rs.resources.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import java.io.IOException;
import tlog16rs.entities.Task;
import tlog16rs.entities.WorkDay;

/**
 * Maps the {@link WorkMonth WorkMonth} class.
 * <br> Made by using Jackson API.
 * 
 * @author Gyapi
 */
public class WorkDaySerializer  extends StdSerializer<WorkDay>{
    
    public WorkDaySerializer() {
        this(null);
    }
   
    public WorkDaySerializer(Class<WorkDay> t) {
        super(t);
    }

    @Override
        public void serialize(WorkDay day, JsonGenerator jgen, SerializerProvider provider) 
            throws IOException, JsonProcessingException {
        
        jgen.writeStartObject();
        jgen.writeStringField("ActualDay", day.getActualDay().toString());
        jgen.writeNumberField("Sum", day.getSumPerDay());
        jgen.writeNumberField("Required", day.getRequiredMinPerDay());
        jgen.writeNumberField("Extra", day.getExtraMinPerDay());
        if (!day.getTasks().isEmpty()){
            jgen.writeArrayFieldStart("Tasks");
            for (Task task : day.getTasks()){
                jgen.writeObject(task);
            }
            jgen.writeEndArray();
        }
        jgen.writeEndObject();        
    }
}
