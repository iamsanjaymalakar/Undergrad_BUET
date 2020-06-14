/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package resources;

import java.io.Serializable;
import java.util.Vector;

/**
 *
 * @author Sanjay
 */
public class Patient implements Serializable{
    public String name;
    public String email;
    public String sex;
    public String dob;
    public String number;
    public String address;
    public String userId;
    public Vector<Appointment> appointments;

    public Patient() {
        name="";
        email="";
        sex="";
        dob="";
        number="";
        address="";
        appointments = new Vector();
    }
    
    
    
}
