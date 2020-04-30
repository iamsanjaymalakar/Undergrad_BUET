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
public class Doctor implements Serializable{
    public String name;
    public String  dept;
    public String sex;
    public String dob;
    public String phone;
    public String address;
    public String userId;
    public Vector<Appointment> appointments;

    public Doctor() {
        name="";
        dept="";
        sex="";
        dob="";
        phone="";
        address="";
        appointments = new Vector();
    }
    
    
}
