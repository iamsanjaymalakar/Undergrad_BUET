package Client;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.net.UnknownHostException;
import javafx.scene.text.Text;

/**
 *
 * @author Sanjay
 */

public class ClientRecieve extends ClientClass implements Runnable {

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
        buffer = new byte[102];
        packet = new DatagramPacket(buffer, buffer.length);
        socket = new DatagramSocket(port);
        socket.receive(packet);
        socket.close();
        String temp=new String(packet.getData());
        String lines[] = temp.split("\\r?\\n");
        setMessage(lines[2].substring(6),lines[3].substring(6));
        
        //String message=lines[2].substring(6)+"$"+lines[3].substring(6);
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
