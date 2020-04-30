/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Client;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

/**
 *
 * @author Sanjay
 */
public class Sub {
    String nam;
    
    
    Sub(String str){
        nam=str;
        try {
            createStage();
        } catch (IOException ex) {
            Logger.getLogger(Sub.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    void showMessage(String text){
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("ClientUI.fxml"));
        ClientUIController controller = loader.getController();
        controller.userAppend(text);
    }
    
    void getMessage(){
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("ClientUI.fxml"));
        ClientUIController controller = loader.getController();
    }
    
    
    void createStage() throws IOException{
        Stage subStage = new Stage();
        subStage.setTitle(nam);
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("ClientUI.fxml"));
        Pane root = loader.load();
        ClientUIController controller = loader.getController();
        controller.display.setText(nam);
        Scene sc = new Scene(root);
        subStage.setScene(sc);
        subStage.show();
    }
}
