/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package resources;

import java.io.Serializable;

/**
 *
 * @author Sanjay
 */
public class Register implements Serializable{
    public String username;
    public String password;
    public int type; // 1 for doc 2 for paitent
    
    public Register() {
        username="";
        password="";
        type=2;
    }
    
}
