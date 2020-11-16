import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class mailFromState implements smtpStates {

    smtpMachine smtp;

    mailFromState(smtpMachine smtp) {
        this.smtp = smtp;
    }

    @Override
    public void helo() throws IOException {
        Client.callTimer(20000);
        System.out.println("C : HELO " + InetAddress.getLocalHost().getHostName());
        Client.out.println("HELO " + InetAddress.getLocalHost().getHostName());
        Client.out.flush();
        String temp = Client.in.readLine();
        System.out.println("S : " + temp);
        Client.timer.cancel();
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
        String reciever = Client.fileReader.readLine(),temp="";
        while(reciever!=null) {
            Client.callTimer(20000);
            System.out.println("C : rcpt to:<"+reciever+">");
            Client.out.println("rcpt to:<"+reciever+">");
            Client.out.flush();
            temp = Client.in.readLine();
            System.out.println("S : " + temp);
            Client.timer.cancel();
            reciever=Client.fileReader.readLine();
        }
        // next state
        if(temp.charAt(0)=='2')
            smtp.setCurrentState(smtp.getRcptToState());
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
        return "MailFromState";
    }
}
