package Client;

import java.io.IOException;
import java.net.SocketException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

/**
 *
 * @author user
 */

public class Client extends Application {
    static Stage stage;
    String userNameX,IPX,serverNameX;
         int portX;
    @Override
    public void start(Stage primaryStage) throws IOException { 
       stage = primaryStage;
       FXMLLoader loader=new FXMLLoader();
       loader.setLocation(getClass().getResource("ClientStart.fxml")); 
       Pane root=loader.load();
       Scene sc=new Scene(root);
       primaryStage.setScene(sc);
       primaryStage.setTitle("Chat Client");
       primaryStage.show(); 
    }
    

    public static void main(String[] args) throws SocketException, IOException {
        
        String userName,IP,serverName;
         int port;
         if(args.length==0){
             userName="Sanjay";
             IP="localhost";
             serverName="Server";
             port=12345;
         }
         else{
             userName=args[0];
             port=Integer.parseInt(args[1]);
             IP=args[2];
             serverName=args[3];
         }
         
         // Starting Client Send And Recieve 
         ClientSend x= new ClientSend(userName,port,IP,serverName);
         ClientRecieve y= new ClientRecieve(userName,port,IP,serverName);
        
        launch(args);
    }
    
}
