package Offline;

import java.net.DatagramPacket;
import java.net.DatagramSocket;

/**
 *
 * @author Sanjay
 */

public class ServerRecieve implements Runnable {

    DatagramPacket packet;
    DatagramSocket socket;
    String serverName,userName;
    Thread t;
    ServerSend ss;

    public ServerRecieve(String s) {
        serverName = s;
        ss= new ServerSend(s);
        t = new Thread(this);
        t.start();
    }

    public void receive() throws Exception {
        byte buffer[] = new byte[1000];
        packet = new DatagramPacket(buffer, buffer.length);
        socket = new DatagramSocket(5050);
        socket.receive(packet);
        socket.close();
        ss.packet=packet;
        ss.Run();
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
