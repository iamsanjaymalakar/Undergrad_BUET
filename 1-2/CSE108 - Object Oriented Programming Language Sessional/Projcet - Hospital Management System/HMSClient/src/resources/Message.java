package resources;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Hashtable;

/**
 *
 * @author Sanjay
 */
public class Message implements Serializable {

    public int type; //1-login 2-registration 3.update profile 3-DocActivity 4-DocToPaiten 5-PaitenToDoc Server
    //1- error 2-login succesful 3- regsuccessfull 4 alert 5 update info
    public Login login; // for login
    public Register register; // for registration
    public Patient paitent; // for PaitentToDoc
    public Doctor doctor; // for DocToPaitent
    public String activity; // for activity
    public String fromID, toID; //sender and reciever
    public String error; // error alert
    public String success; // successalert
    public int successType; // 1 doctor 2 patient
    public int upType; // 2 for initial
    public Hashtable <String, String> online;
    public Appointment appointment;
    public Hashtable<String,Appointment> medium;
    
    public Message() {
        type = 0;
        login = new Login();
        register = new Register();
        paitent = new Patient();
        doctor = new Doctor();
        activity = "";
        fromID = "";
        toID = "";
        successType = 0;
        upType = 0;
        appointment = new Appointment();
        online = new Hashtable<>();
        medium = new Hashtable<>();
    }
}
