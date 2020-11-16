import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.lang.ref.Cleaner;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class rcptToState implements smtpStates {

    smtpMachine smtp;

    public rcptToState(smtpMachine smtp) {
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
        Client.fileReader = new BufferedReader(new FileReader(Client.file));
        String header="";
        header="From: Mr. Sender <"+ Client.fileReader.readLine()+">\n";
        temp = Client.fileReader.readLine();
        while(temp!=null) {
            header += "To: Mr. A <" + temp + ">\n";
            temp = Client.fileReader.readLine();
        }
        header+="Subject: Subject of mail.";
        System.out.println("C : "+header);
        Client.callTimer(20000);
        Client.out.println(header);
        Client.out.flush();
        Client.timer.cancel();
        //
        Client.fileReader = new BufferedReader(new FileReader(Client.file2));
        String msg = Client.fileReader.readLine();
        String totalMsg="";
        while (msg!=null){
            msg+="\n";
            totalMsg+=msg;
            msg=Client.fileReader.readLine();
        }
        System.out.println("\n"+totalMsg);
        Client.callTimer(20000);
        Client.out.println("\n"+totalMsg);
        Client.out.flush();
        Client.out.println(".\r\n");
        Client.out.flush();
        Client.timer.cancel();
        temp = Client.in.readLine();
        System.out.println("S : " + temp);
        System.exit(0);
    }

    public String toString() {
        return "RcptToState";
    }
}
