/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hmsclient;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Vector;
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
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import resources.Appointment;
import resources.Doctor;
import resources.Message;
import resources.NetworkUtil;
import resources.Patient;

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

    public HMSClient() {
        serverAddress = "127.0.0.1";
        serverPort = 33333;
        doctor = new Doctor();
        patient = new Patient();
        appointment = new Appointment();
        online = new HashMap<>();
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
        Text dobp = new Text("Address : " + app.doctor.address);
        dobp.setLayoutX(9);
        dobp.setLayoutY(64);
        dobp.setFont(new Font(24));
        Text sexp = new Text("Sex : " + app.doctor.sex);
        sexp.setLayoutX(9);
        sexp.setLayoutY(96);
        sexp.setFont(new Font(24));
        Text nump = new Text("Contact No : " + app.doctor.phone);
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
        Text dateT = new Text("Date :");
        dateT.setLayoutX(26);
        dateT.setLayoutY(412);
        dateT.setFont(new Font(22));
        TextField timeTf = new TextField();
        timeTf.setLayoutX(113);
        timeTf.setLayoutY(430);
        timeTf.setPrefHeight(31);
        timeTf.setPrefWidth(291);
        timeTf.setText(app.time);
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
        pane.getChildren().addAll(appT, confirmT, sc, ptimeT, pdateT, dateT, datepic, timeT, timeTf, submit);
        //submit button action
        submit.setOnMouseClicked((MouseEvent event) -> {
            msg = new Message();
            msg.type = 4;
            msg.successType = 1;
            app.date = datepic.getValue().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
            app.time = timeTf.getText();
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
        popupp.setX(x);
        popupp.setY(y);
        popupp.getContent().addAll(pane);
        popupp.show(stage);
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
        Image img = new Image("logo.png");
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
        user.setPrefWidth(254);
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
        pass.setPrefWidth(254);
        pass.setPromptText("Password");
        RadioButton docB = new RadioButton("Doctor");
        docB.setLayoutX(352);
        docB.setLayoutY(478);
        RadioButton paitB = new RadioButton("Patient");
        paitB.setLayoutX(257);
        paitB.setLayoutY(478);
        paitB.setSelected(true);
        ToggleGroup toggleB = new ToggleGroup();
        docB.setToggleGroup(toggleB);
        paitB.setToggleGroup(toggleB);
        //login button
        Button loginButton = new Button("Login");
        loginButton.setLayoutX(431);
        loginButton.setLayoutY(521);
        f = new Font(19);
        loginButton.setFont(f);
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
        Scene scene = new Scene(mainPane);
        stage.setScene(scene);
        stage.setTitle("Hospital Management System : Login");
        stage.show();
        stage.getIcons().add(new Image("icon.png"));
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
        Image img = new Image("logo.png");
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
        user.setPrefWidth(254);
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
        pass.setPrefWidth(254);
        pass.setPromptText("Password");
        RadioButton docB = new RadioButton("Doctor");
        docB.setLayoutX(352);
        docB.setLayoutY(478);
        RadioButton paitB = new RadioButton("Patient");
        paitB.setLayoutX(257);
        paitB.setLayoutY(478);
        paitB.setSelected(true);
        ToggleGroup toggleB = new ToggleGroup();
        docB.setToggleGroup(toggleB);
        paitB.setToggleGroup(toggleB);
        //login button
        Button registerButton = new Button("Register");
        registerButton.setLayoutX(431);
        registerButton.setLayoutY(521);
        f = new Font(19);
        registerButton.setFont(f);
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
        Image img = new Image("logo.png");
        ImageView imgv = new ImageView(img);
        imgv.setFitHeight(85);
        imgv.setFitWidth(80);
        imgv.setLayoutX(36);
        imgv.setLayoutY(35);
        //patIcon
        Image patIm = new Image("doctorIcon.png");
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
        nameTf.setPrefWidth(300);
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
        deptTf.setPrefWidth(300);
        deptTf.setPromptText("Enter department.");
        deptTf.setFont(Font.font("Serif", 20));
        //sex
        Text sexT = new Text("Sex :");
        sexT.setLayoutX(122);
        sexT.setLayoutY(323);
        sexT.setFont(new Font("System Bold", 22));
        ComboBox combo = new ComboBox();
        combo.setLayoutX(122);
        combo.setLayoutY(333);
        combo.setPrefWidth(300);
        combo.setPromptText("Select Sex");
        combo.getItems().add("Male");
        combo.getItems().add("Female");
        combo.getItems().add("Other");
        combo.setStyle("-fx-border-color: #bfbfbf");
        combo.getStylesheets().add(HMSClient.class.getResource("comboStyle.css").toExternalForm());
        //dob 
        Text dobT = new Text("Date of birth :");
        dobT.setLayoutX(122);
        dobT.setLayoutY(390);
        dobT.setFont(new Font("System Bold", 22));
        TextField dobTf = new TextField();
        dobTf.setLayoutX(122);
        dobTf.setLayoutY(400);
        dobTf.setPrefWidth(300);
        dobTf.setPromptText("dd-mm-yyyy");
        dobTf.setFont(Font.font("Serif", 20));
        // contact number 
        Text numT = new Text("Contact Number :");
        numT.setLayoutX(122);
        numT.setLayoutY(460);
        numT.setFont(new Font("System Bold", 22));
        TextField numTf = new TextField();
        numTf.setLayoutX(122);
        numTf.setLayoutY(470);
        numTf.setPrefWidth(300);
        numTf.setPromptText("Enter contact number.");
        numTf.setFont(Font.font("Serif", 20));
        // Address
        Text addT = new Text("Address :");
        addT.setLayoutX(122);
        addT.setLayoutY(460 + 70);
        addT.setFont(new Font("System Bold", 22));
        TextField addTf = new TextField();
        addTf.setLayoutX(122);
        addTf.setLayoutY(470 + 70);
        addTf.setPrefWidth(300);
        addTf.setPromptText("Enter address.");
        addTf.setFont(Font.font("Serif", 20));
        Button submit = new Button();
        submit.setText("Submit");
        submit.setLayoutX(305 + 50);
        submit.setLayoutY(470 + 70 + 50);
        submit.setOnMouseClicked((MouseEvent event) -> {
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
            } else if ("".equals(dobTf.getText())) {
                okay = false;
                errorAlert("Enter your date of birth !", "Enter your date of birth.", null);
            } else if ("".equals(numTf.getText())) {
                okay = false;
                errorAlert("Enter your number !", "Enter your number", null);
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
                msg.doctor.dob = dobTf.getText();
                msg.doctor.phone = numTf.getText();
                nc.send(msg);
                // clearing 
                nameTf.clear();
                addTf.clear();
                deptTf.clear();
                dobTf.clear();
                numTf.clear();
            }
        });
        contentPane.getChildren().addAll(titleT, nameT, nameTf, deptT, deptTf, sexT, combo, dobT, dobTf, numT, numTf, addT, addTf, submit);
        scPane.setContent(contentPane);
        mainPane.getChildren().addAll(titlePane, scPane);
        Scene scene = new Scene(mainPane);
        stage.setScene(scene);
        stage.setTitle("Hospital Management System : Doctor Registration");
        stage.show();
        stage.getIcons().add(new Image("icon.png"));
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
        Image img = new Image("logo.png");
        ImageView imgv = new ImageView(img);
        imgv.setFitHeight(85);
        imgv.setFitWidth(80);
        imgv.setLayoutX(36);
        imgv.setLayoutY(35);
        //patIcon
        Image patIm = new Image("patientIcon.png");
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
        nameTf.setPrefWidth(300);
        nameTf.setFont(Font.font("Serif", 18));
        nameTf.setPromptText("Enter name.");
        //email
        Text emailT = new Text("E-mail :");
        emailT.setLayoutX(122);
        emailT.setLayoutY(250);
        emailT.setFont(new Font("System Bold", 20));
        TextField emailTf = new TextField();
        emailTf.setLayoutX(122);
        emailTf.setLayoutY(261);
        emailTf.setPrefWidth(300);
        emailTf.setFont(Font.font("Serif", 18));
        emailTf.setPromptText("Enter e-mail.");
        //sex
        Text sexT = new Text("Sex :");
        sexT.setLayoutX(122);
        sexT.setLayoutY(323);
        sexT.setFont(new Font("System Bold", 20));
        ComboBox combo = new ComboBox();
        combo.setLayoutX(122);
        combo.setLayoutY(333);
        combo.setPrefWidth(300);
        combo.setPromptText("Select Sex");
        combo.getItems().add("Male");
        combo.getItems().add("Female");
        combo.getItems().add("Other");
        combo.setStyle("-fx-border-color: #bfbfbf");
        combo.getStylesheets().add(HMSClient.class.getResource("comboStyle.css").toExternalForm());
        //dob 
        Text dobT = new Text("Date of birth :");
        dobT.setLayoutX(122);
        dobT.setLayoutY(390);
        dobT.setFont(new Font("System Bold", 20));
        TextField dobTf = new TextField();
        dobTf.setLayoutX(122);
        dobTf.setLayoutY(400);
        dobTf.setPrefWidth(300);
        dobTf.setFont(Font.font("Serif", 18));
        dobTf.setPromptText("dd-mm-yyyy");
        // contact number 
        Text numT = new Text("Contact Number :");
        numT.setLayoutX(122);
        numT.setLayoutY(460);
        numT.setFont(new Font("System Bold", 20));
        TextField numTf = new TextField();
        numTf.setLayoutX(122);
        numTf.setLayoutY(470);
        numTf.setPrefWidth(300);
        numTf.setFont(Font.font("Serif", 18));
        numTf.setPromptText("Enter your number.");
        // Address
        Text addT = new Text("Address :");
        addT.setLayoutX(122);
        addT.setLayoutY(460 + 70);
        addT.setFont(new Font("System Bold", 20));
        TextField addTf = new TextField();
        addTf.setLayoutX(122);
        addTf.setLayoutY(470 + 70);
        addTf.setPrefWidth(300);
        addTf.setFont(Font.font("Serif", 18));
        addTf.setPromptText("Enter address.");
        Button submit = new Button();
        submit.setText("Submit");
        submit.setLayoutX(305 + 50);
        submit.setLayoutY(470 + 70 + 50);
        submit.setOnMouseClicked((MouseEvent event) -> {
            boolean okay = true;
            String s = (String) combo.getValue();
            if ("".equals(nameTf.getText())) {
                okay = false;
                errorAlert("Enter your name !", "Enter your name.", null);
            } else if ("".equals(emailTf.getText())) {
                okay = false;
                errorAlert("Enter your e-mail !", "Enter your e-mail.", null);
            } else if (s == null) {
                okay = false;
                errorAlert("Select your sex !", "Select your sex.", null);
            } else if ("".equals(dobTf.getText())) {
                okay = false;
                errorAlert("Enter your date of birth !", "Enter your date of birth.", null);
            } else if ("".equals(numTf.getText())) {
                okay = false;
                errorAlert("Enter your number !", "Enter your number", null);
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
                msg.paitent.address = addTf.getText();
                msg.paitent.email = emailTf.getText();
                msg.paitent.dob = dobTf.getText();
                msg.paitent.number = numTf.getText();
                nc.send(msg);
                patient = msg.paitent;
                //clearing 
                nameTf.clear();
                addTf.clear();
                emailTf.clear();
                dobTf.clear();
                numTf.clear();
            }
        });
        contentPane.getChildren().addAll(titleT, nameT, nameTf, emailT, emailTf, sexT, combo, dobT, dobTf, numT, numTf, addT, addTf, submit);
        //
        scPane.setContent(contentPane);
        //
        mainPane.getChildren().addAll(titlePane, scPane);
        Scene scene = new Scene(mainPane);
        stage.setScene(scene);
        stage.setTitle("Hospital Management System : Patient Registration");
        stage.show();
        stage.getIcons().add(new Image("icon.png"));
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
        Image logo = new Image("logo.png");
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
        ImageView pim = new ImageView(new Image("doctorIcon.png"));
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
            ListView<Appointment> pendingL = new ListView();
            pendingL.setLayoutX(50);
            pendingL.setLayoutY(95);
            pendingL.setPrefHeight(499);
            pendingL.setPrefWidth(758);
            //updating cell of pendingL
            pendingL.setCellFactory((ListView<Appointment> param) -> {
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
            Collections.sort(HMSClient.doctor.appointments);
            for (Appointment appointment : HMSClient.doctor.appointments) {
                if (appointment.seen == true) {
                    pendingL.getItems().add(appointment);
                }
            }
            //adding css
            pendingL.getStylesheets().add(HMSClient.class.getResource("appointlistStyle.css").toExternalForm());
            //event for pendingL
            pendingL.setOnMouseClicked((MouseEvent event) -> {
                Appointment app = new Appointment();
                if (!pendingL.getSelectionModel().isEmpty()) {
                    app = (Appointment) pendingL.getSelectionModel().getSelectedItem();
                    popupDocSeen(app, event.getScreenX(), event.getScreenY());
                }
            });
            // refresh
            Text refresh = new Text("Refresh");
            refresh.setLayoutX(689);
            refresh.setLayoutY(52);
            refresh.setFill(Color.CHOCOLATE);
            refresh.setFont(new Font("System Bold", 27));
            pane.getChildren().addAll(appT, pendingL, refresh);
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
            ListView<Appointment> pendingL = new ListView();
            pendingL.setLayoutX(50);
            pendingL.setLayoutY(95);
            pendingL.setPrefHeight(499);
            pendingL.setPrefWidth(758);
            //updating cell of pendingL
            pendingL.setCellFactory((ListView<Appointment> param) -> {
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
            Collections.sort(HMSClient.doctor.appointments);
            for (Appointment appointment : HMSClient.doctor.appointments) {
                if (appointment.seen == false) {
                    pendingL.getItems().add(appointment);
                }
            }
            //adding css
            pendingL.getStylesheets().add(HMSClient.class.getResource("appointlistStyle.css").toExternalForm());
            //event for pendingL
            pendingL.setOnMouseClicked((MouseEvent event) -> {
                Appointment app = new Appointment();
                if (!pendingL.getSelectionModel().isEmpty()) {
                    app = (Appointment) pendingL.getSelectionModel().getSelectedItem();
                    popupDocUnseen(app, event.getScreenX(), event.getScreenY());
                }
            });
            Text refresh = new Text("Refresh");
            refresh.setLayoutX(689);
            refresh.setLayoutY(52);
            refresh.setFill(Color.CHOCOLATE);
            refresh.setFont(new Font("System Bold", 27));
            pane.getChildren().addAll(appT, pendingL, refresh);
            //events
            pane.setOnMouseClicked((MouseEvent event) -> {
                popup.hide();
                popupp.hide();
            });
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
        } else if (sceneNum == 3) {
            //update profile scene
            Text up = new Text("Update Profile");
            up.setLayoutX(39);
            up.setLayoutY(60);
            up.setFont(new Font("System Bold", 33));
            //name
            Text nameT = new Text("Name :");
            nameT.setLayoutX(39);
            nameT.setLayoutY(118);
            nameT.setFont(new Font("System Bold", 25));
            TextField nameTf = new TextField();
            nameTf.setLayoutX(39);
            nameTf.setLayoutY(133);
            nameTf.setPrefWidth(300);
            nameTf.setText(doctor.name);
            nameTf.setFont(Font.font("Serif", 20));
            //email
            Text deptT = new Text("Department :");
            deptT.setLayoutX(39);
            deptT.setLayoutY(206);
            deptT.setFont(new Font("System Bold", 25));
            TextField deptTf = new TextField();
            deptTf.setLayoutX(39);
            deptTf.setLayoutY(220);
            deptTf.setPrefWidth(300);
            deptTf.setText(doctor.dept);
            deptTf.setFont(Font.font("Serif", 20));
            //sex
            Text sexT = new Text("Sex :");
            sexT.setLayoutX(39);
            sexT.setLayoutY(293);
            sexT.setFont(new Font("System Bold", 25));
            ComboBox combo = new ComboBox();
            combo.setLayoutX(39);
            combo.setLayoutY(305);
            combo.setPrefWidth(300);
            combo.setPromptText("Select Sex");
            combo.getItems().add("Male");
            combo.getItems().add("Female");
            combo.getItems().add("Other");
            combo.setStyle("-fx-border-color: #bfbfbf");
            combo.getStylesheets().add(HMSClient.class.getResource("comboStyle.css").toExternalForm());
            combo.getSelectionModel().select(doctor.sex);
            //dob 
            Text dobT = new Text("Date of birth :");
            dobT.setLayoutX(39);
            dobT.setLayoutY(373);
            dobT.setFont(new Font("System Bold", 25));
            TextField dobTf = new TextField();
            dobTf.setLayoutX(39);
            dobTf.setLayoutY(385);
            dobTf.setPrefWidth(300);
            dobTf.setText(doctor.dob);
            dobTf.setFont(Font.font("Serif", 20));
            // contact number 
            Text numT = new Text("Contact Number :");
            numT.setLayoutX(39);
            numT.setLayoutY(453);
            numT.setFont(new Font("System Bold", 25));
            TextField numTf = new TextField();
            numTf.setLayoutX(39);
            numTf.setLayoutY(463);
            numTf.setPrefWidth(300);
            numTf.setText(doctor.phone);
            numTf.setFont(Font.font("Serif", 20));
            // Address
            Text addT = new Text("Address :");
            addT.setLayoutX(39);
            addT.setLayoutY(528);
            addT.setFont(new Font("System Bold", 25));
            TextField addTf = new TextField();
            addTf.setLayoutX(39);
            addTf.setLayoutY(540);
            addTf.setPrefWidth(300);
            addTf.setText(doctor.address);
            addTf.setFont(Font.font("Serif", 20));
            //button
            Button submit = new Button();
            submit.setText("Update");
            submit.setLayoutX(248);
            submit.setLayoutY(597);
            //adding to pane
            pane.getChildren().addAll(up, nameT, nameTf, deptT, deptTf, sexT, combo, dobT, dobTf, numT, numTf, addT, addTf, submit);
            // events 
            submit.setOnMouseClicked((MouseEvent event) -> {
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
                } else if ("".equals(dobTf.getText())) {
                    okay = false;
                    errorAlert("Enter your date of birth !", "Enter your date of birth.", null);
                } else if ("".equals(numTf.getText())) {
                    okay = false;
                    errorAlert("Enter your number !", "Enter your number", null);
                } else if ("".equals(addTf.getText())) {
                    okay = false;
                    errorAlert("Enter your address !", "Enter your address", null);
                }
                if (okay) {
                    doctor.name = nameTf.getText();
                    doctor.dept = deptTf.getText();
                    doctor.address = addTf.getText();
                    doctor.dob = dobTf.getText();
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
        Scene scene = new Scene(mainPane);
        stage.setScene(scene);
        stage.setTitle("Hospital Management System : " + doctor.name);
        stage.show();
        stage.getIcons().add(new Image("icon.png"));
        stage.setOnCloseRequest((WindowEvent t) -> {
            //offline message
            msg = new Message();
            msg.type = 6;
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
        Image logo = new Image("logo.png");
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
        ImageView pim = new ImageView(new Image("patientIcon.png"));
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
        ImageView ref = new ImageView(new Image("refresh.png"));
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
        ListView onlineL = new ListView();
        onlineL.setLayoutX(16);
        onlineL.setLayoutY(49);
        onlineL.setPrefHeight(327);
        onlineL.setPrefWidth(230);
        //adding to onlineL
        for (HashMap.Entry<String, String> entry : online.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            onlineL.getItems().add(value);
        }
        //adding css
        onlineL.getStylesheets().add(HMSClient.class.getResource("onlinelistStyle.css").toExternalForm());
        AnchorPane.setTopAnchor(onlineL, 49.0);
        AnchorPane.setBottomAnchor(onlineL, 0.0);
        AnchorPane.setLeftAnchor(onlineL, 0.0);
        AnchorPane.setRightAnchor(onlineL, 0.0);
        //adding items
        pn3.getChildren().addAll(pn4, onlineL);
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
                ListView<Appointment> appointL = new ListView();
                appointL.setLayoutX(33);
                appointL.setLayoutY(96);
                appointL.setPrefHeight(512);
                appointL.setPrefWidth(788);
                //updating cell of appointL
                appointL.setCellFactory((ListView<Appointment> param) -> {
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
                Collections.sort(HMSClient.patient.appointments);
                for (Appointment appointment : HMSClient.patient.appointments) {
                    if (appointment.seen == true) {
                        appointL.getItems().add(appointment);
                    }
                }
                appointL.getStylesheets().add(HMSClient.class.getResource("appointlistStyle.css").toExternalForm());
                appointL.setOnMouseClicked((MouseEvent event) -> {
                    Appointment app = new Appointment();
                    if (!appointL.getSelectionModel().isEmpty()) {
                        app = (Appointment) appointL.getSelectionModel().getSelectedItem();
                        popupPatient(app, event.getScreenX(), event.getScreenY());
                    }
                });
                pane.getChildren().addAll(appT, appointL, dot, appTp, refresh);
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
                Text appTp = new Text("Pending Appointments(" + appCounter(patient.appointments, true) + ")");
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
                ListView<Appointment> pendingL = new ListView<>();
                pendingL.setLayoutX(33);
                pendingL.setLayoutY(96);
                pendingL.setPrefHeight(512);
                pendingL.setPrefWidth(788);
                //updating cell of appointL
                pendingL.setCellFactory((ListView<Appointment> param) -> {
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
                Collections.sort(HMSClient.patient.appointments);
                for (Appointment appointment : HMSClient.patient.appointments) {
                    if (appointment.seen == false) {
                        pendingL.getItems().add(appointment);
                    }
                }
                pendingL.getStylesheets().add(HMSClient.class.getResource("appointlistStyle.css").toExternalForm());
                pendingL.setOnMouseClicked((MouseEvent event) -> {
                    Appointment app = new Appointment();
                    if (!pendingL.getSelectionModel().isEmpty()) {
                        app = (Appointment) pendingL.getSelectionModel().getSelectedItem();
                        popupPatient(app, event.getScreenX(), event.getScreenY());
                    }
                });
                pane.getChildren().addAll(appT, refresh, pendingL, dot, appTp);
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
            ComboBox combo = new ComboBox();
            combo.setLayoutX(27);
            combo.setLayoutY(128);
            combo.setPrefHeight(31);
            combo.setPrefWidth(323);
            combo.setPromptText("Select Doctor");
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
            apptTf.setPrefWidth(323);
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
            dateP.setPrefWidth(323);
            Text timeT = new Text("Preferable Time:");
            timeT.setLayoutX(28);
            timeT.setLayoutY(373);
            timeT.setFont(new Font("System Bold", 25));
            TextField timeTf = new TextField();
            timeTf.setLayoutX(28);
            timeTf.setLayoutY(385);
            timeTf.setPrefHeight(31);
            timeTf.setPrefWidth(323);
            timeTf.setFont(Font.font("Serif", 20));
            //setting current time
            String timeStamp = new SimpleDateFormat("hh:mm a").format(Calendar.getInstance().getTime());
            timeTf.setText(timeStamp);
            //description
            Text desT = new Text("Description of symptoms :");
            desT.setLayoutX(28);
            desT.setLayoutY(450);
            desT.setFont(new Font("System Bold", 25));
            TextArea desTa = new TextArea();
            desTa.setLayoutX(28);
            desTa.setLayoutY(470);
            desTa.setPrefWidth(323);
            desTa.setPrefHeight(80);
            desTa.setFont(Font.font("Serif", 20));
            desTa.setPromptText("Enter a brief description of your problems.");
            //button
            Button submit = new Button();
            submit.setText("Submit");
            submit.setLayoutX(284);
            submit.setLayoutY(570);
            //adding to pane
            pane.getChildren().addAll(app, docT, combo, apptT, apptTf, dateP, dateT, timeT, timeTf, desT, desTa, submit);
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
                } else if ("".equals(timeTf.getText())) {
                    okay = false;
                    errorAlert("Select a valid time !", "Select a valid time.", null);
                } else if ("".equals(desTa.getText())) {
                    okay = false;
                    errorAlert("Enter description of symptoms !", "Enter description of symptoms.", null);
                }
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
                    msg.appointment.time = timeTf.getText();
                    msg.appointment.title = apptTf.getText();
                    msg.appointment.description = desTa.getText();
                    nc.send(msg);
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
            nameTf.setPrefWidth(300);
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
            emailTf.setPrefWidth(300);
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
            combo.setPrefWidth(300);
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
            TextField dobTf = new TextField();
            dobTf.setLayoutX(29);
            dobTf.setLayoutY(385);
            dobTf.setPrefWidth(300);
            dobTf.setText(patient.dob);
            dobTf.setFont(Font.font("Serif", 20));
            // contact number 
            Text numT = new Text("Contact Number :");
            numT.setLayoutX(29);
            numT.setLayoutY(453);
            numT.setFont(new Font("System Bold", 25));
            TextField numTf = new TextField();
            numTf.setLayoutX(29);
            numTf.setLayoutY(463);
            numTf.setPrefWidth(300);
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
            addTf.setPrefWidth(300);
            addTf.setText(patient.address);
            addTf.setFont(Font.font("Serif", 20));
            //button
            Button submit = new Button();
            submit.setText("Update");
            submit.setLayoutX(238);
            submit.setLayoutY(597);
            //adding to pane
            pane.getChildren().addAll(up, nameT, nameTf, emailT, emailTf, sexT, combo, dobT, dobTf, numT, numTf, addT, addTf, submit);
            // events 
            submit.setOnMouseClicked((MouseEvent event) -> {
                boolean okay = true;
                String s = (String) combo.getValue();
                if ("".equals(nameTf.getText())) {
                    okay = false;
                    errorAlert("Enter your name !", "Enter your name.", null);
                } else if ("".equals(emailTf.getText())) {
                    okay = false;
                    errorAlert("Enter your e-mail !", "Enter your e-mail.", null);
                } else if (s == null) {
                    okay = false;
                    errorAlert("Select your sex !", "Select your sex.", null);
                } else if ("".equals(dobTf.getText())) {
                    okay = false;
                    errorAlert("Enter your date of birth !", "Enter your date of birth.", null);
                } else if ("".equals(numTf.getText())) {
                    okay = false;
                    errorAlert("Enter your number !", "Enter your number", null);
                } else if ("".equals(addTf.getText())) {
                    okay = false;
                    errorAlert("Enter your address !", "Enter your address", null);
                }
                if (okay) {
                    patient.name = nameTf.getText();
                    patient.email = emailTf.getText();
                    patient.address = addTf.getText();
                    patient.dob = dobTf.getText();
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
            loginScene();
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
        mainPane.setOnMouseClicked((MouseEvent event) -> {
            patpopup.hide();
        });
        Scene scene = new Scene(mainPane);
        stage.setScene(scene);
        stage.setTitle("Hospital Management System : " + patient.name);
        stage.show();
        stage.getIcons().add(new Image("icon.png"));
        stage.setOnCloseRequest((WindowEvent t) -> {
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
            if (appointment.seen == bool) {
                c++;
            }
        }
        return String.valueOf(c);
    }

}
