package tlog16rs.resources;

import com.avaje.ebean.EbeanServer;
import com.avaje.ebean.EbeanServerFactory;
import com.avaje.ebean.SqlRow;
import com.avaje.ebean.config.ServerConfig;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.LocalDate;
import liquibase.Contexts;
import liquibase.Liquibase;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.LiquibaseException;
import liquibase.resource.ClassLoaderResourceAccessor;
import lombok.extern.slf4j.Slf4j;
import org.avaje.datasource.DataSourceConfig;
import tlog16rs.TLOG16RSConfiguration;
import tlog16rs.entities.Task;
import tlog16rs.entities.TimeLogger;
import tlog16rs.entities.WorkDay;
import tlog16rs.entities.WorkMonth;


/**
 *
 * This class is responsible for creating, and managing the database connection.
 * <br> Uses Lombok's @SLf4j annotation for log message handling.
 * <br> @see <a href="https://projectlombok.org/">https://projectlombok.org/</a>
 * 
 * @author Gyapi
 */
@Slf4j
public class CreateDatabase {
    
    private DataSourceConfig dataSourceConfig;
    private ServerConfig serverConfig;
    private EbeanServer ebeanServer;

    /**
     * 
     * @param config : the {@link TLOG16RSConfiguration TLOG16RSConfiguration} object
     * 
     * @throws SQLException
     * @throws LiquibaseException 
     */
    public CreateDatabase(TLOG16RSConfiguration config) 
            throws SQLException, LiquibaseException {  
        
        updateSchema(config);
        initDataSourceConfig(config);
        initServerConfig(config);
        createServer();        
    } 
    
    /**
     * 
     * Responsible for updating the database's schematic trough liquibase.
     * Only updates if there are changes in migrations.xml
     * 
     * @param config : the {@link TLOG16RSConfiguration TLOG16RSConfiguration} object
     * 
     * @throws SQLException
     * @throws LiquibaseException 
     */
    private void updateSchema(TLOG16RSConfiguration config)
            throws SQLException, LiquibaseException{
        
        Connection connection = getConnection(config);        
        Liquibase liquibase = new Liquibase(
                "migrations.xml", 
                new ClassLoaderResourceAccessor(),
                new JdbcConnection(connection));  
        
        liquibase.update(new Contexts());
    }
    
    /**
     * 
     * Creates the database connection.
     * 
     * @param config : the {@link TLOG16RSConfiguration TLOG16RSConfiguration} object
     * 
     * @return
     * @throws SQLException 
     */
    private Connection getConnection(TLOG16RSConfiguration config) 
            throws SQLException{
        return DriverManager.getConnection(config.getDbUrl(), config.getDbUser(), config.getDbPassword());
    }
    
    /**
     * 
     * Sets up the database connection.
     * 
     * @param config : the {@link TLOG16RSConfiguration TLOG16RSConfiguration} object 
     */
    private void initDataSourceConfig(TLOG16RSConfiguration config){ 
        log.info("Setting up Database Connection");
        dataSourceConfig = new DataSourceConfig();
        dataSourceConfig.setDriver(config.getDbDriver());
        dataSourceConfig.setUrl(config.getDbUrl()); 
        dataSourceConfig.setUsername(config.getDbUser());
        dataSourceConfig.setPassword(config.getDbPassword());        
        log.info("Done");
    }   
    
    /**
     * 
     * Configures the Ebean server.
     * 
     * @param config : the {@link TLOG16RSConfiguration TLOG16RSConfiguration} object 
     */
    private void initServerConfig (TLOG16RSConfiguration config){
        log.info("Starting Ebean server");
        serverConfig = new ServerConfig();
        serverConfig.setName(config.getDbName());
        serverConfig.setDdlGenerate(false);
        serverConfig.setDdlRun(false);
        serverConfig.setRegister(true);
        serverConfig.setDataSourceConfig(dataSourceConfig);
        serverConfig.addClass(TimeLogger.class);
        serverConfig.addClass(WorkMonth.class);
        serverConfig.addClass(WorkDay.class);
        serverConfig.addClass(Task.class);
        serverConfig.setDefaultServer(true);
    }
    
    /**
     * 
     * Starts the Ebean server
     */
    private void createServer(){
        ebeanServer = EbeanServerFactory.create(serverConfig);
        log.info("Ebean server up and running.");
    }

    public EbeanServer getEbeanServer() {
        return ebeanServer;
    }  
    
    /**
     * Database HealthCheck method.
     * 
     * @return true if it is healthy
     */
    public boolean ping(){
        try{
            SqlRow row = ebeanServer.createSqlQuery("SELECT 1").findUnique();
            return row.isEmpty();
        }
        catch(Exception ex){
            log.error("{} : During database query(SELECT 1) something went wrong! : {}", 
                    LocalDate.now(), ex.toString());
            return true;
        }
    }
}
