package Offline;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Scanner;

/**
 *
 * @author Sanjay
 */
public class ClientSend implements Runnable {

    DatagramPacket packet;
    DatagramSocket socket;
    Scanner sc = new Scanner(System.in);
    byte[] buffer;
    int port;
    String IP, serverName, userName;
    Thread t;
    String initial;

    public ClientSend(String userName,int port,String IP,String serverName) throws UnknownHostException, SocketException, IOException {
        this.userName=userName;
        this.port=port;
        this.IP=IP;
        this.serverName=serverName;
        initial="Via: "+serverName+"\nTo: "+serverName+"\nFrom: "+userName+"\nPort: "+port+"\n";
        buffer=initial.getBytes();
        packet = new DatagramPacket(buffer, buffer.length);
        packet.setAddress(InetAddress.getByName("localhost"));
        packet.setPort(5050);
        socket = new DatagramSocket();
        socket.send(packet);
        t= new Thread(this);
        t.start();
    }
    
    public void send() throws Exception {
        String message = sc.nextLine();
        String messages[] = message.split("\\$");
        messages[1] = messages[1].substring(1);
        message = "Via: "+serverName+"\nTo: "+messages[0]+"\nFrom: "+userName+"\nBody: "+messages[1]+"\n";
        buffer=message.getBytes();
        packet = new DatagramPacket(buffer, buffer.length);
        packet.setAddress(InetAddress.getByName("localhost"));
        packet.setPort(5050);
        socket = new DatagramSocket();
        socket.send(packet);
    }

    @Override
    public void run() {
        while (true) {
            try {
                send();
            } catch (Exception e) {
                System.out.println(e);
            }
        }
    }

}
