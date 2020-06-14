/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hmsclient;

import static hmsclient.HMSClient.online;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Set;
import java.util.Vector;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import resources.Appointment;
import resources.Doctor;
import resources.Message;
import resources.NetworkUtil;
import resources.Patient;

/**
 *
 * @author Sanjay
 */
public class ClientThread implements Runnable {

    NetworkUtil nc;
    Thread t;
    Alert alert;
    Message msg;
    Doctor doctor;
    Patient patient;

    ClientThread(NetworkUtil nc, Alert alert, Doctor doctor, Patient patient) {
        this.nc = nc;
        this.alert = alert;
        this.doctor = doctor;
        this.patient = patient;
        t = new Thread(this);
        t.start();
    }

    public void errorAlert(String title, String header, String content) {
        Platform.runLater(() -> {
            alert = new Alert(AlertType.ERROR);
            alert.setTitle(title);
            alert.setHeaderText(header);
            alert.setContentText(content);
            alert.showAndWait();
        });
    }

    public void infoAlert(String title, String header, String content) {
        Platform.runLater(() -> {
            alert = new Alert(AlertType.INFORMATION);
            alert.setTitle(title);
            alert.setHeaderText(header);
            alert.setContentText(content);
            alert.showAndWait();
        });
    }

    @Override
    public void run() {
        while (true) {
            msg = new Message();
            try {
                msg = (Message) nc.receive();
            } catch (Exception e) {

            }
            if (msg == null) {
                errorAlert("Server closed!", "Try again later.", null);
                break;
            }

            switch (msg.type) {
                case 1: { // error 
                    errorAlert("Error", msg.error, null);
                    break;
                }
                case 2: { // login successfull
                    //showing alert
                    infoAlert("Login Successfull", msg.success, null);
                    //updating the scene
                    if (msg.successType == 1) { // doctor
                        Platform.runLater(() -> {
                            HMSClient.doctorScene();
                        });
                    } else if (msg.successType == 2) { //patient
                        Platform.runLater(() -> {
                            HMSClient.patientScene();
                        });
                    }
                    break;
                }
                case 3: { //regis successfull
                    //showing alert
                    infoAlert("Registration Successfull", msg.success, null);
                    if (msg.successType == 1) { // doctor
                        Platform.runLater(() -> {
                            HMSClient.regDocScene();
                        });
                    } else if (msg.successType == 2) { // patient
                        Platform.runLater(() -> {
                            HMSClient.regPatScene();
                        });
                    }
                    break;
                }
                case 4: { // showing alert 
                    infoAlert(msg.success, msg.success, null);
                    break;
                }
                case 5: { // updating info
                    if (msg.successType == 1) { // doctor
                        HMSClient.doctor=new Doctor();
                        HMSClient.doctor = msg.doctor;
                        //online
                        Set<String> keys = msg.online.keySet();
                        HMSClient.online.clear();
                        for (String key : keys) {
                            HMSClient.online.put(key, msg.online.get(key));
                        }
                        //vector
                        Set<String> i = msg.medium.keySet();
                        HMSClient.doctor.appointments.clear();
                        for (String key : i) {
                            HMSClient.doctor.appointments.add(msg.medium.get(key));
                        }
                    } else if (msg.successType == 2) { // patient
                        HMSClient.patient = msg.paitent;
                        //online
                        Set<String> keys = msg.online.keySet();
                        HMSClient.online.clear();
                        for (String key : keys) {
                            HMSClient.online.put(key, msg.online.get(key));
                        }
                        //vector
                        Set<String> i = msg.medium.keySet();
                        HMSClient.patient.appointments.clear();
                        for (String key : i) {
                            HMSClient.patient.appointments.add(msg.medium.get(key));
                        }
                    }
                    break;
                }
            }

        }

    }
}
