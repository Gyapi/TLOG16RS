/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tlog16rs.exceptions;

/**
 *
 * @author Gyapi
 */
public class NegativeMinutesOfWorkException extends Exception{

    public NegativeMinutesOfWorkException(String message) {
        super(message);
    }
}
