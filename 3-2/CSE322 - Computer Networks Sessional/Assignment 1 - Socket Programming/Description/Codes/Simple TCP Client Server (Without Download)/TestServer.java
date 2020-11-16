package testserver;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import javafx.scene.chart.BubbleChart;

public class TestServer {

    public static void main(String[] args) throws Exception {
        int id = 1;
        ServerSocket welcomeSocket = new ServerSocket(5000);
        String clientSentence;
        String serverSentence;
        while (true) {
            Socket connectionSocket = welcomeSocket.accept();
            Worker wt = new Worker(connectionSocket, id);
            Thread t = new Thread(wt);
            t.start();
            System.out.println("Client " + id + " connected to server");
            id++;
        }
    }
}

class Worker implements Runnable {

    private Socket connectionSocket;
    private int id;

    public Worker(Socket s, int id) {
        this.connectionSocket = s;
        this.id = id;
    }

    public void run() {

        while (true) {
            String clientSentence, serverSentence;
            try {
                BufferedReader inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
                PrintWriter outToClient = new PrintWriter(connectionSocket.getOutputStream());
                clientSentence = inFromClient.readLine();
                serverSentence = clientSentence.toUpperCase();
                outToClient.println(serverSentence);
                outToClient.flush();
            } catch (Exception e) {

            }
        }
    }
}