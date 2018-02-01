/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tlog16rs.resources.utilities;

import com.codahale.metrics.health.HealthCheck;
import java.net.InetAddress;
import lombok.extern.slf4j.Slf4j;
import tlog16rs.resources.CreateDatabase;

/**
 *
 * Checks the application"s health.
 * <br> Uses Lombok's @SLf4j annotation for log and error message handling.
 * <br> @see <a href="https://projectlombok.org/">https://projectlombok.org/</a>
 * 
 * @author Gyapi
 */
@Slf4j
public class TLOG16RSHealthCheck extends HealthCheck{
    
    CreateDatabase database;
    
    /**
     * 
     * @param database : {@link CreateDatabase CreateDatabase} object, to test the database integrity.
     */
    public TLOG16RSHealthCheck(CreateDatabase database) {
        super();
        this.database = database;
    }

    /**
     * 
     * Checks the appliaction's health.
     * First step: chekcs that the host in reachable in local network.
     * Second step: checks the database connection, and it's integrity.
     * 
     * @return {@link Result Result} depending on the application's state.
     */
    @Override
    protected Result check(){
        
        log.info("Sending Ping Request to 127.0.0.1:8080");
        try{
            InetAddress inet = InetAddress.getByName("127.0.0.1");
            inet.isReachable(8080);
            log.info("127.0.0.1:8080 reached.");  
        }
        catch (Exception ex){
            return Result.unhealthy("ERROR: 127.0.0.1:8080 Unreachable\n" + ex.toString());            
        }
        
        log.info("Testing Database Connection");
        if (!database.ping()){
            log.info("ERROR: Database Unreachable!");
            return Result.unhealthy("ERROR: Database Unreachable!");
        }    
        log.info("Database Reached");
        return Result.healthy();        
    }    
}
