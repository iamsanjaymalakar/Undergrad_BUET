/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hmsserver;

import com.sun.javafx.scene.control.skin.LabeledText;
import resources.Message;
import resources.NetworkUtil;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import javafx.application.Platform;
import javafx.scene.control.ListView;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import resources.Doctor;
import resources.Patient;

/**
 *
 * @author Sanjay
 */
public class Server implements Runnable {

    public Text text;
    public ServerSocket ServSock;
    public ListView logsList;
    public ListView docsList;
    public ListView paitList;
    public ListView idPassList;
    public ListView onlineList;
    public Message msg;
    public HashMap<String, String> hmIdPass;
    public HashMap<String, Patient> hmPaitent;
    public HashMap<String, Doctor> hmDoctor;
    public HashMap<String, NetworkUtil> hmNet;
    HashMap<String, String> online;
    public Thread t;
    public String s;

    public Server(ListView logsList, ListView docsList, ListView paitList, ListView idPassList, HashMap<String, NetworkUtil> hm, HashMap<String, String> hmIdPass, HashMap<String, Doctor> hmDoctor, HashMap<String, Patient> hmPaitent, HashMap<String, String> online, ListView onlineList) {
        this.logsList = logsList;
        this.docsList = docsList;
        this.paitList = paitList;
        this.idPassList = idPassList;
        this.hmIdPass = hmIdPass;
        this.hmDoctor = hmDoctor;
        this.hmPaitent = hmPaitent;
        this.online = online;
        this.onlineList = onlineList;
        hmNet = hm;
        t = new Thread(this);
        t.start();
    }

    public void listAdd(ListView list, String s, int size, Color color) {
        Platform.runLater(() -> {
            text = new Text(s);
            text.setFont(new Font(size));
            text.setFill(color);
            if (list == logsList) {
                TextFlow tf= new TextFlow();
                String timeStamp = new SimpleDateFormat("hh:mm a").format(Calendar.getInstance().getTime());
                Text tt= new Text(timeStamp+"  ");
                tt.setFont(new Font(size));
                tt.setFill(Color.ROYALBLUE);
                tf.getChildren().addAll(tt,text);
                list.getItems().add(tf);
            } else {
                list.getItems().add(text);
            }
        });
    }

    @Override
    public void run() {
        try {
            ServSock = new ServerSocket(33333);
            listAdd(logsList, "Server started.", 25, Color.BLACK);
            while (true) {
                Socket clientSock = ServSock.accept();
                NetworkUtil nc = new NetworkUtil(clientSock);
                new ServerThread(nc, hmNet, logsList, docsList, paitList, idPassList, hmIdPass, hmDoctor, hmPaitent, online, onlineList);
                listAdd(logsList, clientSock.toString() + " connected.", 25, Color.BLUE);
            }
        } catch (IOException ex) {
            listAdd(logsList, ex.toString(), 25, Color.RED);
        }
    }

}
