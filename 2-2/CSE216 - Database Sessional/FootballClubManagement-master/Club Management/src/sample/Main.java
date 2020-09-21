package sample;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.EventHandler;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.VBox;
import resources.BoardStaff;
import resources.ManagerStaff;
import resources.PlayerStat;
import resources.Team;
import resources.ComBoard;
import resources.SalaryUpdate;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;


import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicReference;


public class Main extends Application {
    public static Stage stage;
    public static DropShadow borderGlow = new DropShadow();
    public static Alert alert;
    public static Connection con;
    public static PreparedStatement pst;
    public static CallableStatement cstmt;
    public static ResultSet rs;
    public static TableView table;
    public static TableView<ManagerStaff> managerStaffTable;
    public static ObservableList<Team> data;
    public static ObservableList<ManagerStaff> msdata;
    public static int tableSize;
    public static Popup popup;
    public static int managerSceneFlag;

    //
    public static TableView<BoardStaff> boardStaffTable;
    public static ObservableList<BoardStaff> bsdata;
    public static int boardMemberSceneFlag;
    //
    public static TableView<ComBoard> comBoardTable;
    public static ObservableList<ComBoard> cbdata;
    public static int superAdminSceneFlag;
    //
    public static TableView<SalaryUpdate> salUpdateTable;
    public static ObservableList<SalaryUpdate> salUpdatedata;
    //
    public static TextField tf = textfieldCreator("", 900, 500, 30, 200);

    public Main() {
        borderGlow.setOffsetY(0f);
        borderGlow.setOffsetX(0f);
        borderGlow.setColor(Color.CORNFLOWERBLUE);
        borderGlow.setWidth(40);
        borderGlow.setHeight(40);
        data = FXCollections.observableArrayList();
        msdata = FXCollections.observableArrayList();
        tableSize = 0;
        managerSceneFlag = 0;
        popup = new Popup();
        //
        bsdata = FXCollections.observableArrayList();
        boardMemberSceneFlag = 0;
        //
        cbdata = FXCollections.observableArrayList();
        superAdminSceneFlag = 0;
        //
        salUpdatedata = FXCollections.observableArrayList();

        try {
            con = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:ORCL", "shoumik", "shoumik123");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        //

    }

    @Override
    public void start(Stage primaryStage) {
        stage = primaryStage;
        //stage.setWidth(1550);
        //stage.setHeight(830);
        //loginScreen();
        //playerPage(1);
        superAdminPage(28);
    }


    public static void loginScreen() {
        AnchorPane mainPane = new AnchorPane();
        mainPane.setPrefHeight(795);
        mainPane.setPrefWidth(1089);
        mainPane.setStyle("-fx-background-color: #ccfbff;");
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
        title.setText("Football\nClub\n");
        title.setLayoutX(200);
        title.setLayoutY(42);
        Font f = new Font("System Bold", 26);
        title.setFont(f);
        //image
        Image img = new Image("logo.png");
        ImageView imgv = new ImageView(img);
        imgv.setFitHeight(90);
        imgv.setFitWidth(90);
        imgv.setLayoutX(50);
        imgv.setLayoutY(20);
        titlePane.getChildren().addAll(title, imgv);
        //table
        createTable();
        updateTable();
        //login
        TextField userName = new TextField();
        userName.setLayoutX(900);
        userName.setLayoutY(70);
        userName.setPrefHeight(35);
        userName.setPrefWidth(200);
        userName.setEffect(borderGlow);
        userName.setPromptText("Username");
        userName.setText("brian");
        PasswordField passWord = new PasswordField();
        passWord.setLayoutX(1110);
        passWord.setLayoutY(70);
        passWord.setPrefHeight(35);
        passWord.setPrefWidth(200);
        passWord.setEffect(borderGlow);
        passWord.setPromptText("Password");
        passWord.setText("brian");
        Button login = new Button("Login");
        login.setLayoutX(1330);
        login.setLayoutY(68);
        login.setOnMouseClicked((MouseEvent event) -> {
            String user = userName.getText();
            String pass = passWord.getText();
            int count = 0, id = 0;
            String userType;
            String sql = "SELECT COUNT(*), TYPE, ID FROM USERS WHERE USERNAME = '" + user + "' and PASSWORD='" + pass + "' GROUP BY TYPE,ID";
            try {
                pst = con.prepareStatement(sql);
                rs = pst.executeQuery();
                while (rs.next()) {
                    count = rs.getInt(1);
                    userType = rs.getString(2);
                    id = rs.getInt(3);
                    if (count > 0) {
                        if (userType.equals("Manager")) {
                            managerPage(id);
                        } else if (userType.equals("Board Member")) {
                            String prs=isStillPresident(id);
                            System.out.println(prs);
                            if(prs.matches("YES"))
                            {
                                superAdminPage(id);
                            }
                            else
                            {
                                boardMemberPage(id);
                            }

                        } else if (userType.equals("Player")) {
                            playerPage(id);
                        } else if (userType.equals("Scout")) {
                            scoutPage(id);
                        }
                    }
                }
                if (count == 0)
                    errorAlert("Invalid Username/Password", "Invalid Username/Password", "Invalid Username/Password");
            } catch (SQLException e) {
                errorAlert("Invalid Username/Password", "Invalid Username/Password", "Invalid Username/Password");
            }
        });
        mainPane.getChildren().addAll(titlePane, userName, passWord, login, table, tf);
        mainPane.getStylesheets().add(Main.class.getResource("button.css").toExternalForm());
        Scene scene = new Scene(mainPane);
        stage.setScene(scene);
        stage.setTitle("Football Club Management");
        stage.show();
        stage.getIcons().add(new Image("icon.png"));
        stage.setOnCloseRequest((WindowEvent t) -> {
            Platform.exit();
            System.exit(0);
        });
    }

    //new


    public static void scoutPage(int sid) {
        String name = "", address = "", contact = "", jdate = "", edate = "", salary = "", report = "", team = "", manager = "", region = "";
        int mid = 1;
        //sql
        String sql = "select t.TEAM_NAME,t.MANAGER_ID,sf.STAFF_NAME,sf.STAFF_ADDRESS,sf.CONTACT_NO,to_char(sf.SDATE,'DD-MON-YYYY'),to_char(sf.EDATE,'DD-MON-YYYY'),sf.SALARY,abs(trunc(sc.REPORTING_DATE)-trunc(SYSDATE)),sc.SCOUTING_REGION,sc.staff_id from staffs sf,scouts sc,TEAMS t where sc.STAFF_ID=sf.STAFF_ID and sc.scouts_id=" + sid + " and t.SCOUT_ID=" + sid;
        try {
            pst = con.prepareStatement(sql);
            rs = pst.executeQuery();
            while (rs.next()) {
                team = rs.getString(1);
                mid = rs.getInt(2);
                name = rs.getString(3);
                address = rs.getString(4);
                contact = rs.getString(5);
                jdate = rs.getString(6);
                edate = rs.getString(7);
                salary = rs.getString(8);
                report = rs.getString(9);
                region = rs.getString(10);
            }
        } catch (SQLException e) {
            errorAlert("Error", "Error", null);
        }
        sql = "select s.STAFF_NAME from staffs s, managers m where s.STAFF_ID=m.STAFF_ID and m.MANAGER_ID=" + mid;
        try {
            pst = con.prepareStatement(sql);
            rs = pst.executeQuery();
            while (rs.next()) {
                manager = rs.getString(1);
            }
        } catch (SQLException e) {
            errorAlert("Error", "Error", "Error");
        }
        //
        VBox box = new VBox();
        box.setPrefHeight(750);
        box.setPrefWidth(1300);
        box.setStyle("-fx-background-color: white");
        box.getStylesheets().add(Main.class.getResource("table.css").toExternalForm());
        AnchorPane topPane = new AnchorPane();
        topPane.setPrefHeight(125);
        topPane.setPrefWidth(1300);
        topPane.setStyle("-fx-background-color: white");
        Image logoImage = new Image("logo.png");
        ImageView logo = new ImageView(logoImage);
        logo.setFitWidth(125);
        logo.setFitHeight(126);
        logo.setPickOnBounds(true);
        logo.setPreserveRatio(true);
        Text title = new Text("Clubname");
        title.setLayoutX(143);
        title.setLayoutY(83);
        title.setFont(new Font(51));
        Image pimg = new Image("scouticon.png");
        ImageView scoutIcon = new ImageView(pimg);
        scoutIcon.setFitHeight(138);
        scoutIcon.setFitWidth(200);
        scoutIcon.setLayoutX(945);
        scoutIcon.setLayoutY(15);
        scoutIcon.setPickOnBounds(true);
        scoutIcon.setPreserveRatio(true);
        //sql to get the player name
        Text adminName = new Text(name);
        adminName.setLayoutX(1095);
        adminName.setLayoutY(64);
        adminName.setFont(new Font("System Bold", 21));
        Text logout = new Text("(Logout)");
        logout.setLayoutX(1209);
        logout.setLayoutY(94);
        logout.setFont(new Font("System Bold Italic", 17));
        logout.setOnMouseEntered((MouseEvent event) -> {
            logout.setFont(new Font("System Bold Italic", 20));
            logout.setFill(Color.DARKGRAY);
        });
        logout.setOnMouseExited((MouseEvent event) -> {
            logout.setFont(new Font("System Bold Italic", 17));
            logout.setFill(Color.BLACK);
        });
        logout.setOnMouseClicked((MouseEvent event) -> {
            loginScreen();
        });
        topPane.getChildren().addAll(logo, title, scoutIcon, adminName, logout);
        TabPane tabs = new TabPane();
        tabs.setPrefHeight(634);
        tabs.setPrefWidth(1300);
        tabs.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        Tab homeTab = new Tab();
        homeTab.setText("Home");
        AnchorPane homePane = new AnchorPane();
        homePane.setPrefHeight(180);
        homePane.setPrefWidth(200);
        homePane.setStyle("-fx-background-color: #ccfbff");
        Text t1 = textCreator(name, 546, 72, "System Bold", 41);
        Text t2 = textCreator("Reporting in " + report + " days", 877, 150, 33);
        Text t3 = textCreator("Team  : " + team, 877, 219, 33);
        Text t4 = textCreator("Under : " + manager, 877, 289, 33);
        Text t5 = textCreator("Name           : " + name, 94, 151, 33);
        Text t6 = textCreator("Contact no   : " + contact, 94, 267, 33);
        Text t7 = textCreator("Address        : " + address, 94, 210, 33);
        Text t8 = textCreator("Joining date : " + jdate, 94, 327, 33);
        Text t9 = textCreator("Contract till  : " + edate, 94, 385, 33);
        Text t10 = textCreator("Salary           : " + salary + "$", 94, 440, 33);
        Text t11 = textCreator("Region  : " + region, 877, 350, 33);
        homePane.getChildren().addAll(t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11);
        homeTab.setContent(homePane);
        Tab editTab = new Tab();
        editTab.setText("Edit Profile");
        AnchorPane editPane = new AnchorPane();
        editPane.setPrefHeight(180);
        editPane.setPrefWidth(200);
        editPane.setStyle("-fx-background-color: #ccfbff");
        TextField nameF = textfieldCreator(name, 83, 119, 30, 300);
        TextField addF = textfieldCreator(address, 83, 215, 30, 300);
        TextField contactF = textfieldCreator(contact, 83, 311, 30, 300);
        t1 = textCreator("Name : ", 83, 97, 28);
        t2 = textCreator("Address : ", 83, 197, 28);
        t3 = textCreator("Contact no :", 83, 293, 28);
        Button sub = new Button("Submit");
        sub.setLayoutX(310);
        sub.setLayoutY(380);
        sub.setOnMouseClicked((MouseEvent event) -> {
            String tname, tcon, tadd;
            tname = nameF.getText();
            tadd = addF.getText();
            tcon = contactF.getText();
            boolean nameA = isAlpha(tname);
            boolean conN = tcon.chars().allMatch(Character::isDigit);
            if (nameA && conN) {
                String ssql = "select STAFF_ID from SCOUTS where SCOUTS_ID=" + sid, stid = "";
                try {
                    pst = con.prepareStatement(ssql);
                    rs = pst.executeQuery();
                    while (rs.next()) {
                        stid = rs.getString(1);
                    }
                } catch (SQLException e) {
                    errorAlert("Error", "Error", null);
                    e.printStackTrace();
                }
                String usql = "update staffs set staff_name='" + tname + "' , contact_no=" + tcon + ",staff_address='" + tadd + "' where staff_id=" + stid;
                try {
                    pst = con.prepareStatement(usql);
                    rs = pst.executeQuery();
                    errorAlert("Success", "Profile updated successfully", null);
                    scoutPage(sid);
                } catch (SQLException e) {
                    errorAlert("Error", "Invalid Input", "Invalid input");
                }
            } else {
                errorAlert("Error", "Invalid Input", "Invalid input");
            }
        });
        editPane.getChildren().addAll(nameF, contactF, addF, t1, t2, t3, sub);
        editTab.setContent(editPane);
        tabs.getTabs().addAll(homeTab, editTab);
        box.getChildren().addAll(topPane, tabs);
        Scene scene = new Scene(box);
        stage.setScene(scene);
        stage.setTitle(name);
        stage.show();
        stage.getIcons().add(new Image("icon.png"));
        stage.setOnCloseRequest((WindowEvent t) -> {
            Platform.exit();
            System.exit(0);
        });

    }


    public static void playerPage(int pid) {
        VBox box = new VBox();
        box.setPrefHeight(750);
        box.setPrefWidth(1300);
        box.setStyle("-fx-background-color: white");
        box.getStylesheets().add(Main.class.getResource("table.css").toExternalForm());
        AnchorPane topPane = new AnchorPane();
        topPane.setPrefHeight(125);
        topPane.setPrefWidth(1300);
        topPane.setStyle("-fx-background-color: white");
        Image logoImage = new Image("logo.png");
        ImageView logo = new ImageView(logoImage);
        logo.setFitWidth(125);
        logo.setFitHeight(126);
        logo.setPickOnBounds(true);
        logo.setPreserveRatio(true);
        Text title = new Text("Clubname");
        title.setLayoutX(143);
        title.setLayoutY(83);
        title.setFont(new Font(51));
        Image pimg = new Image("playericon.png");
        ImageView playerIcon = new ImageView(pimg);
        playerIcon.setFitHeight(138);
        playerIcon.setFitWidth(200);
        playerIcon.setLayoutX(957);
        playerIcon.setPickOnBounds(true);
        playerIcon.setPreserveRatio(true);
        //sql to get the player name
        String name = "";
        String sql = "select PLAYER_NAME from PLAYERS where PLAYER_ID=" + pid;
        try {
            pst = con.prepareStatement(sql);
            rs = pst.executeQuery();
            while (rs.next()) {
                name = rs.getString(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            errorAlert("Error", "Error", null);
        }
        Text playerName = new Text(name);
        playerName.setLayoutX(1095);
        playerName.setLayoutY(64);
        playerName.setFont(new Font("System Bold", 21));
        Text logout = new Text("(Logout)");
        logout.setLayoutX(1209);
        logout.setLayoutY(94);
        logout.setFont(new Font("System Bold Italic", 17));
        logout.setOnMouseEntered((MouseEvent event) -> {
            logout.setFont(new Font("System Bold Italic", 20));
            logout.setFill(Color.DARKGRAY);
        });
        logout.setOnMouseExited((MouseEvent event) -> {
            logout.setFont(new Font("System Bold Italic", 17));
            logout.setFill(Color.BLACK);
        });
        logout.setOnMouseClicked((MouseEvent event) -> {
            loginScreen();
        });
        topPane.getChildren().addAll(logo, title, playerIcon, playerName, logout);
        TabPane tabs = new TabPane();
        tabs.setPrefHeight(634);
        tabs.setPrefWidth(1300);
        tabs.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        Tab homeTab = new Tab();
        homeTab.setText("Home");
        AnchorPane homePane = new AnchorPane();
        homePane.setPrefHeight(180);
        homePane.setPrefWidth(200);
        homePane.setStyle("-fx-background-color: #ccfbff");
        //sql
        String nationality = "", position = "", contract = "";
        sql = "select NATIONALITY,POSITION,abs(trunc(SYSDATE)-trunc(CONTACT_TILL)) Time from PLAYERS where PLAYER_ID=" + pid;
        try {
            pst = con.prepareStatement(sql);
            rs = pst.executeQuery();
            while (rs.next()) {
                nationality = rs.getString(1);
                position = rs.getString(2);
                contract = rs.getString(3);
            }
        } catch (SQLException e) {
            errorAlert("Error", "Error", null);
        }
        Text nameText = new Text(name);
        nameText.setLayoutX(540);
        nameText.setLayoutY(62);
        nameText.setFont(new Font("System Bold", 35));
        Text natpos = new Text(nationality + "    -    " + position);
        natpos.setLayoutX(528);
        natpos.setLayoutY(107);
        natpos.setFont(new Font(26));
        Text contactEx = new Text("Contact expires in : " + contract + " days");
        contactEx.setLayoutX(983);
        contactEx.setLayoutY(79);
        contactEx.setFont(new Font(23));
        //sql
        int cnt = 0, sgoals = 0, sfouls = 0, smp = 0;
        double avgrating = 0;
        sql = "select count(*),SUM(GOALS),SUM(FOULS),SUM(MINUTES_PLAYED),AVG(RATING) from PLAYER_MATCH where PLAYER_ID=" + pid;
        try {
            pst = con.prepareStatement(sql);
            rs = pst.executeQuery();
            while (rs.next()) {
                cnt = rs.getInt(1);
                sgoals = rs.getInt(2);
                sfouls = rs.getInt(3);
                smp = rs.getInt(4);
                avgrating = rs.getDouble(5);
            }
        } catch (SQLException e) {
            errorAlert("Error", "Error", null);
        }
        Text tmp = new Text("Total Matches Played : ");
        tmp.setLayoutX(69);
        tmp.setLayoutY(221);
        tmp.setFont(new Font(25));
        Text tmpc = new Text(String.valueOf(cnt));
        tmpc.setLayoutX(333);
        tmpc.setLayoutY(224);
        tmpc.setFont(new Font("System Bold", 33));
        Text tgs = new Text("Total Goals Scored     : ");
        tgs.setLayoutX(69);
        tgs.setLayoutY(271);
        tgs.setFont(new Font(25));
        Text tgsc = new Text(String.valueOf(sgoals));
        tgsc.setLayoutX(333);
        tgsc.setLayoutY(274);
        tgsc.setFont(new Font("System Bold", 33));
        Text gpg = new Text("Goals Per Game");
        gpg.setLayoutX(619);
        gpg.setLayoutY(265);
        gpg.setFont(new Font(26));
        double temp = 0;
        if (cnt != 0) {
            temp = (double) sgoals / (double) cnt;
            temp = temp * 10;
            temp = Math.round(temp);
            temp = temp / 10;
        }
        Text gpgc = new Text(String.valueOf(temp));
        gpgc.setLayoutX(553);
        gpgc.setLayoutY(269);
        gpgc.setFont(new Font("System Bold", 41));
        Text gpm = new Text("Minutes Per Goal");
        gpm.setLayoutX(619);
        gpm.setLayoutY(321);
        gpm.setFont(new Font(26));
        temp = 0;
        if (sgoals != 0) {
            temp = (double) smp / (double) sgoals;
            temp = temp * 10;
            temp = Math.round(temp);
            temp = temp / 10;
        }
        Text gpmc = new Text(String.valueOf(temp));
        gpmc.setLayoutX(535);
        gpmc.setLayoutY(326);
        gpmc.setFont(new Font("System Bold", 41));
        Text tf = new Text("Total Fouls                 : ");
        tf.setLayoutX(69);
        tf.setLayoutY(324);
        tf.setFont(new Font(25));
        Text tfc = new Text(String.valueOf(sfouls));
        tfc.setLayoutX(333);
        tfc.setLayoutY(328);
        tfc.setFont(new Font("System Bold", 33));
        Text tminp = new Text("Total Minutes Played : ");
        tminp.setLayoutX(69);
        tminp.setLayoutY(374);
        tminp.setFont(new Font(25));
        Text tminpc = new Text(String.valueOf(smp));
        tminpc.setLayoutX(333);
        tminpc.setLayoutY(377);
        tminpc.setFont(new Font("System Bold", 33));
        ProgressIndicator pi = new ProgressIndicator(avgrating / 10);
        pi.setLayoutX(966);
        pi.setLayoutY(174);
        pi.setPrefHeight(193);
        pi.setPrefWidth(279);
        Text ar = new Text("Averege Rating :");
        ar.setLayoutX(990);
        ar.setLayoutY(404);
        ar.setFont(new Font(25));
        Text arc = new Text(String.valueOf(avgrating));
        arc.setLayoutX(1188);
        arc.setLayoutY(410);
        arc.setFont(new Font("System Bold", 41));
        homePane.getChildren().addAll(nameText, natpos, contactEx, tmp, tmpc, tgs, tgsc, gpg, gpgc, tf, tfc, tminp, tminpc, pi, ar, arc, gpm, gpmc);
        homeTab.setContent(homePane);
        Tab profileTab = new Tab();
        profileTab.setText("Profile");
        AnchorPane profilePane = new AnchorPane();
        profilePane.setPrefHeight(180);
        profilePane.setPrefWidth(200);
        profilePane.setStyle("-fx-background-color: #ccfbff");
        //sql
        sql = "select TO_CHAR(DATE_OF_BIRTH,'dd-MON-yyyy'),HEIGHT,WEIGHT,CONTACT_NO,WAGE,MARKET_VALUE,BUY_OUT_CLAUSE,AGENT_NAME,TO_CHAR(CONTACT_TILL,'dd-MON-yyyy') from PLAYERS where PLAYER_ID=" + pid;
        String dob = "", height = "", weight = "", contact = "", wage = "", mv = "", boc = "", agent = "", contracttill = "", jdate = "";
        try {
            pst = con.prepareStatement(sql);
            rs = pst.executeQuery();
            while (rs.next()) {
                dob = rs.getString(1);
                height = rs.getString(2);
                weight = rs.getString(3);
                contact = rs.getString(4);
                wage = rs.getString(5);
                mv = rs.getString(6);
                boc = rs.getString(7);
                agent = rs.getString(8);
                contracttill = rs.getString(9);
            }
        } catch (SQLException e) {
            errorAlert("Error", "Error", null);
        }
        sql = "SELECT TO_CHAR(jdate,'dd-MON-yyyy') from PLAYER_TEAM where PLAYER_ID=" + pid;
        try {
            pst = con.prepareStatement(sql);
            rs = pst.executeQuery();
            while (rs.next()) {
                jdate = rs.getString(1);
            }
        } catch (SQLException e) {
            errorAlert("Errorr", "Errorr", null);
        }
        Text t1 = new Text(name);
        t1.setLayoutX(545);
        t1.setLayoutY(49);
        t1.setFont(new Font("System Bold", 35));
        Text t2 = new Text(nationality + "    -    " + position);
        t2.setLayoutX(533);
        t2.setLayoutY(99);
        t2.setFont(new Font(26));
        Text t3 = new Text("Date of birth : " + dob);
        t3.setLayoutX(106);
        t3.setLayoutY(177);
        t3.setFont(new Font(26));
        Text t4 = new Text("Height           : " + height + " cm");
        t4.setLayoutX(106);
        t4.setLayoutY(224);
        t4.setFont(new Font(26));
        Text t5 = new Text("Contact no    : " + contact);
        t5.setLayoutX(106);
        t5.setLayoutY(314);
        t5.setFont(new Font(26));
        Text t6 = new Text("Weight          : " + weight + " kg");
        t6.setLayoutX(106);
        t6.setLayoutY(269);
        t6.setFont(new Font(26));
        Text t7 = new Text("Wage             : " + wage + " $");
        t7.setLayoutX(106);
        t7.setLayoutY(359);
        t7.setFont(new Font(26));
        Text t8 = new Text("Market Value : " + mv + " $");
        t8.setLayoutX(106);
        t8.setLayoutY(404);
        t8.setFont(new Font(26));
        Text t9 = new Text("Buyout Clause: " + boc + " $");
        t9.setLayoutX(106);
        t9.setLayoutY(449);
        t9.setFont(new Font(26));
        Text t10 = new Text("Agent Name  : " + agent);
        t10.setLayoutX(905);
        t10.setLayoutY(176);
        t10.setFont(new Font(26));
        Text t11 = new Text("Joined At       : " + jdate);
        t11.setLayoutX(905);
        t11.setLayoutY(222);
        t11.setFont(new Font(26));
        Text t12 = new Text("Contract Till   : " + contracttill);
        t12.setLayoutX(905);
        t12.setLayoutY(269);
        t12.setFont(new Font(26));
        profilePane.getChildren().addAll(t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12);
        profileTab.setContent(profilePane);
        Tab statsTab = new Tab();
        statsTab.setText("Stats");
        AnchorPane statsPane = new AnchorPane();
        statsPane.setPrefHeight(180);
        statsPane.setPrefWidth(200);
        statsPane.setStyle("-fx-background-color: #ccfbff");
        ObservableList<PlayerStat> pslist = FXCollections.observableArrayList();
        //table
        TableView<PlayerStat> pstable = new TableView<>();
        pstable.setLayoutX(29);
        pstable.setLayoutY(26);
        pstable.setPrefHeight(446);
        pstable.setPrefWidth(874);
        pstable.setEditable(false);
        TableColumn<PlayerStat, String> c1 = new TableColumn("Date");
        TableColumn<PlayerStat, String> c2 = new TableColumn("Opponent");
        TableColumn<PlayerStat, String> c3 = new TableColumn("Tournament");
        TableColumn<PlayerStat, String> c4 = new TableColumn("Stage");
        TableColumn<PlayerStat, String> c5 = new TableColumn("Score");
        TableColumn<PlayerStat, String> c6 = new TableColumn("Minutes");
        TableColumn<PlayerStat, String> c7 = new TableColumn("Goals");
        TableColumn<PlayerStat, String> c8 = new TableColumn("Fouls");
        TableColumn<PlayerStat, String> c9 = new TableColumn("Saves");
        TableColumn<PlayerStat, String> c10 = new TableColumn("Rating");
        TableColumn<PlayerStat, String> c11 = new TableColumn("Result");
        c1.setCellValueFactory(new PropertyValueFactory<>("date"));
        c2.setCellValueFactory(new PropertyValueFactory<>("opponent"));
        c3.setCellValueFactory(new PropertyValueFactory<>("tournament"));
        c4.setCellValueFactory(new PropertyValueFactory<>("stage"));
        c5.setCellValueFactory(new PropertyValueFactory<>("result"));
        c6.setCellValueFactory(new PropertyValueFactory<>("minutes"));
        c7.setCellValueFactory(new PropertyValueFactory<>("goals"));
        c8.setCellValueFactory(new PropertyValueFactory<>("fouls"));
        c9.setCellValueFactory(new PropertyValueFactory<>("saves"));
        c10.setCellValueFactory(new PropertyValueFactory<>("rating"));
        c11.setCellValueFactory(new PropertyValueFactory<>("wdl"));
        c1.setMinWidth(70);
        c2.setMinWidth(100);
        c3.setMinWidth(120);
        c4.setMinWidth(60);
        c5.setMinWidth(70);
        c6.setMinWidth(80);
        c7.setMinWidth(70);
        c8.setMinWidth(63);
        c9.setMinWidth(60);
        c10.setMinWidth(60);
        c11.setMinWidth(70);
        pstable.getColumns().addAll(c1, c2, c3, c4, c5, c6, c7, c8, c9, c10, c11);
        XYChart.Series gs = new XYChart.Series();
        gs.setName("No of goals in match");
        XYChart.Series rts = new XYChart.Series();
        rts.setName("Rating in match");
        //sql
        int i = 1;
        sql = "select to_char(m.MATCH_DATE,'dd-MON-YY'),m.OPPONENT,m.TOURNAMENT_NAME,m.STAGE,m.RESULT,pm.MINUTES_PLAYED,pm.GOALS,pm.FOULS,pm.SAVES,pm.RATING,wol(m.RESULT) from PLAYER_MATCH pm,MATCHES m where PLAYER_ID=" + pid + " and pm.MATCH_ID=m.MATCH_ID order by m.MATCH_DATE";
        try {
            pst = con.prepareStatement(sql);
            rs = pst.executeQuery();
            pslist.clear();
            while (rs.next()) {
                pslist.add(new PlayerStat(new SimpleStringProperty(rs.getString(1)), new SimpleStringProperty(rs.getString(2)), new SimpleStringProperty(rs.getString(3)), new SimpleStringProperty(rs.getString(4)), new SimpleStringProperty(rs.getString(5)), new SimpleStringProperty(rs.getString(6)), new SimpleStringProperty(rs.getString(7)), new SimpleStringProperty(rs.getString(8)), new SimpleStringProperty(rs.getString(9)), new SimpleStringProperty(rs.getString(10)), new SimpleStringProperty(rs.getString(11))));
                gs.getData().add(new XYChart.Data(i, rs.getInt(7)));
                rts.getData().add(new XYChart.Data(i++, rs.getInt(10)));
            }
        } catch (SQLException e) {
            errorAlert("Error", "Error", null);
        }
        pstable.setItems(pslist);
        //chart
        NumberAxis xAxis = new NumberAxis("Match", 1, cnt, 1);
        NumberAxis yAxis = new NumberAxis("Goals", 0, 5, 1);
        LineChart goalChart = new LineChart(xAxis, yAxis);
        xAxis = new NumberAxis("Match", 1, cnt, 1);
        yAxis = new NumberAxis("Rating", 0, 10, 1);
        LineChart ratingChart = new LineChart(xAxis, yAxis);
        goalChart.setLayoutX(924);
        goalChart.setLayoutY(16);
        goalChart.setPrefHeight(232);
        goalChart.setPrefWidth(362);
        ratingChart.setLayoutX(924);
        ratingChart.setLayoutY(259);
        ratingChart.setPrefHeight(232);
        ratingChart.setPrefWidth(362);
        goalChart.getData().add(gs);
        ratingChart.getData().add(rts);
        statsPane.getChildren().addAll(pstable, goalChart, ratingChart);
        statsTab.setContent(statsPane);
        //Tab teamTab = new Tab();
        //teamTab.setText("Team");
        //AnchorPane teamPane = new AnchorPane();
        //teamPane.setPrefHeight(180);
        //teamPane.setPrefWidth(200);
        //teamPane.setStyle("-fx-background-color: #ccfbff");
        //teamTab.setContent(teamPane);
        Tab editTab = new Tab();
        editTab.setText("Edit Profile");
        AnchorPane editPane = new AnchorPane();
        editPane.setPrefHeight(180);
        editPane.setPrefWidth(200);
        editPane.setStyle("-fx-background-color: #ccfbff");
        TextField nameF = new TextField(name);
        nameF.setLayoutX(83);
        nameF.setLayoutY(119);
        nameF.setPrefHeight(30);
        nameF.setPrefWidth(300);
        TextField contactF = new TextField(contact);
        contactF.setLayoutX(83);
        contactF.setLayoutY(215);
        contactF.setPrefHeight(30);
        contactF.setPrefWidth(300);
        TextField agentF = new TextField(agent);
        agentF.setLayoutX(83);
        agentF.setLayoutY(311);
        agentF.setPrefHeight(30);
        agentF.setPrefWidth(300);
        t1 = new Text("Name :");
        t1.setLayoutX(83);
        t1.setLayoutY(97);
        t1.setFont(new Font(28));
        t2 = new Text("Contact no:");
        t2.setLayoutX(83);
        t2.setLayoutY(197);
        t2.setFont(new Font(28));
        t3 = new Text("Agent name:");
        t3.setLayoutX(83);
        t3.setLayoutY(293);
        t3.setFont(new Font(28));
        Button sub = new Button("Submit");
        sub.setLayoutX(310);
        sub.setLayoutY(380);
        sub.setOnMouseClicked((MouseEvent event) -> {
            String tname, tcon, tag;
            tname = nameF.getText();
            tcon = contactF.getText();
            tag = agentF.getText();
            boolean nameA = isAlpha(tname);
            boolean conN = tcon.chars().allMatch(Character::isDigit);
            boolean agentA = isAlpha(tag);
            if (nameA && conN && agentA) {
                //sql
                String usql = "update players set player_name='" + tname + "' , contact_no=" + tcon + ",agent_name='" + tag + "' where player_id=" + pid;
                try {
                    pst = con.prepareStatement(usql);
                    rs = pst.executeQuery();
                    errorAlert("Success", "Profile updated successfully", null);
                    playerPage(pid);
                } catch (SQLException e) {
                    errorAlert("Error", "Invalid Input", "Invalid input");
                }
            } else {
                errorAlert("Error", "Invalid Input", "Invalid input");
            }
        });
        Text wageText = textCreator("Present Wage : ", 800, 120,  30);
        Text wageAmount = textCreator(wage+"$",1000,120,25);
        Text upText = textCreator("Upgrade to ", 800, 170,  30);
        TextField reqWage = textfieldCreator(wage, 980, 150, 30, 120);
        Button req = new Button("Send Request");
        req.setLayoutX(1100);
        req.setLayoutY(200);
        req.setOnMouseClicked((MouseEvent event) -> {
            int amount;
            amount = Integer.valueOf(reqWage.getText());
            String upsql="UPDATE PLAYERS SET WAGE= "+amount+" WHERE PLAYER_ID="+pid;

                //sql
                try {
                    pst = con.prepareStatement(upsql);
                    rs = pst.executeQuery();
                    errorAlert("Success", "Salary Request", "Your request for salary has been sent to the president!");
                    playerPage(pid);
                } catch (SQLException e) {
                    errorAlert("Error", "Invalid Input", "Invalid input");
                }

        });



        editPane.getChildren().addAll(nameF, contactF, agentF, t1, t2, t3, wageText, wageAmount, upText, reqWage, req, sub);
        editTab.setContent(editPane);
        tabs.getTabs().addAll(homeTab, profileTab, statsTab, editTab);
        box.getChildren().addAll(topPane, tabs);
        Scene scene = new Scene(box);
        stage.setScene(scene);
        stage.setTitle(name);
        stage.show();
        stage.getIcons().add(new Image("icon.png"));
        stage.setOnCloseRequest((WindowEvent t) -> {
            Platform.exit();
            System.exit(0);
        });
    }
    //new

    public static void managerPage(int mid) {
        AnchorPane mainPane = new AnchorPane();
        mainPane.setPrefWidth(1550);
        mainPane.setPrefHeight(830);
        // query to retrive managers name
        AtomicReference<String> sql = new AtomicReference<>("SELECT STAFF_NAME FROM STAFFS WHERE STAFF_ID= (SELECT STAFF_ID FROM MANAGERS WHERE MANAGER_ID = " + mid + ")");
        String managerName = "";
        try {
            pst = con.prepareStatement(sql.get());
            rs = pst.executeQuery();
            while (rs.next()) {
                managerName = rs.getString(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        Text t1 = new Text(managerName);
        t1.setX(1300);
        t1.setY(40);
        Font f = new Font("System Bold", 30);
        t1.setFont(f);
        Text t2 = new Text("(Logout)");
        t2.setFont(f);
        t2.setX(1330);
        t2.setY(80);
        t2.setOnMouseEntered((MouseEvent event) -> {
            t2.setFont(new Font("System Bold", 35));
            t2.setFill(Color.DARKGRAY);
        });
        t2.setOnMouseExited((MouseEvent event) -> {
            t2.setFont(new Font("System Bold", 30));
            t2.setFill(Color.BLACK);
        });
        t2.setOnMouseClicked((MouseEvent event) -> {
            loginScreen();
        });
        Text viewEmployees = new Text("View Employees");
        viewEmployees.setLayoutX(70);
        viewEmployees.setLayoutY(130);
        viewEmployees.setFont(new Font("System Bold", 40));
        Text editProfile = new Text("Edit Profile");
        editProfile.setLayoutX(450);
        editProfile.setLayoutY(130);
        editProfile.setFont(new Font("System Bold", 40));
        if (managerSceneFlag == 0) {
            viewEmployees.setFill(Color.BLACK);
            editProfile.setFill(Color.DARKGRAY);
        } else {
            viewEmployees.setFill(Color.DARKGRAY);
            editProfile.setFill(Color.BLACK);
        }
        viewEmployees.setOnMouseClicked((MouseEvent event) -> {
            managerSceneFlag = 0;
            managerPage(mid);
        });
        editProfile.setOnMouseClicked((MouseEvent event) -> {

            managerSceneFlag = 1;
            managerPage(mid);
        });
        Text name, address, contact;
        TextField nameField, addressField, contactField;
        Button updateButton;
        if (managerSceneFlag == 0) { // view employees
            // create table
            managerStaffTable = new TableView<ManagerStaff>();
            managerStaffTable.setLayoutX(80);
            managerStaffTable.setLayoutY(200);
            managerStaffTable.setPrefHeight(530);
            managerStaffTable.setPrefWidth(955);
            managerStaffTable.setEditable(false);
            TableColumn<ManagerStaff, Integer> c1 = new TableColumn("Staff ID");
            TableColumn<ManagerStaff, String> c2 = new TableColumn("Staff Name");
            TableColumn<ManagerStaff, String> c3 = new TableColumn("Staff Address");
            TableColumn<ManagerStaff, Integer> c4 = new TableColumn("Contact No");
            TableColumn<ManagerStaff, String> c5 = new TableColumn("Type");
            TableColumn<ManagerStaff, Integer> c6 = new TableColumn("Salary");
            c1.setCellValueFactory(new PropertyValueFactory<>("staff_id"));
            c2.setCellValueFactory(new PropertyValueFactory<>("staff_name"));
            c3.setCellValueFactory(new PropertyValueFactory<>("staff_address"));
            c4.setCellValueFactory(new PropertyValueFactory<>("contact_no"));
            c5.setCellValueFactory(new PropertyValueFactory<>("type"));
            c6.setCellValueFactory(new PropertyValueFactory<>("salary"));
            c1.setMinWidth(150);
            c2.setMinWidth(150);
            c3.setMinWidth(200);
            c4.setMinWidth(150);
            c5.setMinWidth(150);
            c6.setMinWidth(150);
            managerStaffTable.getColumns().addAll(c1, c2, c3, c4, c5, c6);
            // run sql
            sql.set("( SELECT ST.STAFF_ID,ST.STAFF_NAME, ST.STAFF_ADDRESS,ST.CONTACT_NO,ST.TYPE,ST.SALARY FROM SCOUTS SC JOIN STAFFS ST ON (SC.STAFF_ID=ST.STAFF_ID) WHERE TEAM_ID = ( SELECT TEAM_ID FROM MANAGERS WHERE MANAGER_ID=" + mid + ") ) UNION ( SELECT ST.STAFF_ID,ST.STAFF_NAME, ST.STAFF_ADDRESS,ST.CONTACT_NO,ST.TYPE,ST.SALARY FROM MEDICALS MC,MEDICAL_TEAMS_TEAMS MTT, STAFFS ST WHERE TEAM_ID = ( SELECT TEAM_ID FROM MANAGERS WHERE MANAGER_ID=" + mid + ") AND MC.MTEAM_ID = MTT.MTEAM_ID AND MC.STAFF_ID = ST.STAFF_ID )");
            // update table
            try {
                pst = con.prepareStatement(sql.get());
                rs = pst.executeQuery();
                msdata.clear();
                while (rs.next()) {
                    msdata.add(new ManagerStaff(new SimpleIntegerProperty(rs.getInt(1)), new SimpleStringProperty(rs.getString(2)), new SimpleStringProperty(rs.getString(3)), new SimpleIntegerProperty(rs.getInt(4)), new SimpleStringProperty(rs.getString(5)), new SimpleIntegerProperty(rs.getInt(6))));
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            managerStaffTable.setItems(msdata);
            mainPane.getChildren().addAll(t1, t2, viewEmployees, editProfile, managerStaffTable);
        } else { // edit profile
            //sql to retrive information about manager
            sql.set("SELECT STAFF_ADDRESS,CONTACT_NO FROM STAFFS WHERE STAFF_ID=(SELECT STAFF_ID FROM MANAGERS WHERE MANAGER_ID = " + mid + ")");
            String managerAddress = "";
            int managerContact = 0;
            try {
                pst = con.prepareStatement(sql.get());
                rs = pst.executeQuery();
                while (rs.next()) {
                    managerAddress = rs.getString(1);
                    managerContact = rs.getInt(2);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            name = new Text("Name :");
            name.setLayoutX(150);
            name.setLayoutY(220);
            name.setFont(new Font("System Bold", 30));
            nameField = new TextField();
            nameField.setLayoutX(150);
            nameField.setLayoutY(240);
            nameField.setPrefHeight(35);
            nameField.setPrefWidth(300);
            nameField.setEffect(borderGlow);
            nameField.setText(managerName);
            address = new Text("Address :");
            address.setLayoutX(150);
            address.setLayoutY(320);
            address.setFont(new Font("System Bold", 30));
            addressField = new TextField();
            addressField.setLayoutX(150);
            addressField.setLayoutY(340);
            addressField.setPrefHeight(35);
            addressField.setPrefWidth(300);
            addressField.setEffect(borderGlow);
            addressField.setText(managerAddress);
            contact = new Text("Contact No :");
            contact.setLayoutX(150);
            contact.setLayoutY(420);
            contact.setFont(new Font("System Bold", 30));
            contactField = new TextField();
            contactField.setLayoutX(150);
            contactField.setLayoutY(440);
            contactField.setPrefHeight(35);
            contactField.setPrefWidth(300);
            contactField.setEffect(borderGlow);
            contactField.setText(String.valueOf(managerContact));
            updateButton = new Button("Update");
            updateButton.setLayoutX(380);
            updateButton.setLayoutY(500);
            updateButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    String tempName, tempAddress;
                    int tempContact = 0;
                    tempName = nameField.getText();
                    tempAddress = addressField.getText();
                    try {
                        tempContact = Integer.parseInt(contactField.getText());
                        String query = "UPDATE STAFFS SET STAFF_NAME = '" + tempName + "', STAFF_ADDRESS='" + tempAddress + "', CONTACT_NO = " + tempContact + "  WHERE STAFF_ID = (SELECT STAFF_ID FROM MANAGERS WHERE MANAGER_ID = " + mid + ")";
                        System.out.println(query);
                        try {
                            pst = con.prepareStatement(query);
                            rs = pst.executeQuery();
                            errorAlert("Updated", "Profile updated successfully", null);
                        } catch (SQLException e) {
                            e.printStackTrace();
                            errorAlert("Error", "Invalid input", null);
                        }
                    } catch (Exception e) {
                        errorAlert("Error", "Invalid input", null);
                    }
                }
            });
            mainPane.getChildren().addAll(t1, t2, viewEmployees, editProfile, name, nameField, address, addressField, contact, contactField, updateButton);
        }
        mainPane.getStylesheets().add(Main.class.getResource("table.css").toExternalForm());
        mainPane.setStyle("-fx-background-color: #d8fbff");
        Scene scene = new Scene(mainPane);
        stage.setScene(scene);
        stage.setTitle("Football Club Management");
        stage.show();
        stage.getIcons().add(new Image("icon.png"));
        stage.setOnCloseRequest((WindowEvent t) -> {
            Platform.exit();
            System.exit(0);
        });
    }




    public static void showData() {
        AnchorPane mainPane = new AnchorPane();
        mainPane.setPrefHeight(795);
        mainPane.setPrefWidth(1089);
        mainPane.setStyle("-fx-background-color: #ccfbff;");
        //titlepane
        AnchorPane titlePane = new AnchorPane();
        titlePane.setPrefHeight(136);
        titlePane.setPrefWidth(1089);
        titlePane.setLayoutX(299);
        titlePane.setLayoutY(26);
        titlePane.setStyle("-fx-background-color: #ffffff;");
        AnchorPane.setTopAnchor(titlePane, 0.0);
        AnchorPane.setBottomAnchor(titlePane, 850.0);
        AnchorPane.setLeftAnchor(titlePane, 0.0);
        AnchorPane.setRightAnchor(titlePane, 0.0);
        //title text
        Text title = new Text();
        title.setText("Football\nClub\nManagement");
        title.setLayoutX(200);
        title.setLayoutY(42);
        Font f = new Font("System Bold", 26);
        title.setFont(f);
        //image
        Image img = new Image("logo.png");
        ImageView imgv = new ImageView(img);
        imgv.setFitHeight(90);
        imgv.setFitWidth(90);
        imgv.setLayoutX(50);
        imgv.setLayoutY(20);
        titlePane.getChildren().addAll(title, imgv);
        //login form
        Text log = new Text("Teams :");
        log.setLayoutX(35.0);
        log.setLayoutY(180.0);
        f = new Font("System Bold", 35);
        log.setFont(f);
        //table
        createTable();
        updateTable();
        //add to table
        Text logg = new Text("Add new team :");
        logg.setLayoutX(1100);
        logg.setLayoutY(256.0 - 100 + 20);
        f = new Font("System Bold", 27);
        logg.setFont(f);
        TextField teamName = new TextField();
        teamName.setLayoutX(1100);
        teamName.setLayoutY(338 - 100);
        teamName.setPrefHeight(35);
        teamName.setPrefWidth(300);
        teamName.setEffect(borderGlow);
        Text t1 = new Text("Team Name :");
        t1.setLayoutX(1100);
        t1.setLayoutY(321 - 100);
        f = new Font("System Bold", 18);
        t1.setFont(f);
        Text t2 = new Text("Manager Id :");
        t2.setLayoutX(1100);
        t2.setLayoutY(408 - 100);
        f = new Font("System Bold", 18);
        t2.setFont(f);
        Text t3 = new Text("Medical Id :");
        t3.setLayoutX(1100);
        t3.setLayoutY(321 - 100 + 200 - 30);
        f = new Font("System Bold", 18);
        t3.setFont(f);
        Text t4 = new Text("Scout Id :");
        t4.setLayoutX(1100);
        t4.setLayoutY(408 - 100 + 200 - 40);
        f = new Font("System Bold", 18);
        t4.setFont(f);
        Text t5 = new Text("Captain :");
        t5.setLayoutX(1100);
        t5.setLayoutY(408 - 100 + 200 + 100 - 70);
        f = new Font("System Bold", 18);
        t5.setFont(f);
        TextField managerID = new TextField();
        managerID.setLayoutX(1100);
        managerID.setLayoutY(423 - 100);
        managerID.setPrefHeight(35);
        managerID.setPrefWidth(300);
        managerID.setEffect(borderGlow);
        TextField medicID = new TextField();
        medicID.setLayoutX(1100);
        medicID.setLayoutY(423 - 100 + 100 - 20);
        medicID.setPrefHeight(35);
        medicID.setPrefWidth(300);
        medicID.setEffect(borderGlow);
        TextField scoutID = new TextField();
        scoutID.setLayoutX(1100);
        scoutID.setLayoutY(423 - 100 + 100 + 100 - 45);
        scoutID.setPrefHeight(35);
        scoutID.setPrefWidth(300);
        scoutID.setEffect(borderGlow);
        TextField captain = new TextField();
        captain.setLayoutX(1100);
        captain.setLayoutY(423 - 100 + 100 + 200 - 70);
        captain.setPrefHeight(35);
        captain.setPrefWidth(300);
        captain.setEffect(borderGlow);
        // button
        Button loginButton = new Button("Add Data");
        loginButton.setLayoutX(1310);
        loginButton.setLayoutY(495 - 100 + 300 - 80);
        loginButton.setOnMouseClicked((MouseEvent event) -> {
            int temp = tableSize + 1;
            String sql = "INSERT INTO TEAMS(TEAM_ID,TEAM_NAME,MANAGER_ID,SCOUT_ID,MEDIC_ID, CAPTAIN) VALUES (" + "'" + temp + "', '" + teamName.getText() + "','" + managerID.getText() + "', '" + medicID.getText() + "', '" + scoutID.getText() + "', '" + captain.getText() + "')";
            try {
                pst = con.prepareStatement(sql);
                rs = pst.executeQuery();
                teamName.clear();
                managerID.clear();
                medicID.clear();
                scoutID.clear();
                captain.clear();
                updateTable();
            } catch (SQLException e) {
                errorAlert("Invalid Data.", "Invalid Data", null);
            }
        });
        table.setOnMouseClicked((MouseEvent event) -> {
            Team app = new Team(1, "ss", "s", "s", "s", "s");
            if (!table.getSelectionModel().isEmpty()) {
                //table.getSelectionModel().getSelectedCells().get
                app = (Team) table.getSelectionModel().getSelectedItem();
                showPopup(app, event.getScreenX(), event.getScreenY());
            }
        });
        mainPane.getChildren().addAll(titlePane, log, table, logg, loginButton, teamName, t1, t2, t3, t4, t5, managerID, medicID, scoutID, captain);
        mainPane.getStylesheets().add(Main.class.getResource("table.css").toExternalForm());
        Scene scene = new Scene(mainPane);
        stage.setScene(scene);
        stage.setTitle("Teams table");
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

    public static void updateTable() {
        String sql = "SELECT * FROM TEAMS ORDER BY TEAM_ID";
        try {
            con = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:ORCL", "shoumik", "shoumik123");
            pst = con.prepareStatement(sql);
            rs = pst.executeQuery();
            data.clear();
            while (rs.next()) {
                data.add(new Team(rs.getInt(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getString(5), rs.getString(6)));
                tableSize++;
                // System.out.println(rs.getInt("team_id") + " , " + rs.getString(2) + " , " + rs.getString(3) + " , " + rs.getString(4) + " , " + rs.getString(5) + " , " + rs.getString(6));
            }
            //table.setItems(data);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        FilteredList<Team> filter = new FilteredList<>(data, flag -> true);
        tf.textProperty().addListener((observable, oldValue, newValue) -> {
            filter.setPredicate(temp -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                String input = newValue.toLowerCase();
                if (temp.getCaptain().toLowerCase().contains(input)) {
                    return true;
                }
                return false;
            });
        });
        SortedList<Team> sort = new SortedList<>(filter);
        sort.comparatorProperty().bind(table.comparatorProperty());
        table.setItems(sort);

    }

    public static void createTable() {
        table = new TableView<Team>();
        table.setLayoutX(33);
        table.setLayoutY(200);
        table.setPrefHeight(512);
        table.setPrefWidth(652);
        table.setEditable(false);
        TableColumn<Team, Integer> c1 = new TableColumn("Team ID");
        TableColumn<Team, String> c2 = new TableColumn("Name");
        TableColumn<Team, String> c3 = new TableColumn("Manager ID");
        TableColumn<Team, String> c4 = new TableColumn("Scout ID");
        TableColumn<Team, String> c5 = new TableColumn("Medic ID");
        TableColumn<Team, String> c6 = new TableColumn("Captain");
        c1.setCellValueFactory(new PropertyValueFactory<>("team_id"));
        c2.setCellValueFactory(new PropertyValueFactory<>("team_name"));
        c3.setCellValueFactory(new PropertyValueFactory<>("manager_id"));
        c4.setCellValueFactory(new PropertyValueFactory<>("scout_id"));
        c5.setCellValueFactory(new PropertyValueFactory<>("medic_id"));
        c6.setCellValueFactory(new PropertyValueFactory<>("captain"));
        c1.setMinWidth(100);
        c2.setMinWidth(100);
        c3.setMinWidth(100);
        c4.setMinWidth(100);
        c5.setMinWidth(100);
        c6.setMinWidth(150);
        table.getColumns().addAll(c1, c2, c3, c4, c5, c6);
    }

    public static void showPopup(Team team, double x, double y) {
        AnchorPane pane = new AnchorPane();
        pane.setStyle("-fx-background-color: linear-gradient(#5595fc 0%, #3a84fc 50%, #1e73fc 100%);-fx-text-fill: white;");
        pane.setPrefHeight(200);
        pane.setPrefWidth(200);
        Text t = new Text();
        t.setText(team.captain.getValue());
        t.setLayoutX(50);
        t.setLayoutY(50);
        t.setStyle("-fx-font-size: 20;-fx-text-fill: blue;-fx-font-weight: bold;");
        pane.getChildren().add(t);
        popup.getContent().clear();
        popup.getContent().addAll(pane);
        popup.setX(x);
        popup.setY(y);
        popup.setAutoHide(true);
        popup.show(stage);
    }

    //
    public static void boardMemberPage(int id) {
        //boardMemberSceneFlag: 0 for view employees, 1 for profile update, 2 for manager, 3 for scout, 4 for board members, 5 for medical staffs
        AnchorPane mainPane = new AnchorPane();
        mainPane.setPrefWidth(1550);
        mainPane.setPrefHeight(830);
        AtomicReference<String> sql = new AtomicReference<>("SELECT STAFF_NAME FROM STAFFS WHERE STAFF_ID = (SELECT STAFF_ID FROM BOARD_MEMBERS WHERE BMEMBER_ID=" + id + ")");
        String memberName = "";
        try {
            pst = con.prepareStatement(sql.get());
            rs = pst.executeQuery();
            while (rs.next()) {
                memberName = rs.getString(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        Text t1 = new Text(memberName);
        t1.setX(1000);
        t1.setY(40);
        Font f = new Font("System Bold", 30);
        t1.setFont(f);
        Text t2 = new Text("(Logout)");
        t2.setFont(f);
        t2.setX(1000);
        t2.setY(80);
        t2.setOnMouseEntered((MouseEvent event) -> {
            t2.setFont(new Font("System Bold", 35));
            t2.setFill(Color.DARKGRAY);
        });
        t2.setOnMouseExited((MouseEvent event) -> {
            t2.setFont(new Font("System Bold", 30));
            t2.setFill(Color.BLACK);
        });
        t2.setOnMouseClicked((MouseEvent event) -> {
            loginScreen();
        });
        Text viewEmployees = new Text("View Employees");
        viewEmployees.setLayoutX(70);
        viewEmployees.setLayoutY(130);
        viewEmployees.setFont(new Font("System Bold", 40));
        Text editProfile = new Text("Edit Profile");
        editProfile.setLayoutX(450);
        editProfile.setLayoutY(130);
        editProfile.setFont(new Font("System Bold", 40));
        if (boardMemberSceneFlag == 0) {
            viewEmployees.setFill(Color.BLACK);
            editProfile.setFill(Color.DARKGRAY);
        } else {
            viewEmployees.setFill(Color.DARKGRAY);
            editProfile.setFill(Color.BLACK);
        }
        viewEmployees.setOnMouseClicked((MouseEvent event) -> {
            boardMemberSceneFlag = 0;
            boardMemberPage(id);
        });
        editProfile.setOnMouseClicked((MouseEvent event) -> {

            boardMemberSceneFlag = 1;
            boardMemberPage(id);
        });
        Button back = new Button("back");
        back.setLayoutX(1000);
        back.setLayoutY(80);
        Button manager = new Button("Managers");
        manager.setLayoutX(90);
        manager.setLayoutY(180);
        Button scout = new Button("Scouts");
        scout.setLayoutX(90);
        scout.setLayoutY(220);
        back.setOnMouseClicked((MouseEvent event) -> {
                    boardMemberSceneFlag = 0;
                    boardMemberPage(id);
                }
        );
        manager.setOnMouseClicked((MouseEvent event) -> {
                    boardMemberSceneFlag = 2;
                    boardMemberPage(id);
                }
        );
        scout.setOnMouseClicked((MouseEvent event) -> {
                    boardMemberSceneFlag = 3;
                    boardMemberPage(id);
                }
        );
        if (boardMemberSceneFlag == 1) {
            sql.set("SELECT STAFF_NAME FROM STAFFS WHERE STAFF_ID= (SELECT STAFF_ID FROM BOARD_MEMBERS WHERE BMEMBER_ID=" + id + ")");
            String Name = "";
            try {
                pst = con.prepareStatement(sql.get());
                rs = pst.executeQuery();
                while (rs.next()) {
                    Name = rs.getString(1);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            sql.set("SELECT STAFF_ADDRESS,CONTACT_NO FROM STAFFS WHERE STAFF_ID= (SELECT STAFF_ID FROM BOARD_MEMBERS WHERE BMEMBER_ID=" + id + ")");
            String Address = "";
            int Contact = 0;
            try {
                pst = con.prepareStatement(sql.get());
                rs = pst.executeQuery();
                while (rs.next()) {
                    Address = rs.getString(1);
                    Contact = rs.getInt(2);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            Text name = new Text("Name :");
            name.setLayoutX(150);
            name.setLayoutY(220);
            name.setFont(new Font("System Bold", 30));
            TextField nameField = new TextField();
            nameField.setLayoutX(150);
            nameField.setLayoutY(240);
            nameField.setPrefHeight(35);
            nameField.setPrefWidth(300);
            nameField.setEffect(borderGlow);
            nameField.setText(Name);
            Text address = new Text("Address :");
            address.setLayoutX(150);
            address.setLayoutY(320);
            address.setFont(new Font("System Bold", 30));
            TextField addressField = new TextField();
            addressField.setLayoutX(150);
            addressField.setLayoutY(340);
            addressField.setPrefHeight(35);
            addressField.setPrefWidth(300);
            addressField.setEffect(borderGlow);
            addressField.setText(Address);
            Text contact = new Text("Contact No :");
            contact.setLayoutX(150);
            contact.setLayoutY(420);
            contact.setFont(new Font("System Bold", 30));
            TextField contactField = new TextField();
            contactField.setLayoutX(150);
            contactField.setLayoutY(440);
            contactField.setPrefHeight(35);
            contactField.setPrefWidth(300);
            contactField.setEffect(borderGlow);
            contactField.setText(String.valueOf(Contact));
            Button updateButton = new Button("Update");
            updateButton.setLayoutX(380);
            updateButton.setLayoutY(500);
            updateButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    String tempName, tempAddress;
                    int tempContact = 0;
                    tempName = nameField.getText();
                    tempAddress = addressField.getText();
                    try {
                        tempContact = Integer.parseInt(contactField.getText());
                        String query = "UPDATE STAFFS SET STAFF_NAME = '" + tempName + "', STAFF_ADDRESS='" + tempAddress + "', CONTACT_NO = " + tempContact + "  WHERE STAFF_ID = (SELECT STAFF_ID FROM BOARD_MEMBERS WHERE BMEMBER_ID=" + id + ")";
                        System.out.println(query);
                        try {
                            pst = con.prepareStatement(query);
                            rs = pst.executeQuery();
                            errorAlert("Updated", "Profile updated successfully", null);
                        } catch (SQLException e) {
                            e.printStackTrace();
                            errorAlert("Error", "Invalid input", null);
                        }
                    } catch (Exception e) {
                        errorAlert("Error", "Invalid input", null);
                    }
                }
            });
            mainPane.getChildren().addAll(t1, t2, viewEmployees, editProfile, name, nameField, address, addressField, contact, contactField, updateButton);
        }
        if (boardMemberSceneFlag == 2) {
            boardStaffTable = new TableView<BoardStaff>();
            boardStaffTable.setLayoutX(80);
            boardStaffTable.setLayoutY(200);
            boardStaffTable.setPrefHeight(530);
            boardStaffTable.setPrefWidth(955);
            boardStaffTable.setEditable(false);
            TableColumn<BoardStaff, Integer> c1 = new TableColumn("Staff ID");
            TableColumn<BoardStaff, String> c2 = new TableColumn("Staff Name");
            TableColumn<BoardStaff, String> c3 = new TableColumn("Staff Address");
            TableColumn<BoardStaff, Integer> c4 = new TableColumn("Contact No");
            //TableColumn<BoardStaff, String> c5 = new TableColumn("Type");
            TableColumn<BoardStaff, Integer> c6 = new TableColumn("Salary");
            c1.setCellValueFactory(new PropertyValueFactory<>("staff_id"));
            c2.setCellValueFactory(new PropertyValueFactory<>("staff_name"));
            c3.setCellValueFactory(new PropertyValueFactory<>("staff_address"));
            c4.setCellValueFactory(new PropertyValueFactory<>("contact_no"));
            //c5.setCellValueFactory(new PropertyValueFactory<>("type"));
            c6.setCellValueFactory(new PropertyValueFactory<>("salary"));
            c1.setMinWidth(150);
            c2.setMinWidth(150);
            c3.setMinWidth(200);
            c4.setMinWidth(150);
            //c5.setMinWidth(150);
            c6.setMinWidth(150);
            boardStaffTable.getColumns().addAll(c1, c2, c3, c4, c6);

            sql.set("SELECT STAFF_ID, STAFF_NAME, STAFF_ADDRESS, CONTACT_NO, SALARY FROM STAFFS WHERE TYPE='Manager' ");
            try {
                pst = con.prepareStatement(sql.get());
                rs = pst.executeQuery();
                System.out.println("rs passed");
                bsdata.clear();
                while (rs.next()) {
                    bsdata.add(new BoardStaff(new SimpleIntegerProperty(rs.getInt(1)), new SimpleStringProperty(rs.getString(2)), new SimpleStringProperty(rs.getString(3)), new SimpleIntegerProperty(rs.getInt(4)), new SimpleIntegerProperty(rs.getInt(5))));
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            boardStaffTable.setItems(bsdata);
            mainPane.getChildren().addAll(boardStaffTable, back);
        }
        if (boardMemberSceneFlag == 3) {
            boardStaffTable = new TableView<BoardStaff>();
            boardStaffTable.setLayoutX(80);
            boardStaffTable.setLayoutY(200);
            boardStaffTable.setPrefHeight(530);
            boardStaffTable.setPrefWidth(955);
            boardStaffTable.setEditable(false);
            TableColumn<BoardStaff, Integer> c1 = new TableColumn("Staff ID");
            TableColumn<BoardStaff, String> c2 = new TableColumn("Staff Name");
            TableColumn<BoardStaff, String> c3 = new TableColumn("Staff Address");
            TableColumn<BoardStaff, Integer> c4 = new TableColumn("Contact No");
            //TableColumn<BoardStaff, String> c5 = new TableColumn("Type");
            TableColumn<BoardStaff, Integer> c6 = new TableColumn("Salary");
            c1.setCellValueFactory(new PropertyValueFactory<>("staff_id"));
            c2.setCellValueFactory(new PropertyValueFactory<>("staff_name"));
            c3.setCellValueFactory(new PropertyValueFactory<>("staff_address"));
            c4.setCellValueFactory(new PropertyValueFactory<>("contact_no"));
            //c5.setCellValueFactory(new PropertyValueFactory<>("type"));
            c6.setCellValueFactory(new PropertyValueFactory<>("salary"));
            c1.setMinWidth(150);
            c2.setMinWidth(150);
            c3.setMinWidth(200);
            c4.setMinWidth(150);
            //c5.setMinWidth(150);
            c6.setMinWidth(150);
            boardStaffTable.getColumns().addAll(c1, c2, c3, c4, c6);
            sql.set("SELECT STAFF_ID, STAFF_NAME, STAFF_ADDRESS, CONTACT_NO, SALARY FROM STAFFS WHERE TYPE='Scout' ");
            try {
                pst = con.prepareStatement(sql.get());
                rs = pst.executeQuery();
                System.out.println("rs passed");
                bsdata.clear();
                while (rs.next()) {
                    bsdata.add(new BoardStaff(new SimpleIntegerProperty(rs.getInt(1)), new SimpleStringProperty(rs.getString(2)), new SimpleStringProperty(rs.getString(3)), new SimpleIntegerProperty(rs.getInt(4)), new SimpleIntegerProperty(rs.getInt(5))));

                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            boardStaffTable.setItems(bsdata);

            mainPane.getChildren().addAll(boardStaffTable, back);
        }
        if (boardMemberSceneFlag == 0) {
            mainPane.getChildren().addAll(t1, t2, viewEmployees, editProfile, manager, scout);
        }
        mainPane.getStylesheets().add(Main.class.getResource("table.css").toExternalForm());
        mainPane.setStyle("-fx-background-color: #abfaff");
        Scene scene = new Scene(mainPane);
        stage.setScene(scene);
        stage.setTitle("Football Club Management");
        stage.show();
        stage.getIcons().add(new Image("icon.png"));
        stage.setOnCloseRequest((WindowEvent t) -> {
            Platform.exit();
            System.exit(0);
        });
    }

    public static void superAdminPage( int id)
    {
        String name = "", address = "", contact = "", salary="", csdate = "", cedate = "", prof = "", role = "", budget = "";
        int totincome=0, st_id=0;

        //sql
        String sql="SELECT CB.ROLE, C.START_DATE, C.END_DATE, C.BUDGET, C.TOTAL_INCOME FROM BOARD_MEMBERS BM JOIN COMMITTEE_BOARD CB ON BM.BMEMBER_ID=CB.BMEMBER_ID JOIN COMMITTEES C ON CB.COMMITTEE_ID=C.COMMITTEE_ID WHERE BM.BMEMBER_ID="+id+"AND SYSDATE>=C.START_DATE AND SYSDATE<=C.END_DATE" ;
        try {
            pst = con.prepareStatement(sql);
            rs = pst.executeQuery();
            while (rs.next()) {
                role = rs.getString(1);

                csdate = rs.getDate(2).toString();
                cedate = rs.getDate(3).toString();

                budget = rs.getString(4);
                totincome = rs.getInt(5);

            }
        } catch (SQLException e) {
            errorAlert("Error", "Error", null);
        }
        sql="SELECT BM.PROFESSION, ST.STAFF_NAME, ST.STAFF_ADDRESS, ST.CONTACT_NO, ST.SALARY, ST.STAFF_ID FROM BOARD_MEMBERS BM JOIN STAFFS ST ON BM.STAFF_ID=ST.STAFF_ID WHERE BM.BMEMBER_ID="+id;
        try {
            pst = con.prepareStatement(sql);
            rs = pst.executeQuery();
            while (rs.next()) {
                prof = rs.getString(1);
                name = rs.getString(2);
                address = rs.getString(3);
                contact = rs.getString(4);
                salary = rs.getString(5);
                st_id=rs.getInt(6);
            }
        } catch (SQLException e) {
            errorAlert("Error", "Error", "Error");
        }
        //
        VBox box = new VBox();
        box.setPrefHeight(750);
        box.setPrefWidth(1300);
        box.setStyle("-fx-background-color: white");
        box.getStylesheets().add(Main.class.getResource("table.css").toExternalForm());
        AnchorPane topPane = new AnchorPane();
        topPane.setPrefHeight(125);
        topPane.setPrefWidth(1300);
        topPane.setStyle("-fx-background-color: white");
        Image logoImage = new Image("logo.png");
        ImageView logo = new ImageView(logoImage);
        logo.setFitWidth(125);
        logo.setFitHeight(126);
        logo.setPickOnBounds(true);
        logo.setPreserveRatio(true);
        Text title = new Text("Clubname");
        title.setLayoutX(143);
        title.setLayoutY(83);
        title.setFont(new Font(51));
        Image pimg = new Image("superAdminIcon1.jpg");
        ImageView adminIcon = new ImageView(pimg);
        adminIcon.setFitHeight(138);
        adminIcon.setFitWidth(200);
        adminIcon.setLayoutX(945);
        adminIcon.setLayoutY(15);
        adminIcon.setPickOnBounds(true);
        adminIcon.setPreserveRatio(true);
        //sql to get the player name
        Text adminName = new Text(name);
        adminName.setLayoutX(1095);
        adminName.setLayoutY(64);
        adminName.setFont(new Font("System Bold", 21));
        Text logout = new Text("(Logout)");
        logout.setLayoutX(1209);
        logout.setLayoutY(94);
        logout.setFont(new Font("System Bold Italic", 17));
        logout.setOnMouseEntered((MouseEvent event) -> {
            logout.setFont(new Font("System Bold Italic", 20));
            logout.setFill(Color.DARKGRAY);
        });
        logout.setOnMouseExited((MouseEvent event) -> {
            logout.setFont(new Font("System Bold Italic", 17));
            logout.setFill(Color.BLACK);
        });
        logout.setOnMouseClicked((MouseEvent event) -> {
            loginScreen();
        });
        topPane.getChildren().addAll(logo, title, adminIcon, adminName, logout);
        TabPane tabs = new TabPane();
        tabs.setPrefHeight(634);
        tabs.setPrefWidth(1300);
        tabs.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        Tab homeTab = new Tab();
        homeTab.setText("Home");
        AnchorPane homePane = new AnchorPane();
        homePane.setPrefHeight(180);
        homePane.setPrefWidth(200);
        homePane.setStyle("-fx-background-color: #ccfbff");

        double perc= (totincome*1.00)/300000;
        ProgressBar pb = new ProgressBar(perc);
        pb.setLayoutX(1000);
        pb.setLayoutY(240);
        pb.setPrefSize(200,5);
        perc=Math.round(perc*100);
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd");
        LocalDate localDate = LocalDate.now();
        System.out.println(dtf.format(localDate));
        String date=localDate.toString();
        Text t1 = textCreator(name, 520, 60, "System Bold", 41);
        Text t2 = textCreator(role , 555, 100, 30);
        Text t3 = textCreator("("+ csdate + " - "+ cedate+ ")", 535, 120, "System Bold Italic" , 15);
        Text t4 = textCreator("Total Budget   : " + budget+"$", 485, 230, 28);
        Text t5 = textCreator("Total Income   : " + Integer.toString(totincome) +"$", 485, 280, 28);
        Text t6 = textCreator(String.valueOf(perc)+"%", 1210,255,"System Bold",20);
        t6.setFill(Color.GREEN);
        Text t7 = textCreator("Expected Income :"+String.valueOf(300000)+"$",485, 330, 28 );
        Text t8 = textCreator("*"+perc+"% has been earned of expected income", 985, 280, "System Bold", 10);
        Text t9 = textCreator("within "+date , 985, 300, "System Bold", 10);
        t8.setOnMouseEntered((MouseEvent event) -> {
            t8.setFont(new Font("System Bold", 15));
            t8.setFill(Color.RED);
            t9.setFont(new Font("System Bold", 15));
            t9.setFill(Color.RED);
        });
        t8.setOnMouseExited((MouseEvent event) -> {
            t8.setFont(new Font("System Bold", 10));
            t8.setFill(Color.BLACK);
            t9.setFont(new Font("System Bold", 10));
            t9.setFill(Color.BLACK);
        });


        homePane.getChildren().addAll(t1, t2, t3,t4,t5,t6,t7,t8,t9,pb);
        homeTab.setContent(homePane);

        Tab profileTab = new Tab();
        profileTab.setText("Profile");
        AnchorPane profilePane = new AnchorPane();
        profilePane.setPrefHeight(180);
        profilePane.setPrefWidth(200);
        profilePane.setStyle("-fx-background-color: #ccfbff");

        comBoardTable = new TableView<ComBoard>();
        comBoardTable.setLayoutX(900);
        comBoardTable.setLayoutY(150);
        comBoardTable.setPrefHeight(240);
        //comBoardTable.setPrefWidth(300);
        comBoardTable.setEditable(false);
        comBoardTable.setManaged(true);

        comBoardTable.setPickOnBounds(true);
        TableColumn<ComBoard, String> c1 = new TableColumn("Role");
        TableColumn<ComBoard, String> c2 = new TableColumn("Year");
        TableColumn<ComBoard, String> c3 = new TableColumn("End Year");
        TableColumn<ComBoard, Integer> c4 = new TableColumn("Budget");
        TableColumn<ComBoard, Integer> c5 = new TableColumn("Income");

        c1.setCellValueFactory(new PropertyValueFactory<>("role"));
        c2.setCellValueFactory(new PropertyValueFactory<>("sdate"));
        c3.setCellValueFactory(new PropertyValueFactory<>("edate"));
        c4.setCellValueFactory(new PropertyValueFactory<>("budget"));
        c5.setCellValueFactory(new PropertyValueFactory<>("income"));


        c1.setMinWidth(150);
        c2.setMinWidth(150);
        c3.setMinWidth(200);
        c4.setMinWidth(150);
        c5.setMinWidth(150);



        sql="SELECT CB.ROLE, to_char(C.START_DATE, 'YYYY'), C.END_DATE, C.BUDGET, C.TOTAL_INCOME FROM BOARD_MEMBERS BM JOIN COMMITTEE_BOARD CB ON BM.BMEMBER_ID=CB.BMEMBER_ID JOIN COMMITTEES C ON CB.COMMITTEE_ID=C.COMMITTEE_ID WHERE BM.BMEMBER_ID= "+id;
        try {
            pst = con.prepareStatement(sql);
            rs = pst.executeQuery();
            cbdata.clear();
            while (rs.next()) {
                cbdata.add(new ComBoard(new SimpleStringProperty(rs.getString(1)), new SimpleStringProperty(rs.getString(2)), new SimpleStringProperty(rs.getString(3)), new SimpleIntegerProperty(rs.getInt(4)), new SimpleIntegerProperty(rs.getInt(5))));
            }


        } catch (SQLException e) {
            errorAlert("Error", "Error", null);
        }
        Text t10 = textCreator("History",1000, 120, "System Bold", 35 );
        comBoardTable.setItems(cbdata);
        comBoardTable.getColumns().addAll(c1, c2);

        Text t11 = textCreator("Address  :",30, 160, "System Bold", 30 );
        Text t12 = textCreator(address,215, 160,  25 );
        Text t13 = textCreator("Profession :",30, 210, "System Bold", 30 );
        Text t14 = textCreator(prof,215, 210,  25 );
        Text t15 = textCreator("Contact No:",30, 260, "System Bold", 30 );
        Text t16 = textCreator(contact,215, 260,  25 );
        Text t17 = textCreator("Salary   :",30, 310, "System Bold", 30 );
        Text t18 = textCreator(salary,215, 310,  25 );
        Text t19 = textCreator(name, 520, 60, "System Bold", 41);
        Text t20 = textCreator(role , 555, 100, 30);
        Text t21 = textCreator("("+ csdate + " - "+ cedate+ ")", 535, 120, "System Bold Italic" , 15);
        profilePane.getChildren().addAll(t10,t11,t12,t13,t14,t15,t16,t17,t18,t19,t20,t21,comBoardTable);
        profileTab.setContent(profilePane);

        Tab editTab = new Tab();
        editTab.setText("Edit Profile");
        AnchorPane editPane = new AnchorPane();
        editPane.setPrefHeight(180);
        editPane.setPrefWidth(200);
        editPane.setStyle("-fx-background-color: #ccfbff");
        TextField nameF = textfieldCreator(name, 83, 119, 30, 300);
        TextField proF = textfieldCreator(prof, 83, 215, 30, 300);
        TextField addF = textfieldCreator(address, 83, 311, 30, 300);
        TextField contactF = textfieldCreator(contact, 83, 407, 30, 300);
        t1 = textCreator("Name : ", 83, 97, 28);
        t2 = textCreator("Profession : ", 83, 195, 28);
        t3 = textCreator("Address : ", 83, 293, 28);
        t4 = textCreator("Contact no :", 83, 391, 28);
        Button sub = new Button("Submit");
        sub.setLayoutX(350);
        sub.setLayoutY(500);
        sub.setOnMouseClicked((MouseEvent event) -> {
            String tname, tprof, tcon, tadd;

            tname = nameF.getText();
            tprof = proF.getText();
            tadd = addF.getText();
            tcon = contactF.getText();
            System.out.println(tname+" "+tprof+" "+tadd+" "+tcon);

            boolean nameA = isAlpha(tname);
            boolean conN = tcon.chars().allMatch(Character::isDigit);


            //if (nameA && conN) {
                String ssql = "UPDATE BOARD_MEMBERS SET PROFESSION='" + tprof + "' WHERE BMEMBER_ID=" + id;

                try {
                    pst = con.prepareStatement(ssql);

                    rs = pst.executeQuery();
                    System.out.println("first passed");

                } catch (SQLException e) {
                    errorAlert("Error", "Error", null);
                    e.printStackTrace();
                }
                String usql = "UPDATE STAFFS SET STAFF_NAME='" + tname + "' ,STAFF_ADDRESS='" + tadd + "' , CONTACT_NO=" + tcon + " WHERE STAFF_ID=" + id;
                try {
                    pst = con.prepareStatement(usql);
                    rs = pst.executeQuery();
                    System.out.println("second passed");
                    errorAlert("Success", "Profile updated successfully", null);
                //scoutPage(sid);
                } catch (SQLException e) {
                    errorAlert("Error", "Error", "Invalid input");
                }
            /*} else {
                errorAlert("Error", "Invalid Input", "Invalid input");
            }*/
        });
        editPane.getChildren().addAll(nameF, contactF, addF, proF, t1, t2, t3, t4, sub);
        editTab.setContent(editPane);
        //
        Tab msgTab = new Tab();
        msgTab.setText("Message Box");
        AnchorPane msgPane = new AnchorPane();
        msgPane.setPrefHeight(180);
        msgPane.setPrefWidth(200);
        msgPane.setStyle("-fx-background-color: #ccfbff");

        Text request= textCreator("Salary Requests", 200, 80, "System Bold", 30);

        salUpdateTable = new TableView<SalaryUpdate>();
        salUpdateTable.setLayoutX(200);
        salUpdateTable.setLayoutY(120);
        salUpdateTable.setPrefHeight(240);
        //comBoardTable.setPrefWidth(300);
        salUpdateTable.setEditable(false);


        //salUpdateTable.setPickOnBounds(true);
        TableColumn<SalaryUpdate, String> c11 = new TableColumn("Name");
        TableColumn<SalaryUpdate, String> c21 = new TableColumn("Type");
        TableColumn<SalaryUpdate, String> c31 = new TableColumn("Previous Wage");
        TableColumn<SalaryUpdate, String> c41 = new TableColumn("Requested Wage");
        TableColumn<SalaryUpdate, String> c51 = new TableColumn("Date");

        c11.setCellValueFactory(new PropertyValueFactory<>("name"));
        c21.setCellValueFactory(new PropertyValueFactory<>("type"));
        c31.setCellValueFactory(new PropertyValueFactory<>("previous wage"));
        c41.setCellValueFactory(new PropertyValueFactory<>("requested wage"));
        c51.setCellValueFactory(new PropertyValueFactory<>("date"));


        c11.setMinWidth(150);
        c21.setMinWidth(150);
        c31.setMinWidth(200);
        c41.setMinWidth(200);
        c51.setMinWidth(150);


        sql="SELECT NAME, TYPE, PREV_SALARY, REQ_SALARY, REQ_DATE FROM SALARY_UPDATE";
        try {
            pst = con.prepareStatement(sql);
            rs = pst.executeQuery();
            salUpdatedata.clear();
            while (rs.next()) {
                salUpdatedata.add(new SalaryUpdate(new SimpleStringProperty(rs.getString(1)), new SimpleStringProperty(rs.getString(2)), new SimpleStringProperty(rs.getString(3)), new SimpleStringProperty(rs.getString(4)), new SimpleStringProperty(rs.getString(5))));
            }


        } catch (SQLException e) {
            errorAlert("Error", "Error", null);
        }
        salUpdateTable.setItems(salUpdatedata);
        salUpdateTable.getColumns().addAll(c11,c21,c31,c41,c51);
        msgPane.getChildren().addAll(salUpdateTable,request);
        msgTab.setContent(msgPane);

        tabs.getTabs().addAll(homeTab,profileTab,editTab,msgTab);
        box.getChildren().addAll(topPane,tabs);
        Scene scene = new Scene(box);
        stage.setScene(scene);
        stage.setTitle(name);
        stage.show();
        stage.getIcons().add(new Image("icon.png"));
        stage.setOnCloseRequest((WindowEvent t) -> {
            Platform.exit();
            System.exit(0);
        });
    }

    public static boolean isAlpha(String name) {
        char[] chars = name.toCharArray();
        for (char c : chars) {
            if (!Character.isLetter(c)) {
                if (c == ' ' || c == '.')
                    continue;
                return false;
            }
        }
        return true;
    }

    public static Text textCreator(String text, double x, double y, String type, int size) {
        Text temp = new Text(text);
        temp.setLayoutX(x);
        temp.setLayoutY(y);
        temp.setFont(new Font(type, size));
        return temp;
    }

    public static Text textCreator(String text, double x, double y, int size) {
        Text temp = new Text(text);
        temp.setLayoutX(x);
        temp.setLayoutY(y);
        temp.setFont(new Font(size));
        return temp;
    }

    public static TextField textfieldCreator(String name, double x, double y, double h, double w) {
        TextField temp = new TextField(name);
        temp.setLayoutX(x);
        temp.setLayoutY(y);
        temp.setPrefHeight(h);
        temp.setPrefWidth(w);
        return temp;
    }

    public static String isStillPresident(int id)
    {
        String res="";
        //String sql="{? = CALL stillPresident(?)}";
        String sql="begin ? := stillPresident(?); end;";
        try {
            cstmt = con.prepareCall(sql);
            //rs = cstmt.executeQuery();
            cstmt.registerOutParameter(1, Types.VARCHAR);
            cstmt.setInt(2, id);
            cstmt.execute();
            res=cstmt.getString(1);

            System.out.println("rs passed");
            System.out.println(res);
            int i=0;
            //bsdata.clear();
            //while (rs.next()) {
                //bsdata.add(new BoardStaff(new SimpleIntegerProperty(rs.getInt(1)), new SimpleStringProperty(rs.getString(2)), new SimpleStringProperty(rs.getString(3)), new SimpleIntegerProperty(rs.getInt(4)), new SimpleIntegerProperty(rs.getInt(5))));
                //res=rs.getString(i);
                //i++;
            //}
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return res;

    }
}


//player id 3
//manager id 1