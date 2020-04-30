package Client;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;

/**
 *
 * @author Sanjay
 */
public class ClientStartController implements Initializable{

    @FXML
    private TextField getUser;

    @FXML
    private ImageView closeImage;
    
    ClientClass cc = new ClientClass();

    @FXML
    void keyPress(KeyEvent event) throws IOException {
            if (event.getCode().equals(KeyCode.ENTER)) {
            if(getUser.getText().length()!=0){
                cc.entry(getUser.getText());
                getUser.clear();
            }
        }
    }

    @FXML
    void screenClose(MouseEvent event) {
            
    }

    @FXML
    void clicked(MouseEvent event) throws IOException {
             if(getUser.getText().length()!=0){
               cc.entry(getUser.getText());
               
            }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
       
    }

}
