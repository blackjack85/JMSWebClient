/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package obe;

import java.io.Serializable;
import javax.enterprise.context.Dependent;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;

@ManagedBean( name = "User")
@SessionScoped
public class UserBean implements Serializable{

    private static final long serialVersionUID = 5671761649767605303L;
    
    public String text = "This is Text!";
    public String htmlInput = "<input type='text' size='20' />";
    
    @Inject
    private AdminBean admin;

    
    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getHtmlInput() {
        return htmlInput;
    }

    public void setHtmlInput(String htmlInput) {
        this.htmlInput = htmlInput;
    }

     public String salute() {
        return "Hi! I am 'A'";
    }

  
    public AdminBean getAdmin() {
        return admin;
    }

    public void setAdmin(AdminBean admin) {
        this.admin = admin;
    }
    //getter and setter methods...
}
