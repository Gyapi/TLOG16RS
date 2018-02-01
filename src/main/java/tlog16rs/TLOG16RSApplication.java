package tlog16rs;

import tlog16rs.resources.TLOG16RSResource;
import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import java.sql.SQLException;
import liquibase.exception.LiquibaseException;
import tlog16rs.resources.utilities.TLOG16RSHealthCheck;
import tlog16rs.resources.CreateDatabase;

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
    }

    @Override
    public void run(final TLOG16RSConfiguration configuration,
                    final Environment environment) throws SQLException, LiquibaseException {
        //Database connection - Ebean server
        final CreateDatabase database = new CreateDatabase(configuration); 
        //HealthCheck
        environment.healthChecks().register("HealthCheck", new TLOG16RSHealthCheck(database));
        //Endpoints
        environment.jersey().register(new TLOG16RSResource());              
    }
}
