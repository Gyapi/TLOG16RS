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
 * @author Gyapi
 */
@Slf4j
public class TLOG16RSHealthCheck extends HealthCheck{
    
    CreateDatabase database;
    
    public TLOG16RSHealthCheck(CreateDatabase database) {
        super();
        this.database = database;
    }

    //TODO: Legyenmánittvalami
    @Override
    protected Result check() throws Exception {
        InetAddress inet = InetAddress.getByName("127.0.0.1");
        log.info("Sending Ping Request to 127.0.0.1");
        if (!inet.isReachable(8080)){
            log.info("127.0.0.1 unreachable");
            return Result.unhealthy("");
        }
        log.info("127.0.0.1 reached.");        
        return Result.healthy();
    }    
}
