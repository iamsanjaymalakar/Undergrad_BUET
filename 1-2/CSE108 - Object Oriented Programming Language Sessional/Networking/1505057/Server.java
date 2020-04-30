package Offline;

/**
 *
 * @author Sanjay
 */
public class Server {

    Server(String name) {
        ServerRecieve s = new ServerRecieve(name);
    }

    public static void main(String args[]) {
        String serverName;
        if (args.length == 0) {
            serverName = "Server";
        } else {
            serverName = args[0];
        }

        //Starting server
        Server sv = new Server(serverName);
    }

}
