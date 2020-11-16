import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;

public class Client {
    static File file = new File("address.txt");
    static File file2 = new File("body.txt");
    static BufferedReader in,fileReader;
    static PrintWriter out;
    static Timer timer = new Timer();

    public static void main(String args[]) throws IOException {
        Scanner sc = new Scanner(System.in);
        String ServerName = "smtp.sendgrid.net";
        InetAddress mailAddress = InetAddress.getByName(ServerName);
        Socket socket = new Socket(mailAddress,587);

        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream());

        String initialID = in.readLine();
        System.out.println(initialID);

        smtpMachine smtp = new smtpMachine();

        while(true) {
            System.out.println("\nState : "+smtp.currentState);
            System.out.println("1.HELO 2.MAIL 3.RCPT 4.DATA");
            int choice = sc.nextInt();
            if (choice == 1)
                smtp.helo();
            else if (choice == 2)
                smtp.mailfrom();
            else if (choice == 3)
                smtp.rcptTo();
            else if (choice == 4)
                smtp.data();
            else
                System.out.println("Invalid choice, try again.");
        }
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
