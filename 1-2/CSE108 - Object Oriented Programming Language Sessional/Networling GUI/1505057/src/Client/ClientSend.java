package Client;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXMLLoader;

/**
 *
 * @author Sanjay
 */
public class ClientSend extends ClientUIController implements Runnable {

    DatagramPacket packet;
    DatagramSocket socket;
    byte[] buffer;
    int port;
    String IP, serverName, userName;
    Thread t;
    String initial;

    public ClientSend(String userName, int port, String IP, String serverName) throws UnknownHostException, SocketException, IOException {
        this.userName = userName;
        this.port = port;
        this.IP = IP;
        this.serverName = serverName;
        initial = "Via: " + serverName + "\nTo: " + serverName + "\nFrom: " + userName + "\nPort: " + port + "\n";
        buffer = initial.getBytes();
        packet = new DatagramPacket(buffer, buffer.length);
        packet.setAddress(InetAddress.getByName(IP));
        packet.setPort(7777);
        socket = new DatagramSocket();
        socket.send(packet);
        socket.close();
        t = new Thread(this);
        t.start();
    }

    public void send(String text) throws Exception {
        {
               System.out.println("here");
            String message = text;
            String messages[] = message.split("\\$");
            messages[1] = messages[1].substring(1);
            message = "Via: " + serverName + "\nTo: " + messages[0] + "\nFrom: " + userName + "\nBody: " + messages[1] + "\n";
            System.out.println(message);
            buffer = message.getBytes();
            packet = new DatagramPacket(buffer, buffer.length);
            packet.setAddress(InetAddress.getByName(IP));
            packet.setPort(7777);
            socket = new DatagramSocket();
            socket.send(packet);
        }
    }

    @Override
    public void run() {
        while (true) {
            try {
                {
                    if (input.getText().contains("$")) {
                        send(input.getText());
                    }
                }
            } catch (Exception ex) {
                Logger.getLogger(ClientSend.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
