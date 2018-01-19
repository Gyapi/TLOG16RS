package tlog16rs;

import tlog16rs.resources.TLOG16RSResource;
import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import tlog16rs.core.Util.TLOG16RSHealthCheck;

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
        environment.jersey().register(new TLOG16RSResource());
        //TODO: Kéne majd ide egy different name
        environment.healthChecks().register("dummy", new TLOG16RSHealthCheck());
    }
}
