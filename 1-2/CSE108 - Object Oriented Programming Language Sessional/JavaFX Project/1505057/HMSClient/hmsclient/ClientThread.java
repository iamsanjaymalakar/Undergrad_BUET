/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hmsclient;

import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;
import org.controlsfx.control.Notifications;
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
                            Timer timer = new Timer();
                            TimerTask task = new TimerTask() {
                                @Override
                                public void run() {
                                    Platform.runLater(() -> {
                                        HMSClient.doctorScene();
                                    });
                                    timer.cancel();
                                }
                            };
                            timer.schedule(task, 500, 1000);
                        });
                    } else if (msg.successType == 2) { //patient
                        Platform.runLater(() -> {
                            HMSClient.patientScene();
                            Timer timer = new Timer();
                            TimerTask task = new TimerTask() {
                                @Override
                                public void run() {
                                    Platform.runLater(() -> {
                                        HMSClient.patientScene();
                                    });
                                    timer.cancel();
                                }
                            };
                            timer.schedule(task, 500, 1000);
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
                        HMSClient.doctor = new Doctor();
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
                case 6: {
                    //notifications
                    if (msg.successType == 1) { //refresh button for doctor
                        //send msg to update profile and refresh, notification
                        String message = msg.appointment.patient.name + " requested appointment at " + msg.appointment.time + "," + msg.appointment.date;
                        Platform.runLater(() -> {
                            HMSClient.refreshRequestDoctor();
                            Image image = new Image("doctorIcon.png");
                            ImageView img = new ImageView(image);
                            img.setFitHeight(120);
                            img.setFitWidth(120);
                            Notifications noty = Notifications.create()
                                    .title("New Appointment.")
                                    .hideAfter(Duration.seconds(20))
                                    .text(message)
                                    .graphic(img)
                                    .position(Pos.BOTTOM_RIGHT)
                                    .darkStyle();
                            noty.onAction((ActionEvent event) -> {
                                HMSClient.sceneNum = 2;
                                HMSClient.doctorScene();
                            });
                            noty.show();
                            HMSClient.mediaPlayer = new MediaPlayer(HMSClient.notiMusic);
                            HMSClient.mediaPlayer.play();
                            HMSClient.mediaPlayer.setCycleCount(1);
                        });
                    } else if (msg.successType == 2) { //refresh button for patient
                        //send msg to update profile and refresh, notification
                        String message = msg.appointment.doctor.name + " approved your appointment at " + msg.appointment.time + "," + msg.appointment.date;
                        Platform.runLater(() -> {
                            HMSClient.refreshRequestPatient();
                            Image image = new Image("patientIcon.png");
                            ImageView img = new ImageView(image);
                            img.setFitHeight(120);
                            img.setFitWidth(120);
                            System.out.println(msg.appointment.doctor.name);
                            Notifications noty = Notifications.create()
                                    .title("Appointment approved.")
                                    .hideAfter(Duration.seconds(20))
                                    .text(message)
                                    .graphic(img)
                                    .position(Pos.BOTTOM_RIGHT)
                                    .darkStyle();
                            noty.onAction((ActionEvent event) -> {
                                HMSClient.sceneNum = 1;
                                HMSClient.subsceneNum = 1;
                                HMSClient.patientScene();
                            });
                            noty.show();
                            HMSClient.mediaPlayer = new MediaPlayer(HMSClient.notiMusic);
                            HMSClient.mediaPlayer.play();
                            HMSClient.mediaPlayer.setCycleCount(1);
                        });
                    } else if (msg.successType == 3) { // doctor login/logout refresh
                        //send msg to update profile and refresh
                        Platform.runLater(() -> {
                            HMSClient.refreshRequestPatient();
                        });
                    }
                    break;
                }
                case 7:{ //delete
                    Platform.runLater(() -> {
                        HMSClient.loginScene();
                    });
                    errorAlert("You have been kicked !", "You have been kicked from the server.", null);
                    break;
                }
            }

        }

    }
}
