package tlog16rs;

import io.dropwizard.Configuration;
import org.hibernate.validator.constraints.NotEmpty;

/**
 * 
 * Basic applicaiton configuration.
 * <br>Gets the required information from ./src/dist/config.yml
 * <br>Next to it, there is a template file for Docker
 * 
 * @author Gyapi
 */
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
