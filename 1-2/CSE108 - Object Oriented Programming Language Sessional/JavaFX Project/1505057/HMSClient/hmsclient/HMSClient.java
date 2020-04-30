/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hmsclient;

import java.io.File;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.PasswordField;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Duration;
import resources.Appointment;
import resources.Doctor;
import resources.Message;
import resources.NetworkUtil;
import resources.Patient;
import resources.TimeSpinner;

/**
 *
 * @author Sanjay
 */
public class HMSClient extends Application {

    static Stage stage;
    static Alert alert;
    static String serverAddress;
    static int serverPort;
    static NetworkUtil nc;
    static Message msg;
    static Doctor doctor;
    static Patient patient;
    static int sceneNum = 1;
    static int subsceneNum = 1;
    static Appointment appointment;
    static HashMap<String, String> online;
    static Popup popup = new Popup();
    static Popup popupp = new Popup();
    static Popup patpopup = new Popup();
    static ListView<Appointment> pendingListDoctor;
    static ListView<Appointment> approvedListDoctor;
    static ListView<Appointment> pendingListPatient;
    static ListView<Appointment> approvedListPatient;
    static ListView<String> onlineList;
    static int comboFlag = 0;
    static String docName = "";
    static Media notiMusic = new Media(new File("noti.mp3").toURI().toString());
    static MediaPlayer mediaPlayer;
    static DropShadow borderGlow = new DropShadow();

    public HMSClient() {
        serverAddress = "127.0.0.1";
        serverPort = 33333;
        doctor = new Doctor();
        patient = new Patient();
        appointment = new Appointment();
        online = new HashMap<>();
        approvedListPatient = new ListView<Appointment>();
        pendingListPatient = new ListView<Appointment>();
        approvedListDoctor = new ListView<Appointment>();
        pendingListDoctor = new ListView<Appointment>();
        onlineList = new ListView<String>();
        borderGlow.setOffsetY(0f);
        borderGlow.setOffsetX(0f);
        borderGlow.setColor(Color.CORNFLOWERBLUE);
        borderGlow.setWidth(40);
        borderGlow.setHeight(40);
    }

    @Override
    public void start(Stage primaryStage) {
        stage = primaryStage;
        nc = new NetworkUtil(serverAddress, serverPort);
        // login
        loginScene();
        // recieving thread
        ClientThread clientthread = new ClientThread(nc, alert, doctor, patient);
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

    public static void popupPatient(Appointment app, double x, double y) {
        AnchorPane pane = new AnchorPane();
        pane.setPrefHeight(391);
        pane.setPrefWidth(486);
        pane.setStyle("-fx-background-color: #99c8ff;");
        Text appT = new Text("Appointment :");
        appT.setLayoutX(26);
        appT.setLayoutY(42);
        appT.setFont(new Font("System Bold", 25));
        Text ptimeT = new Text("Time : " + app.time);
        ptimeT.setLayoutX(26);
        ptimeT.setLayoutY(368);
        ptimeT.setFont(new Font("System Bold", 30));
        Text pdateT = new Text("Date : " + app.date);
        pdateT.setLayoutX(26);
        pdateT.setLayoutY(327);
        pdateT.setFont(new Font("System Bold", 30));
        ScrollPane sc = new ScrollPane();
        sc.setLayoutX(26);
        sc.setLayoutY(57);
        sc.setPrefHeight(224);
        sc.setPrefWidth(442);
        sc.setStyle("-fx-background-color: #99c8ff;");
        AnchorPane scpane = new AnchorPane();
        scpane.setPrefHeight(417);
        scpane.setPrefWidth(423);
        scpane.setStyle("-fx-background-color: #bad9fc;");
        Text namep = new Text("Doctor Name : " + app.doctor.name);
        namep.setLayoutX(9);
        namep.setLayoutY(32);
        namep.setFont(new Font(24));
        Text dobp = new Text("Doctor Address : " + app.doctor.address);
        dobp.setLayoutX(9);
        dobp.setLayoutY(64);
        dobp.setFont(new Font(24));
        Text sexp = new Text("Doctor Sex : " + app.doctor.sex);
        sexp.setLayoutX(9);
        sexp.setLayoutY(96);
        sexp.setFont(new Font(24));
        Text nump = new Text("Doctot Contact No : " + app.doctor.phone);
        nump.setLayoutX(9);
        nump.setLayoutY(128);
        nump.setFont(new Font(24));
        Text appp = new Text("Appointment for : " + app.title);
        appp.setLayoutX(9);
        appp.setLayoutY(160);
        appp.setFont(new Font(24));
        Text desp = new Text("Description : " + app.description);
        desp.setLayoutX(9);
        desp.setLayoutY(192);
        desp.setFont(new Font(24));
        scpane.getChildren().addAll(namep, dobp, sexp, nump, appp, desp);
        sc.setContent(scpane);
        pane.getChildren().addAll(appT, sc, ptimeT, pdateT);
        patpopup.setX(x);
        patpopup.setY(y);
        patpopup.getContent().addAll(pane);
        patpopup.show(stage);
        ScaleTransition st = new ScaleTransition(Duration.millis(500), pane);
        st.setByX(1);
        st.setByY(1);
        st.setToX(1.05);
        st.setToY(1.05);
        st.setAutoReverse(false);
        st.play();
        FadeTransition ft = new FadeTransition(Duration.millis(500), pane);
        ft.setFromValue(0);
        ft.setToValue(1);
        ft.setCycleCount(1);
        ft.setAutoReverse(false);
        ft.play();
    }

    public static void popupDocUnseen(Appointment app, double x, double y) {
        AnchorPane pane = new AnchorPane();
        pane.setPrefHeight(514);
        pane.setPrefWidth(486);
        pane.setStyle("-fx-background-color: #99c8ff;");
        Text appT = new Text("Confirm Appointment :");
        appT.setLayoutX(26);
        appT.setLayoutY(42);
        appT.setFont(new Font("System Bold", 25));
        Text ptimeT = new Text("Preferred Time : " + app.time);
        ptimeT.setLayoutX(26);
        ptimeT.setLayoutY(316);
        ptimeT.setFont(new Font("System Bold", 22));
        Text pdateT = new Text("Preferred Date : " + app.date);
        pdateT.setLayoutX(26);
        pdateT.setLayoutY(346);
        pdateT.setFont(new Font("System Bold", 22));
        Text confirmT = new Text("Confirm date and time:");
        confirmT.setLayoutX(26);
        confirmT.setLayoutY(379);
        confirmT.setFont(new Font("System Bold", 25));
        DatePicker datepic = new DatePicker();
        datepic.setValue(LocalDate.parse(app.date, DateTimeFormatter.ofPattern("dd-MM-yyyy")));
        datepic.setEditable(false);
        datepic.setLayoutX(113);
        datepic.setLayoutY(389);
        datepic.setPrefHeight(31);
        datepic.setPrefWidth(291);
        datepic.setEffect(borderGlow);
        Text dateT = new Text("Date :");
        dateT.setLayoutX(26);
        dateT.setLayoutY(412);
        dateT.setFont(new Font(22));
        String[] hhmm = app.time.split(" ", 2);
        String a = hhmm[1];
        String[] hhmms = hhmm[0].split(":", 2);
        String hh = hhmms[0];
        String mm = hhmms[1];
        ComboBox<String> hhc = new ComboBox<String>();
        ComboBox<String> mmc = new ComboBox<String>();
        ComboBox<String> ac = new ComboBox<String>();
        hhc.setLayoutX(113);
        mmc.setLayoutX(80 + 113);
        ac.setLayoutX(145 + 133);
        hhc.setLayoutY(430);
        mmc.setLayoutY(430);
        ac.setLayoutY(430);
        for (int i = 1; i <= 12; i++) {
            if (i < 10) {
                hhc.getItems().add("0" + String.valueOf(i));
            } else {
                hhc.getItems().add(String.valueOf(i));
            }
        }
        for (int i = 00; i <= 60; i++) {
            if (i < 10) {
                mmc.getItems().add("0" + String.valueOf(i));
            } else {
                mmc.getItems().add(String.valueOf(i));
            }
        }
        hhc.setValue(hh);
        mmc.setValue(mm);
        ac.setValue(a);
        ac.getItems().add("AM");
        ac.getItems().add("PM");
        hhc.setEffect(borderGlow);
        mmc.setEffect(borderGlow);
        ac.setEffect(borderGlow);
        Text timeT = new Text("Time :");
        timeT.setLayoutX(26);
        timeT.setLayoutY(454);
        timeT.setFont(new Font(22));
        Button submit = new Button("Confirm");
        submit.setLayoutX(331);
        submit.setLayoutY(469);
        ScrollPane sc = new ScrollPane();
        sc.setLayoutX(26);
        sc.setLayoutY(57);
        sc.setPrefHeight(224);
        sc.setPrefWidth(442);
        sc.setStyle("-fx-background-color: #99c8ff;");
        AnchorPane scpane = new AnchorPane();
        scpane.setPrefHeight(417);
        scpane.setPrefWidth(423);
        scpane.setStyle("-fx-background-color: #bad9fc;");
        Text namep = new Text("Name : " + app.patient.name);
        namep.setLayoutX(9);
        namep.setLayoutY(32);
        namep.setFont(new Font(24));
        Text dobp = new Text("Date of birth : " + app.patient.dob);
        dobp.setLayoutX(9);
        dobp.setLayoutY(64);
        dobp.setFont(new Font(24));
        Text sexp = new Text("Sex : " + app.patient.sex);
        sexp.setLayoutX(9);
        sexp.setLayoutY(96);
        sexp.setFont(new Font(24));
        Text nump = new Text("Contact No : " + app.patient.number);
        nump.setLayoutX(9);
        nump.setLayoutY(128);
        nump.setFont(new Font(24));
        Text appp = new Text("Appointment for : " + app.title);
        appp.setLayoutX(9);
        appp.setLayoutY(160);
        appp.setFont(new Font(24));
        Text desp = new Text("Description : " + app.description);
        desp.setLayoutX(9);
        desp.setLayoutY(192);
        desp.setFont(new Font(24));
        scpane.getChildren().addAll(namep, dobp, sexp, nump, appp, desp);
        sc.setContent(scpane);
        pane.getChildren().addAll(appT, confirmT, sc, ptimeT, pdateT, dateT, datepic, timeT, hhc, mmc, ac, submit);
        //submit button action
        submit.setOnMouseClicked((MouseEvent event) -> {
            msg = new Message();
            msg.type = 4;
            msg.successType = 1;
            app.date = datepic.getValue().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
            app.time = hhc.getValue() + ":" + mmc.getValue() + " " + ac.getValue();
            app.seen = true;
            msg.appointment = new Appointment();
            msg.appointment = app;
            nc.send(msg);
            popup.hide();
            doctorScene();
        });
        popup.setX(x);
        popup.setY(y);
        popup.getContent().addAll(pane);
        popup.show(stage);
        ScaleTransition st = new ScaleTransition(Duration.millis(500), pane);
        st.setByX(1);
        st.setByY(1);
        st.setToX(1.05);
        st.setToY(1.05);
        st.setAutoReverse(false);
        st.play();
        FadeTransition ft = new FadeTransition(Duration.millis(500), pane);
        ft.setFromValue(0);
        ft.setToValue(1);
        ft.setCycleCount(1);
        ft.setAutoReverse(false);
        ft.play();
    }

    public static void popupDocSeen(Appointment app, double x, double y) {
        AnchorPane pane = new AnchorPane();
        pane.setPrefHeight(391);
        pane.setPrefWidth(486);
        pane.setStyle("-fx-background-color: #99c8ff;");
        Text appT = new Text("Appointment :");
        appT.setLayoutX(26);
        appT.setLayoutY(42);
        appT.setFont(new Font("System Bold", 25));
        Text ptimeT = new Text("Time : " + app.time);
        ptimeT.setLayoutX(26);
        ptimeT.setLayoutY(368);
        ptimeT.setFont(new Font("System Bold", 30));
        Text pdateT = new Text("Date : " + app.date);
        pdateT.setLayoutX(26);
        pdateT.setLayoutY(327);
        pdateT.setFont(new Font("System Bold", 30));
        ScrollPane sc = new ScrollPane();
        sc.setLayoutX(26);
        sc.setLayoutY(57);
        sc.setPrefHeight(224);
        sc.setPrefWidth(442);
        sc.setStyle("-fx-background-color: #99c8ff;");
        AnchorPane scpane = new AnchorPane();
        scpane.setPrefHeight(417);
        scpane.setPrefWidth(423);
        scpane.setStyle("-fx-background-color: #bad9fc;");
        Text namep = new Text("Name : " + app.patient.name);
        namep.setLayoutX(9);
        namep.setLayoutY(32);
        namep.setFont(new Font(24));
        Text dobp = new Text("Date of birth : " + app.patient.dob);
        dobp.setLayoutX(9);
        dobp.setLayoutY(64);
        dobp.setFont(new Font(24));
        Text sexp = new Text("Sex : " + app.patient.sex);
        sexp.setLayoutX(9);
        sexp.setLayoutY(96);
        sexp.setFont(new Font(24));
        Text nump = new Text("Contact No : " + app.patient.number);
        nump.setLayoutX(9);
        nump.setLayoutY(128);
        nump.setFont(new Font(24));
        Text appp = new Text("Appointment for : " + app.title);
        appp.setLayoutX(9);
        appp.setLayoutY(160);
        appp.setFont(new Font(24));
        Text desp = new Text("Description : " + app.description);
        desp.setLayoutX(9);
        desp.setLayoutY(192);
        desp.setFont(new Font(24));
        scpane.getChildren().addAll(namep, dobp, sexp, nump, appp, desp);
        sc.setContent(scpane);
        pane.getChildren().addAll(appT, sc, ptimeT, pdateT);
        pane.setOpacity(0);
        popupp.setX(x);
        popupp.setY(y);
        popupp.getContent().addAll(pane);
        popupp.show(stage);
        ScaleTransition st = new ScaleTransition(Duration.millis(500), pane);
        st.setByX(1);
        st.setByY(1);
        st.setToX(1.05);
        st.setToY(1.05);
        st.setAutoReverse(false);
        st.play();
        FadeTransition ft = new FadeTransition(Duration.millis(500), pane);
        ft.setFromValue(0);
        ft.setToValue(1);
        ft.setCycleCount(1);
        ft.setAutoReverse(false);
        ft.play();
    }

    public static void loginScene() {
        //main pane
        AnchorPane mainPane = new AnchorPane();
        mainPane.setPrefHeight(795);
        mainPane.setPrefWidth(1089);
        mainPane.setStyle("-fx-background-color: #e2fdff;");
        //titlepane
        AnchorPane titlePane = new AnchorPane();
        titlePane.setPrefHeight(136);
        titlePane.setPrefWidth(1089);
        titlePane.setLayoutX(299);
        titlePane.setLayoutY(26);
        titlePane.setStyle("-fx-background-color: #ffffff;");
        AnchorPane.setTopAnchor(titlePane, 0.0);
        AnchorPane.setBottomAnchor(titlePane, 659.0);
        AnchorPane.setLeftAnchor(titlePane, 0.0);
        AnchorPane.setRightAnchor(titlePane, 0.0);
        //title text
        Text title = new Text();
        title.setText("Hospital\nManagement\nSystem");
        title.setLayoutX(128);
        title.setLayoutY(42);
        Font f = new Font("System Bold", 24);
        title.setFont(f);
        //image
        Image img = new Image("BinaryContent/logo.png");
        ImageView imgv = new ImageView(img);
        imgv.setFitHeight(78);
        imgv.setFitWidth(73);
        imgv.setLayoutX(36);
        imgv.setLayoutY(26);
        titlePane.getChildren().addAll(title, imgv);
        //login form
        Text log = new Text("Login");
        log.setLayoutX(257.0);
        log.setLayoutY(256.0);
        f = new Font("System Bold", 48);
        log.setFont(f);
        TextField user = new TextField();
        user.setLayoutX(257);
        user.setLayoutY(338);
        user.setPrefHeight(31);
        user.setPrefWidth(300);
        user.setEffect(borderGlow);
        user.setPromptText("Username");
        Text t1 = new Text("Username :");
        t1.setLayoutX(257);
        t1.setLayoutY(321);
        f = new Font("System Bold", 22);
        t1.setFont(f);
        Text t2 = new Text("Password :");
        t2.setLayoutX(257);
        t2.setLayoutY(408);
        f = new Font("System Bold", 22);
        t2.setFont(f);
        PasswordField pass = new PasswordField();
        pass.setLayoutX(257);
        pass.setLayoutY(423);
        pass.setPrefHeight(31);
        pass.setPrefWidth(300);
        pass.setPromptText("Password");
        pass.setEffect(borderGlow);
        RadioButton docB = new RadioButton("Doctor");
        docB.setLayoutX(352);
        docB.setLayoutY(478);
        docB.setEffect(borderGlow);
        RadioButton paitB = new RadioButton("Patient");
        paitB.setLayoutX(257);
        paitB.setLayoutY(478);
        paitB.setSelected(true);
        paitB.setEffect(borderGlow);
        ToggleGroup toggleB = new ToggleGroup();
        docB.setToggleGroup(toggleB);
        paitB.setToggleGroup(toggleB);
        //login button
        Button loginButton = new Button("Login");
        loginButton.setLayoutX(485);
        loginButton.setLayoutY(521);
        loginButton.setOnMouseClicked((MouseEvent event) -> {
            boolean okay = true;
            if ("".equals(user.getText()) && "".equals(pass.getText())) {
                okay = false;
                errorAlert("Enter username and password!", "Enter your username and password.", null);
            } else if ("".equals(user.getText())) { // login message
                okay = false;
                errorAlert("Enter username !", "Enter your username.", null);
            } else if ("".equals(pass.getText())) {
                okay = false;
                errorAlert("Enter password !", "Enter your password", null);
            }
            if (okay) {
                msg = new Message();
                msg.type = 1;
                msg.login.username = user.getText();
                msg.login.password = pass.getText();
                if (docB.isSelected()) {
                    msg.login.type = 1;
                    doctor.userId = user.getText();
                } else {
                    msg.login.type = 2;
                    patient.userId = user.getText();
                }
                nc.send(msg);
                user.clear();
                pass.clear();
            }

        });
        Text regis = new Text("Registration");
        regis.setLayoutX(714);
        regis.setLayoutY(352);
        f = new Font("System Bold", 27);
        regis.setFont(f);
        Text fpass = new Text("Forget Password");
        fpass.setLayoutX(714);
        fpass.setLayoutY(411);
        f = new Font("System Bold", 27);
        fpass.setFont(f);
        regis.setOnMouseClicked((MouseEvent event) -> {
            registrationScene();
        });
        regis.setOnMouseEntered((MouseEvent event) -> {
            regis.setFill(Color.CORNFLOWERBLUE);
            regis.setFont(new Font("System Bold", 33));
        });
        regis.setOnMouseExited((MouseEvent event) -> {
            regis.setFill(Color.BLACK);
            regis.setFont(new Font("System Bold", 27));
        });
        fpass.setOnMouseClicked((MouseEvent event) -> {
            forgetPassScene();
        });
        fpass.setOnMouseEntered((MouseEvent event) -> {
            fpass.setFill(Color.CORNFLOWERBLUE);
            fpass.setFont(new Font("System Bold", 33));
        });
        fpass.setOnMouseExited((MouseEvent event) -> {
            fpass.setFill(Color.BLACK);
            fpass.setFont(new Font("System Bold", 27));
        });
        mainPane.getChildren().addAll(titlePane, log, user, t1, pass, t2, paitB, docB, loginButton, regis, fpass);
        mainPane.getStylesheets().add(HMSClient.class.getResource("button.css").toExternalForm());
        Scene scene = new Scene(mainPane);
        stage.setScene(scene);
        stage.setTitle("Hospital Management System : Login");
        stage.show();
        stage.getIcons().add(new Image("BinaryContent/icon.png"));
        stage.setOnCloseRequest((WindowEvent t) -> {
            Platform.exit();
            System.exit(0);
        });
    }

    public static void registrationScene() {
        // main pane
        AnchorPane mainPane = new AnchorPane();
        mainPane.setPrefHeight(795);
        mainPane.setPrefWidth(1089);
        mainPane.setStyle("-fx-background-color: #e2fdff;");
        //titlepane
        AnchorPane titlePane = new AnchorPane();
        titlePane.setPrefHeight(136);
        titlePane.setPrefWidth(1089);
        titlePane.setLayoutX(299);
        titlePane.setLayoutY(26);
        titlePane.setStyle("-fx-background-color: #ffffff;");
        AnchorPane.setTopAnchor(titlePane, 0.0);
        AnchorPane.setBottomAnchor(titlePane, 659.0);
        AnchorPane.setLeftAnchor(titlePane, 0.0);
        AnchorPane.setRightAnchor(titlePane, 0.0);
        //title text
        Text title = new Text();
        title.setText("Hospital\nManagement\nSystem");
        title.setLayoutX(128);
        title.setLayoutY(42);
        Font f = new Font("System Bold", 24);
        title.setFont(f);
        // image
        Image img = new Image("BinaryContent/logo.png");
        ImageView imgv = new ImageView(img);
        imgv.setFitHeight(78);
        imgv.setFitWidth(73);
        imgv.setLayoutX(36);
        imgv.setLayoutY(26);
        titlePane.getChildren().addAll(title, imgv);
        //login form
        Text log = new Text("Registration");
        log.setLayoutX(257.0);
        log.setLayoutY(256.0);
        f = new Font("System Bold", 48);
        log.setFont(f);
        TextField user = new TextField();
        user.setLayoutX(257);
        user.setLayoutY(338);
        user.setPrefHeight(31);
        user.setPrefWidth(300);
        user.setEffect(borderGlow);
        user.setPromptText("Username");
        Text t1 = new Text("Username :");
        t1.setLayoutX(257);
        t1.setLayoutY(321);
        f = new Font("System Bold", 22);
        t1.setFont(f);
        Text t2 = new Text("Password :");
        t2.setLayoutX(257);
        t2.setLayoutY(408);
        f = new Font("System Bold", 22);
        t2.setFont(f);
        PasswordField pass = new PasswordField();
        pass.setLayoutX(257);
        pass.setLayoutY(423);
        pass.setPrefHeight(31);
        pass.setPrefWidth(300);
        pass.setEffect(borderGlow);
        pass.setPromptText("Password");
        RadioButton docB = new RadioButton("Doctor");
        docB.setLayoutX(352);
        docB.setLayoutY(478);
        docB.setEffect(borderGlow);
        RadioButton paitB = new RadioButton("Patient");
        paitB.setLayoutX(257);
        paitB.setLayoutY(478);
        paitB.setSelected(true);
        paitB.setEffect(borderGlow);
        ToggleGroup toggleB = new ToggleGroup();
        docB.setToggleGroup(toggleB);
        paitB.setToggleGroup(toggleB);
        //login button
        Button registerButton = new Button("Register");
        registerButton.setLayoutX(470);
        registerButton.setLayoutY(521);
        registerButton.setOnMouseClicked((MouseEvent event) -> {
            boolean okay = true;
            if ("".equals(user.getText()) && "".equals(pass.getText())) {
                okay = false;
                errorAlert("Enter username and password!", "Enter your username and password.", null);
            } else if ("".equals(user.getText())) { // login message
                okay = false;
                errorAlert("Enter username !", "Enter your username.", null);
            } else if ("".equals(pass.getText())) {
                okay = false;
                errorAlert("Enter password !", "Enter your password", null);
            }
            if (okay) {
                msg = new Message();
                msg.type = 2;
                msg.register.username = user.getText();
                msg.register.password = pass.getText();
                if (docB.isSelected()) {
                    msg.register.type = 1;
                    doctor.userId = user.getText();
                } else {
                    msg.register.type = 2;
                    patient.userId = user.getText();
                }
                nc.send(msg);
                user.clear();
                pass.clear();
            }
        });
        Text login = new Text("Login");
        login.setLayoutX(714);
        login.setLayoutY(352);
        f = new Font("System Bold", 27);
        login.setFont(f);
        Text fpass = new Text("Forget Password");
        fpass.setLayoutX(714);
        fpass.setLayoutY(411);
        f = new Font("System Bold", 27);
        fpass.setFont(f);
        login.setOnMouseClicked((MouseEvent event) -> {
            loginScene();
        });
        login.setOnMouseEntered((MouseEvent event) -> {
            login.setFill(Color.CORNFLOWERBLUE);
            login.setFont(new Font("System Bold", 33));
        });
        login.setOnMouseExited((MouseEvent event) -> {
            login.setFill(Color.BLACK);
            login.setFont(new Font("System Bold", 27));
        });
        fpass.setOnMouseClicked((MouseEvent event) -> {
            forgetPassScene();
        });
        fpass.setOnMouseEntered((MouseEvent event) -> {
            fpass.setFill(Color.CORNFLOWERBLUE);
            fpass.setFont(new Font("System Bold", 33));
        });
        fpass.setOnMouseExited((MouseEvent event) -> {
            fpass.setFill(Color.BLACK);
            fpass.setFont(new Font("System Bold", 27));
        });
        mainPane.getChildren().addAll(titlePane, log, user, t1, pass, t2, paitB, docB, registerButton, login, fpass);
        mainPane.getStylesheets().add(HMSClient.class.getResource("button.css").toExternalForm());
        Scene scene = new Scene(mainPane);
        stage.setScene(scene);
        stage.setTitle("Hospital Management System : Registration");
        stage.show();
    }

    public static void forgetPassScene() {
        // add forget password pane
    }

    public static void regDocScene() {
        // mainPane 
        AnchorPane mainPane = new AnchorPane();
        mainPane.setPrefHeight(795);
        mainPane.setPrefWidth(1089);
        mainPane.setStyle("-fx-background-color: #e2fdff;");
        //titlepane
        AnchorPane titlePane = new AnchorPane();
        titlePane.setPrefHeight(136);
        titlePane.setPrefWidth(1089);
        titlePane.setLayoutX(299);
        titlePane.setLayoutY(26);
        titlePane.setStyle("-fx-background-color: #ffffff;");
        AnchorPane.setTopAnchor(titlePane, 0.0);
        AnchorPane.setBottomAnchor(titlePane, 659.0);
        AnchorPane.setLeftAnchor(titlePane, 0.0);
        AnchorPane.setRightAnchor(titlePane, 0.0);
        //title text
        Text title = new Text();
        title.setText("Hospital\nManagement\nSystem");
        title.setLayoutX(132);
        title.setLayoutY(48);
        Font f = new Font("System Bold", 29);
        title.setFont(f);
        //image
        Image img = new Image("BinaryContent/logo.png");
        ImageView imgv = new ImageView(img);
        imgv.setFitHeight(85);
        imgv.setFitWidth(80);
        imgv.setLayoutX(36);
        imgv.setLayoutY(35);
        //patIcon
        Image patIm = new Image("BinaryContent/doctorIcon.png");
        ImageView patImv = new ImageView(patIm);
        patImv.setFitHeight(168);
        patImv.setFitWidth(160);
        patImv.setLayoutX(906);
        patImv.setLayoutY(0);
        titlePane.getChildren().addAll(title, imgv, patImv);
        // Scrollpane 
        ScrollPane scPane = new ScrollPane();
        scPane.setLayoutX(531);
        scPane.setLayoutY(364);
        scPane.setPrefHeight(657);
        scPane.setPrefWidth(1089);
        scPane.setStyle("-fx-background-color: #e2fdff;");
        AnchorPane.setBottomAnchor(scPane, 0.0);
        AnchorPane.setLeftAnchor(scPane, 0.0);
        AnchorPane.setRightAnchor(scPane, 0.0);
        //contentPane
        AnchorPane contentPane = new AnchorPane();
        contentPane.setPrefHeight(690);
        contentPane.setPrefWidth(1069);
        contentPane.setStyle("-fx-background-color: #e2fdff;");
        Text titleT = new Text("Please complete the form below\nto complete registration .");
        titleT.setLayoutX(122);
        titleT.setLayoutY(69);
        titleT.setFont(new Font("System Bold", 27));
        //name
        Text nameT = new Text("Name :");
        nameT.setLayoutX(122);
        nameT.setLayoutY(173);
        nameT.setFont(new Font("System Bold", 22));
        TextField nameTf = new TextField();
        nameTf.setLayoutX(122);
        nameTf.setLayoutY(185);
        nameTf.setPrefWidth(350);
        nameTf.setEffect(borderGlow);
        nameTf.setPromptText("Enter name.");
        nameTf.setFont(Font.font("Serif", 20));
        //email
        Text deptT = new Text("Department :");
        deptT.setLayoutX(122);
        deptT.setLayoutY(250);
        deptT.setFont(new Font("System Bold", 22));
        TextField deptTf = new TextField();
        deptTf.setLayoutX(122);
        deptTf.setLayoutY(261);
        deptTf.setPrefWidth(350);
        deptTf.setPromptText("Enter department.");
        deptTf.setFont(Font.font("Serif", 20));
        deptTf.setEffect(borderGlow);
        //sex
        Text sexT = new Text("Sex :");
        sexT.setLayoutX(122);
        sexT.setLayoutY(323);
        sexT.setFont(new Font("System Bold", 22));
        ComboBox combo = new ComboBox();
        combo.setLayoutX(122);
        combo.setLayoutY(333);
        combo.setPrefWidth(350);
        combo.setPromptText("Select Sex");
        combo.getItems().add("Male");
        combo.getItems().add("Female");
        combo.getItems().add("Other");
        combo.setStyle("-fx-border-color: #bfbfbf");
        combo.setEffect(borderGlow);
        combo.getStylesheets().add(HMSClient.class.getResource("comboStyle.css").toExternalForm());
        //dob 
        Text dobT = new Text("Date of birth :");
        dobT.setLayoutX(122);
        dobT.setLayoutY(390);
        dobT.setFont(new Font("System Bold", 22));
        DatePicker dobTf = new DatePicker(LocalDate.now());
        dobTf.setEditable(false);
        dobTf.setLayoutX(122);
        dobTf.setLayoutY(400);
        dobTf.setPrefWidth(350);
        dobTf.setEffect(borderGlow);
        // contact number 
        Text numT = new Text("Contact Number :");
        numT.setLayoutX(122);
        numT.setLayoutY(460);
        numT.setFont(new Font("System Bold", 22));
        TextField numTf = new TextField();
        numTf.setLayoutX(122);
        numTf.setLayoutY(470);
        numTf.setPrefWidth(350);
        numTf.setPromptText("Enter contact number.");
        numTf.setFont(Font.font("Serif", 20));
        numTf.setEffect(borderGlow);
        // Address
        Text addT = new Text("Address :");
        addT.setLayoutX(122);
        addT.setLayoutY(460 + 70);
        addT.setFont(new Font("System Bold", 22));
        TextField addTf = new TextField();
        addTf.setLayoutX(122);
        addTf.setLayoutY(470 + 70);
        addTf.setPrefWidth(350);
        addTf.setEffect(borderGlow);
        addTf.setPromptText("Enter address.");
        addTf.setFont(Font.font("Serif", 20));
        Button submit = new Button();
        submit.setText("Submit");
        submit.setLayoutX(305 + 50 + 35);
        submit.setLayoutY(470 + 70 + 50 + 5);
        submit.setOnMouseClicked((MouseEvent event) -> {
            //date
            DateFormat format = new SimpleDateFormat("dd-MM-yyyy");
            Date yesterday = null;
            Date date = null;
            try {
                date = format.parse(dobTf.getValue().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));
                yesterday = new Date(System.currentTimeMillis() - 24 * 60 * 60 * 1000);
            } catch (ParseException ex) {

            }
            boolean okay = true;
            String s = (String) combo.getValue();
            if ("".equals(nameTf.getText())) {
                okay = false;
                errorAlert("Enter your name !", "Enter your name.", null);
            } else if ("".equals(deptTf.getText())) {
                okay = false;
                errorAlert("Enter your department !", "Enter your department.", null);
            } else if (s == null) {
                okay = false;
                errorAlert("Select your sex !", "Select your sex.", null);
            } else if (date.after(yesterday)) {
                okay = false;
                errorAlert("Enter your valid date of birth !", "Enter your valid date of birth.", null);
            } else if ("".equals(numTf.getText())) {
                okay = false;
                errorAlert("Enter your number !", "Enter your number", null);
            } else if (!isNumeric(numTf.getText())) {
                okay = false;
                errorAlert("Enter your valid number !", "Enter your valid number", null);
            } else if (numTf.getText().length() <= 8) {
                okay = false;
                errorAlert("Enter your valid number !", "Enter your valid number", null);
            } else if ("".equals(addTf.getText())) {
                okay = false;
                errorAlert("Enter your address !", "Enter your address", null);
            }
            if (okay) {
                msg = new Message();
                msg.type = 3;
                msg.successType = 1;
                msg.upType = 2;
                msg.doctor.userId = doctor.userId;
                msg.doctor.name = nameTf.getText();
                msg.doctor.sex = (String) combo.getValue();
                msg.doctor.address = addTf.getText();
                msg.doctor.dept = deptTf.getText();
                msg.doctor.dob = dobTf.getValue().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
                msg.doctor.phone = numTf.getText();
                nc.send(msg);
                // clearing 
                nameTf.clear();
                addTf.clear();
                deptTf.clear();
                numTf.clear();
            }
        });
        contentPane.getChildren().addAll(titleT, nameT, nameTf, deptT, deptTf, sexT, combo, dobT, dobTf, numT, numTf, addT, addTf, submit);
        scPane.setContent(contentPane);
        mainPane.getChildren().addAll(titlePane, scPane);
        mainPane.getStylesheets().add(HMSClient.class.getResource("button.css").toExternalForm());
        Scene scene = new Scene(mainPane);
        stage.setScene(scene);
        stage.setTitle("Hospital Management System : Doctor Registration");
        stage.show();
        stage.getIcons().add(new Image("BinaryContent/icon.png"));
        stage.setOnCloseRequest((WindowEvent t) -> {
            Platform.exit();
            System.exit(0);
        });
    }

    public static void regPatScene() {
        // mainPane 
        AnchorPane mainPane = new AnchorPane();
        mainPane.setPrefHeight(795);
        mainPane.setPrefWidth(1089);
        mainPane.setStyle("-fx-background-color: #e2fdff;");
        //titlepane
        AnchorPane titlePane = new AnchorPane();
        titlePane.setPrefHeight(136);
        titlePane.setPrefWidth(1089);
        titlePane.setLayoutX(299);
        titlePane.setLayoutY(26);
        titlePane.setStyle("-fx-background-color: #ffffff;");
        AnchorPane.setTopAnchor(titlePane, 0.0);
        AnchorPane.setBottomAnchor(titlePane, 659.0);
        AnchorPane.setLeftAnchor(titlePane, 0.0);
        AnchorPane.setRightAnchor(titlePane, 0.0);
        //title text
        Text title = new Text();
        title.setText("Hospital\nManagement\nSystem");
        title.setLayoutX(132);
        title.setLayoutY(48);
        Font f = new Font("System Bold", 29);
        title.setFont(f);
        //image
        Image img = new Image("BinaryContent/logo.png");
        ImageView imgv = new ImageView(img);
        imgv.setFitHeight(85);
        imgv.setFitWidth(80);
        imgv.setLayoutX(36);
        imgv.setLayoutY(35);
        //patIcon
        Image patIm = new Image("BinaryContent/patientIcon.png");
        ImageView patImv = new ImageView(patIm);
        patImv.setFitHeight(168);
        patImv.setFitWidth(160);
        patImv.setLayoutX(906);
        patImv.setLayoutY(0);
        titlePane.getChildren().addAll(title, imgv, patImv);
        // Scrollpane 
        ScrollPane scPane = new ScrollPane();
        scPane.setLayoutX(531);
        scPane.setLayoutY(364);
        scPane.setPrefHeight(657);
        scPane.setPrefWidth(1089);
        scPane.setStyle("-fx-background-color: #e2fdff;");
        AnchorPane.setBottomAnchor(scPane, 0.0);
        AnchorPane.setLeftAnchor(scPane, 0.0);
        AnchorPane.setRightAnchor(scPane, 0.0);
        //contentPane
        AnchorPane contentPane = new AnchorPane();
        contentPane.setPrefHeight(690);
        contentPane.setPrefWidth(1069);
        contentPane.setStyle("-fx-background-color: #e2fdff;");
        Text titleT = new Text("Please complete the form below\nto complete registration .");
        titleT.setLayoutX(122);
        titleT.setLayoutY(69);
        titleT.setFont(new Font("System Bold", 27));
        //name
        Text nameT = new Text("Name :");
        nameT.setLayoutX(122);
        nameT.setLayoutY(173);
        nameT.setFont(new Font("System Bold", 20));
        TextField nameTf = new TextField();
        nameTf.setLayoutX(122);
        nameTf.setLayoutY(185);
        nameTf.setPrefWidth(350);
        nameTf.setFont(Font.font("Serif", 18));
        nameTf.setPromptText("Enter name.");
        nameTf.setEffect(borderGlow);
        //email
        Text emailT = new Text("E-mail :");
        emailT.setLayoutX(122);
        emailT.setLayoutY(250);
        emailT.setFont(new Font("System Bold", 20));
        TextField emailTf = new TextField();
        emailTf.setLayoutX(122);
        emailTf.setLayoutY(261);
        emailTf.setPrefWidth(350);
        emailTf.setFont(Font.font("Serif", 18));
        emailTf.setPromptText("Enter e-mail.");
        emailTf.setEffect(borderGlow);
        //sex
        Text sexT = new Text("Sex :");
        sexT.setLayoutX(122);
        sexT.setLayoutY(323);
        sexT.setFont(new Font("System Bold", 20));
        ComboBox combo = new ComboBox();
        combo.setLayoutX(122);
        combo.setLayoutY(333);
        combo.setPrefWidth(350);
        combo.setPromptText("Select Sex");
        combo.getItems().add("Male");
        combo.getItems().add("Female");
        combo.getItems().add("Other");
        combo.setStyle("-fx-border-color: #bfbfbf");
        combo.getStylesheets().add(HMSClient.class.getResource("comboStyle.css").toExternalForm());
        combo.setEffect(borderGlow);
        //dob 
        Text dobT = new Text("Date of birth :");
        dobT.setLayoutX(122);
        dobT.setLayoutY(390);
        dobT.setFont(new Font("System Bold", 20));
        DatePicker dobTf = new DatePicker(LocalDate.now());
        dobTf.setEditable(false);
        dobTf.setLayoutX(122);
        dobTf.setLayoutY(400);
        dobTf.setPrefWidth(350);
        dobTf.setEffect(borderGlow);
        // contact number 
        Text numT = new Text("Contact Number :");
        numT.setLayoutX(122);
        numT.setLayoutY(460);
        numT.setFont(new Font("System Bold", 20));
        TextField numTf = new TextField();
        numTf.setLayoutX(122);
        numTf.setLayoutY(470);
        numTf.setPrefWidth(350);
        numTf.setFont(Font.font("Serif", 18));
        numTf.setPromptText("Enter your number.");
        numTf.setEffect(borderGlow);
        // Address
        Text addT = new Text("Address :");
        addT.setLayoutX(122);
        addT.setLayoutY(460 + 70);
        addT.setFont(new Font("System Bold", 20));
        TextField addTf = new TextField();
        addTf.setLayoutX(122);
        addTf.setLayoutY(470 + 70);
        addTf.setPrefWidth(350);
        addTf.setFont(Font.font("Serif", 18));
        addTf.setPromptText("Enter address.");
        addTf.setEffect(borderGlow);
        Button submit = new Button();
        submit.setText("Submit");
        submit.setLayoutX(305 + 50 + 35);
        submit.setLayoutY(470 + 70 + 50);
        submit.setOnMouseClicked((MouseEvent event) -> {
            boolean okay = true;
            String EMAIL_REGEX = "^[\\w-_\\.+]*[\\w-_\\.]\\@([\\w]+\\.)+[\\w]+[\\w]$";
            String mail = emailTf.getText();
            Boolean b = mail.matches(EMAIL_REGEX);
            String s = (String) combo.getValue();
            //date
            DateFormat format = new SimpleDateFormat("dd-MM-yyyy");
            Date yesterday = null;
            Date date = null;
            try {
                date = format.parse(dobTf.getValue().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));
                yesterday = new Date(System.currentTimeMillis() - 24 * 60 * 60 * 1000);
            } catch (ParseException ex) {

            }
            //checking
            if ("".equals(nameTf.getText())) {
                okay = false;
                errorAlert("Enter your name !", "Enter your name.", null);
            } else if (!b) {
                okay = false;
                errorAlert("Enter your valid e-mail !", "Enter your valid e-mail.", null);
            } else if (s == null) {
                okay = false;
                errorAlert("Select your sex !", "Select your sex.", null);
            } else if (date.after(yesterday)) {
                okay = false;
                errorAlert("Enter your date of birth !", "Enter your date of birth.", null);
            } else if ("".equals(numTf.getText())) {
                okay = false;
                errorAlert("Enter your number !", "Enter your number", null);
            } else if (numTf.getText().length() <= 8) {
                okay = false;
                errorAlert("Enter your valid number !", "Enter your valid number", null);
            } else if (!isNumeric(numTf.getText())) {
                okay = false;
                errorAlert("Enter your valid number !", "Enter your valid number", null);
            } else if ("".equals(addTf.getText())) {
                okay = false;
                errorAlert("Enter your address !", "Enter your address", null);
            }
            if (okay) {
                msg = new Message();
                msg.type = 3;
                msg.successType = 2;
                msg.upType = 2;
                msg.paitent.userId = patient.userId;
                msg.paitent.name = nameTf.getText();
                msg.paitent.sex = (String) combo.getValue();
                msg.paitent.dob = dobTf.getValue().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
                msg.paitent.address = addTf.getText();
                msg.paitent.email = emailTf.getText();
                msg.paitent.number = numTf.getText();
                nc.send(msg);
                patient = msg.paitent;
                //clearing 
                nameTf.clear();
                addTf.clear();
                emailTf.clear();
                numTf.clear();
            }
        });
        contentPane.getChildren().addAll(titleT, nameT, nameTf, emailT, emailTf, sexT, combo, dobT, dobTf, numT, numTf, addT, addTf, submit);
        //
        scPane.setContent(contentPane);
        //
        mainPane.getChildren().addAll(titlePane, scPane);
        mainPane.getStylesheets().add(HMSClient.class.getResource("button.css").toExternalForm());
        Scene scene = new Scene(mainPane);
        stage.setScene(scene);
        stage.setTitle("Hospital Management System : Patient Registration");
        stage.show();
        stage.getIcons().add(new Image("BinaryContent/icon.png"));
        stage.setOnCloseRequest((WindowEvent t) -> {
            Platform.exit();
            System.exit(0);
        });
    }

    public static void doctorScene() {
        //mainPane
        AnchorPane mainPane = new AnchorPane();
        mainPane.setPrefHeight(795);
        mainPane.setPrefWidth(1089);
        mainPane.setStyle("-fx-background-color: #e2fdff;");
        mainPane.setOnMouseClicked((MouseEvent event) -> {
            popup.hide();
            popupp.hide();
        });
        //topPane
        AnchorPane topPane = new AnchorPane();
        topPane.setLayoutX(299);
        topPane.setLayoutY(26);
        topPane.setPrefHeight(136);
        topPane.setPrefWidth(1089);
        topPane.setStyle("-fx-background-color: #ffffff;");
        topPane.setOnMouseClicked((MouseEvent event) -> {
            popup.hide();
            popupp.hide();
        });
        AnchorPane.setTopAnchor(topPane, 0.0);
        AnchorPane.setBottomAnchor(topPane, 659.0);
        AnchorPane.setLeftAnchor(topPane, 0.0);
        AnchorPane.setRightAnchor(topPane, 0.0);
        //image
        Image logo = new Image("BinaryContent/logo.png");
        ImageView logov = new ImageView(logo);
        logov.setFitHeight(78);
        logov.setFitWidth(73);
        logov.setLayoutX(36);
        logov.setLayoutY(26);
        //text
        Text titleT = new Text();
        titleT.setText("Hospital\nManagement\nSystem");
        titleT.setLayoutX(128);
        titleT.setLayoutY(42);
        Font f = new Font("System Bold", 24);
        titleT.setFont(f);
        //doctor image
        ImageView pim = new ImageView(new Image("BinaryContent/doctorIcon.png"));
        pim.setFitHeight(136);
        pim.setFitWidth(127);
        pim.setLayoutX(726);
        pim.setLayoutY(1);
        //nameText
        Text nameTop = new Text(doctor.name);
        nameTop.setLayoutX(858);
        nameTop.setLayoutY(59);
        nameTop.setFont(new Font("System Bold Italic", 23));
        //logoutT
        Text logoutT = new Text("(Logout)");
        logoutT.setLayoutX(945);
        logoutT.setLayoutY(95);
        logoutT.setFont(new Font(21));
        //adding nodes to topPane
        topPane.getChildren().addAll(logov, titleT, pim, nameTop, logoutT);
        //vbox
        VBox vb = new VBox();
        vb.setLayoutY(136);
        vb.setPrefHeight(660);
        vb.setPrefWidth(228);
        vb.setStyle("-fx-background-color: #58fcc8;");
        //panes 
        AnchorPane pn1 = new AnchorPane();
        pn1.setPrefHeight(433);
        pn1.setPrefWidth(254);
        AnchorPane pn2 = new AnchorPane();
        pn2.setPrefHeight(49);
        pn2.setPrefWidth(254);
        pn2.setStyle("-fx-background-color: #19c69e;");
        //menu
        Text menuT = new Text("Menu");
        menuT.setLayoutX(92);
        menuT.setLayoutY(32);
        menuT.setFont(new Font("System Bold", 21));
        pn2.getChildren().add(menuT);
        //menu 
        Text appoint = new Text("- Appoitments (" + appCounter(doctor.appointments, true) + ")");
        appoint.setLayoutX(20);
        appoint.setLayoutY(107);
        appoint.setFont(new Font(23));
        Text sappoint = new Text("-Pending \nAppointments (" + appCounter(doctor.appointments, false) + ")");
        sappoint.setLayoutX(22);
        sappoint.setLayoutY(177);
        sappoint.setFont(new Font(23));
        Text profile = new Text("- Update Profile");
        profile.setLayoutX(21);
        profile.setLayoutY(275);
        profile.setFont(new Font(23));
        //pn1
        pn1.getChildren().addAll(pn2, appoint, sappoint, profile);
        //vbox
        vb.getChildren().addAll(pn1);
        // pane content
        AnchorPane pane = new AnchorPane();
        pane.setLayoutX(230);
        pane.setLayoutY(136);
        pane.setPrefHeight(660);
        pane.setPrefWidth(859);
        // 3 scenes
        if (sceneNum == 1) {
            //appointment scene
            Text appT = new Text("Appointments :");
            appT.setLayoutX(50);
            appT.setLayoutY(54);
            appT.setFont(new Font("System Bold", 33));
            approvedListDoctor = new ListView<Appointment>();
            approvedListDoctor.setLayoutX(50);
            approvedListDoctor.setLayoutY(95);
            approvedListDoctor.setPrefHeight(499);
            approvedListDoctor.setPrefWidth(758);
            //updating cell of approvedListDoctor
            approvedListDoctor.setCellFactory((ListView<Appointment> param) -> {
                final ListCell<Appointment> cell = new ListCell<Appointment>() {
                    @Override
                    public void updateItem(Appointment item, boolean empty) {
                        super.updateItem(item, empty);
                        if (item != null) {
                            setText(item.Doc());
                        }
                    }
                };
                return cell;
            });
            approvedListDoctor.getItems().clear();
            Collections.sort(HMSClient.doctor.appointments);
            for (Appointment appointment : HMSClient.doctor.appointments) {
                if (appointment.seen == true) {
                    DateFormat format = new SimpleDateFormat("dd-MM-yyyy hh:mm a");
                    Date day = null, today = null;
                    try {
                        day = format.parse(appointment.date + " " + appointment.time);
                        today = new Date(System.currentTimeMillis());
                    } catch (ParseException ex) {
                        Logger.getLogger(Appointment.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    if (day.after(today)) {
                        approvedListDoctor.getItems().add(appointment);
                    }
                }
            }
            //adding css
            approvedListDoctor.getStylesheets().add(HMSClient.class.getResource("appointlistStyle.css").toExternalForm());
            //event for approvedListDoctor
            approvedListDoctor.setOnMouseClicked((MouseEvent event) -> {
                Appointment app = new Appointment();
                if (!approvedListDoctor.getSelectionModel().isEmpty()) {
                    app = (Appointment) approvedListDoctor.getSelectionModel().getSelectedItem();
                    popupDocSeen(app, event.getScreenX(), event.getScreenY());
                }
            });
            // refresh
            Text refresh = new Text("Refresh");
            refresh.setLayoutX(689);
            refresh.setLayoutY(52);
            refresh.setFill(Color.CHOCOLATE);
            refresh.setFont(new Font("System Bold", 27));
            pane.getChildren().addAll(appT, approvedListDoctor, refresh);
            pane.setOnMouseClicked((MouseEvent event) -> {
                popup.hide();
                popupp.hide();
            });
            //events
            refresh.setOnMouseClicked((MouseEvent event) -> {
                //update profile msg
                msg = new Message();
                msg.type = 5;
                msg.successType = 1;
                msg.doctor = doctor;
                nc.send(msg);
                doctorScene();
            });
            refresh.setOnMouseEntered((MouseEvent event) -> {
                refresh.setFont(new Font("System Italic", 30));
                refresh.setFill(Color.DARKBLUE);
            });
            refresh.setOnMouseExited((MouseEvent event) -> {
                refresh.setFont(new Font("System Bold", 27));
                refresh.setFill(Color.CHOCOLATE);
            });
        } else if (sceneNum == 2) {
            //appointment scene
            Text appT = new Text("Pending Appointments :");
            appT.setLayoutX(50);
            appT.setLayoutY(54);
            appT.setFont(new Font("System Bold", 33));
            pendingListDoctor = new ListView<Appointment>();
            pendingListDoctor.setLayoutX(50);
            pendingListDoctor.setLayoutY(95);
            pendingListDoctor.setPrefHeight(499);
            pendingListDoctor.setPrefWidth(758);
            //updating cell of pendingListDoctor
            pendingListDoctor.setCellFactory((ListView<Appointment> param) -> {
                final ListCell<Appointment> cell = new ListCell<Appointment>() {
                    @Override
                    public void updateItem(Appointment item, boolean empty) {
                        super.updateItem(item, empty);
                        if (item != null) {
                            setText(item.pendingDoc());
                        }
                    }
                };
                return cell;
            });
            pendingListDoctor.getItems().clear();
            Collections.sort(HMSClient.doctor.appointments);
            for (Appointment appointment : HMSClient.doctor.appointments) {
                if (appointment.seen == false) {
                    pendingListDoctor.getItems().add(appointment);
                }
            }
            //adding css
            pendingListDoctor.getStylesheets().add(HMSClient.class.getResource("appointlistStyle.css").toExternalForm());
            //event for pendingListDoctor
            pendingListDoctor.setOnMouseClicked((MouseEvent event) -> {
                Appointment app = new Appointment();
                if (!pendingListDoctor.getSelectionModel().isEmpty()) {
                    app = (Appointment) pendingListDoctor.getSelectionModel().getSelectedItem();
                    popupDocUnseen(app, event.getScreenX(), event.getScreenY());
                }
            });
//            Text refresh = new Text("Refresh");
//            refresh.setLayoutX(689);
//            refresh.setLayoutY(52);
//            refresh.setFill(Color.CHOCOLATE);
//            refresh.setFont(new Font("System Bold", 27));
            pane.getChildren().addAll(appT, pendingListDoctor);
            //events
            pane.setOnMouseClicked((MouseEvent event) -> {
                popup.hide();
                popupp.hide();
            });
//            refresh.setOnMouseClicked((MouseEvent event) -> {
//                //update profile msg
//                msg = new Message();
//                msg.type = 5;
//                msg.successType = 1;
//                msg.doctor = doctor;
//                nc.send(msg);
//                doctorScene();
//            });
//            refresh.setOnMouseEntered((MouseEvent event) -> {
//                refresh.setFont(new Font("System Italic", 30));
//                refresh.setFill(Color.DARKBLUE);
//            });
//            refresh.setOnMouseExited((MouseEvent event) -> {
//                refresh.setFont(new Font("System Bold", 27));
//                refresh.setFill(Color.CHOCOLATE);
//            });
        } else if (sceneNum == 3) {
            //update profile scene
            Text up = new Text("Update Profile");
            up.setLayoutX(50);
            up.setLayoutY(60);
            up.setFont(new Font("System Bold", 33));
            //name
            Text nameT = new Text("Name :");
            nameT.setLayoutX(50);
            nameT.setLayoutY(118);
            nameT.setFont(new Font("System Bold", 25));
            TextField nameTf = new TextField();
            nameTf.setLayoutX(50);
            nameTf.setLayoutY(133);
            nameTf.setPrefWidth(400);
            nameTf.setText(doctor.name);
            nameTf.setFont(Font.font("Serif", 20));
            //email
            Text deptT = new Text("Department :");
            deptT.setLayoutX(50);
            deptT.setLayoutY(206);
            deptT.setFont(new Font("System Bold", 25));
            TextField deptTf = new TextField();
            deptTf.setLayoutX(50);
            deptTf.setLayoutY(220);
            deptTf.setPrefWidth(400);
            deptTf.setText(doctor.dept);
            deptTf.setFont(Font.font("Serif", 20));
            //sex
            Text sexT = new Text("Sex :");
            sexT.setLayoutX(50);
            sexT.setLayoutY(293);
            sexT.setFont(new Font("System Bold", 25));
            ComboBox combo = new ComboBox();
            combo.setLayoutX(50);
            combo.setLayoutY(305);
            combo.setPrefWidth(400);
            combo.setPromptText("Select Sex");
            combo.getItems().add("Male");
            combo.getItems().add("Female");
            combo.getItems().add("Other");
            combo.setStyle("-fx-border-color: #bfbfbf");
            combo.getStylesheets().add(HMSClient.class.getResource("comboStyle.css").toExternalForm());
            combo.getSelectionModel().select(doctor.sex);
            //dob 
            Text dobT = new Text("Date of birth :");
            dobT.setLayoutX(50);
            dobT.setLayoutY(373);
            dobT.setFont(new Font("System Bold", 25));
            DatePicker dobTf = new DatePicker();
            dobTf.setValue(LocalDate.parse(doctor.dob, DateTimeFormatter.ofPattern("dd-MM-yyyy")));
            dobTf.setEditable(false);
            dobTf.setLayoutX(50);
            dobTf.setLayoutY(385);
            dobTf.setPrefWidth(400);
            // contact number 
            Text numT = new Text("Contact Number :");
            numT.setLayoutX(50);
            numT.setLayoutY(453);
            numT.setFont(new Font("System Bold", 25));
            TextField numTf = new TextField();
            numTf.setLayoutX(50);
            numTf.setLayoutY(463);
            numTf.setPrefWidth(400);
            numTf.setText(doctor.phone);
            numTf.setFont(Font.font("Serif", 20));
            // Address
            Text addT = new Text("Address :");
            addT.setLayoutX(50);
            addT.setLayoutY(528);
            addT.setFont(new Font("System Bold", 25));
            TextField addTf = new TextField();
            addTf.setLayoutX(50);
            addTf.setLayoutY(540);
            addTf.setPrefWidth(400);
            addTf.setText(doctor.address);
            addTf.setFont(Font.font("Serif", 20));
            //button
            Button submit = new Button();
            submit.setText("Update");
            submit.setLayoutX(360);
            submit.setLayoutY(597);
            //adding to pane
            pane.getChildren().addAll(up, nameT, nameTf, deptT, deptTf, sexT, combo, dobT, dobTf, numT, numTf, addT, addTf, submit);
            // events 
            submit.setOnMouseClicked((MouseEvent event) -> {
                //date
                DateFormat format = new SimpleDateFormat("dd-MM-yyyy");
                Date yesterday = null;
                Date date = null;
                try {
                    date = format.parse(dobTf.getValue().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));
                    yesterday = new Date(System.currentTimeMillis() - 24 * 60 * 60 * 1000);
                } catch (ParseException ex) {

                }
                boolean okay = true;
                String s = (String) combo.getValue();
                if ("".equals(nameTf.getText())) {
                    okay = false;
                    errorAlert("Enter your name !", "Enter your name.", null);
                } else if ("".equals(deptTf.getText())) {
                    okay = false;
                    errorAlert("Enter your department !", "Enter your department.", null);
                } else if (s == null) {
                    okay = false;
                    errorAlert("Select your sex !", "Select your sex.", null);
                } else if (date.after(yesterday)) {
                    okay = false;
                    errorAlert("Enter your valid date of birth !", "Enter your valid date of birth.", null);
                } else if ("".equals(numTf.getText())) {
                    okay = false;
                    errorAlert("Enter your number !", "Enter your number", null);
                } else if (!isNumeric(numTf.getText())) {
                    okay = false;
                    errorAlert("Enter your valid number !", "Enter your valid number", null);
                } else if (numTf.getText().length() <= 8) {
                    okay = false;
                    errorAlert("Enter your valid number !", "Enter your valid number", null);
                } else if ("".equals(addTf.getText())) {
                    okay = false;
                    errorAlert("Enter your address !", "Enter your address", null);
                }
                if (okay) {
                    doctor.name = nameTf.getText();
                    doctor.dept = deptTf.getText();
                    doctor.address = addTf.getText();
                    doctor.dob = dobTf.getValue().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
                    doctor.sex = (String) combo.getValue();
                    doctor.phone = numTf.getText();
                    msg = new Message();
                    msg.type = 3;
                    msg.successType = 1;
                    msg.doctor = doctor;
                    nc.send(msg);
                    //update profile msg
                    msg = new Message();
                    msg.type = 5;
                    msg.successType = 1;
                    msg.doctor = doctor;
                    nc.send(msg);
                }
            });

        }
        //events
        logoutT.setOnMouseClicked((MouseEvent event) -> {
            sceneNum = 1;
            subsceneNum = 1;
            loginScene();
            //offline message
            msg = new Message();
            msg.type = 6;
            msg.successType = 1;
            msg.doctor = doctor;
            nc.send(msg);
        });
        logoutT.setOnMouseEntered((MouseEvent event) -> {
            logoutT.setFont(new Font("System Bold", 24));
            logoutT.setFill(Color.DARKBLUE);
        });
        logoutT.setOnMouseExited((MouseEvent event) -> {
            logoutT.setFont(new Font(21));
            logoutT.setFill(Color.BLACK);
        });
        appoint.setOnMouseClicked((MouseEvent event) -> {
            sceneNum = 1;
            doctorScene();
        });
        appoint.setOnMouseEntered((MouseEvent event) -> {
            appoint.setFont(new Font("System Bold", 26));
        });
        appoint.setOnMouseExited((MouseEvent event) -> {
            appoint.setFont(new Font(23));
        });
        sappoint.setOnMouseClicked((MouseEvent event) -> {
            sceneNum = 2;
            doctorScene();
        });
        sappoint.setOnMouseEntered((MouseEvent event) -> {
            sappoint.setFont(new Font("System Bold", 26));
        });
        sappoint.setOnMouseExited((MouseEvent event) -> {
            sappoint.setFont(new Font(23));
        });
        profile.setOnMouseClicked((MouseEvent event) -> {
            //updating scene
            sceneNum = 3;
            doctorScene();
        });
        profile.setOnMouseEntered((MouseEvent event) -> {
            profile.setFont(new Font("System Bold", 26));
        });
        profile.setOnMouseExited((MouseEvent event) -> {
            profile.setFont(new Font(23));
        });
        //mainPane
        mainPane.getChildren().addAll(topPane, vb, pane);
        mainPane.getStylesheets().add(HMSClient.class.getResource("button.css").toExternalForm());
        Scene scene = new Scene(mainPane);
        stage.setScene(scene);
        stage.setTitle("Hospital Management System : " + doctor.name);
        stage.show();
        stage.getIcons().add(new Image("BinaryContent/icon.png"));
        stage.setOnCloseRequest((WindowEvent t) -> {
            //offline message
            msg = new Message();
            msg.type = 6;
            msg.successType = 1;
            msg.doctor = doctor;
            nc.send(msg);
            Platform.exit();
            System.exit(0);
        });
    }

    public static void patientScene() {
        //mainPane
        AnchorPane mainPane = new AnchorPane();
        mainPane.setPrefHeight(795);
        mainPane.setPrefWidth(1089);
        mainPane.setStyle("-fx-background-color: #e2fdff;");
        //topPane
        AnchorPane topPane = new AnchorPane();
        topPane.setLayoutX(299);
        topPane.setLayoutY(26);
        topPane.setPrefHeight(136);
        topPane.setPrefWidth(1089);
        topPane.setStyle("-fx-background-color: #ffffff;");
        AnchorPane.setTopAnchor(topPane, 0.0);
        AnchorPane.setBottomAnchor(topPane, 659.0);
        AnchorPane.setLeftAnchor(topPane, 0.0);
        AnchorPane.setRightAnchor(topPane, 0.0);
        //image
        Image logo = new Image("BinaryContent/logo.png");
        ImageView logov = new ImageView(logo);
        logov.setFitHeight(78);
        logov.setFitWidth(73);
        logov.setLayoutX(36);
        logov.setLayoutY(26);
        //text
        Text titleT = new Text();
        titleT.setText("Hospital\nManagement\nSystem");
        titleT.setLayoutX(128);
        titleT.setLayoutY(42);
        Font f = new Font("System Bold", 24);
        titleT.setFont(f);
        //paitentiamge
        ImageView pim = new ImageView(new Image("BinaryContent/patientIcon.png"));
        pim.setFitHeight(88);
        pim.setFitWidth(97);
        pim.setLayoutX(757);
        pim.setLayoutY(24);
        //nameText
        Text nameTop = new Text(patient.name);
        nameTop.setLayoutX(858);
        nameTop.setLayoutY(59);
        nameTop.setFont(new Font("System Bold Italic", 23));
        //logoutT
        Text logoutT = new Text("(Logout)");
        logoutT.setLayoutX(945);
        logoutT.setLayoutY(95);
        logoutT.setFont(new Font(21));
        //adding nodes to topPane
        topPane.getChildren().addAll(logov, titleT, pim, nameTop, logoutT);
        //vbox
        VBox vb = new VBox();
        vb.setLayoutY(136);
        vb.setPrefHeight(660);
        vb.setPrefWidth(228);
        vb.setStyle("-fx-background-color: #58fcc8;");
        //panes 
        AnchorPane pn1 = new AnchorPane();
        pn1.setPrefHeight(266);
        pn1.setPrefWidth(230);
        AnchorPane pn2 = new AnchorPane();
        pn2.setPrefHeight(49);
        pn2.setPrefWidth(230);
        pn2.setStyle("-fx-background-color: #19c69e;");
        //menu
        Text menuT = new Text("Menu");
        menuT.setLayoutX(83);
        menuT.setLayoutY(32);
        menuT.setFont(new Font("System Bold", 21));
        pn2.getChildren().add(menuT);
        //menu 
        Text appoint = new Text("- Appoitments");
        appoint.setLayoutX(17);
        appoint.setLayoutY(107);
        appoint.setFont(new Font(23));
        Text sappoint = new Text("- Set Appoitment");
        sappoint.setLayoutX(17);
        sappoint.setLayoutY(163);
        sappoint.setFont(new Font(23));
        Text profile = new Text("- Update Profile");
        profile.setLayoutX(17);
        profile.setLayoutY(219);
        profile.setFont(new Font(23));
        //pn1
        pn1.getChildren().addAll(pn2, appoint, sappoint, profile);
        AnchorPane pn3 = new AnchorPane();
        pn3.setPrefHeight(394);
        pn3.setPrefWidth(230);
        AnchorPane pn4 = new AnchorPane();
        pn4.setLayoutX(0);
        pn4.setPrefHeight(49);
        pn4.setPrefWidth(230);
        pn4.setStyle("-fx-background-color: #19c69e;");
        //text and image
        Text t2 = new Text("Online Doctors");
        t2.setLayoutX(19);
        t2.setLayoutY(32);
        t2.setFont(new Font("System Bold", 21));
        ImageView ref = new ImageView(new Image("BinaryContent/refresh.png"));
        ref.setFitHeight(39);
        ref.setFitWidth(41);
        ref.setLayoutX(175);
        ref.setLayoutY(5);
        ref.setOnMouseClicked((MouseEvent event) -> {
            msg = new Message();
            msg.type = 5;
            msg.successType = 2;
            msg.paitent = patient;
            nc.send(msg);
            patientScene();
        });
        //adding to pane
        pn4.getChildren().addAll(t2, ref);
        //listview 
        onlineList = new ListView<String>();
        onlineList.setLayoutX(16);
        onlineList.setLayoutY(49);
        onlineList.setPrefHeight(327);
        onlineList.setPrefWidth(230);
        //adding to onlineList
        for (HashMap.Entry<String, String> entry : online.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            onlineList.getItems().add(value);
        }
        //adding css
        onlineList.getStylesheets().add(HMSClient.class.getResource("onlinelistStyle.css").toExternalForm());
        AnchorPane.setTopAnchor(onlineList, 49.0);
        AnchorPane.setBottomAnchor(onlineList, 0.0);
        AnchorPane.setLeftAnchor(onlineList, 0.0);
        AnchorPane.setRightAnchor(onlineList, 0.0);
        //onlinelist events
        onlineList.setOnMouseClicked((MouseEvent event) -> {
            if (!onlineList.getSelectionModel().isEmpty()) {
                docName = (String) onlineList.getSelectionModel().getSelectedItem();
            }
            comboFlag = 1;
            sceneNum = 2;
            patientScene();
        });
        //adding items
        pn3.getChildren().addAll(pn4, onlineList);
        //vbox
        vb.getChildren().addAll(pn1, pn3);
        // pane content
        AnchorPane pane = new AnchorPane();
        pane.setLayoutX(230);
        pane.setLayoutY(136);
        pane.setPrefHeight(660);
        pane.setPrefWidth(859);
        // 3 scenes
        if (sceneNum == 1) {
            //appointment scene
            if (subsceneNum == 1) {
                Text appT = new Text("Appointments (" + appCounter(patient.appointments, true) + ")");
                appT.setLayoutX(29);
                appT.setLayoutY(60);
                appT.setFont(new Font("System Bold", 29));
                Circle dot = new Circle(8);
                dot.setLayoutX(293);
                dot.setLayoutY(49);
                dot.setFill(Color.GAINSBORO);
                Text appTp = new Text("Pending Appointments(" + appCounter(patient.appointments, false) + ")");
                appTp.setLayoutX(316);
                appTp.setLayoutY(60);
                appTp.setFont(new Font("System Bold", 29));
                appTp.setFill(Color.GREY);
                // refresh
                Text refresh = new Text("(Refresh)");
                refresh.setLayoutX(705);
                refresh.setLayoutY(58);
                refresh.setFill(Color.CHOCOLATE);
                refresh.setFont(new Font("System Bold", 27));
                //events
                refresh.setOnMouseClicked((MouseEvent event) -> {
                    //update profile msg
                    msg = new Message();
                    msg.type = 5;
                    msg.successType = 2;
                    msg.paitent = patient;
                    nc.send(msg);
                    patientScene();
                });
                refresh.setOnMouseEntered((MouseEvent event) -> {
                    refresh.setFont(new Font("System Italic", 30));
                    refresh.setFill(Color.DARKBLUE);
                });
                refresh.setOnMouseExited((MouseEvent event) -> {
                    refresh.setFont(new Font("System Bold", 27));
                    refresh.setFill(Color.CHOCOLATE);
                });
                approvedListPatient = new ListView<Appointment>();
                approvedListPatient.setLayoutX(33);
                approvedListPatient.setLayoutY(96);
                approvedListPatient.setPrefHeight(512);
                approvedListPatient.setPrefWidth(788);
                //updating cell of approvedListPatient
                approvedListPatient.setCellFactory((ListView<Appointment> param) -> {
                    final ListCell<Appointment> cell = new ListCell<Appointment>() {
                        @Override
                        public void updateItem(Appointment item, boolean empty) {
                            super.updateItem(item, empty);
                            if (item != null) {
                                setText(item.Pat());
                            }
                        }
                    };
                    return cell;
                });
                approvedListPatient.getItems().clear();
                Collections.sort(HMSClient.patient.appointments);
                for (Appointment appointment : HMSClient.patient.appointments) {
                    if (appointment.seen == true) {
                        DateFormat format = new SimpleDateFormat("dd-MM-yyyy hh:mm a");
                        Date day = null, today = null;
                        try {
                            day = format.parse(appointment.date + " " + appointment.time);
                            today = new Date(System.currentTimeMillis());
                        } catch (ParseException ex) {
                            Logger.getLogger(Appointment.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        if (day.after(today)) {
                            approvedListPatient.getItems().add(appointment);
                        }
                    }
                }
                approvedListPatient.getStylesheets().add(HMSClient.class.getResource("appointlistStyle.css").toExternalForm());
                approvedListPatient.setOnMouseClicked((MouseEvent event) -> {
                    Appointment app = new Appointment();
                    if (!approvedListPatient.getSelectionModel().isEmpty()) {
                        app = (Appointment) approvedListPatient.getSelectionModel().getSelectedItem();
                        popupPatient(app, event.getScreenX(), event.getScreenY());
                    }
                });
                pane.getChildren().addAll(appT, approvedListPatient, dot, appTp, refresh);
                pane.setOnMouseClicked((MouseEvent event) -> {
                    patpopup.hide();
                });
                //events 
                appTp.setOnMouseClicked((MouseEvent event) -> {
                    sceneNum = 1;
                    subsceneNum = 2;
                    patientScene();
                });
            } else if (subsceneNum == 2) {
                Text appT = new Text("Appointments (" + appCounter(patient.appointments, true) + ")");
                appT.setLayoutX(29);
                appT.setLayoutY(60);
                appT.setFont(new Font("System Bold", 29));
                appT.setFill(Color.GREY);
                Circle dot = new Circle(8);
                dot.setFill(Color.GAINSBORO);
                dot.setLayoutX(293);
                dot.setLayoutY(49);
                Text appTp = new Text("Pending Appointments(" + appCounter(patient.appointments, false) + ")");
                appTp.setLayoutX(316);
                appTp.setLayoutY(60);
                appTp.setFont(new Font("System Bold", 29));
                // refresh
                Text refresh = new Text("(Refresh)");
                refresh.setLayoutX(705);
                refresh.setLayoutY(58);
                refresh.setFill(Color.CHOCOLATE);
                refresh.setFont(new Font("System Bold", 27));
                //events
                refresh.setOnMouseClicked((MouseEvent event) -> {
                    //update profile msg
                    msg = new Message();
                    msg.type = 5;
                    msg.successType = 2;
                    msg.paitent = patient;
                    nc.send(msg);
                    patientScene();
                });
                refresh.setOnMouseEntered((MouseEvent event) -> {
                    refresh.setFont(new Font("System Italic", 30));
                    refresh.setFill(Color.DARKBLUE);
                });
                refresh.setOnMouseExited((MouseEvent event) -> {
                    refresh.setFont(new Font("System Bold", 27));
                    refresh.setFill(Color.CHOCOLATE);
                });
                pendingListPatient = new ListView<Appointment>();
                pendingListPatient.setLayoutX(33);
                pendingListPatient.setLayoutY(96);
                pendingListPatient.setPrefHeight(512);
                pendingListPatient.setPrefWidth(788);
                //updating cell of appointL
                pendingListPatient.setCellFactory((ListView<Appointment> param) -> {
                    final ListCell<Appointment> cell = new ListCell<Appointment>() {
                        @Override
                        public void updateItem(Appointment item, boolean empty) {
                            super.updateItem(item, empty);
                            if (item != null) {
                                setText(item.pendingPat());
                            }
                        }
                    };
                    return cell;
                });
                pendingListPatient.getItems().clear();
                Collections.sort(HMSClient.patient.appointments);
                for (Appointment appointment : HMSClient.patient.appointments) {
                    if (appointment.seen == false) {
                        pendingListPatient.getItems().add(appointment);
                    }
                }
                pendingListPatient.getStylesheets().add(HMSClient.class.getResource("appointlistStyle.css").toExternalForm());
                pendingListPatient.setOnMouseClicked((MouseEvent event) -> {
                    Appointment app = new Appointment();
                    if (!pendingListPatient.getSelectionModel().isEmpty()) {
                        app = (Appointment) pendingListPatient.getSelectionModel().getSelectedItem();
                        popupPatient(app, event.getScreenX(), event.getScreenY());
                    }
                });
                pane.getChildren().addAll(appT, refresh, pendingListPatient, dot, appTp);
                pane.setOnMouseClicked((MouseEvent event) -> {
                    patpopup.hide();
                });
                //events 
                appT.setOnMouseClicked((MouseEvent event) -> {
                    sceneNum = 1;
                    subsceneNum = 1;
                    patientScene();
                });
            }
        } else if (sceneNum == 2) {
            Text app = new Text("Set Appointments :");
            app.setLayoutX(27);
            app.setLayoutY(54);
            app.setFont(new Font("System Bold", 35));
            Text docT = new Text("Select Doctor :");
            docT.setLayoutX(27);
            docT.setLayoutY(110);
            docT.setFont(new Font("System Bold", 25));
            Text nodoc = new Text("     (No doctors online)");
            nodoc.setLayoutX(170);
            nodoc.setLayoutY(110);
            nodoc.setFont(new Font("System Bold", 25));
            nodoc.setFill(Color.RED);
            if (online.size() == 0) {
                nodoc.setOpacity(1);
            } else {
                nodoc.setOpacity(0);
            }
            ComboBox combo = new ComboBox();
            combo.setLayoutX(27);
            combo.setLayoutY(128);
            combo.setPrefHeight(31);
            combo.setPrefWidth(400);
            combo.setPromptText("Select Doctor");
            if (comboFlag == 1) {
                combo.setValue(docName);
                comboFlag = 0;
            }
            combo.getStylesheets().add(HMSClient.class.getResource("comboStyle.css").toExternalForm());
            //updating combo
            for (HashMap.Entry<String, String> entry : online.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
                combo.getItems().add(value);
            }
            //update the combo box
            Text apptT = new Text("Appointment Type :");
            apptT.setLayoutX(27);
            apptT.setLayoutY(198);
            apptT.setFont(new Font("System Bold", 25));
            TextField apptTf = new TextField();
            apptTf.setLayoutX(28);
            apptTf.setLayoutY(215);
            apptTf.setPrefWidth(400);
            apptTf.setFont(Font.font("Serif", 20));
            apptTf.setPromptText("Enter appointment type.");
            Text dateT = new Text("Preferable Date:");
            dateT.setLayoutX(27);
            dateT.setLayoutY(286);
            dateT.setFont(new Font("System Bold", 25));
            DatePicker dateP = new DatePicker(LocalDate.now());
            dateP.setEditable(false);
            dateP.setLayoutX(28);
            dateP.setLayoutY(304);
            dateP.setPrefHeight(31);
            dateP.setPrefWidth(400);
            Text timeT = new Text("Preferable Time:");
            timeT.setLayoutX(28);
            timeT.setLayoutY(373);
            timeT.setFont(new Font("System Bold", 25));
            Text time = new Text("");
            time.setLayoutX(350);
            time.setLayoutY(405);
            time.setFont(new Font(20));
            TimeSpinner spinner = new TimeSpinner();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("hh:mm a");
            spinner.valueProperty().addListener((obs, oldTime, newTime)
                    -> time.setText(formatter.format(newTime)));
            //System.out.println(spinner.getValue().format(formatter));
            spinner.setEditable(false);
            // TextField timeTf = new TextField();
            spinner.setLayoutX(28);
            spinner.setLayoutY(385);
            spinner.setPrefHeight(31);
            spinner.setPrefWidth(310);
            //   timeTf.setFont(Font.font("Serif", 20));
            //setting current time
            //  String timeStamp = new SimpleDateFormat("hh:mm a").format(Calendar.getInstance().getTime());
            // timeTf.setText(timeStamp);
            //description
            Text desT = new Text("Description of symptoms :");
            desT.setLayoutX(28);
            desT.setLayoutY(450);
            desT.setFont(new Font("System Bold", 25));
            TextArea desTa = new TextArea();
            desTa.setLayoutX(28);
            desTa.setLayoutY(470);
            desTa.setPrefWidth(400);
            desTa.setPrefHeight(80);
            desTa.setFont(Font.font("Serif", 20));
            desTa.setPromptText("Enter a brief description of your problems.");
            //button
            Button submit = new Button();
            submit.setText("Submit");
            submit.setLayoutX(350);
            submit.setLayoutY(570);
            //adding to pane
            pane.getChildren().addAll(app, docT, nodoc, combo, apptT, apptTf, dateP, dateT, timeT, time, spinner, desT, desTa, submit);
            //handler
            submit.setOnMouseClicked((MouseEvent event) -> {
                //date
                DateFormat format = new SimpleDateFormat("dd-MM-yyyy");
                Date yesterday = null;
                Date date = null;
                try {
                    date = format.parse(dateP.getValue().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));
                    yesterday = new Date(System.currentTimeMillis() - 24 * 60 * 60 * 1000);
                } catch (ParseException ex) {

                }
                boolean okay = true;
                String s = (String) combo.getValue();
                if (s == null) {
                    okay = false;
                    errorAlert("Select doctor !", "Select a doctor.", null);
                } else if ("".equals(apptTf.getText())) {
                    okay = false;
                    errorAlert("Enter type of appointment !", "Enter type of appointment.", null);
                } else if (date.before(yesterday)) {
                    okay = false;
                    errorAlert("Select a valid date !", "Select a valid date.", null);
                } else if ("".equals(desTa.getText())) {
                    okay = false;
                    errorAlert("Enter description of symptoms !", "Enter description of symptoms.", null);
                }
                System.out.println(spinner.getValue().format(formatter));
                //appointment for patient
                if (okay) {
                    msg = new Message();
                    msg.type = 4;
                    msg.successType = 2;
                    //combo gaiing username
                    String name = (String) combo.getValue();
                    String ID = "";
                    for (HashMap.Entry<String, String> entry : online.entrySet()) {
                        String key = entry.getKey();
                        String value = entry.getValue();
                        if (value.equals(name)) {
                            ID = key;
                        }
                    }
                    msg.appointment = new Appointment();
                    msg.appointment.seen = false;
                    msg.appointment.doctorID = ID;
                    msg.appointment.patientID = patient.userId;
                    msg.appointment.date = dateP.getValue().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
                    msg.appointment.time = spinner.getValue().format(formatter);
                    msg.appointment.title = apptTf.getText();
                    msg.appointment.description = desTa.getText();
                    nc.send(msg);
                    //setting scene to pending appointments
                    sceneNum = 1;
                    subsceneNum = 2;
                    refreshRequestPatient();
                }
            });
        } else if (sceneNum == 3) {
            //update profile scene
            Text up = new Text("Update Profile");
            up.setLayoutX(29);
            up.setLayoutY(60);
            up.setFont(new Font("System Bold", 33));
            //name
            Text nameT = new Text("Name :");
            nameT.setLayoutX(29);
            nameT.setLayoutY(118);
            nameT.setFont(new Font("System Bold", 25));
            TextField nameTf = new TextField();
            nameTf.setLayoutX(29);
            nameTf.setLayoutY(133);
            nameTf.setPrefWidth(350);
            nameTf.setText(patient.name);
            nameTf.setFont(Font.font("Serif", 20));
            //email
            Text emailT = new Text("E-mail :");
            emailT.setLayoutX(29);
            emailT.setLayoutY(206);
            emailT.setFont(new Font("System Bold", 25));
            TextField emailTf = new TextField();
            emailTf.setLayoutX(29);
            emailTf.setLayoutY(220);
            emailTf.setPrefWidth(350);
            emailTf.setText(patient.email);
            emailTf.setFont(Font.font("Serif", 20));
            //sex
            Text sexT = new Text("Sex :");
            sexT.setLayoutX(29);
            sexT.setLayoutY(293);
            sexT.setFont(new Font("System Bold", 25));
            ComboBox combo = new ComboBox();
            combo.setLayoutX(29);
            combo.setLayoutY(305);
            combo.setPrefWidth(350);
            combo.setPromptText("Select Sex");
            combo.getItems().add("Male");
            combo.getItems().add("Female");
            combo.getItems().add("Other");
            combo.setStyle("-fx-border-color: #bfbfbf");
            combo.getStylesheets().add(HMSClient.class.getResource("comboStyle.css").toExternalForm());
            combo.getSelectionModel().select(patient.sex);
            //dob 
            Text dobT = new Text("Date of birth :");
            dobT.setLayoutX(29);
            dobT.setLayoutY(373);
            dobT.setFont(new Font("System Bold", 25));
            DatePicker dobTf = new DatePicker(LocalDate.now());
            dobTf.setValue(LocalDate.parse(patient.dob, DateTimeFormatter.ofPattern("dd-MM-yyyy")));
            dobTf.setEditable(false);
            dobTf.setLayoutX(29);
            dobTf.setLayoutY(385);
            dobTf.setPrefWidth(350);
            // contact number 
            Text numT = new Text("Contact Number :");
            numT.setLayoutX(29);
            numT.setLayoutY(453);
            numT.setFont(new Font("System Bold", 25));
            TextField numTf = new TextField();
            numTf.setLayoutX(29);
            numTf.setLayoutY(463);
            numTf.setPrefWidth(350);
            numTf.setText(patient.number);
            numTf.setFont(Font.font("Serif", 20));
            // Address
            Text addT = new Text("Address :");
            addT.setLayoutX(29);
            addT.setLayoutY(528);
            addT.setFont(new Font("System Bold", 25));
            TextField addTf = new TextField();
            addTf.setLayoutX(29);
            addTf.setLayoutY(540);
            addTf.setPrefWidth(350);
            addTf.setText(patient.address);
            addTf.setFont(Font.font("Serif", 20));
            //button
            Button submit = new Button();
            submit.setText("Update");
            submit.setLayoutX(300);
            submit.setLayoutY(597);
            //adding to pane
            pane.getChildren().addAll(up, nameT, nameTf, emailT, emailTf, sexT, combo, dobT, dobTf, numT, numTf, addT, addTf, submit);
            // events 
            submit.setOnMouseClicked((MouseEvent event) -> {
                boolean okay = true;
                String EMAIL_REGEX = "^[\\w-_\\.+]*[\\w-_\\.]\\@([\\w]+\\.)+[\\w]+[\\w]$";
                String mail = emailTf.getText();
                Boolean b = mail.matches(EMAIL_REGEX);
                String s = (String) combo.getValue();
                //date
                DateFormat format = new SimpleDateFormat("dd-MM-yyyy");
                Date yesterday = null;
                Date date = null;
                try {
                    date = format.parse(dobTf.getValue().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));
                    yesterday = new Date(System.currentTimeMillis() - 24 * 60 * 60 * 1000);
                } catch (ParseException ex) {

                }
                //checking
                if ("".equals(nameTf.getText())) {
                    okay = false;
                    errorAlert("Enter your name !", "Enter your name.", null);
                } else if (!b) {
                    okay = false;
                    errorAlert("Enter your valid e-mail !", "Enter your valid e-mail.", null);
                } else if (s == null) {
                    okay = false;
                    errorAlert("Select your sex !", "Select your sex.", null);
                } else if (date.after(yesterday)) {
                    okay = false;
                    errorAlert("Enter your date of birth !", "Enter your date of birth.", null);
                } else if ("".equals(numTf.getText())) {
                    okay = false;
                    errorAlert("Enter your number !", "Enter your number", null);
                } else if (numTf.getText().length() <= 8) {
                    okay = false;
                    errorAlert("Enter your valid number !", "Enter your valid number", null);
                } else if (!isNumeric(numTf.getText())) {
                    okay = false;
                    errorAlert("Enter your valid number !", "Enter your valid number", null);
                } else if ("".equals(addTf.getText())) {
                    okay = false;
                    errorAlert("Enter your address !", "Enter your address", null);
                }
                if (okay) {
                    patient.name = nameTf.getText();
                    patient.email = emailTf.getText();
                    patient.address = addTf.getText();
                    patient.dob = dobTf.getValue().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
                    patient.sex = (String) combo.getValue();
                    patient.number = numTf.getText();
                    msg = new Message();
                    msg.type = 3;
                    msg.successType = 2;
                    msg.paitent = patient;
                    nc.send(msg);
                }
            });
        }
        //events
        logoutT.setOnMouseClicked((MouseEvent event) -> {
            sceneNum = 1;
            loginScene();
            //logout message
            msg = new Message();
            msg.type = 6;
            msg.successType = 2;
            msg.paitent = patient;
            nc.send(msg);
        });
        logoutT.setOnMouseEntered((MouseEvent event) -> {
            logoutT.setFont(new Font("System Bold", 24));
            logoutT.setFill(Color.DARKBLUE);
        });
        logoutT.setOnMouseExited((MouseEvent event) -> {
            logoutT.setFont(new Font(21));
            logoutT.setFill(Color.BLACK);
        });
        appoint.setOnMouseClicked((MouseEvent event) -> {
            sceneNum = 1;
            patientScene();
        });
        appoint.setOnMouseEntered((MouseEvent event) -> {
            appoint.setFont(new Font("System Bold", 26));
        });
        appoint.setOnMouseExited((MouseEvent event) -> {
            appoint.setFont(new Font(23));
        });
        sappoint.setOnMouseClicked((MouseEvent event) -> {
            sceneNum = 2;
            patientScene();
        });
        sappoint.setOnMouseEntered((MouseEvent event) -> {
            sappoint.setFont(new Font("System Bold", 26));
        });
        sappoint.setOnMouseExited((MouseEvent event) -> {
            sappoint.setFont(new Font(23));
        });
        profile.setOnMouseClicked((MouseEvent event) -> {
            sceneNum = 3;
            patientScene();
        });
        profile.setOnMouseEntered((MouseEvent event) -> {
            profile.setFont(new Font("System Bold", 26));
        });
        profile.setOnMouseExited((MouseEvent event) -> {
            profile.setFont(new Font(23));
        });

        //mainPane
        mainPane.getChildren().addAll(topPane, vb, pane);
        mainPane.getStylesheets().add(HMSClient.class.getResource("button.css").toExternalForm());
        mainPane.setOnMouseClicked((MouseEvent event) -> {
            patpopup.hide();
        });
        Scene scene = new Scene(mainPane);
        stage.setScene(scene);
        stage.setTitle("Hospital Management System : " + patient.name);
        stage.show();
        stage.getIcons().add(new Image("BinaryContent/icon.png"));
        stage.setOnCloseRequest((WindowEvent t) -> {
            //offline message
            msg = new Message();
            msg.type = 6;
            msg.successType = 2;
            msg.paitent = patient;
            nc.send(msg);
            Platform.exit();
            System.exit(0);
        });
    }

    public static void errorAlert(String title, String header, String content) {
        alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public static String appCounter(Vector<Appointment> vec, boolean bool) {
        int c = 0;
        for (Appointment appointment : vec) {
            DateFormat format = new SimpleDateFormat("dd-MM-yyyy hh:mm a");
            Date day = null, today = null;
            try {
                day = format.parse(appointment.date + " " + appointment.time);
                today = new Date(System.currentTimeMillis());
            } catch (ParseException ex) {
                Logger.getLogger(Appointment.class.getName()).log(Level.SEVERE, null, ex);
            }
            if (day.after(today)) {
                if (appointment.seen == bool) {
                    c++;
                }
            }
        }
        return String.valueOf(c);
    }

    public static void refreshRequestDoctor() {
        //update profile msg
        msg = new Message();
        msg.type = 5;
        msg.successType = 1;
        msg.doctor = doctor;
        nc.send(msg);
        //refreshing scene after 500ms
        Timer timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> {
                    doctorScene();
                });
                timer.cancel();
            }
        };
        timer.schedule(task, 500, 1000);
    }

    public static void refreshRequestPatient() {
        //update profile msg
        msg = new Message();
        msg.type = 5;
        msg.successType = 2;
        msg.paitent = patient;
        nc.send(msg);
        //refreshing scene after 500ms
        Timer timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> {
                    patientScene();
                });
                timer.cancel();
            }
        };
        timer.schedule(task, 500, 1000);
    }

    public static boolean isNumeric(String str) {
        try {
            double d = Double.parseDouble(str);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }

}
