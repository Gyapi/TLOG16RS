/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tlog16rs.resources;

import com.avaje.ebean.EbeanServer;
import com.avaje.ebean.EbeanServerFactory;
import com.avaje.ebean.config.ServerConfig;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import liquibase.Contexts;
import liquibase.Liquibase;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.LiquibaseException;
import liquibase.resource.ClassLoaderResourceAccessor;
import lombok.extern.slf4j.Slf4j;
import org.avaje.datasource.DataSourceConfig;
import tlog16rs.TLOG16RSConfiguration;
import tlog16rs.entities.TimeLogger;


/**
 *
 * @author Gyapi
 */
@Slf4j
public class CreateDatabase {
    
    private DataSourceConfig dataSourceConfig;
    private ServerConfig serverConfig;
    private EbeanServer ebeanServer;

    public CreateDatabase(TLOG16RSConfiguration config) 
            throws SQLException, LiquibaseException {
       
        updateSchema(config);
        initDataSourceConfig(config);
        initServerConfig (config);
        createServer();
        
    } 
    
    private void updateSchema(TLOG16RSConfiguration config)
            throws SQLException, LiquibaseException{
        
        Connection connection = getConnection(config);        
        Liquibase liquibase = new Liquibase("migrations.xml", new ClassLoaderResourceAccessor(),
                new JdbcConnection(connection));        
         liquibase.update(new Contexts());
    }
    
    private Connection getConnection(TLOG16RSConfiguration config) 
            throws SQLException{
        return DriverManager.getConnection(config.getDbUrl(), config.getDbUser(), config.getDbPassword());
    }
    
    private void initDataSourceConfig(TLOG16RSConfiguration config){        
        dataSourceConfig = new DataSourceConfig();
        dataSourceConfig.setDriver(config.getDbDriver());
        dataSourceConfig.setUrl(config.getDbUrl()); 
        dataSourceConfig.setUsername(config.getDbUser());
        dataSourceConfig.setPassword(config.getDbPassword());
    }
    
    private void initServerConfig (TLOG16RSConfiguration config){
        serverConfig = new ServerConfig();
        serverConfig.setName(config.getDbName());
        serverConfig.setDdlGenerate(false);
        serverConfig.setDdlRun(false);
        serverConfig.setRegister(true);
        serverConfig.setDataSourceConfig(dataSourceConfig);
        serverConfig.addClass(TimeLogger.class);
        serverConfig.setDefaultServer(true);
    }
    
    private void createServer(){
        ebeanServer = EbeanServerFactory.create(serverConfig);
    }
    
    public boolean ping(){
        //TODO: Hibernate get metod database halthcheckelni
        return true;
    }
}