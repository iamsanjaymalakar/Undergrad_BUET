/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hmsserver;

import resources.Doctor;
import resources.Patient;
import resources.NetworkUtil;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.StrokeType;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

/**
 *
 * @author Sanjay
 */
public class HMSServer extends Application {

    Stage stage;
    HashMap<String, String> hmIdPass;
    HashMap<String, Patient> hmPaitent;
    HashMap<String, Doctor> hmDoctor;
    HashMap<String, NetworkUtil> hmNet;
    public HashMap<String, String> online;
    ListView logsList;
    ListView idPassList;
    ListView docsList;
    ListView paitList;
    ListView onlineList;
    DataStore data;
    Text text;
    int textError = 1;

    public HMSServer() {
        hmIdPass = new HashMap<>();
        hmPaitent = new HashMap<>();
        hmDoctor = new HashMap<>();
        hmNet = new HashMap<>();
        logsList = new ListView();
        idPassList = new ListView();
        docsList = new ListView();
        paitList = new ListView();
        onlineList = new ListView();
        data = new DataStore();
        online = new HashMap<>();
    }

    @Override
    public void start(Stage primaryStage) {
        stage = primaryStage;
        //timer
        timer();
        //updatingData
        updateData();
        //initiallly showing id and password
        updateScene(idPassList);
        // you gotta implement listview autoscroll
        Server server = new Server(logsList, docsList, paitList, idPassList, hmNet, hmIdPass, hmDoctor, hmPaitent, online, onlineList);
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

    void updateScene(ListView list) {
        // mainPane
        AnchorPane mainPane = new AnchorPane();
        mainPane.setPrefHeight(890.0);
        mainPane.setPrefWidth(1280);
        mainPane.setStyle("-fx-border-color: #000000; -fx-border-width: 2; -fx-border-style: solid;");
        // titlePane
        AnchorPane titlePane = new AnchorPane();
        titlePane.setPrefHeight(159.0);
        titlePane.setPrefWidth(1280.0);
        titlePane.setStyle("-fx-background-color: #000000; -fx-border-color: #000000;");
        AnchorPane.setBottomAnchor(titlePane, 730.0);
        AnchorPane.setTopAnchor(titlePane, 0.0);
        AnchorPane.setLeftAnchor(titlePane, 0.0);
        AnchorPane.setRightAnchor(titlePane, 0.0);
        // Logo Image
        ImageView logo = new ImageView();
        Image Logo = new Image("serverLogo.png");
        logo.setFitHeight(78.0);
        logo.setFitWidth(465.0);
        logo.setLayoutX(203.0);
        logo.setLayoutY(42.0);
        logo.setImage(Logo);
        // icon Image
        ImageView icon = new ImageView();
        Image Icon = new Image("icon.png");
        icon.setFitHeight(105.0);
        icon.setFitWidth(137.0);
        icon.setLayoutX(61.0);
        icon.setLayoutY(28.0);
        icon.setImage(Icon);
        // adding 2 image to titilePane
        titlePane.getChildren().addAll(logo, icon);
        // rightpane
        AnchorPane rightPane = new AnchorPane();
        rightPane.setLayoutX(463.0);
        rightPane.setLayoutY(159.0);
        rightPane.setPrefHeight(78.0);
        rightPane.setPrefWidth(817.0);
        rightPane.setStyle("-fx-background-color: #000000;");
        AnchorPane.setBottomAnchor(rightPane, 650.0);
        AnchorPane.setTopAnchor(rightPane, 157.0);
        AnchorPane.setLeftAnchor(rightPane, 460.0);
        AnchorPane.setRightAnchor(rightPane, 0.0);
        // logs text
        Text logs = new Text();
        logs.setFill(Color.web("#0f9fff"));
        logs.setLayoutX(354.0);
        logs.setLayoutY(52.0);
        logs.setStrokeType(StrokeType.OUTSIDE);
        logs.setStrokeWidth(0.0);
        logs.setText("L O G S");
        logs.setFont(new Font(42));
        // adding text to pane
        rightPane.getChildren().add(logs);
        // dataPane
        AnchorPane dataPane = new AnchorPane();
        dataPane.setLayoutX(0);
        dataPane.setLayoutY(161.0);
        dataPane.setPrefHeight(729.0);
        dataPane.setPrefWidth(458.0);
        dataPane.setStyle("-fx-background-color: #d1fff1;");
        AnchorPane.setBottomAnchor(dataPane, 2.0);
        AnchorPane.setTopAnchor(dataPane, 159.0);
        AnchorPane.setLeftAnchor(dataPane, 2.0);
        // scroll
        ScrollPane sp = new ScrollPane();
        sp.setLayoutX(8.0);
        sp.setLayoutY(142.0);
        sp.setPrefHeight(578.0);
        sp.setPrefWidth(444.0);
        sp.setStyle("-fx-background-color: #f7e6af;");
        list.setPrefHeight(575);
        list.setPrefWidth(441);
        sp.setContent(list);
        // text
        Text show = new Text();
        show.setLayoutX(14);
        show.setLayoutY(113);
        show.setStrokeType(StrokeType.OUTSIDE);
        show.setStrokeWidth(0);
        switch (textError) {
            case 1:
                show.setText("Users");
                break;
            case 2:
                show.setText("Doctor");
                break;
            case 3:
                show.setText("Paitent");
                break;
            case 4:
                show.setText("Online Doctors");
            default:
                break;
        }
        show.setFont(new Font(53));
        // buttons
        Button users = new Button();
        users.setLayoutX(26);
        users.setLayoutY(15);
        users.setText("Users");
        users.setMnemonicParsing(false);
        users.setOnMouseClicked((MouseEvent event) -> {
            textError = 1;
            updateScene(idPassList);
        });
        Button ref = new Button();
        ref.setLayoutX(300);
        ref.setLayoutY(15);
        ref.setText("Online");
        ref.setMnemonicParsing(false);
        ref.setOnMouseClicked((MouseEvent event) -> {
            textError = 4;
            updateScene(onlineList);
        });
        Button docs = new Button();
        docs.setLayoutX(110);
        docs.setLayoutY(15);
        docs.setText("Doctors");
        docs.setMnemonicParsing(false);
        docs.setOnMouseClicked((MouseEvent event) -> {
            textError = 2;
            updateScene(docsList);
        });
        Button paits = new Button();
        paits.setLayoutX(208);
        paits.setLayoutY(15);
        paits.setText("Paitents");
        paits.setMnemonicParsing(false);
        paits.setOnMouseClicked((MouseEvent event) -> {
            textError = 3;
            updateScene(paitList);
        });
        // adding to pane
        dataPane.getChildren().addAll(sp, show, ref, docs, paits, users);
        // logs pane
        ScrollPane logsPane = new ScrollPane();
        logsPane.setLayoutX(873);
        logsPane.setLayoutY(241);
        logsPane.setPrefHeight(649);
        logsPane.setPrefWidth(615);
        AnchorPane.setBottomAnchor(logsPane, 0.0);
        AnchorPane.setTopAnchor(logsPane, 238.0);
        AnchorPane.setLeftAnchor(logsPane, 460.0);
        AnchorPane.setRightAnchor(logsPane, 0.0);
        logsList.setPrefHeight(646);
        logsList.setPrefWidth(814);
        logsList.setStyle("-fx-background-color: #73a4f4;");
        logsPane.setContent(logsList);
        // Adding all to mainPane
        mainPane.getChildren().addAll(titlePane, rightPane, dataPane, logsPane);
        //scene
        Scene scene = new Scene(mainPane);
        stage.setTitle("HMS Server");
        stage.setScene(scene);
        stage.show();
        stage.getIcons().add(new Image("icon.png"));
        stage.setOnCloseRequest((WindowEvent t) -> {
            Platform.exit();
            System.exit(0);
        });
    }

    public void updateData() {
        hmIdPass = data.getFile("hmIdPass");
        hmPaitent = data.getFile("hmPaitent");
        hmDoctor = data.getFile("hmDoctor");
        //id pass
        for (HashMap.Entry<String, String> entry : hmIdPass.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            listAdd(idPassList, key, 24, Color.BLACK);
        }
        //doctors
        for (HashMap.Entry<String, Doctor> entry : hmDoctor.entrySet()) {
            String key = entry.getKey();
            Doctor value = entry.getValue();
            listAdd(docsList, value.name + "(" + value.dept + ")", 24, Color.BLACK);
        }
        //paitents
        for (HashMap.Entry<String, Patient> entry : hmPaitent.entrySet()) {
            String key = entry.getKey();
            Patient value = entry.getValue();
            listAdd(paitList, value.name, 24, Color.BLACK);
        }
    }

    public void listAdd(ListView list, String s, int size, Color color) {
        Platform.runLater(() -> {
            text = new Text(s);
            text.setFont(new Font(size));
            text.setFill(color);
            list.getItems().add(text);
        });
    }

    public void timer() {
        new Timer().schedule(
                new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> {
                    onlineList.getItems().clear();
                });
                for (HashMap.Entry<String, String> entry : online.entrySet()) {
                    String key = entry.getKey();
                    String value = entry.getValue();
                    listAdd(onlineList, value, 24, Color.BLACK);
                }
            }
        }, 0, 1000);
    }

}
