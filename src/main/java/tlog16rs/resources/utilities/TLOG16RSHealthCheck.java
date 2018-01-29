/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tlog16rs.resources.utilities;

import com.codahale.metrics.health.HealthCheck;

/**
 *
 * @author Gyapi
 */
public class TLOG16RSHealthCheck extends HealthCheck{

    //TODO: Legyenmánittvalami
    @Override
    protected Result check() throws Exception {
        return Result.healthy();
    }    
}
