package tlog16rs;

import tlog16rs.resources.TLOG16RSResource;
import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import java.sql.SQLException;
import liquibase.exception.LiquibaseException;
import lombok.extern.slf4j.Slf4j;
import tlog16rs.core.Util.TLOG16RSHealthCheck;
import tlog16rs.resources.CreateDatabase;

@Slf4j
public class TLOG16RSApplication extends Application<TLOG16RSConfiguration> {

    public static void main(final String[] args) throws Exception {
        new TLOG16RSApplication().run(args);
    }

    @Override
    public String getName() {
        return "TLOG16RS";
    }

    @Override
    public void initialize(final Bootstrap<TLOG16RSConfiguration> bootstrap) {
        // TODO: application initialization
    }

    @Override
    public void run(final TLOG16RSConfiguration configuration,
                    final Environment environment) {
        //Endpoints
        environment.jersey().register(new TLOG16RSResource());        
        //HealthCheck
        environment.healthChecks().register("HealthCheck", new TLOG16RSHealthCheck());
        try {
            //Database
            CreateDatabase database = new CreateDatabase(configuration);
        } 
        catch (SQLException | LiquibaseException ex) {
           log.error(ex.toString());
        }        
    }
}
