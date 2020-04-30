package hmsserver;

import java.math.MathContext;
import resources.Message;
import java.net.ServerSocket;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import javafx.application.Platform;
import javafx.scene.control.ListView;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import resources.Appointment;
import resources.Doctor;
import resources.NetworkUtil;
import resources.Patient;

/**
 *
 * @author Sanjay
 */
public class ServerThread implements Runnable {

    public ServerSocket ServSock;
    public Thread t;
    public Message msg;
    public HashMap<String, String> hmIdPass;
    public HashMap<String, Patient> hmPaitent;
    public HashMap<String, Doctor> hmDoctor;
    public HashMap<String, NetworkUtil> hmNet;
    public HashMap<String, String> online;
    public DataStore data;
    public NetworkUtil nc;
    public ListView logsList;
    public ListView docsList;
    public ListView paitList;
    public ListView idPassList;
    public Text text;
    public ListView onlineList;

    ServerThread(NetworkUtil nc, HashMap<String, NetworkUtil> hmNet, ListView logsList, ListView docsList, ListView paitList, ListView idPassList, HashMap<String, String> hmIdPass, HashMap<String, Doctor> hmDoctor, HashMap<String, Patient> hmPaitent, HashMap<String, String> online, ListView onlineList) {
        this.nc = nc;
        this.hmNet = hmNet;
        this.logsList = logsList;
        this.docsList = docsList;
        this.paitList = paitList;
        this.idPassList = idPassList;
        this.hmIdPass = hmIdPass;
        this.hmDoctor = hmDoctor;
        this.hmPaitent = hmPaitent;
        this.online = online;
        this.onlineList = onlineList;
        data = new DataStore();
        t = new Thread(this);
        t.start();
    }

    public void listAdd(ListView list, String s, int size, Color color) {
        Platform.runLater(() -> {
            text = new Text(s);
            text.setFont(new Font(size));
            text.setFill(color);
            if (list == logsList) {
                TextFlow tf = new TextFlow();
                String timeStamp = new SimpleDateFormat("hh:mm a").format(Calendar.getInstance().getTime());
                Text tt = new Text(timeStamp + "  ");
                tt.setFont(new Font(size));
                tt.setFill(Color.ROYALBLUE);
                tf.getChildren().addAll(tt, text);
                list.getItems().add(tf);
            } else {
                list.getItems().add(text);
            }
        });
    }

    @Override
    public void run() {
        while (true) {
            msg = new Message();
            try {
                msg = (Message) nc.receive();
            } catch (Exception e) {
                listAdd(logsList, e.toString(), 25, Color.RED);
            }
            if (msg == null) {
                listAdd(logsList, "Connection terminated by " + nc.socket.toString() + ".", 25, Color.RED);
                break;
            }
            // msg type
            switch (msg.type) {
                case 1: {
                    // login
                    if (msg.login.type == 1) { //doc
                        if (hmIdPass.containsKey(msg.login.username)) {
                            if (hmIdPass.get(msg.login.username).equals(msg.login.password)) {
                                String userID = msg.login.username;
                                if (hmDoctor.get(userID) == null) {
                                    // username doesnt exists
                                    listAdd(logsList, "Doctor username doesn't exists for \"" + msg.login.username + "\"", 25, Color.RED);
                                    //response 
                                    msg = new Message();
                                    msg.type = 1;
                                    msg.error = "Username doesn't exists";
                                    nc.send(msg);
                                } else {
                                    //login successfull
                                    listAdd(logsList, "Login successfull for " + "\"" + msg.login.username + "\"", 25, Color.GREEN);
                                    //adding nc to map
                                    HMSServer.hmDocNet.put(userID, nc);
                                    // setting doctor online 
                                    msg = new Message();
                                    msg.doctor = hmDoctor.get(userID);
                                    online.put(msg.doctor.userId, msg.doctor.name);
                                    //response
                                    msg = new Message();
                                    msg.type = 2;
                                    msg.successType = 1;
                                    msg.success = "Login Successful.";
                                    nc.send(msg);
                                    // sending info to client 
                                    msg = new Message();
                                    msg.type = 5;
                                    msg.successType = 1;
                                    msg.online.clear();
                                    for (HashMap.Entry<String, String> entry : online.entrySet()) {
                                        String key = entry.getKey();
                                        String value = entry.getValue();
                                        msg.online.put(key, value);
                                    }
                                    msg.doctor = hmDoctor.get(userID);
                                    //appointments
                                    msg.medium.clear();
                                    for (int i = 0; i < msg.doctor.appointments.size(); i++) {
                                        String key = String.valueOf(msg.medium.size());
                                        msg.medium.put(key, msg.doctor.appointments.elementAt(i));
                                    }
                                    nc.send(msg);
                                    //sending request to refresh all patients
                                    for (HashMap.Entry<String, NetworkUtil> entry : HMSServer.hmPatNet.entrySet()) {
                                        String key = entry.getKey();
                                        NetworkUtil value = entry.getValue();
                                        //sending refresh comand to patients
                                        msg = new Message();
                                        msg.type = 6;
                                        msg.successType = 3;
                                        value.send(msg);
                                        System.out.println("here");
                                    }
                                }
                            } else {
                                // pass didnt match
                                listAdd(logsList, "Password didn't match for \"" + msg.login.username + "\"", 25, Color.RED);
                                //response
                                msg = new Message();
                                msg.type = 1;
                                msg.error = "Password didn't match.";
                                nc.send(msg);
                            }
                        } else {
                            // username doesnt exists
                            listAdd(logsList, "Username doesn't exists for \"" + msg.login.username + "\"", 25, Color.RED);
                            //response
                            msg = new Message();
                            msg.type = 1;
                            msg.error = "Username doesn't exists";
                            nc.send(msg);
                        }
                    } else if (msg.login.type == 2) { // piatent
                        if (hmIdPass.containsKey(msg.login.username)) {
                            if (hmIdPass.get(msg.login.username).equals(msg.login.password)) {
                                String userID = msg.login.username;
                                if (hmPaitent.get(userID) == null) {
                                    // username doesnt exists
                                    listAdd(logsList, "Patient username doesn't exists for \"" + msg.login.username + "\"", 25, Color.RED);
                                    //response 
                                    msg = new Message();
                                    msg.type = 1;
                                    msg.error = "Username doesn't exists";
                                    nc.send(msg);
                                } else {
                                    //login successfull
                                    listAdd(logsList, "Login successfull for \"" + msg.login.username + "\"", 25, Color.GREEN);
                                    //adding nc to map
                                    HMSServer.hmPatNet.put(userID, nc);
                                    //response
                                    msg = new Message();
                                    msg.type = 2;
                                    msg.successType = 2;
                                    msg.success = "Login Successful.";
                                    nc.send(msg);
                                    //sending info to client 
                                    msg = new Message();
                                    msg.type = 5;
                                    msg.successType = 2;
                                    msg.online.clear();
                                    for (HashMap.Entry<String, String> entry : online.entrySet()) {
                                        String key = entry.getKey();
                                        String value = entry.getValue();
                                        msg.online.put(key, value);
                                    }
                                    msg.paitent = hmPaitent.get(userID);
                                    //appointments
                                    msg.medium.clear();
                                    for (int i = 0; i < msg.paitent.appointments.size(); i++) {
                                        String key = String.valueOf(msg.medium.size());
                                        msg.medium.put(key, msg.paitent.appointments.elementAt(i));
                                    }
                                    nc.send(msg);
                                }
                            } else {
                                // pass didnt match
                                listAdd(logsList, "Password didn't match for \"" + msg.login.username + "\"", 25, Color.RED);
                                //response
                                msg = new Message();
                                msg.type = 1;
                                msg.error = "Password didn't match.";
                                nc.send(msg);
                            }
                        } else {
                            // username doesnt exists
                            listAdd(logsList, "Username doesn't exists for \"" + msg.login.username + "\"", 25, Color.RED);
                            //response 
                            msg = new Message();
                            msg.type = 1;
                            msg.error = "Username doesn't exists";
                            nc.send(msg);
                        }
                    }
                    break;
                }

                case 2: { // register
                    if (msg.register.type == 1) { // doctor
                        if (hmIdPass.containsKey(msg.register.username)) {
                            listAdd(logsList, "Username doesn't available for \"" + msg.register.username + "\"", 25, Color.RED);
                            //response
                            msg = new Message();
                            msg.type = 1;
                            msg.error = "Username not available.";
                            nc.send(msg);
                        } else {
                            listAdd(logsList, "Registration successfull for \"" + msg.register.username + "\"", 25, Color.GREEN);
                            //adding nc to map
                            HMSServer.hmDocNet.put(msg.register.username, nc);
                            // updating map
                            hmIdPass.put(msg.register.username, msg.register.password);
                            listAdd(idPassList, msg.register.username, 24, Color.BLACK);
                            //adding nc to map
                            hmNet.put(msg.register.username, nc);
                            //response 
                            msg = new Message();
                            msg.type = 3;
                            msg.successType = 1;
                            msg.success = "Registration Successful.";
                            nc.send(msg);
                            // saving file 
                            data.save(hmIdPass, "hmIdPass");
                            data.save(hmDoctor, "hmDoctor");
                            data.save(hmPaitent, "hmPaitent");
                            //sending request to refresh all patients
                            for (HashMap.Entry<String, NetworkUtil> entry : HMSServer.hmPatNet.entrySet()) {
                                String key = entry.getKey();
                                NetworkUtil value = entry.getValue();
                                //sending refresh comand to patients
                                msg = new Message();
                                msg.type = 6;
                                msg.successType = 3;
                                value.send(msg);
                            }
                        }
                    } else if (msg.register.type == 2) { // paitent
                        if (hmIdPass.containsKey(msg.register.username)) {
                            listAdd(logsList, "Username doesn't available for \"" + msg.register.username + "\"", 25, Color.RED);
                            //response
                            msg = new Message();
                            msg.type = 1;
                            msg.error = "Username not available.";
                            nc.send(msg);
                        } else {
                            listAdd(logsList, "Registration successfull for " + msg.register.username, 25, Color.GREEN);
                            //adding nc to map
                            HMSServer.hmPatNet.put(msg.register.username, nc);
                            // updating map
                            hmIdPass.put(msg.register.username, msg.register.password);
                            listAdd(idPassList, msg.register.username, 24, Color.BLACK);
                            //response 
                            msg = new Message();
                            msg.type = 3;
                            msg.successType = 2;
                            msg.success = "Registration Successful.";
                            nc.send(msg);
                            // saving file 
                            data.save(hmIdPass, "hmIdPass");
                            data.save(hmDoctor, "hmDoctor");
                            data.save(hmPaitent, "hmPaitent");
                        }
                    }
                    break;
                }
                // update profile
                case 3: {
                    if (msg.successType == 1) { // updating doctors profile
                        String userID = msg.doctor.userId;
                        listAdd(logsList, "Profile updated for " + userID, 25, Color.BLACK);
                        // updating profile 
                        hmDoctor.put(msg.doctor.userId, msg.doctor);
                        //setting doctor online 
                        if (msg.upType == 2) { // initial profile update
                            online.put(msg.doctor.userId, msg.doctor.name);
                            listAdd(docsList, msg.doctor.name + "(" + msg.doctor.dept + ")", 24, Color.BLACK);
                            //login msg
                            msg = new Message();
                            msg.type = 2;
                            msg.successType = 1;
                            msg.success = "Login Successful.";
                            nc.send(msg);
                        }
                        // saving file 
                        data.save(hmIdPass, "hmIdPass");
                        data.save(hmDoctor, "hmDoctor");
                        data.save(hmPaitent, "hmPaitent");
                        //sending alert
                        msg = new Message();
                        msg.type = 4;
                        msg.success = "Profile updated.";
                        nc.send(msg);
                        // sending info to client 
                        msg = new Message();
                        msg.type = 5;
                        msg.successType = 1;
                        msg.doctor = hmDoctor.get(userID);
                        //online
                        msg.online.clear();
                        for (HashMap.Entry<String, String> entry : online.entrySet()) {
                            String key = entry.getKey();
                            String value = entry.getValue();
                            msg.online.put(key, value);
                        }
                        //appointments
                        msg.medium.clear();
                        for (int i = 0; i < msg.doctor.appointments.size(); i++) {
                            String key = String.valueOf(msg.medium.size());
                            msg.medium.put(key, msg.doctor.appointments.elementAt(i));
                        }
                        nc.send(msg);
                    } else if (msg.successType == 2) { // updating patients profile
                        String userID = msg.paitent.userId;
                        listAdd(logsList, "Profile updated for " + userID, 25, Color.BLACK);
                        // updating profile 
                        hmPaitent.put(msg.paitent.userId, msg.paitent);
                        if (msg.upType == 2) { // initial profile update
                            listAdd(paitList, msg.paitent.name, 24, Color.BLACK);
                            //sending login msg
                            msg = new Message();
                            msg.type = 2;
                            msg.successType = 2;
                            msg.success = "Login Successful.";
                            nc.send(msg);
                        }
                        // saving file 
                        data.save(hmIdPass, "hmIdPass");
                        data.save(hmDoctor, "hmDoctor");
                        data.save(hmPaitent, "hmPaitent");
                        //sending alert
                        msg = new Message();
                        msg.type = 4;
                        msg.success = "Profile updated.";
                        nc.send(msg);
                        // sending info to client 
                        msg = new Message();
                        msg.type = 5;
                        msg.successType = 2;
                        msg.online.clear();
                        for (HashMap.Entry<String, String> entry : online.entrySet()) {
                            String key = entry.getKey();
                            String value = entry.getValue();
                            msg.online.put(key, value);
                        }
                        msg.paitent = hmPaitent.get(userID);
                        //appointments
                        msg.medium.clear();
                        for (int i = 0; i < msg.paitent.appointments.size(); i++) {
                            String key = String.valueOf(msg.medium.size());
                            msg.medium.put(key, msg.paitent.appointments.elementAt(i));
                        }
                        nc.send(msg);
                    }
                    break;
                }
                case 4: { // appointment 
                    if (msg.successType == 1) { // doctor appointment
                        listAdd(logsList, "\"" + msg.appointment.doctorID + "\"" + " confirmed appointment for " + "\"" + msg.appointment.patientID + "\"", 25, Color.BLACK);
                        String userID = msg.appointment.doctorID;
                        String patientID = msg.appointment.patientID;
                        Appointment temp = msg.appointment;
                        //receiving appointment
                        int index;
                        Patient tempP = new Patient();
                        Doctor tempD = new Doctor();
                        tempP = hmPaitent.get(msg.appointment.patientID);
                        tempD = hmDoctor.get(msg.appointment.doctorID);
                        //Updating vectors
                        msg.appointment.seen = true;
                        tempP.appointments.set(msg.appointment.indexP, msg.appointment);
                        tempD.appointments.set(msg.appointment.indexD, msg.appointment);
                        //updating map
                        hmDoctor.put(msg.appointment.doctorID, tempD);
                        hmPaitent.put(msg.appointment.patientID, tempP);
                        // saving file 
                        data.save(hmIdPass, "hmIdPass");
                        data.save(hmDoctor, "hmDoctor");
                        data.save(hmPaitent, "hmPaitent");
                        //updating doctor profile
                        msg = new Message();
                        msg.type = 5;
                        msg.successType = 1;
                        msg.online.clear();
                        for (HashMap.Entry<String, String> entry : online.entrySet()) {
                            String key = entry.getKey();
                            String value = entry.getValue();
                            msg.online.put(key, value);
                        }
                        msg.doctor = hmDoctor.get(userID);
                        //appointments
                        msg.medium.clear();
                        for (int i = 0; i < msg.doctor.appointments.size(); i++) {
                            String key = String.valueOf(msg.medium.size());
                            msg.medium.put(key, msg.doctor.appointments.elementAt(i));
                        }
                        nc.send(msg);
                        //sending notification to patient 
                        msg = new Message();
                        msg.type = 6;
                        msg.successType = 2;
                        msg.appointment = temp;
                        msg.appointment.patient = hmPaitent.get(patientID);
                        NetworkUtil tempnc = HMSServer.hmPatNet.get(patientID);
                        if (tempnc != null) {
                            tempnc.send(msg);
                        }
                        //sending alert
                        msg = new Message();
                        msg.type = 4;
                        msg.success = "Confirmation Sent to " + tempP.name;
                        nc.send(msg);
                    } else if (msg.successType == 2) { // patient appointment
                        String userID = msg.appointment.patientID;
                        String doctorID = msg.appointment.doctorID;
                        Appointment temp = msg.appointment;
                        listAdd(logsList, "\"" + msg.appointment.patientID + "\"" + " requested appointment to " + "\"" + msg.appointment.doctorID + "\"", 25, Color.BLACK);
                        //receiving appointment
                        int index;
                        Patient tempP = new Patient();
                        Doctor tempD = new Doctor();
                        tempP = hmPaitent.get(msg.appointment.patientID);
                        tempD = hmDoctor.get(msg.appointment.doctorID);
                        msg.appointment.patient = tempP;
                        msg.appointment.doctor = tempD;
                        //Patient
                        index = tempP.appointments.size();
                        msg.appointment.indexP = index;
                        tempP.appointments.add(msg.appointment);
                        //Doctor
                        index = tempD.appointments.size();
                        msg.appointment.indexD = index;
                        tempD.appointments.add(msg.appointment);
                        //updating map
                        hmDoctor.put(msg.appointment.doctorID, tempD);
                        hmPaitent.put(msg.appointment.patientID, tempP);
                        // saving file 
                        data.save(hmIdPass, "hmIdPass");
                        data.save(hmDoctor, "hmDoctor");
                        data.save(hmPaitent, "hmPaitent");
                        //updating patients profile
                        msg = new Message();
                        msg.type = 5;
                        msg.successType = 2;
                        msg.online.clear();
                        for (HashMap.Entry<String, String> entry : online.entrySet()) {
                            String key = entry.getKey();
                            String value = entry.getValue();
                            msg.online.put(key, value);
                        }
                        msg.paitent = hmPaitent.get(userID);
                        //appointments
                        msg.medium.clear();
                        for (int i = 0; i < msg.paitent.appointments.size(); i++) {
                            String key = String.valueOf(msg.medium.size());
                            msg.medium.put(key, msg.paitent.appointments.elementAt(i));
                        }
                        nc.send(msg);
                        //sending alert
                        msg = new Message();
                        msg.type = 4;
                        msg.success = "Appointment Sent to \"" + tempD.name + "\" for confirmation.";
                        nc.send(msg);
                        //sending notificatinon to doctor
                        msg = new Message();
                        msg.type = 6;
                        msg.successType = 1;
                        msg.appointment = temp;
                        msg.appointment.doctor = hmDoctor.get(doctorID);
                        NetworkUtil tempnc = HMSServer.hmDocNet.get(doctorID);
                        if (tempnc != null) {
                            tempnc.send(msg);
                        }
                    }
                    break;
                }
                case 5: { // refresh
                    if (msg.successType == 1) { // doctor
                        String userID = msg.doctor.userId;
                        //sending updaed info 
                        msg = new Message();
                        msg.type = 5;
                        msg.successType = 1;
                        msg.doctor = hmDoctor.get(userID);
                        msg.online.clear();
                        //online
                        for (HashMap.Entry<String, String> entry : online.entrySet()) {
                            String key = entry.getKey();
                            String value = entry.getValue();
                            msg.online.put(key, value);
                        }
                        //appointments
                        msg.medium.clear();
                        for (int i = 0; i < msg.doctor.appointments.size(); i++) {
                            String key = String.valueOf(msg.medium.size());
                            msg.medium.put(key, msg.doctor.appointments.elementAt(i));
                        }
                        nc.send(msg);
                    } else if (msg.successType == 2) { //patient
                        String userID = msg.paitent.userId;
                        //sending updated info 
                        msg = new Message();
                        msg.type = 5;
                        msg.successType = 2;
                        msg.paitent = hmPaitent.get(userID);
                        msg.online.clear();
                        for (HashMap.Entry<String, String> entry : online.entrySet()) {
                            String key = entry.getKey();
                            String value = entry.getValue();
                            msg.online.put(key, value);
                        }
                        //appointments
                        msg.medium.clear();
                        for (int i = 0; i < msg.paitent.appointments.size(); i++) {
                            String key = String.valueOf(msg.medium.size());
                            msg.medium.put(key, msg.paitent.appointments.elementAt(i));
                        }
                        nc.send(msg);
                    }
                    break;
                }
                case 6: {//logout
                    if (msg.successType == 1) { // logout for doctor
                        listAdd(logsList, "\"" + msg.doctor.userId + "\"" + "logged out", 25, Color.RED);
                        HMSServer.hmDocNet.remove(msg.doctor.userId);
                        online.remove(msg.doctor.userId);
                        //sending info to all users to refresh
                        for (HashMap.Entry<String, NetworkUtil> entry : HMSServer.hmPatNet.entrySet()) {
                            String key = entry.getKey();
                            NetworkUtil value = entry.getValue();
                            //sending refresh comand to patients
                            msg = new Message();
                            msg.type = 6;
                            msg.successType = 3;
                            value.send(msg);
                        }
                    } else if (msg.successType == 2) { // logout for patient
                        listAdd(logsList, "\"" + msg.paitent.userId + "\"" + "logged out", 25, Color.RED);
                        HMSServer.hmPatNet.remove(msg.paitent.userId);
                    }
                    break;
                }
                default:
                    break;
            }
        }
        nc.closeConnection();
    }
}
