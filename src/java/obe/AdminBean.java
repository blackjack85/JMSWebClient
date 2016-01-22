/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package obe;

import java.io.Serializable;
import javax.faces.bean.SessionScoped;
import javax.inject.Named;

/**
 *
 * @author Satija
 */
@Named
@SessionScoped
public class AdminBean implements Serializable {

    private static final long serialVersionUID = -4786092545430477941L;

    public String doSomething() {
        System.out.println("Hello!!!");
        return "I am a SessionScopped Backing Bean, my name is 'Admin' and i am doing something";
    }
}
