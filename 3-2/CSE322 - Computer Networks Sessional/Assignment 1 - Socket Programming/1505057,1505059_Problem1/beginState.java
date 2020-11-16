import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class beginState implements smtpStates {

    smtpMachine smtp;

    beginState(smtpMachine smtp) {
        this.smtp = smtp;
    }

    @Override
    public void helo() throws IOException {
        //helo
        Client.callTimer(20000);
        System.out.println("C : HELO " + InetAddress.getLocalHost().getHostName());
        Client.out.println("HELO " + InetAddress.getLocalHost().getHostName());
        Client.out.flush();
        String temp = Client.in.readLine();
        System.out.println("S : " + temp);
        Client.timer.cancel();

        //auth
        Client.callTimer(20000);
        System.out.println("C : AUTH LOGIN");
        Client.out.println("AUTH LOGIN");
        Client.out.flush();
        temp = Client.in.readLine();
        System.out.println("S : " + temp);
        Client.timer.cancel();
        Client.callTimer(20000);
        System.out.println("C : YXBpa2V5");
        Client.out.println("YXBpa2V5");
        Client.out.flush();
        temp = Client.in.readLine();
        System.out.println("S : " + temp);
        Client.timer.cancel();
        Client.callTimer(20000);
        System.out.println("C : U0cuYTRsVG9TUXhURnlBRWRIYnRNM1p2dy5odnIyZTMzTlJkLWYwQ2dmVkFCaFZCRHBJV01OcVdwalVZZG5NSUhVUkNZ");
        Client.out.println("U0cuYTRsVG9TUXhURnlBRWRIYnRNM1p2dy5odnIyZTMzTlJkLWYwQ2dmVkFCaFZCRHBJV01OcVdwalVZZG5NSUhVUkNZ");
        Client.out.flush();
        temp = Client.in.readLine();
        System.out.println("S : " + temp);
        Client.timer.cancel();

        //to next state
        smtp.setCurrentState(smtp.getHeloState());
    }

    @Override
    public void mailFrom() throws IOException {
        Client.fileReader = new BufferedReader(new FileReader(Client.file));
        String temp = Client.fileReader.readLine();
        Client.callTimer(20000);
        System.out.println("C : mail from:<" + temp + ">");
        Client.out.println("mail from:<" + temp + ">");
        Client.out.flush();
        temp = Client.in.readLine();
        System.out.println("S : " + temp);
        Client.timer.cancel();
    }

    @Override
    public void rcptTo() throws IOException {
        Client.fileReader = new BufferedReader(new FileReader(Client.file));
        Client.fileReader.readLine();
        String reciever = Client.fileReader.readLine();
        while (reciever != null) {
            Client.callTimer(20000);
            System.out.println("C : rcpt to:<" + reciever + ">");
            Client.out.println("rcpt to:<" + reciever + ">");
            Client.out.flush();
            String temp = Client.in.readLine();
            System.out.println("S : " + temp);
            Client.timer.cancel();
            reciever = Client.fileReader.readLine();
        }
    }

    @Override
    public void data() throws IOException {
        Client.callTimer(20000);
        System.out.println("C : DATA");
        Client.out.println("DATA");
        Client.out.flush();
        String temp = Client.in.readLine();
        System.out.println("S : " + temp);
        Client.timer.cancel();
    }

    public String toString() {
        return "BeginState";
    }
}
