/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package resources;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Sanjay
 */
public class Appointment implements Serializable, Comparable<Appointment> {

    public boolean seen;
    public String doctorID;
    public String patientID;
    public String title;
    public String description;
    //doctor preferences
    public String date;
    public String time;
    public int indexD;
    public int indexP;
    public Patient patient;
    public Doctor doctor;

    public Appointment() {
        doctorID = "";
        title = "";
        patientID = "";
        description = "";
        date = "";
        time = "";
        seen = false;
        indexD = 0;
        indexP = 0;
        patient = new Patient();
        doctor = new Doctor();
    }

    public String pendingDoc() {
        String s = "Patient :" + patient.name + ", Date & time :" + date + "," + time;
        return s;
    }

    public String pendingPat() {
        String s = "Doctor :" + doctor.name + ", Date & time :" + date + "," + time;
        return s;
    }

    public String Doc() {
        String s = "Patient :" + patient.name + ", Date & time :" + date + "," + time;
        return s;
    }

    public String Pat() {
        String s = "Doctor :" + doctor.name + ", Date & time :" + date + "," + time;
        return s;
    }

    @Override
    public int compareTo(Appointment o) {
        DateFormat format = new SimpleDateFormat("dd-MM-yyyy hh:mm a");
        Date d1 = null, d2 = null;
        try {
            d1 = format.parse(date+" "+time);
            d2 = format.parse(o.date+" "+o.time);
        } catch (ParseException ex) {
            Logger.getLogger(Appointment.class.getName()).log(Level.SEVERE, null, ex);
        }
        int bool = d1.compareTo(d2);
        return bool;
    }

}
