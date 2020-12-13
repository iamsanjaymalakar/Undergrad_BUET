import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Timer;
import java.util.TimerTask;

public class Client {
    static BufferedReader in;
    static PrintWriter out;
    static Timer timer = new Timer();

    public static void main(String args[]) throws IOException {
        String ServerName = "smtp.sendgrid.net";
        InetAddress mailAddress = InetAddress.getByName(ServerName);
        Socket socket = new Socket(mailAddress,587);

        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream());

        String initialID = in.readLine();
        System.out.println(initialID);


        out.println("HELO "+InetAddress.getLocalHost().getHostName());
        out.flush();
        callTimer(1000);
        long startTime = System.currentTimeMillis();
        initialID = in.readLine();
        long estimatedTime = System.currentTimeMillis() - startTime;
        System.out.println(initialID+"  ["+estimatedTime+"]");
        timer.cancel();
        out.println("AUTH LOGIN");
        out.flush();
        initialID = in.readLine();
        System.out.println(initialID);

        out.println("YXBpa2V5");
        out.flush();
        initialID = in.readLine();
        System.out.println(initialID);

        out.println("U0cuYTRsVG9TUXhURnlBRWRIYnRNM1p2dy5odnIyZTMzTlJkLWYwQ2dmVkFCaFZCRHBJV01OcVdwalVZZG5NSUhVUkNZ");
        out.flush();
        initialID = in.readLine();
        System.out.println(initialID);

        out.println("mail from:<okkkk@waptwist.com>");
        out.flush();
        initialID = in.readLine();
        System.out.println(initialID);

        out.println("rcpt to:<iamsanjaymalakar@gmail.com>");
        out.flush();
        initialID = in.readLine();
        System.out.println(initialID);

        out.println("rcpt to:<19malakar@gmail.com>");
        out.flush();
        initialID = in.readLine();
        System.out.println(initialID);

        out.println("DATA");
        out.flush();
        initialID = in.readLine();
        System.out.println(initialID);

        out.println("To: Sanjay <iamsanjaymalakar@gmail.com>\n" +
                "To: Malakar <19malakar@gmail.com>\n"+
                "From: Myname Isss <okkkk@waptwist.com>\n" +
                "Subject: Test");
        out.flush();

        out.println("\nThis is the body of the message.");
        out.flush();
        out.println(".\r\n");
        out.flush();
        callTimer(5000);

        initialID = in.readLine();
        System.out.println(initialID);
        timer.cancel();
    }

    public static void callTimer(int delay){
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                System.out.println("TimeOut, Quiting server");
                out.println("QUIT");
                out.flush();
                String temp=null;
                try {
                    temp = in.readLine();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if(temp!=null)
                    System.out.println("asdasd "+temp);
                System.exit(0);
            }
        }, delay);
    }
}
