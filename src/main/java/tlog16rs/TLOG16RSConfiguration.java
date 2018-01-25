package tlog16rs;

import io.dropwizard.Configuration;
import org.hibernate.validator.constraints.NotEmpty;

@lombok.Getter
@lombok.Setter
public class TLOG16RSConfiguration extends Configuration {
    
    @NotEmpty    
    protected String dbDriver;
            
    @NotEmpty    
    protected String dbUrl;
    
    @NotEmpty    
    protected String dbUser;
    
    @NotEmpty    
    protected String dbPassword;
    
    @NotEmpty    
    protected String dbName;
}
