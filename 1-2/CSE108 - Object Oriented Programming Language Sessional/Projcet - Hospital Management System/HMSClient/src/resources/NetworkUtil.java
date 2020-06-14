package resources;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class NetworkUtil {

    public Socket socket;
    public ObjectOutputStream oos;
    public ObjectInputStream ois;

    public NetworkUtil(String s, int port) {
        try {
            this.socket = new Socket(s, port);
            oos = new ObjectOutputStream(socket.getOutputStream());
            ois = new ObjectInputStream(socket.getInputStream());
        } catch (Exception e) {
            System.out.println("In NetworkUtil : " + e.toString());
        }
    }

    public NetworkUtil(Socket s) {
        try {
            this.socket = s;
            oos = new ObjectOutputStream(socket.getOutputStream());
            ois = new ObjectInputStream(socket.getInputStream());
        } catch (Exception e) {
            System.out.println("In NetworkUtil : " + e.toString());
        }
    }

    public Object receive() {
        Object o = null;
        try {
            o = ois.readObject();
        } catch (Exception e) {
            System.out.println("Reading Error in network : " + e.toString());
        }
        return o;
    }

    public void send(Object o) {
        try {
            oos.writeObject(o);
        } catch (IOException e) {
            System.out.println("Writing  Error in network : " + e.toString());
        }
    }

    public void closeConnection() {
        try {
            ois.close();
            oos.close();
        } catch (Exception e) {
            System.out.println("Closing Error in network : " + e.toString());
        }
    }
}
