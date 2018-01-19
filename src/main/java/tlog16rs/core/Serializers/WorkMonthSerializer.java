/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tlog16rs.core.Serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import java.io.IOException;
import tlog16rs.core.Entities.WorkMonth;

/**
 *
 * @author Gyapi
 */
public class WorkMonthSerializer extends StdSerializer<WorkMonth>{
    
    public WorkMonthSerializer() {
        this(null);
    }
   
    public WorkMonthSerializer(Class<WorkMonth> t) {
        super(t);
    }

    @Override
    public void serialize(WorkMonth month, JsonGenerator jgen, SerializerProvider provider) 
            throws IOException, JsonProcessingException {
        
        ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();
        String days = objectMapper.writeValueAsString(month.getDays());
                
        jgen.writeStartObject();
        jgen.writeNumberField("Year", month.getDate().getYear());
        jgen.writeStringField("Month", month.getDate().getMonth().toString());
        jgen.writeNumberField("Sum", month.getSumPerMonth());
        jgen.writeNumberField("Required", month.getRequiredMinPerMonth());
        jgen.writeNumberField("Extra", month.getExtraMinPerMonth());        
        jgen.writeStringField("Days:", days);
        jgen.writeEndObject();        
    }
}
