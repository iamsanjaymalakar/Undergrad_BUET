package Offline;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.net.UnknownHostException;

/**
 *
 * @author Sanjay
 */
public class ClientRecieve implements Runnable {

    DatagramPacket packet;
    DatagramSocket socket;
    byte[] buffer;
    int port;
    String IP, serverName, userName;
    Thread t;

    public ClientRecieve(String userName,int port,String IP,String serverName) throws UnknownHostException, SocketException, IOException {
        this.userName=userName;
        this.port=port;
        this.IP=IP;
        this.serverName=serverName;
        t= new Thread(this);
        t.start();
    }

    public void receive() throws Exception {
        buffer = new byte[1000];
        packet = new DatagramPacket(buffer, buffer.length);
        socket = new DatagramSocket(port);
        socket.receive(packet);
        socket.close();
        String temp=new String(packet.getData());
        String lines[] = temp.split("\\r?\\n");
        String message=lines[2].substring(6)+" says: "+lines[3].substring(6);
        System.out.println(message);
    }

    @Override
    public void run() {
        while (true) {
            try {
                receive();
            } catch (Exception e) {
                System.out.println(e);
            }
        }
    }

}
