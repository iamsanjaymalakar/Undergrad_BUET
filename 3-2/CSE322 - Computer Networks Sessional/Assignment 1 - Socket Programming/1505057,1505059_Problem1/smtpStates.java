import java.io.FileNotFoundException;
import java.io.IOException;

public interface smtpStates {
    void helo() throws IOException;
    void mailFrom() throws IOException;
    void rcptTo() throws IOException;
    void data() throws IOException;
}
