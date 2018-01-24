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
import java.util.logging.Level;
import java.util.logging.Logger;
import liquibase.Contexts;
import liquibase.Liquibase;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.LiquibaseException;
import liquibase.resource.ClassLoaderResourceAccessor;
import lombok.extern.slf4j.Slf4j;
import org.avaje.datasource.DataSourceConfig;
import tlog16rs.core.Entities.TestEntity;


/**
 *
 * @author Gyapi
 */
@Slf4j
public class CreateDatabase {
    
    private DataSourceConfig dataSourceConfig;
    private ServerConfig serverConfig;
    private EbeanServer ebeanServer;

    public CreateDatabase() 
            throws SQLException, LiquibaseException {
       
        updateSchema();
        configDataSource();
        configServer();
        createServer();
        
    } 
    
    private void updateSchema() throws SQLException, LiquibaseException{
        System.setProperty("dbDriver", "org.mariadb.jdbc.Driver");
        System.setProperty("dbUrl", "jdbc:mariadb://127.0.0.1:9001/timelogger");
        System.setProperty("dbUser", "timelogger");
        System.setProperty("dbPassword", "633Ym2aZ5b9Wtzh4EJc4pANx");
        //TODO: Át kell majd ezt baszni hogy a yamlbó szedje ki
        
        Connection connection = DriverManager.getConnection(System.getProperty("dbURL"),
                System.getProperty("dbUser"), System.getProperty("dbPassword"));
        
        Liquibase liquibase = new Liquibase("migrations.xml", new ClassLoaderResourceAccessor(),
                new JdbcConnection(connection));
        
         liquibase.update(new Contexts());
    }
    
    private void configDataSource(){        
        dataSourceConfig = new DataSourceConfig();
        dataSourceConfig.setDriver(System.getProperty("dbDriver"));
        dataSourceConfig.setUrl(System.getProperty("dbURL")); 
        dataSourceConfig.setUsername(System.getProperty("dbUser"));
        dataSourceConfig.setPassword(System.getProperty("dbPassword"));
    }
    
    private void configServer(){
        serverConfig = new ServerConfig();
        serverConfig.setName("timelogger");
        serverConfig.setDdlGenerate(false);
        serverConfig.setDdlRun(false);
        serverConfig.setRegister(true);
        serverConfig.setDataSourceConfig(dataSourceConfig);
        serverConfig.addClass(TestEntity.class);
        serverConfig.setDefaultServer(true);
    }
    
    private void createServer(){
        ebeanServer = EbeanServerFactory.create(serverConfig);
    }
}
