package Client;

import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;

public class ClientUIController {
    @FXML
    public Text display;
    @FXML
    public TextField input= new TextField("");
    @FXML
    public Button sendButton;
    @FXML
    public ListView chatPane; 
    @FXML
    private ImageView emo01;
    boolean bool=false;
   public String id="",text="",temp="",f="assa$sacs";
    
    
    @FXML
    public void click(MouseEvent e) throws Exception {
      {
            ownAppend();
        }
    }

    @FXML
    void keyPressed(KeyEvent e) throws Exception {
        if (e.getCode().equals(KeyCode.ENTER)) {
            ownAppend();
        }
    }
    
    @FXML
    void emo01(MouseEvent event) {
          
    }

    public void ownAppend() throws Exception {
        HBox x = new HBox();
        x.setMaxWidth(chatPane.getWidth() - 20);
        x.setAlignment(Pos.TOP_RIGHT);
        text=input.getText();
        Label l= new Label(text);
        id=display.getText();
        l.setStyle("-fx-background-color:  #90ee90 ; -fx-padding: 6px; -fx-font-size: 1em; -fx-background-radius: 3 15 0 15;  -fx-padding: 5 10 5 10 ;");
        x.getChildren().add(l);
        chatPane.getItems().add(x);
        input.clear();
        temp=id+"$"+text;
        bool=true;
    }

    
    String get(){
        return temp;
    }
    
    public void userAppend(String msg) {
        HBox x = new HBox();
        x.setMaxWidth(chatPane.getWidth() - 20);
        x.setAlignment(Pos.TOP_LEFT);
        Label l= new Label(msg);
        l.setStyle("-fx-background-color: #66b8ff ;-fx-padding:6px;-fx-font-size:1em;-fx-background-radius:15 3 15 0;-fx-padding: 5 10 5 10 ;");
        x.getChildren().add(l);
        chatPane.getItems().add(x);
    } 
    
}
