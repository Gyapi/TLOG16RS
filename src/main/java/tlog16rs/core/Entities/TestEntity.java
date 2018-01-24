/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tlog16rs.core.Entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 *
 * @author Gyapi
 */
@Entity
public class TestEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)    
    @Column(name = "id")  
    private int id;
    private String text;

    public TestEntity(String text) {
        this.text = text;        
    }

    public void setId(int id) {
        this.id =  id;
    } 

    public void setText(String text) {
        this.text = text;
    }
    
    public int getId() {
        return id;
    } 
    
    public String getText() {
        return text;
    }   
    
}
