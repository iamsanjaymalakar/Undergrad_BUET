/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hmsserver;

import resources.Message;
import resources.NetworkUtil;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import javafx.application.Platform;
import javafx.scene.control.ListView;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
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

    public Server(ListView logsList,ListView docsList,ListView paitList,ListView idPassList, HashMap<String, NetworkUtil> hm, HashMap<String, String> hmIdPass, HashMap<String, Doctor> hmDoctor, HashMap<String, Patient> hmPaitent, HashMap<String, String> online, ListView onlineList) {
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
            list.getItems().add(text);
        });
    }

    @Override
    public void run() {
        try {
            ServSock = new ServerSocket(33333);
            listAdd(logsList, "Server started.", 20, Color.BLACK);
            while (true) {
                Socket clientSock = ServSock.accept();
                System.out.println("Socket added");
                NetworkUtil nc = new NetworkUtil(clientSock);
                new ServerThread(nc, hmNet, logsList,docsList,paitList,idPassList,hmIdPass, hmDoctor, hmPaitent , online,onlineList);
                listAdd(logsList, clientSock.toString() + " connected.", 20, Color.BLUE);
            }
        } catch (IOException ex) {
            listAdd(logsList, ex.toString(), 20, Color.RED);
        }
    }

}
