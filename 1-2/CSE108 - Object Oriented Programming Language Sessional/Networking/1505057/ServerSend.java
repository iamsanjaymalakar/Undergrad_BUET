package Offline;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.Hashtable;

/**
 *
 * @author Sanjay
 */
public class ServerSend {

    DatagramPacket packet;
    DatagramSocket socket;
    String serverName, userName;
    Hashtable<String, String> table;

    public ServerSend(String serverName) {
        this.serverName = serverName;
        table = new Hashtable<String, String>();
        packet = new DatagramPacket("test".getBytes(), 0);
    }

    public void send(DatagramPacket packet) throws Exception {
        String temp = table.get(userName);
        String[] temps = temp.split("-");
        packet.setAddress(packet.getAddress());
        packet.setPort(Integer.parseInt(temps[1]));
        socket = new DatagramSocket();
        socket.send(packet);
        socket.close();
    }

    public void Process(DatagramPacket p) throws Exception {
        String temp = new String(p.getData());
        String lines[] = temp.split("\\r?\\n");
        if (lines[3].substring(0, 5).equals("Body:")) {
            System.out.println(temp+"\n");
            userName = lines[1].substring(4);
            if (!lines[0].substring(5).equals(serverName)) {
                System.out.println("Warning: Server name mismatch. Message dropped.");
            }
            if (table.containsKey(userName)) {
                send(p);
            } else {
                System.out.println("Warning: Unknown recipient. Message dropped.");
            }

        } else if (lines[3].substring(0, 5).equals("Port:")) {

            if (lines[0].substring(5).equals(serverName)) {
                String ipport;
                ipport = String.valueOf(p.getAddress()) + "-" + lines[3].substring(6);
                table.put(lines[2].substring(6), ipport);
            }
        }
    }

    public void Run() {
        {
            try {
                Process(packet);
            } catch (Exception e) {
                System.out.println(e);
            }
        }
    }
}
