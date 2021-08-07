package sample;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.EventHandler;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import resources.*;
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
import java.util.Date;
import java.util.concurrent.atomic.AtomicReference;


public class Main extends Application {
    public static Stage stage;
    public static DropShadow borderGlow = new DropShadow();
    public static Alert alert;
    public static Connection con;
    public static PreparedStatement pst;
    public static ResultSet rs;
    public static TableView table;
    public static ObservableList<Team> data;
    public static TableView<ManagerStaff> managerStaffTable;
    public static ObservableList<ManagerStaff> msdata;
    public static int tableSize;
    public static Popup popup;
    public static int managerSceneFlag;
    public static TableView<BoardStaff> boardStaffTable;
    public static ObservableList<BoardStaff> bsdata;
    public static int boardMemberSceneFlag;
    public static TableView<ComBoard> comBoardTable;
    public static ObservableList<ComBoard> cbdata;
    public static int superAdminSceneFlag;
    public static TableView<SalaryUpdate> salUpdateTable;
    public static ObservableList<SalaryUpdate> salUpdatedata;
    public static int superAdminMsgFlag;
    public static TableView<Player_Basic> playerBasicTable;
    public static ObservableList<Player_Basic> playerBasicdata;
    public static int index;
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
        cbdata = FXCollections.observableArrayList();
        salUpdatedata = FXCollections.observableArrayList();
        playerBasicdata = FXCollections.observableArrayList();
        index = 0;
        boardMemberSceneFlag = 0;
        //
        try {
            con = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:globaldb", "clubmanagement", "football");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void start(Stage primaryStage) {
        stage = primaryStage;
        loginPage();
    }

    public static void loginPage() {
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
        TextField userName = textfieldCreator("", 737, 91, 26, 189);
        userName.setPromptText("Username");
        userName.clear();
        userName.setText("brian");
        PasswordField passWord = new PasswordField();
        passWord.setLayoutX(955);
        passWord.setLayoutY(91);
        passWord.setPrefHeight(26);
        passWord.setPrefWidth(189);
        passWord.setEffect(borderGlow);
        passWord.setPromptText("Password");
        passWord.setText("brian");
        passWord.clear();
        Button login = new Button("Login");
        login.setLayoutX(1173);
        login.setLayoutY(86);
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
                        } else if (userType.equals("Player")) {
                            playerPage(id, "NotAdmin");
                        } else if (userType.equals("Scout")) {
                            scoutPage(id);
                        } else if (userType.equals("Medical")) {
                            medicPage(id);
                        } else if (userType.equals("Board Member")) {
                            String prs = isStillPresident(id);
                            if (prs.matches("YES")) {
                                superAdminPage(id, "Admin");
                            } else {
                                superAdminPage(id, "NotAdmin");
                            }
                        }
                    }
                }
                if (count == 0)
                    errorAlert("Invalid Username/Password", "Invalid Username/Password", "Invalid Username/Password");
            } catch (SQLException e) {
                errorAlert("Invalid Username/Password", "Invalid Username/Password", "Invalid Username/Password");
            }
        });
        topPane.getChildren().addAll(logo, title, userName, passWord, login);
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
        //
        String sql = "select trunc(MATCH_DATE)-trunc(sysdate),OPPONENT,TOURNAMENT_NAME,STAGE FROM MATCHES WHERE MATCH_ID=upcomingmatch";
        String mdate = "", mopp = "", mtname = "", mstage = "";
        try {
            pst = con.prepareStatement(sql);
            rs = pst.executeQuery();
            while (rs.next()) {
                mdate = rs.getString(1);
                mopp = rs.getString(2);
                mtname = rs.getString(3);
                mstage = rs.getString(4);
            }
        } catch (SQLException e) {
            errorAlert("Error.", "Error", "Error");
        }
        System.out.println(mdate + "  " + mopp + "  " + mtname + "  " + mstage);

        Text t1 = textCreator("Next Match in " + mdate + " Days", 935, 151, 31);
        Text t2 = textCreator(mtname, 984, 292, 25);
        //
        Text t3 = textCreator(mopp, 1007, 208, "System Bold", 31);
        StackPane t3p = new StackPane(t3);
        Text t4 = textCreator("vs", 1071, 178, 20);
        Text t5 = textCreator(mstage, 1030, 251, 25);
        ObservableList<Matches> list1 = FXCollections.observableArrayList();
        TableView<Matches> tempTable1 = new TableView<>();
        tempTable1.setLayoutX(47);
        tempTable1.setLayoutY(60);
        tempTable1.setPrefHeight(450);
        tempTable1.setPrefWidth(814);
        tempTable1.setEditable(false);
        tempTable1.setManaged(true);
        TableColumn<Matches, String> c11 = new TableColumn("Match Date");
        TableColumn<Matches, String> c21 = new TableColumn("Venue");
        TableColumn<Matches, String> c31 = new TableColumn("Opponent");
        TableColumn<Matches, String> c41 = new TableColumn("Tournament");
        TableColumn<Matches, String> c51 = new TableColumn("Stage");
        TableColumn<Matches, String> c61 = new TableColumn("Score");
        TableColumn<Matches, String> c71 = new TableColumn("Result");
        c11.setCellValueFactory(new PropertyValueFactory<>("date"));
        c21.setCellValueFactory(new PropertyValueFactory<>("venue"));
        c31.setCellValueFactory(new PropertyValueFactory<>("opp"));
        c41.setCellValueFactory(new PropertyValueFactory<>("tournament"));
        c51.setCellValueFactory(new PropertyValueFactory<>("stage"));
        c61.setCellValueFactory(new PropertyValueFactory<>("score"));
        c71.setCellValueFactory(new PropertyValueFactory<>("result"));
        c11.setMinWidth(120);
        c21.setMinWidth(130);
        c31.setMinWidth(100);
        c41.setMinWidth(120);
        c51.setMinWidth(120);
        c61.setMinWidth(105);
        c71.setMinWidth(100);
        tempTable1.getColumns().addAll(c11, c21, c31, c41, c51, c61, c71);
        //sql
        sql = "select to_char(MATCH_DATE,'DD-MON-YYYY'),Venue,OPPONENT,TOURNAMENT_NAME,stage,RESULT,wol(result) from MATCHES" +
                " order by match_date";
        try {
            pst = con.prepareStatement(sql);
            rs = pst.executeQuery();
            list1.clear();
            while (rs.next()) {
                list1.add(new Matches(new SimpleStringProperty(rs.getString(1)), new SimpleStringProperty(rs.getString(2)), new SimpleStringProperty(rs.getString(3)), new SimpleStringProperty(rs.getString(4)), new SimpleStringProperty(rs.getString(5)), new SimpleStringProperty(rs.getString(6)), new SimpleStringProperty(rs.getString(7))));
            }
        } catch (SQLException e) {
            errorAlert("Error", "Error", null);
        }
        TextField search = textfieldCreator("", 655, 20, 26, 206);
        search.clear();
        search.setPromptText("Search table");
        FilteredList<Matches> filter1 = new FilteredList<>(list1, flag -> true);
        search.textProperty().addListener((observable, oldValue, newValue) -> {
            filter1.setPredicate(temp -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                String input = newValue.toLowerCase();
                if (temp.getDate().toLowerCase().contains(input)) {
                    return true;
                }
                if (temp.getOpp().toLowerCase().contains(input)) {
                    return true;
                }
                if (temp.getResult().toLowerCase().contains(input)) {
                    return true;
                }
                if (temp.getStage().toLowerCase().contains(input)) {
                    return true;
                }
                if (temp.getTournament().toLowerCase().contains(input)) {
                    return true;
                }
                if (temp.getVenue().toLowerCase().contains(input)) {
                    return true;
                }
                return false;
            });
        });
        SortedList<Matches> sort1 = new SortedList<>(filter1);
        sort1.comparatorProperty().bind(tempTable1.comparatorProperty());
        tempTable1.setItems(sort1);
        homePane.getChildren().addAll(t1, t2, t3, t4, t5, tempTable1, search);
        homeTab.setContent(homePane);
        Tab medicTab = new Tab();
        medicTab.setText("Players");
        AnchorPane medicPane = new AnchorPane();
        medicPane.setPrefHeight(180);
        medicPane.setPrefWidth(200);
        medicPane.setStyle("-fx-background-color: #ccfbff");
        //
        ObservableList<PlayersHome> list2 = FXCollections.observableArrayList();
        TableView<PlayersHome> tempTable2 = new TableView<>();
        tempTable2.setLayoutX(47);
        tempTable2.setLayoutY(60);
        tempTable2.setPrefHeight(450);
        tempTable2.setPrefWidth(920);
        tempTable2.setEditable(false);
        TableColumn<PlayersHome, String> c12 = new TableColumn("Name");
        TableColumn<PlayersHome, String> c22 = new TableColumn("Nationality");
        TableColumn<PlayersHome, String> c32 = new TableColumn("Position");
        TableColumn<PlayersHome, String> c42 = new TableColumn("Team Name");
        TableColumn<PlayersHome, Integer> c52 = new TableColumn("Total Matches");
        TableColumn<PlayersHome, Integer> c62 = new TableColumn("Total Goals");
        TableColumn<PlayersHome, Integer> c72 = new TableColumn("Total Fouls");
        TableColumn<PlayersHome, Double> c82 = new TableColumn("Averege Rating");
        c12.setCellValueFactory(new PropertyValueFactory<>("name"));
        c22.setCellValueFactory(new PropertyValueFactory<>("nat"));
        c32.setCellValueFactory(new PropertyValueFactory<>("pos"));
        c42.setCellValueFactory(new PropertyValueFactory<>("team"));
        c52.setCellValueFactory(new PropertyValueFactory<>("cnt"));
        c62.setCellValueFactory(new PropertyValueFactory<>("sgoals"));
        c72.setCellValueFactory(new PropertyValueFactory<>("sfouls"));
        c82.setCellValueFactory(new PropertyValueFactory<>("arating"));
        c12.setMinWidth(120);
        c22.setMinWidth(130);
        c32.setMinWidth(100);
        c42.setMinWidth(120);
        c52.setMinWidth(120);
        c62.setMinWidth(105);
        c72.setMinWidth(100);
        c82.setMinWidth(120);
        tempTable2.getColumns().addAll(c12, c22, c32, c42, c52, c62, c72, c82);
        //sql
        sql = "select p.player_name,p.NATIONALITY,p.POSITION,t.TEAM_NAME,count(pm.MATCH_ID),sum(pm.goals),sum(pm.fouls)," +
                "round(avg(pm.rating),2) from players p,player_team pt,teams t,PLAYER_MATCH pm where p.PLAYER_ID=pt.PLAYER_ID" +
                " and pt.team_id=t.TEAM_ID and pm.PLAYER_ID=p.PLAYER_ID group by p.PLAYER_ID,p.PLAYER_NAME,p.NATIONALITY,p.POSITION,t.TEAM_NAME";
        try {
            pst = con.prepareStatement(sql);
            rs = pst.executeQuery();
            list2.clear();
            while (rs.next()) {
                list2.add(new PlayersHome(new SimpleStringProperty(rs.getString(1)), new SimpleStringProperty(rs.getString(2)), new SimpleStringProperty(rs.getString(3)), new SimpleStringProperty(rs.getString(4)), new SimpleIntegerProperty(rs.getInt(5)), new SimpleIntegerProperty(rs.getInt(6)), new SimpleIntegerProperty(rs.getInt(7)), new SimpleDoubleProperty(rs.getDouble(8))));
            }
        } catch (SQLException e) {
            errorAlert("Error", "Error", null);
        }
        TextField search2 = textfieldCreator("", 750, 20, 26, 206);
        search2.clear();
        search2.setPromptText("Search table");
        FilteredList<PlayersHome> filter2 = new FilteredList<>(list2, flag -> true);
        search2.textProperty().addListener((observable, oldValue, newValue) -> {
            filter2.setPredicate(temp -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                String input = newValue.toLowerCase();
                if (temp.getName().toLowerCase().contains(input)) {
                    return true;
                }
                if (temp.getNat().toLowerCase().contains(input)) {
                    return true;
                }
                if (temp.getPos().toLowerCase().contains(input)) {
                    return true;
                }
                if (temp.getTeam().toLowerCase().contains(input)) {
                    return true;
                }
                return false;
            });
        });
        SortedList<PlayersHome> sort2 = new SortedList<>(filter2);
        sort2.comparatorProperty().bind(tempTable2.comparatorProperty());
        tempTable2.setItems(sort2);
        medicPane.getChildren().addAll(tempTable2, search2);
        medicTab.setContent(medicPane);
        Tab editTab = new Tab();
        editTab.setText("Team");
        AnchorPane editPane = new AnchorPane();
        editPane.setPrefHeight(180);
        editPane.setPrefWidth(200);
        editPane.setStyle("-fx-background-color: #ccfbff");
        //
        ObservableList<TeamHome> list3 = FXCollections.observableArrayList();
        TableView<TeamHome> tempTable3 = new TableView<>();
        tempTable3.setLayoutX(47);
        tempTable3.setLayoutY(60);
        tempTable3.setPrefHeight(450);
        tempTable3.setPrefWidth(920);
        tempTable3.setEditable(false);
        TableColumn<TeamHome, Integer> c13 = new TableColumn("ID");
        TableColumn<TeamHome, String> c23 = new TableColumn("Team Name");
        TableColumn<TeamHome, String> c33 = new TableColumn("Captain");
        TableColumn<TeamHome, String> c43 = new TableColumn("Manager");
        TableColumn<TeamHome, Integer> c53 = new TableColumn("Total Matches");
        TableColumn<TeamHome, Integer> c63 = new TableColumn("Win");
        TableColumn<TeamHome, Integer> c73 = new TableColumn("Loss");
        TableColumn<TeamHome, Integer> c83 = new TableColumn("Draw");
        c13.setCellValueFactory(new PropertyValueFactory<>("id"));
        c23.setCellValueFactory(new PropertyValueFactory<>("name"));
        c33.setCellValueFactory(new PropertyValueFactory<>("captain"));
        c43.setCellValueFactory(new PropertyValueFactory<>("manager"));
        c53.setCellValueFactory(new PropertyValueFactory<>("total"));
        c63.setCellValueFactory(new PropertyValueFactory<>("win"));
        c73.setCellValueFactory(new PropertyValueFactory<>("loss"));
        c83.setCellValueFactory(new PropertyValueFactory<>("draw"));
        c13.setMinWidth(120);
        c23.setMinWidth(130);
        c33.setMinWidth(100);
        c43.setMinWidth(120);
        c53.setMinWidth(120);
        c63.setMinWidth(105);
        c73.setMinWidth(100);
        c83.setMinWidth(120);
        tempTable3.getColumns().addAll(c13, c23, c33, c43, c53, c63, c73, c83);
        //sql
        sql = "select t.TEAM_ID,t.TEAM_NAME,t.CAPTAIN,(select s.staff_name from staffs s,managers m " +
                "where s.STAFF_ID=m.STAFF_ID and m.team_id=t.team_id) Manager,(teamWin(t.team_id)+teamLoss(t.team_id)+" +
                "teamDraw(t.team_id)) Total,teamWin\t(t.team_id) Win,teamLoss(t.team_id) Loss,teamDraw(t.team_id) Draw from teams t";
        try {
            pst = con.prepareStatement(sql);
            rs = pst.executeQuery();
            list3.clear();
            while (rs.next()) {
                list3.add(new TeamHome(new SimpleIntegerProperty(rs.getInt(1)), new SimpleStringProperty(rs.getString(2)), new SimpleStringProperty(rs.getString(3)), new SimpleStringProperty(rs.getString(4)), new SimpleIntegerProperty(rs.getInt(5)), new SimpleIntegerProperty(rs.getInt(6)), new SimpleIntegerProperty(rs.getInt(7)), new SimpleIntegerProperty(rs.getInt(8))));
            }
        } catch (SQLException e) {
            errorAlert("Error", "Error", null);
        }
        TextField search3 = textfieldCreator("", 750, 20, 26, 206);
        search3.clear();
        search3.setPromptText("Search table");
        FilteredList<TeamHome> filter3 = new FilteredList<>(list3, flag -> true);
        search3.textProperty().addListener((observable, oldValue, newValue) -> {
            filter3.setPredicate(temp -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                String input = newValue.toLowerCase();
                if (temp.getName().toLowerCase().contains(input)) {
                    return true;
                }
                if (temp.getCaptain().toLowerCase().contains(input)) {
                    return true;
                }
                if (temp.getManager().toLowerCase().contains(input)) {
                    return true;
                }
                if (String.valueOf(temp.getId()).toLowerCase().contains(input)) {
                    return true;
                }
                return false;
            });
        });
        SortedList<TeamHome> sort3 = new SortedList<>(filter3);
        sort3.comparatorProperty().bind(tempTable3.comparatorProperty());
        tempTable3.setItems(sort3);
        editPane.getChildren().addAll(tempTable3,search3);
        editTab.setContent(editPane);
        tabs.getTabs().addAll(homeTab, medicTab, editTab);
        box.getChildren().addAll(topPane, tabs);
        Scene scene = new Scene(box);
        stage.setScene(scene);
        stage.setTitle("Club Management");
        stage.show();
        stage.getIcons().add(new Image("icon.png"));
        stage.setOnCloseRequest((WindowEvent t) -> {
            Platform.exit();
            System.exit(0);
        });
    }


    public static void medicPage(int medicid) {
        String name = "", address = "", contact = "", jdate = "", edate = "", salary = "", mteam = "", chief = "", team = "", manager = "";
        int mid = 1, cid = 1;
        //sql
        String sql = "select t.team_name,t.MANAGER_ID,s.STAFF_NAME,s.STAFF_ADDRESS,s.CONTACT_NO,to_char(s.SDATE,'DD-MON-YYYY')," +
                "to_char(s.eDATE,'DD-MON-YYYY'),s.SALARY,m.MTEAM_ID,mt.CHIEF_ID from MEDICAL_TEAMS_TEAMS mtt,medicals m,staffs s,teams t" +
                ",MEDICAL_TEAMS mt where mt.MTEAM_ID=m.MTEAM_ID and t.TEAM_ID=mtt.TEAM_ID " +
                "and  s.STAFF_ID=m.STAFF_ID and m.medic_id=" + medicid + " " +
                "and mtt.MTEAM_ID=(select mteam_id from medicals where MEDIC_ID=" + medicid + ")";
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
                mteam = rs.getString(9);
                cid = rs.getInt(10);
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
            errorAlert("Error.", "Error", "Error");
        }
        sql = "select staff_name from staffs where staff_id=(select STAFF_ID from medicals where MEDIC_ID=" + cid + ")";
        try {
            pst = con.prepareStatement(sql);
            rs = pst.executeQuery();
            while (rs.next()) {
                chief = rs.getString(1);
            }
        } catch (SQLException e) {
            errorAlert("Error.", "Error.", "Error");
        }
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
        Image pimg = new Image("medicalicon.png");
        ImageView scoutIcon = new ImageView(pimg);
        scoutIcon.setFitHeight(138);
        scoutIcon.setFitWidth(200);
        scoutIcon.setLayoutX(945);
        scoutIcon.setLayoutY(15);
        scoutIcon.setPickOnBounds(true);
        scoutIcon.setPreserveRatio(true);
        //sql to get the player name
        Text scoutName = new Text(name);
        scoutName.setLayoutX(1095);
        scoutName.setLayoutY(64);
        scoutName.setFont(new Font("System Bold", 21));
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
            loginPage();
        });
        topPane.getChildren().addAll(logo, title, scoutIcon, scoutName, logout);
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
        Text t5 = textCreator("Name           : " + name, 94, 151, 33);
        Text t6 = textCreator("Contact no   : " + contact, 94, 267, 33);
        Text t7 = textCreator("Address        : " + address, 94, 210, 33);
        Text t8 = textCreator("Joining date : " + jdate, 94, 327, 33);
        Text t9 = textCreator("Contract till  : " + edate, 94, 385, 33);
        Text t10 = textCreator("Salary           : " + salary + "$", 94, 440, 33);
        Text t11 = textCreator("Chief : " + chief, 877, 277, 33);
        Text t12 = textCreator("Team : " + team, 877, 161, 33);
        Text t13 = textCreator("Medical Team : " + mteam, 877, 217, 33);
        homePane.getChildren().addAll(t1, t12, t13, t5, t6, t7, t8, t9, t10, t11);
        homeTab.setContent(homePane);
        Tab medicTab = new Tab();
        medicTab.setText("Medical Team");
        AnchorPane medicPane = new AnchorPane();
        medicPane.setPrefHeight(180);
        medicPane.setPrefWidth(200);
        medicPane.setStyle("-fx-background-color: #ccfbff");
        ObservableList<MedicalTeam> list = FXCollections.observableArrayList();
        TableView<MedicalTeam> tempTable = new TableView<>();
        tempTable.setLayoutX(61);
        tempTable.setLayoutY(39);
        tempTable.setPrefHeight(450);
        tempTable.setPrefWidth(550);
        tempTable.setEditable(false);
        TableColumn<MedicalTeam, String> c11 = new TableColumn("ID");
        TableColumn<MedicalTeam, String> c21 = new TableColumn("Name");
        TableColumn<MedicalTeam, String> c31 = new TableColumn("Address");
        TableColumn<MedicalTeam, String> c41 = new TableColumn("Contact");
        TableColumn<MedicalTeam, String> c51 = new TableColumn("Contract");
        c11.setCellValueFactory(new PropertyValueFactory<>("id"));
        c21.setCellValueFactory(new PropertyValueFactory<>("name"));
        c31.setCellValueFactory(new PropertyValueFactory<>("address"));
        c41.setCellValueFactory(new PropertyValueFactory<>("contact"));
        c51.setCellValueFactory(new PropertyValueFactory<>("edate"));
        c11.setMinWidth(120);
        c21.setMinWidth(130);
        c31.setMinWidth(100);
        c41.setMinWidth(100);
        c51.setMinWidth(100);
        tempTable.getColumns().addAll(c11, c21, c31, c41, c51);
        //sql
        sql = "select m.MEDIC_ID,s.STAFF_NAME,s.STAFF_ADDRESS,s.CONTACT_NO,to_char(s.EDATE,'DD-MON-YYYY') " +
                "from MEDICAL_TEAMS mt,medicals m,staffs s where s.staff_id=m.STAFF_ID and m.MTEAM_ID=mt.MTEAM_ID " +
                "and mt.MTEAM_ID=(select MTEAM_ID from medicals where MEDIC_ID=" + medicid + ")";
        try {
            pst = con.prepareStatement(sql);
            rs = pst.executeQuery();
            list.clear();
            while (rs.next()) {
                list.add(new MedicalTeam(new SimpleStringProperty(rs.getString(1)), new SimpleStringProperty(rs.getString(2)), new SimpleStringProperty(rs.getString(3)), new SimpleStringProperty(rs.getString(4)), new SimpleStringProperty(rs.getString(5))));
            }
        } catch (SQLException e) {
            errorAlert("Error", "Error", null);
        }
        TextField tftemp = textfieldCreator("", 800, 50, 30, 250);
        tftemp.clear();
        tftemp.setPromptText("Search table");
        FilteredList<MedicalTeam> filter1 = new FilteredList<>(list, flag -> true);
        tftemp.textProperty().addListener((observable, oldValue, newValue) -> {
            filter1.setPredicate(temp -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                String input = newValue.toLowerCase();
                if (temp.getName().toLowerCase().contains(input)) {
                    return true;
                }
                if (temp.getAddress().toLowerCase().contains(input)) {
                    return true;
                }
                if (temp.getContact().toLowerCase().contains(input)) {
                    return true;
                }
                if (temp.getId().toLowerCase().contains(input)) {
                    return true;
                }
                if (temp.getEdate().toLowerCase().contains(input)) {
                    return true;
                }
                return false;
            });
        });
        SortedList<MedicalTeam> sort1 = new SortedList<>(filter1);
        sort1.comparatorProperty().bind(tempTable.comparatorProperty());
        tempTable.setItems(sort1);
        medicPane.getChildren().addAll(tempTable, tftemp);
        medicTab.setContent(medicPane);
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
        Text t2 = textCreator("Address : ", 83, 197, 28);
        Text t3 = textCreator("Contact no :", 83, 293, 28);
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
                String ssql = "select staff_id from medicals where MEDIC_ID =" + medicid, staffid = "";
                try {
                    pst = con.prepareStatement(ssql);
                    rs = pst.executeQuery();
                    while (rs.next()) {
                        staffid = rs.getString(1);
                    }
                } catch (SQLException e) {
                    errorAlert("Error.", "Error", null);
                    e.printStackTrace();
                }
                String usql = "update staffs set staff_name='" + tname + "' , contact_no=" + tcon + ",staff_address='" + tadd + "' where staff_id=" + staffid;
                try {
                    pst = con.prepareStatement(usql);
                    rs = pst.executeQuery();
                    errorAlert("Success", "Profile updated successfully", null);
                    medicPage(medicid);
                } catch (SQLException e) {
                    errorAlert("Error", "Invalid Input", "Invalid input");
                }
            } else {
                errorAlert("Error", "Invalid Input", "Invalid input");
            }
        });
        editPane.getChildren().addAll(nameF, contactF, addF, t1, t2, t3, sub);
        editTab.setContent(editPane);
        tabs.getTabs().addAll(homeTab, medicTab, editTab);
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


    public static void managerPage(int mid) {
        String sql = "";
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
        Image pimg = new Image("managericon.png");
        ImageView managerIcon = new ImageView(pimg);
        managerIcon.setFitHeight(138);
        managerIcon.setFitWidth(200);
        managerIcon.setLayoutX(950);
        managerIcon.setLayoutY(20);
        managerIcon.setPickOnBounds(true);
        managerIcon.setPreserveRatio(true);
        //sql to get the player name
        String name = "Manager Name", address = "", contact = "";
        //sql
        String not = "";
        sql = "select s.staff_name,m.NO_OF_TROPHIES,s.staff_address,s.contact_no from managers m,staffs s where m.STAFF_ID=s.STAFF_ID and m.manager_id =" + mid;
        try {
            pst = con.prepareStatement(sql);
            rs = pst.executeQuery();
            while (rs.next()) {
                name = rs.getString(1);
                not = rs.getString(2);
                address = rs.getString(3);
                contact = rs.getString(4);
            }
        } catch (SQLException e) {
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
            loginPage();
        });
        topPane.getChildren().addAll(logo, title, managerIcon, playerName, logout);
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
        int win = 0, draw = 0, loss = 0;
        try {
            CallableStatement csmnt = con.prepareCall("{call managerMatch(?,?,?,?)}");
            csmnt.registerOutParameter(2, Types.INTEGER);
            csmnt.registerOutParameter(3, Types.INTEGER);
            csmnt.registerOutParameter(4, Types.INTEGER);
            csmnt.setString(1, String.valueOf(mid));
            csmnt.setInt(2, 0);
            csmnt.setInt(3, 0);
            csmnt.setInt(4, 0);
            csmnt.execute();
            win = csmnt.getInt(2);
            draw = csmnt.getInt(3);
            loss = csmnt.getInt(4);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        double winp = (double) win / (double) (win + loss + draw);
        winp = winp * 1000;
        winp = Math.round(winp);
        winp = winp / 10;
        double drawp = (double) draw / (double) (win + loss + draw);
        drawp = drawp * 1000;
        drawp = Math.round(drawp);
        drawp = drawp / 10;
        double lossp = (double) loss / (double) (win + loss + draw);
        lossp = lossp * 1000;
        lossp = Math.round(lossp);
        lossp = lossp / 10;
        Text text1 = textCreator(name, 510, 62, "System Bold", 37);
        Text text2 = textCreator("Total matches played : " + String.valueOf(win + loss + draw), 70, 134, 32);
        Text text3 = textCreator("Won : " + String.valueOf(win), 70, 195, 32);
        Text text4 = textCreator("Draw : " + String.valueOf(draw), 70, 251, 32);
        Text text5 = textCreator("Loss : " + String.valueOf(loss), 70, 308, 32);
        Text text6 = textCreator("Total trophies : " + not, 70, 461, 32);
        Text text7 = textCreator("Win % : " + String.valueOf(winp), 70, 368, 32);
        PieChart pc = new PieChart();
        pc.setLayoutX(580);
        pc.setLayoutY(70);
        pc.setPrefHeight(420);
        pc.setPrefWidth(580);
        PieChart.Data slice1 = new PieChart.Data("Win (" + String.valueOf(winp) + "%)", win);
        PieChart.Data slice2 = new PieChart.Data("Draw (" + String.valueOf(drawp) + "%)", draw);
        PieChart.Data slice3 = new PieChart.Data("Loss (" + String.valueOf(lossp) + "%)", loss);
        pc.getData().addAll(slice1, slice2, slice3);
        homePane.getChildren().addAll(pc, text1, text2, text3, text4, text5, text6, text7);
        homeTab.setContent(homePane);
        Tab playersTab = new Tab();
        playersTab.setText("Players");
        AnchorPane playersPane = new AnchorPane();
        playersPane.setPrefHeight(180);
        playersPane.setPrefWidth(200);
        playersPane.setStyle("-fx-background-color: #ccfbff");
        TabPane plTab = new TabPane();
        plTab.setLayoutX(20);
        plTab.setLayoutY(8);
        plTab.setPrefHeight(550);
        plTab.setPrefWidth(1270);
        plTab.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        Tab info = new Tab("Info");
        AnchorPane p1 = new AnchorPane();
        p1.setPrefWidth(811);
        p1.setPrefHeight(340);
        ObservableList<ManagerPlayer> list = FXCollections.observableArrayList();
        //table
        TableView<ManagerPlayer> tempTable = new TableView<>();
        tempTable.setLayoutX(7);
        tempTable.setLayoutY(14);
        tempTable.setPrefHeight(450);
        tempTable.setPrefWidth(930);
        tempTable.setEditable(false);
        TableColumn<ManagerPlayer, String> c11 = new TableColumn("Name");
        TableColumn<ManagerPlayer, String> c21 = new TableColumn("Nationality");
        TableColumn<ManagerPlayer, String> c31 = new TableColumn("Position");
        TableColumn<ManagerPlayer, String> c41 = new TableColumn("Height");
        TableColumn<ManagerPlayer, String> c51 = new TableColumn("Weight");
        TableColumn<ManagerPlayer, String> c61 = new TableColumn("Contact no");
        TableColumn<ManagerPlayer, String> c71 = new TableColumn("Wage");
        TableColumn<ManagerPlayer, String> c81 = new TableColumn("Contract Remaining");
        TableColumn<ManagerPlayer, String> c91 = new TableColumn("Agent");
        c11.setCellValueFactory(new PropertyValueFactory<>("name"));
        c21.setCellValueFactory(new PropertyValueFactory<>("nationality"));
        c31.setCellValueFactory(new PropertyValueFactory<>("position"));
        c41.setCellValueFactory(new PropertyValueFactory<>("height"));
        c51.setCellValueFactory(new PropertyValueFactory<>("weight"));
        c61.setCellValueFactory(new PropertyValueFactory<>("contact"));
        c71.setCellValueFactory(new PropertyValueFactory<>("wage"));
        c81.setCellValueFactory(new PropertyValueFactory<>("contract"));
        c91.setCellValueFactory(new PropertyValueFactory<>("agent"));
        c11.setMinWidth(120);
        c21.setMinWidth(130);
        c31.setMinWidth(80);
        c41.setMinWidth(80);
        c51.setMinWidth(80);
        c61.setMinWidth(120);
        c71.setMinWidth(100);
        c81.setMinWidth(87);
        c91.setMinWidth(130);
        tempTable.getColumns().addAll(c11, c21, c31, c41, c51, c61, c71, c81, c91);
        //sql
        sql = " select p.PLAYER_NAME,p.NATIONALITY,p.POSITION,p.HEIGHT,p.WEIGHT,p.CONTACT_NO,p.WAGE,trunc(p.CONTACT_TILL)-trunc(sysdate),p.AGENT_NAME from players p,player_team pt where p.PLAYER_ID=pt.PLAYER_ID and pt.team_id=(select team_id from managers where manager_id=" + mid + ") order by p.CONTACT_TILL ";
        try {
            pst = con.prepareStatement(sql);
            rs = pst.executeQuery();
            list.clear();
            while (rs.next()) {
                list.add(new ManagerPlayer(new SimpleStringProperty(rs.getString(1)), new SimpleStringProperty(rs.getString(2)), new SimpleStringProperty(rs.getString(3)), new SimpleStringProperty(rs.getString(9)), new SimpleIntegerProperty(rs.getInt(4)), new SimpleIntegerProperty(rs.getInt(5)), new SimpleIntegerProperty(rs.getInt(6)), new SimpleIntegerProperty(rs.getInt(7)), new SimpleIntegerProperty(rs.getInt(8))));
            }
        } catch (SQLException e) {
            errorAlert("Error", "Error", null);
        }
        TextField tftemp = textfieldCreator("", 980, 20, 30, 250);
        tftemp.clear();
        tftemp.setPromptText("Search table");
        FilteredList<ManagerPlayer> filter1 = new FilteredList<>(list, flag -> true);
        tftemp.textProperty().addListener((observable, oldValue, newValue) -> {
            filter1.setPredicate(temp -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                String input = newValue.toLowerCase();
                if (temp.getName().toLowerCase().contains(input)) {
                    return true;
                }
                if (temp.getAgent().toLowerCase().contains(input)) {
                    return true;
                }
                if (temp.getNationality().toLowerCase().contains(input)) {
                    return true;
                }
                if (temp.getPosition().toLowerCase().contains(input)) {
                    return true;
                }
                return false;
            });
        });
        SortedList<ManagerPlayer> sort1 = new SortedList<>(filter1);
        sort1.comparatorProperty().bind(tempTable.comparatorProperty());
        tempTable.setItems(sort1);
        p1.getChildren().addAll(tempTable, tftemp);
        info.setContent(p1);
        Tab stat = new Tab("Stats");
        AnchorPane p2 = new AnchorPane();
        p2.setPrefWidth(200);
        p2.setPrefHeight(180);
        ObservableList<ManagerPlayerStat> list1 = FXCollections.observableArrayList();
        //table
        TableView<ManagerPlayerStat> tempTable1 = new TableView<>();
        tempTable1.setLayoutX(7);
        tempTable1.setLayoutY(14);
        tempTable1.setPrefHeight(450);
        tempTable1.setPrefWidth(922);
        tempTable1.setEditable(false);
        TableColumn<ManagerPlayerStat, Integer> c12 = new TableColumn("ID");
        TableColumn<ManagerPlayerStat, String> c22 = new TableColumn("Name");
        TableColumn<ManagerPlayerStat, Integer> c32 = new TableColumn("Total matches");
        TableColumn<ManagerPlayerStat, Integer> c42 = new TableColumn("Totals goals");
        TableColumn<ManagerPlayerStat, Double> c52 = new TableColumn("Goals per match");
        TableColumn<ManagerPlayerStat, Integer> c62 = new TableColumn("Total fouls");
        TableColumn<ManagerPlayerStat, Double> c72 = new TableColumn("Fouls per match");
        TableColumn<ManagerPlayerStat, Double> c82 = new TableColumn("Averege rating");
        c12.setCellValueFactory(new PropertyValueFactory<>("id"));
        c22.setCellValueFactory(new PropertyValueFactory<>("name"));
        c32.setCellValueFactory(new PropertyValueFactory<>("count"));
        c42.setCellValueFactory(new PropertyValueFactory<>("sgoals"));
        c52.setCellValueFactory(new PropertyValueFactory<>("agoals"));
        c62.setCellValueFactory(new PropertyValueFactory<>("sfouls"));
        c72.setCellValueFactory(new PropertyValueFactory<>("afouls"));
        c82.setCellValueFactory(new PropertyValueFactory<>("arating"));
        c12.setMinWidth(50);
        c22.setMinWidth(150);
        c32.setMinWidth(120);
        c42.setMinWidth(120);
        c52.setMinWidth(120);
        c62.setMinWidth(120);
        c72.setMinWidth(120);
        c82.setMinWidth(120);
        tempTable1.getColumns().addAll(c12, c22, c32, c42, c52, c62, c72, c82);
        //sql
        sql = "select p.PLAYER_ID,p.PLAYER_NAME,count(*),SUM(pm.GOALS),round(SUM(pm.GOALS)/count(*),2),sum(pm.fouls),round(SUM(pm.fouls)/count(*),2),round(avg(pm.rating),2) from players p,PLAYER_TEAM pt,PLAYER_MATCH pm where  p.PLAYER_ID=pt.PLAYER_ID and pm.PLAYER_ID=p.PLAYER_ID and pt.TEAM_ID=(select team_id from MANAGERS where MANAGER_ID=" + mid + ") group by p.PLAYER_ID,p.PLAYER_NAME order by avg(pm.rating) desc";
        try {
            pst = con.prepareStatement(sql);
            rs = pst.executeQuery();
            list1.clear();
            while (rs.next()) {
                list1.add(new ManagerPlayerStat(new SimpleIntegerProperty(rs.getInt(1)), new SimpleStringProperty(rs.getString(2)), new SimpleIntegerProperty(rs.getInt(3)), new SimpleIntegerProperty(rs.getInt(4)), new SimpleDoubleProperty(rs.getDouble(5)), new SimpleIntegerProperty(rs.getInt(6)), new SimpleDoubleProperty(rs.getDouble(7)), new SimpleDoubleProperty(rs.getDouble(8))));
            }
        } catch (SQLException e) {
            errorAlert("Error", "Error", null);
            e.printStackTrace();
        }
        TextField tftemp1 = textfieldCreator("", 980, 20, 30, 250);
        tftemp1.clear();
        tftemp1.setPromptText("Search table");
        FilteredList<ManagerPlayerStat> filter2 = new FilteredList<>(list1, flag -> true);
        tftemp1.textProperty().addListener((observable, oldValue, newValue) -> {
            filter2.setPredicate(temp -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                String input = newValue.toLowerCase();
                if (temp.getName().toLowerCase().contains(input)) {
                    return true;
                }
                return false;
            });
        });
        SortedList<ManagerPlayerStat> sort2 = new SortedList<>(filter2);
        sort2.comparatorProperty().bind(tempTable1.comparatorProperty());
        tempTable1.setItems(sort2);
        p2.getChildren().addAll(tempTable1, tftemp1);
        stat.setContent(p2);
        plTab.getTabs().addAll(info, stat);
        playersPane.getChildren().addAll(plTab);
        playersTab.setContent(playersPane);
        Tab teamTab = new Tab();
        teamTab.setText("Team");
        AnchorPane teamPane = new AnchorPane();
        teamPane.setPrefHeight(180);
        teamPane.setPrefWidth(200);
        teamPane.setStyle("-fx-background-color: #ccfbff");
        //
        ObservableList<ManagerTeam> list2 = FXCollections.observableArrayList();
        TableView<ManagerTeam> tempTable2 = new TableView<>();
        tempTable2.setLayoutX(31);
        tempTable2.setLayoutY(45);
        tempTable2.setPrefHeight(450);
        tempTable2.setPrefWidth(922);
        tempTable2.setEditable(false);
        TableColumn<ManagerTeam, String> c13 = new TableColumn("Match Date");
        TableColumn<ManagerTeam, String> c23 = new TableColumn("Venue");
        TableColumn<ManagerTeam, String> c33 = new TableColumn("Opponent");
        TableColumn<ManagerTeam, String> c43 = new TableColumn("Tournament Name");
        TableColumn<ManagerTeam, String> c53 = new TableColumn("Stage");
        TableColumn<ManagerTeam, String> c63 = new TableColumn("Score");
        TableColumn<ManagerTeam, String> c73 = new TableColumn("Result");
        c13.setCellValueFactory(new PropertyValueFactory<>("date"));
        c23.setCellValueFactory(new PropertyValueFactory<>("venue"));
        c33.setCellValueFactory(new PropertyValueFactory<>("opponent"));
        c43.setCellValueFactory(new PropertyValueFactory<>("tournament"));
        c53.setCellValueFactory(new PropertyValueFactory<>("stage"));
        c63.setCellValueFactory(new PropertyValueFactory<>("result"));
        c73.setCellValueFactory(new PropertyValueFactory<>("wdl"));
        c13.setMinWidth(130);
        c23.setMinWidth(150);
        c33.setMinWidth(120);
        c43.setMinWidth(150);
        c53.setMinWidth(120);
        c63.setMinWidth(120);
        c73.setMinWidth(120);
        tempTable2.getColumns().addAll(c13, c23, c33, c43, c53, c63, c73);
        //sql
        sql = "select to_char(m.MATCH_DATE,'DD-MON-YYYY'),m.VENUE,m.OPPONENT,m.TOURNAMENT_NAME,m.STAGE,m.RESULT,wol(m.RESULT) from player_team pt,PLAYER_MATCH pm,matches m where pm.player_id=pt.player_id and pm.match_id=m.match_id and team_id=(select team_id from managers where manager_id=" + mid + ") group by m.MATCH_ID,m.MATCH_DATE,m.VENUE,m.OPPONENT,m.TOURNAMENT_NAME,m.STAGE,m.RESULT order by m.MATCH_DATE";
        try {
            pst = con.prepareStatement(sql);
            rs = pst.executeQuery();
            list2.clear();
            while (rs.next()) {
                list2.add(new ManagerTeam(new SimpleStringProperty(rs.getString(1)), new SimpleStringProperty(rs.getString(2)), new SimpleStringProperty(rs.getString(3)), new SimpleStringProperty(rs.getString(4)), new SimpleStringProperty(rs.getString(5)), new SimpleStringProperty(rs.getString(6)), new SimpleStringProperty(rs.getString(7))));
            }
        } catch (SQLException e) {
            errorAlert("Error", "Error", null);
            e.printStackTrace();
        }
        TextField tftemp2 = textfieldCreator("", 1000, 50, 30, 250);
        tftemp2.clear();
        tftemp2.setPromptText("Search table");
        FilteredList<ManagerTeam> filter3 = new FilteredList<>(list2, flag -> true);
        tftemp2.textProperty().addListener((observable, oldValue, newValue) -> {
            filter3.setPredicate(temp -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                String input = newValue.toLowerCase();
                if (temp.getDate().toLowerCase().contains(input)) {
                    return true;
                }
                if (temp.getOpponent().toLowerCase().contains(input)) {
                    return true;
                }
                if (temp.getResult().toLowerCase().contains(input)) {
                    return true;
                }
                if (temp.getStage().toLowerCase().contains(input)) {
                    return true;
                }
                if (temp.getTournament().toLowerCase().contains(input)) {
                    return true;
                }
                if (temp.getVenue().toLowerCase().contains(input)) {
                    return true;
                }
                if (temp.getWdl().toLowerCase().contains(input)) {
                    return true;
                }
                return false;
            });
        });
        SortedList<ManagerTeam> sort3 = new SortedList<>(filter3);
        sort3.comparatorProperty().bind(tempTable2.comparatorProperty());
        tempTable2.setItems(sort3);
        teamPane.getChildren().addAll(tempTable2, tftemp2);
        teamTab.setContent(teamPane);
        Tab employeeTab = new Tab();
        employeeTab.setText("Employees");
        AnchorPane employeePane = new AnchorPane();
        employeePane.setPrefHeight(180);
        employeePane.setPrefWidth(200);
        employeePane.setStyle("-fx-background-color: #ccfbff");
        ObservableList<ManagerEmployees> melist = FXCollections.observableArrayList();
        //table
        TableView<ManagerEmployees> meTable = new TableView<>();
        meTable.setLayoutX(30);
        meTable.setLayoutY(30);
        meTable.setPrefHeight(450);
        meTable.setPrefWidth(834);
        meTable.setEditable(false);
        TableColumn<ManagerEmployees, String> c1 = new TableColumn("Staff ID");
        TableColumn<ManagerEmployees, String> c2 = new TableColumn("Name");
        TableColumn<ManagerEmployees, String> c3 = new TableColumn("Address");
        TableColumn<ManagerEmployees, String> c4 = new TableColumn("Contact no");
        TableColumn<ManagerEmployees, String> c5 = new TableColumn("Type");
        TableColumn<ManagerEmployees, String> c6 = new TableColumn("Salary");
        TableColumn<ManagerEmployees, String> c7 = new TableColumn("Contract Till");
        TableColumn<ManagerEmployees, String> c8 = new TableColumn("Remaining");
        c1.setCellValueFactory(new PropertyValueFactory<>("id"));
        c2.setCellValueFactory(new PropertyValueFactory<>("name"));
        c3.setCellValueFactory(new PropertyValueFactory<>("address"));
        c4.setCellValueFactory(new PropertyValueFactory<>("contact"));
        c5.setCellValueFactory(new PropertyValueFactory<>("type"));
        c6.setCellValueFactory(new PropertyValueFactory<>("salary"));
        c7.setCellValueFactory(new PropertyValueFactory<>("edate"));
        c8.setCellValueFactory(new PropertyValueFactory<>("remaining"));
        c1.setMinWidth(70);
        c2.setMinWidth(120);
        c3.setMinWidth(170);
        c4.setMinWidth(100);
        c5.setMinWidth(90);
        c6.setMinWidth(80);
        c7.setMinWidth(100);
        c8.setMinWidth(87);
        meTable.getColumns().addAll(c1, c2, c3, c4, c5, c6, c7, c8);
        //sql
        int i = 1;
        sql = " SELECT ST.STAFF_ID,ST.STAFF_NAME, ST.STAFF_ADDRESS,ST.CONTACT_NO,ST.TYPE,ST.SALARY,to_char(st.edate,'DD-MON-YYYY'),trunc(st.edate)-trunc(sysdate) FROM SCOUTS SC JOIN STAFFS ST ON (SC.STAFF_ID=ST.STAFF_ID) WHERE TEAM_ID = (SELECT TEAM_ID FROM MANAGERS WHERE MANAGER_ID=" + mid + ") order by st.EDATE ";
        try {
            pst = con.prepareStatement(sql);
            rs = pst.executeQuery();
            melist.clear();
            while (rs.next()) {
                melist.add(new ManagerEmployees(new SimpleStringProperty(rs.getString(1)), new SimpleStringProperty(rs.getString(2)), new SimpleStringProperty(rs.getString(3)), new SimpleStringProperty(rs.getString(4)), new SimpleStringProperty(rs.getString(5)), new SimpleStringProperty(rs.getString(6)), new SimpleStringProperty(rs.getString(7)), new SimpleStringProperty(rs.getString(8))));
            }
        } catch (SQLException e) {
            errorAlert("Error", "Error", null);
        }
        sql = "SELECT ST.STAFF_ID,ST.STAFF_NAME, ST.STAFF_ADDRESS,ST.CONTACT_NO,ST.TYPE,ST.SALARY,to_char(st.edate,'DD-MON-YYYY'),trunc(st.edate)-trunc(sysdate) FROM MEDICALS MC,MEDICAL_TEAMS_TEAMS MTT, STAFFS ST WHERE TEAM_ID = (SELECT TEAM_ID FROM MANAGERS WHERE MANAGER_ID=" + mid + ") AND MC.MTEAM_ID = MTT.MTEAM_ID AND MC.STAFF_ID = ST.STAFF_ID order by st.EDATE";
        try {
            pst = con.prepareStatement(sql);
            rs = pst.executeQuery();
            while (rs.next()) {
                melist.add(new ManagerEmployees(new SimpleStringProperty(rs.getString(1)), new SimpleStringProperty(rs.getString(2)), new SimpleStringProperty(rs.getString(3)), new SimpleStringProperty(rs.getString(4)), new SimpleStringProperty(rs.getString(5)), new SimpleStringProperty(rs.getString(6)), new SimpleStringProperty(rs.getString(7)), new SimpleStringProperty(rs.getString(8))));
            }
        } catch (SQLException e) {
            errorAlert("Error", "Error", null);
        }
        TextField tf = textfieldCreator("", 920, 100, 35, 320);
        tf.setPromptText("Search table");
        FilteredList<ManagerEmployees> filter = new FilteredList<>(melist, flag -> true);

        tf.textProperty().addListener((observable, oldValue, newValue) -> {
            filter.setPredicate(temp -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                String input = newValue.toLowerCase();
                if (temp.getName().toLowerCase().contains(input)) {
                    return true;
                }
                if (temp.getAddress().toLowerCase().contains(input)) {
                    return true;
                }
                if (temp.getContact().contains(input)) {
                    return true;
                }
                if (temp.getEdate().toLowerCase().contains(input)) {
                    return true;
                }
                if (temp.getType().toLowerCase().contains(input)) {
                    return true;
                }
                return false;
            });
        });
        SortedList<ManagerEmployees> sort = new SortedList<>(filter);
        sort.comparatorProperty().bind(meTable.comparatorProperty());
        meTable.setItems(sort);
        employeePane.getChildren().addAll(meTable, tf);
        employeeTab.setContent(employeePane);
        Tab editTab = new Tab();
        editTab.setText("Edit Profile");
        AnchorPane editPane = new AnchorPane();
        editPane.setPrefHeight(180);
        editPane.setPrefWidth(200);
        editPane.setStyle("-fx-background-color: #ccfbff");
        //
        TextField nameF = textfieldCreator(name, 83, 119, 30, 300);
        TextField addF = textfieldCreator(address, 83, 215, 30, 300);
        TextField contactF = textfieldCreator(contact, 83, 311, 30, 300);
        Text t1 = textCreator("Name : ", 83, 97, 28);
        Text t2 = textCreator("Address : ", 83, 197, 28);
        Text t3 = textCreator("Contact no :", 83, 293, 28);
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
                String ssql = "select staff_id from managers where manager_id =" + mid, staffid = "";
                try {
                    pst = con.prepareStatement(ssql);
                    rs = pst.executeQuery();
                    while (rs.next()) {
                        staffid = rs.getString(1);
                    }
                } catch (SQLException e) {
                    errorAlert("Error", "Error", null);
                }
                String usql = "update staffs set staff_name='" + tname + "' , contact_no=" + tcon + ",staff_address='" + tadd + "' where staff_id=" + staffid;
                try {
                    pst = con.prepareStatement(usql);
                    rs = pst.executeQuery();
                    errorAlert("Success", "Profile updated successfully", null);
                    managerPage(mid);
                } catch (SQLException e) {
                    errorAlert("Error", "Invalid Input", "Invalid input");
                }
            } else {
                errorAlert("Error", "Invalid Input", "Invalid input");
            }
        });
        editPane.getChildren().addAll(nameF, contactF, addF, t1, t2, t3, sub);
        editTab.setContent(editPane);
        tabs.getTabs().addAll(homeTab, playersTab, teamTab, employeeTab, editTab);
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
        Text scoutName = new Text(name);
        scoutName.setLayoutX(1095);
        scoutName.setLayoutY(64);
        scoutName.setFont(new Font("System Bold", 21));
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
            loginPage();
        });
        topPane.getChildren().addAll(logo, title, scoutIcon, scoutName, logout);
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


    public static void playerPage(int pid, String detector) {
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
        int adminid = 0;
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
        if (detector == "Admin") {
            sql = "SELECT BMEMBER_ID FROM BOARD_MEMBERS WHERE STILLPRESIDENT(BMEMBER_ID)='YES'";
            try {
                pst = con.prepareStatement(sql);
                rs = pst.executeQuery();
                while (rs.next()) {
                    adminid = rs.getInt(1);
                }
            } catch (SQLException e) {
                e.printStackTrace();
                errorAlert("Error", "Error", null);
            }
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
            loginPage();
        });


        final int superAdminId = adminid;
        Text back = new Text("(Back)");
        back.setLayoutX(1209);
        back.setLayoutY(75);
        back.setFont(new Font("System Bold Italic", 17));
        back.setOnMouseEntered((MouseEvent event) -> {
            back.setFont(new Font("System Bold Italic", 20));
            back.setFill(Color.DARKGRAY);
        });
        back.setOnMouseExited((MouseEvent event) -> {
            back.setFont(new Font("System Bold Italic", 17));
            back.setFill(Color.BLACK);
        });
        back.setOnMouseClicked((MouseEvent event) -> {
            superAdminPage(superAdminId, "Admin");
        });

        if (detector == "NotAdmin") {
            topPane.getChildren().addAll(logo, title, playerIcon, playerName, logout);
        } else if (detector == "Admin") {
            topPane.getChildren().addAll(logo, title, back, logout);
        }

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
        String infosql = "select NATIONALITY,POSITION,abs(trunc(SYSDATE)-trunc(CONTACT_TILL)) Time from PLAYERS where PLAYER_ID=" + pid;
        try {
            pst = con.prepareStatement(infosql);
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
        String statsql = "select count(*),SUM(GOALS),SUM(FOULS),SUM(MINUTES_PLAYED),AVG(RATING) from PLAYER_MATCH where PLAYER_ID=" + pid;
        try {
            pst = con.prepareStatement(statsql);
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
        String insql = "select TO_CHAR(DATE_OF_BIRTH,'dd-MON-yyyy'),HEIGHT,WEIGHT,CONTACT_NO,WAGE,MARKET_VALUE,BUY_OUT_CLAUSE,AGENT_NAME,TO_CHAR(CONTACT_TILL,'dd-MON-yyyy') from PLAYERS where PLAYER_ID=" + pid;
        String dob = "", height = "", weight = "", contact = "", wage = "", mv = "", boc = "", agent = "", contracttill = "", jdate = "";
        try {
            pst = con.prepareStatement(insql);
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
        String psql = "SELECT TO_CHAR(jdate,'dd-MON-yyyy') from PLAYER_TEAM where PLAYER_ID=" + pid;
        try {
            pst = con.prepareStatement(psql);
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
        String dsql = "select to_char(m.MATCH_DATE,'dd-MON-YY'),m.OPPONENT,m.TOURNAMENT_NAME,m.STAGE,m.RESULT,pm.MINUTES_PLAYED,pm.GOALS,pm.FOULS,pm.SAVES,pm.RATING,wol(m.RESULT) from PLAYER_MATCH pm,MATCHES m where PLAYER_ID=" + pid + " and pm.MATCH_ID=m.MATCH_ID order by m.MATCH_DATE";
        try {
            pst = con.prepareStatement(dsql);
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
                    playerPage(pid, "NotAdmin");
                } catch (SQLException e) {
                    errorAlert("Error", "Invalid Input", "Invalid input");
                }
            } else {
                errorAlert("Error", "Invalid Input", "Invalid input");
            }
        });
        // shoumik
        Text wageText = textCreator("Present Wage : ", 800, 120, 30);
        Text wageAmount = textCreator(wage + "$", 1000, 120, 25);
        Text upText = textCreator("Upgrade to ", 800, 170, 30);
        TextField reqWage = textfieldCreator(wage, 980, 150, 30, 120);
        Button req = new Button("Send Request");
        req.setLayoutX(1100);
        req.setLayoutY(200);
        req.setOnMouseClicked((MouseEvent event) -> {
            int amount;
            amount = Integer.valueOf(reqWage.getText());
            String upsql = "UPDATE PLAYERS SET WAGE= " + amount + " WHERE PLAYER_ID=" + pid;
            String trig = "ALTER TRIGGER SALARYUPDATEPLAYERS ENABLE";

            //sql
            try {
                pst = con.prepareStatement(trig);
                rs = pst.executeQuery();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            try {

                pst = con.prepareStatement(upsql);
                rs = pst.executeQuery();

                errorAlert("Success", "Salary Request", "Your request for salary has been sent to the president!");
                playerPage(pid, "NotAdmin");
            } catch (SQLException e) {
                errorAlert("Error", "Invalid Input", "Invalid input");
            }

        });
        editPane.getChildren().addAll(nameF, contactF, agentF, t1, t2, t3, wageText, wageAmount, upText, reqWage, req, sub);
        editTab.setContent(editPane);
        if (detector == "NotAdmin") {
            tabs.getTabs().addAll(homeTab, profileTab, statsTab, editTab);
        } else if (detector == "Admin") {
            tabs.getTabs().addAll(homeTab, profileTab, statsTab);
        }

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

    public static void managerPage2(int mid) {
        AnchorPane mainPane = new AnchorPane();
        mainPane.setPrefWidth(1550);
        mainPane.setPrefHeight(650);
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
            loginPage();
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


    public static void errorAlert(String title, String header, String content) {
        alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
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
        temp.setEffect(borderGlow);
        return temp;
    }

    public static void updateTable() {
        String sql = "SELECT * FROM TEAMS ORDER BY TEAM_ID";
        try {
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

    // shoumik


    public static void superAdminPage(int id, String detector) {
        //superAdminMsgFlag 0 for hometab, 1 for msgtab, 2 for playertab, 3for managertab, 4 for profiletab
        String name = "", address = "", contact = "", salary = "", csdate = "", cedate = "", prof = "", role = "", budget = "";
        int totincome = 0, st_id = 0;

        //sql
        String sql = "SELECT CB.ROLE, C.START_DATE, C.END_DATE, C.BUDGET, C.TOTAL_INCOME FROM BOARD_MEMBERS BM JOIN COMMITTEE_BOARD CB ON BM.BMEMBER_ID=CB.BMEMBER_ID JOIN COMMITTEES C ON CB.COMMITTEE_ID=C.COMMITTEE_ID WHERE BM.BMEMBER_ID=" + id;
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
        sql = "SELECT BM.PROFESSION, ST.STAFF_NAME, ST.STAFF_ADDRESS, ST.CONTACT_NO, ST.SALARY, ST.STAFF_ID FROM BOARD_MEMBERS BM JOIN STAFFS ST ON BM.STAFF_ID=ST.STAFF_ID WHERE BM.BMEMBER_ID=" + id;
        try {
            pst = con.prepareStatement(sql);
            rs = pst.executeQuery();
            while (rs.next()) {
                prof = rs.getString(1);
                name = rs.getString(2);
                address = rs.getString(3);
                contact = rs.getString(4);
                salary = rs.getString(5);
                st_id = rs.getInt(6);
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
            superAdminMsgFlag = 0;
            loginPage();
        });
        topPane.getChildren().addAll(logo, title, adminIcon, adminName, logout);
        TabPane tabs = new TabPane();
        tabs.setPrefHeight(634);
        tabs.setPrefWidth(1300);
        tabs.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        SingleSelectionModel<Tab> selectionModel = tabs.getSelectionModel();


        Tab homeTab = new Tab();
        homeTab.setText("Home");
        AnchorPane homePane = new AnchorPane();
        homePane.setPrefHeight(180);
        homePane.setPrefWidth(200);
        homePane.setStyle("-fx-background-color: #ccfbff");
        double perc = (totincome * 1.00) / 300000;
        ProgressBar pb = new ProgressBar(perc);
        pb.setLayoutX(1000);
        pb.setLayoutY(240);
        pb.setPrefSize(200, 5);
        perc = Math.round(perc * 100);
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd");
        LocalDate localDate = LocalDate.now();
        System.out.println(dtf.format(localDate));
        String date = localDate.toString();
        Text t1 = textCreator(name, 520, 60, "System Bold", 41);
        Text t2 = textCreator(role, 555, 100, 30);
        Text boardmemberText = textCreator("Board Member", 555, 100, 30);
        Text t3 = textCreator("(" + csdate + " - " + cedate + ")", 535, 120, "System Bold Italic", 15);
        Text t4 = textCreator("Total Budget   : " + budget + "$", 485, 230, 28);
        Text t5 = textCreator("Total Income   : " + Integer.toString(totincome) + "$", 485, 280, 28);
        Text t6 = textCreator(String.valueOf(perc) + "%", 1210, 255, "System Bold", 20);
        t6.setFill(Color.GREEN);
        Text t7 = textCreator("Expected Income :" + String.valueOf(300000) + "$", 485, 330, 28);
        Text t8 = textCreator("*" + perc + "% has been earned of expected income", 985, 280, "System Bold", 10);
        Text t9 = textCreator("within " + date, 985, 300, "System Bold", 10);
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

        XYChart.Series budgetSeries = new XYChart.Series();
        budgetSeries.setName("Budget in years");
        XYChart.Series incomeSeries = new XYChart.Series();
        incomeSeries.setName("Income in years");

        int count = 1;
        String bsql = "SELECT * FROM COMMITTEES";
        try {
            pst = con.prepareStatement(bsql);
            rs = pst.executeQuery();

            while (rs.next()) {
                //pslist.add(new PlayerStat(new SimpleStringProperty(rs.getString(1)), new SimpleStringProperty(rs.getString(2)), new SimpleStringProperty(rs.getString(3)), new SimpleStringProperty(rs.getString(4)), new SimpleStringProperty(rs.getString(5)), new SimpleStringProperty(rs.getString(6)), new SimpleStringProperty(rs.getString(7)), new SimpleStringProperty(rs.getString(8)), new SimpleStringProperty(rs.getString(9)), new SimpleStringProperty(rs.getString(10)), new SimpleStringProperty(rs.getString(11))));
                budgetSeries.getData().add(new XYChart.Data(2010 + count, rs.getInt(4)));
                incomeSeries.getData().add(new XYChart.Data(2010 + count, rs.getInt(5)));
                //System.out.println(budgetSeries.getData());
                count++;
                //rts.getData().add(new XYChart.Data(i++, rs.getInt(10)));
            }
        } catch (SQLException e) {
            errorAlert("Error", "Error", null);
        }
        //pstable.setItems(pslist);
        //chart
        NumberAxis xAxis = new NumberAxis("Year", 2011, 2011 + count - 1, 1);
        NumberAxis yAxis = new NumberAxis("Budget", 0, 700000, 30000);
        LineChart budgetChart = new LineChart(xAxis, yAxis);
        budgetChart.getData().addAll(budgetSeries, incomeSeries);
        //xAxis = new NumberAxis("Match", 1, cnt, 1);
        //yAxis = new NumberAxis("Rating", 0, 10, 1);
        //LineChart ratingChart = new LineChart(xAxis, yAxis);
        budgetChart.setLayoutX(25);
        budgetChart.setLayoutY(40);
        budgetChart.setPrefHeight(400);
        budgetChart.setPrefWidth(450);
        if (detector == "Admin") {
            homePane.getChildren().addAll(t1, t2, t3, t4, t5, t6, t7, t8, t9, pb, budgetChart);
        } else if (detector == "NotAdmin") {
            homePane.getChildren().addAll(t1, t2, t3, t4, t5, t7, budgetChart);
        }
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
        sql = "SELECT CB.ROLE, to_char(C.START_DATE, 'YYYY'), C.END_DATE, C.BUDGET, C.TOTAL_INCOME FROM BOARD_MEMBERS BM JOIN COMMITTEE_BOARD CB ON BM.BMEMBER_ID=CB.BMEMBER_ID JOIN COMMITTEES C ON CB.COMMITTEE_ID=C.COMMITTEE_ID WHERE BM.BMEMBER_ID= " + id;
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
        Text t10 = textCreator("History", 1000, 120, "System Bold", 35);
        comBoardTable.setItems(cbdata);
        comBoardTable.getColumns().addAll(c1, c2);
        Text t11 = textCreator("Address  :", 30, 160, "System Bold", 30);
        Text t12 = textCreator(address, 215, 160, 25);
        Text t13 = textCreator("Profession :", 30, 210, "System Bold", 30);
        Text t14 = textCreator(prof, 215, 210, 25);
        Text t15 = textCreator("Contact No:", 30, 260, "System Bold", 30);
        Text t16 = textCreator(contact, 215, 260, 25);
        Text t17 = textCreator("Salary   :", 30, 310, "System Bold", 30);
        Text t18 = textCreator(salary, 215, 310, 25);
        Text t19 = textCreator(name, 520, 60, "System Bold", 41);
        Text t20 = textCreator(role, 555, 100, 30);
        Text t21 = textCreator("(" + csdate + " - " + cedate + ")", 535, 120, "System Bold Italic", 15);
        profilePane.getChildren().addAll(t10, t11, t12, t13, t14, t15, t16, t17, t18, t19, t20, t21, comBoardTable);
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
            System.out.println(tname + " " + tprof + " " + tadd + " " + tcon);
            boolean nameA = isAlpha(tname);
            boolean conN = tcon.chars().allMatch(Character::isDigit);
            if (nameA && conN) {
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
                    superAdminMsgFlag = 4;
                    superAdminPage(id, detector);
                    errorAlert("Success", "Profile updated successfully", null);
                    //scoutPage(sid);
                } catch (SQLException e) {
                    errorAlert("Error", "Error", "Invalid input");
                }
            } else {
                errorAlert("Error", "Invalid Input", "Invalid input");
            }
        });
        if (superAdminMsgFlag == 4) {
            selectionModel.select(profileTab);
        }
        editPane.getChildren().addAll(nameF, contactF, addF, proF, t1, t2, t3, t4, sub);
        editTab.setContent(editPane);
        //
        Tab msgTab = new Tab();
        msgTab.setText("Message Box");
        AnchorPane msgPane = new AnchorPane();
        msgPane.setPrefHeight(180);
        msgPane.setPrefWidth(200);
        msgPane.setStyle("-fx-background-color: #ccfbff");
        Text request = textCreator("Salary Requests", 200, 80, "System Bold", 30);
        salUpdateTable = new TableView<SalaryUpdate>();
        salUpdateTable.setLayoutX(200);
        salUpdateTable.setLayoutY(120);
        salUpdateTable.setPrefHeight(240);
        //comBoardTable.setPrefWidth(300);
        salUpdateTable.setEditable(false);
        Button approve = new Button("Approve");
        approve.setLayoutX(850);
        approve.setLayoutY(400);
        approve.setMinSize(80, 20);
        //approve.setStyle("-fx-background-color: #39e600; -fx-border-color: #33cc00; -fx-border-width: 2px;");
        //approve.setMaxWidth(100);
        //approve.setPrefHeight(20);
        Button cancel = new Button("Cancel");
        cancel.setLayoutX(950);
        cancel.setLayoutY(400);
        cancel.setMinSize(80, 20);
        //approve.setMinWidth(100);
        //approve.setPrefHeight(20);
        //salUpdateTable.setPickOnBounds(true);
        TableColumn<SalaryUpdate, String> c11 = new TableColumn("Name");
        TableColumn<SalaryUpdate, String> c21 = new TableColumn("Type");
        TableColumn<SalaryUpdate, String> c31 = new TableColumn("Previous Wage");
        TableColumn<SalaryUpdate, String> c41 = new TableColumn("Requested Wage");
        TableColumn<SalaryUpdate, String> c51 = new TableColumn("Date");
        c11.setCellValueFactory(new PropertyValueFactory<>("name"));
        c21.setCellValueFactory(new PropertyValueFactory<>("type"));
        c31.setCellValueFactory(new PropertyValueFactory<>("pSal"));
        c41.setCellValueFactory(new PropertyValueFactory<>("rSal"));
        c51.setCellValueFactory(new PropertyValueFactory<>("date"));
        c11.setMinWidth(150);
        c21.setMinWidth(150);
        c31.setMinWidth(200);
        c41.setMinWidth(200);
        c51.setMinWidth(150);
        sql = "SELECT NAME, TYPE, PREV_SALARY, REQ_SALARY, REQ_DATE FROM SALARY_UPDATE";
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
        salUpdateTable.getColumns().addAll(c11, c21, c31, c41, c51);
        salUpdateTable.setManaged(true);
        //salUpdateTable.setEditable(true);
        salUpdateTable.setOnMouseClicked((MouseEvent event) -> {
                    SalaryUpdate sObject = salUpdateTable.getSelectionModel().getSelectedItem();
                    String newSal = sObject.rSal.get(), newName = sObject.name.get();
                    System.out.println(newName + " " + newSal + " ");
                    int flag;
                    String trsql = "ALTER TRIGGER SALARYUPDATEPLAYERS DISABLE";
                    try {
                        pst = con.prepareStatement(trsql);
                        rs = pst.executeQuery();
                        System.out.println("trigger disabled!");

                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    approve.setOnMouseClicked((MouseEvent e) -> {
                        String upsql = "UPDATE PLAYERS SET WAGE=" + newSal + " WHERE PLAYER_NAME= '" + newName + "'";
                        String desql = "DELETE FROM SALARY_UPDATE WHERE NAME='" + newName + "'";
                        try {

                            pst = con.prepareStatement(upsql);
                            rs = pst.executeQuery();

                            System.out.println("update passed!");
                        } catch (SQLException e1) {
                            e1.printStackTrace();
                            System.out.println("Error!");
                        }

                        try {
                            pst = con.prepareStatement(desql);
                            rs = pst.executeQuery();
                            System.out.println("delete passed!");
                            superAdminMsgFlag = 1;
                            superAdminPage(id, "Admin");
                        } catch (SQLException e1) {
                            e1.printStackTrace();
                        }
                    });

                    cancel.setOnMouseClicked((MouseEvent e) -> {

                        String desql = "DELETE FROM SALARY_UPDATE WHERE NAME='" + newName + "'";


                        try {
                            pst = con.prepareStatement(desql);
                            rs = pst.executeQuery();
                            System.out.println("delete passed!");
                            superAdminMsgFlag = 1;
                            superAdminPage(id, "Admin");
                        } catch (SQLException e1) {
                            e1.printStackTrace();
                        }
                    });


                }
        );


        msgPane.getChildren().addAll(salUpdateTable, request, approve, cancel);
        msgTab.setContent(msgPane);
        if (superAdminMsgFlag == 1) {
            selectionModel.select(msgTab);
        } else if (superAdminMsgFlag == 0) {
            selectionModel.selectFirst();
        }


        Tab exploreTab = new Tab();
        exploreTab.setText("Explore");
        AnchorPane explorePane = new AnchorPane();
        explorePane.setPrefHeight(180);
        explorePane.setPrefWidth(200);
        explorePane.setStyle("-fx-background-color: #ccfbff");


        TabPane tablePane = new TabPane();
        tablePane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        SingleSelectionModel<Tab> selectionModelExplore = tablePane.getSelectionModel();

        Tab playerTab = new Tab();
        playerTab.setText("Players");
        AnchorPane playerPane = new AnchorPane();
        playerPane.setPrefHeight(180);
        playerPane.setPrefWidth(200);
        playerPane.setStyle("-fx-background-color: #ccfbff");

        Text playerText = textCreator("Players", 50, 32, "System Bold", 30);
        playerBasicTable = new TableView<Player_Basic>();
        playerBasicTable.setLayoutX(50);
        playerBasicTable.setLayoutY(40);
        playerBasicTable.setPrefHeight(300);
        playerBasicTable.setPrefWidth(1200);
        playerBasicTable.setEditable(true);
        playerBasicTable.setPickOnBounds(true);
        playerBasicTable.setManaged(true);
        playerBasicTable.setTableMenuButtonVisible(true);

        TableColumn<Player_Basic, String> c13 = new TableColumn("Id");
        TableColumn<Player_Basic, String> c23 = new TableColumn("Name");
        TableColumn<Player_Basic, String> c33 = new TableColumn("Date of Birth");
        TableColumn<Player_Basic, String> c43 = new TableColumn("Nationality");
        TableColumn<Player_Basic, String> c53 = new TableColumn("Position");
        TableColumn<Player_Basic, String> c63 = new TableColumn("Height");
        TableColumn<Player_Basic, String> c73 = new TableColumn("Weight");
        TableColumn<Player_Basic, String> c83 = new TableColumn("Contact No");
        TableColumn<Player_Basic, String> c93 = new TableColumn("Wage");
        TableColumn<Player_Basic, String> c103 = new TableColumn("Contact Till");
        TableColumn<Player_Basic, String> c113 = new TableColumn("Market Value");
        TableColumn<Player_Basic, String> c123 = new TableColumn("Buy Out Clause");
        TableColumn<Player_Basic, String> c133 = new TableColumn("Agent Name");

        c13.setCellValueFactory(new PropertyValueFactory<>("id"));
        c23.setCellValueFactory(new PropertyValueFactory<>("name"));
        c33.setCellValueFactory(new PropertyValueFactory<>("dob"));
        c43.setCellValueFactory(new PropertyValueFactory<>("nat"));
        c53.setCellValueFactory(new PropertyValueFactory<>("pos"));
        c63.setCellValueFactory(new PropertyValueFactory<>("height"));
        c73.setCellValueFactory(new PropertyValueFactory<>("weight"));
        c83.setCellValueFactory(new PropertyValueFactory<>("contactNo"));
        c93.setCellValueFactory(new PropertyValueFactory<>("wage"));
        c103.setCellValueFactory(new PropertyValueFactory<>("contacTill"));
        c113.setCellValueFactory(new PropertyValueFactory<>("value"));
        c123.setCellValueFactory(new PropertyValueFactory<>("buyClause"));
        c133.setCellValueFactory(new PropertyValueFactory<>("agname"));


        c13.setMinWidth(50);
        c23.setMinWidth(100);
        c33.setMinWidth(150);
        c43.setMinWidth(150);
        c53.setMinWidth(80);
        c63.setMinWidth(80);
        c73.setMinWidth(80);
        c83.setMinWidth(100);
        c93.setMinWidth(80);
        c103.setMinWidth(100);
        c113.setMinWidth(80);
        c123.setMinWidth(50);
        c133.setMinWidth(100);


        sql = "SELECT player_id,player_name,to_char(date_of_birth,'DD-MM-YYYY'),NATIONALITY,POSITION,HEIGHT,weight,CONTACT_NO," +
                "wage,to_char(CONTACT_TILL,'DD-MM-YYYY'),MARKET_VALUE,BUY_OUT_CLAUSE,AGENT_NAME FROM PLAYERS";
        try {
            pst = con.prepareStatement(sql);
            rs = pst.executeQuery();
            playerBasicdata.clear();
            while (rs.next()) {
                index++;
                playerBasicdata.add(new Player_Basic(new SimpleStringProperty(rs.getString(1)), new SimpleStringProperty(rs.getString(2)), new SimpleStringProperty(rs.getString(3)), new SimpleStringProperty(rs.getString(4)), new SimpleStringProperty(rs.getString(5)), new SimpleStringProperty(rs.getString(6)), new SimpleStringProperty(rs.getString(7)), new SimpleStringProperty(rs.getString(8)), new SimpleStringProperty(rs.getString(9)), new SimpleStringProperty(rs.getString(10)), new SimpleStringProperty(rs.getString(11)), new SimpleStringProperty(rs.getString(12)), new SimpleStringProperty(rs.getString(13))));
            }


        } catch (SQLException e) {
            e.printStackTrace();
            errorAlert("Error", "Error", null);
        }
        playerBasicTable.setItems(playerBasicdata);
        playerBasicTable.getColumns().addAll(c13, c23, c33, c43, c53, c63, c73, c83, c93, c103, c113, c123, c133);

        TextField search = new TextField("");
        search.setLayoutX(1050);
        search.setLayoutY(8);
        search.setPrefSize(200, 20);
        FilteredList<Player_Basic> filter = new FilteredList<>(playerBasicdata, flag -> true);
        search.textProperty().addListener((observable, oldValue, newValue) -> {
            filter.setPredicate(temp -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                String input = newValue.toLowerCase();
                if (temp.getName().toLowerCase().contains(input)) {
                    return true;
                }
                if (temp.getAgname().toLowerCase().contains(input)) {
                    return true;
                }
                if (temp.getPos().toLowerCase().contains(input)) {
                    return true;
                }
                if (temp.getNat().toLowerCase().contains(input)) {
                    return true;
                }
                return false;
            });
        });
        SortedList<Player_Basic> sort = new SortedList<>(filter);
        sort.comparatorProperty().bind(playerBasicTable.comparatorProperty());
        playerBasicTable.setItems(sort);

        Button add = new Button("Add");
        add.setLayoutX(1200);
        add.setLayoutY(400);
        add.setVisible(true);
        TextField f_name = textfieldCreatorNew("Name", 50, 360, 15, 100);
        //TextField f_dob = textfieldCreatorNew("Birthdate", 170, 360, 15, 100);
        DatePicker f_dob = datePickerCreator(170, 360, 15, 100);
        TextField f_nat = textfieldCreatorNew("Nationality", 290, 360, 15, 100);
        TextField f_pos = textfieldCreatorNew("Position", 410, 360, 15, 60);
        TextField f_height = textfieldCreatorNew("Height", 490, 360, 15, 60);
        TextField f_weight = textfieldCreatorNew("Weight", 570, 360, 15, 60);
        TextField f_contactno = textfieldCreatorNew("Contact No", 650, 360, 15, 80);
        TextField f_wage = textfieldCreatorNew("Wage", 750, 360, 15, 60);
        //TextField f_contacttill = textfieldCreatorNew("Contact Till", 830, 360, 15, 100);
        DatePicker f_contacttill = datePickerCreator(830, 360, 15, 100);
        TextField f_buy = textfieldCreatorNew("Buy Out Clause", 950, 360, 15, 80);
        TextField f_value = textfieldCreatorNew("Market Value", 1050, 360, 15, 80);
        TextField f_agname = textfieldCreatorNew("Agent Name", 1150, 360, 15, 100);

        System.out.println(index);
        index++;
        add.setOnMouseClicked((MouseEvent event) -> {
                    String aname, adob, anat, apos, aheight, aweight, acontact, avalue, aagname, awage, acontacttill, abuy;
                    aname = f_name.getText();
                    //adob = f_dob.getText();
                    anat = f_nat.getText();
                    apos = f_pos.getText();
                    aheight = f_height.getText();
                    aweight = f_weight.getText();
                    acontact = f_contactno.getText();
                    awage = f_wage.getText();
                    //acontacttill = f_contacttill.getText();
                    abuy = f_buy.getText();
                    avalue = f_value.getText();
                    aagname = f_agname.getText();

                   /* String isql = "INSERT INTO PLAYERS (PLAYER_ID, PLAYER_NAME, DATE_OF_BIRTH, NATIONALITY, POSITION, HEIGHT, WEIGHT, CONTACT_NO, WAGE, CONTACT_TILL, MARKET_VALUE, BUY_OUT_CLAUSE, AGENT_NAME) VALUES (" + index + ", '" + aname + "', '" + adob + "' , '" + anat + "', '" + apos + "', '" + aheight + "', '" + aweight + "', '" + acontact + "', '" + awage + "', '" + acontacttill + "', '" + avalue + "', '" + abuy + "', '" + aagname + "')";
                    try {
                        pst = con.prepareStatement(isql);
                        rs = pst.executeQuery();
                        superAdminMsgFlag = 2;
                        superAdminPage(id, "Admin");
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }*/
                }

        );

        Button edit = new Button("Edit");
        edit.setLayoutX(1100);
        edit.setLayoutY(400);
        edit.setVisible(false);
        Button delete = new Button("Delete");
        delete.setLayoutX(1200);
        delete.setLayoutY(400);
        delete.setVisible(false);
        Button view = new Button("View");
        view.setLayoutX(970);
        view.setLayoutY(2);
        view.setVisible(false);

        playerPane.setOnMouseClicked((MouseEvent event) -> {
            edit.setVisible(false);
            delete.setVisible(false);
            view.setVisible(false);
            add.setVisible(true);
            f_name.clear();
            f_dob.setValue(LocalDate.now());
            f_dob.setPromptText("dd MM yyy");
            f_nat.clear();
            f_pos.clear();
            f_height.clear();
            f_weight.clear();
            f_contactno.clear();
            f_wage.clear();
            f_contacttill.setValue(LocalDate.now());
            f_buy.clear();
            f_value.clear();
            f_agname.clear();


        });

        playerBasicTable.setOnMouseClicked((MouseEvent event) -> {
                    Player_Basic pObject = playerBasicTable.getSelectionModel().getSelectedItem();
                    //String newSal=sObject.rSal.get(), newName=sObject.name.get();
                    //System.out.println(newName+" "+newSal+" ");
                    //SimpleDateFormat formatter1=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                    edit.setVisible(true);
                    delete.setVisible(true);
                    view.setVisible(true);
                    add.setVisible(false);
                    String aid, aname, adob, anat, apos, aheight, aweight, acontact, avalue, aagname, awage, acontacttill, abuy;
                    aid = pObject.getId();
                    aname = pObject.getName();
                    adob = pObject.getDob();
                    anat = pObject.getNat();
                    apos = pObject.getPos();
                    aheight = pObject.getHeight();
                    aweight = pObject.getWeight();
                    acontact = pObject.getContactNo();

                    avalue = pObject.getValue();
                    aagname = pObject.getAgname();
                    awage = pObject.getWage();
                    acontacttill = pObject.getContacTill();
                    System.out.println(acontacttill);

                    abuy = pObject.getBuyClause();
                    f_name.setText(aname);
                    //
                    /*DateTimeFormatter format = DateTimeFormatter.ofPattern("dd-MMM-yyyy");
                    LocalDate dobDate = LocalDate.parse(acontacttill, format);*/
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
                    LocalDate Date = LocalDate.parse(adob, formatter);
                    f_dob.setValue(Date);
                    f_dob.setPromptText("dd MM yyy");
                    f_nat.setText(anat);
                    f_pos.setText(apos);
                    f_height.setText(aheight);
                    f_weight.setText(aweight);
                    f_contactno.setText(acontact);
                    f_wage.setText(awage);
                    LocalDate contractDate = LocalDate.parse(acontacttill, formatter);
                    f_contacttill.setPromptText("dd MM yyyy");
                    LocalDate cDate = LocalDate.parse(acontacttill, formatter);
                    f_contacttill.setValue(cDate);
                    f_buy.setText(abuy);
                    f_value.setText(avalue);
                    f_agname.setText(aagname);

                    String trsql = "ALTER TRIGGER SALARYUPDATEPLAYERS DISABLE";
                    try {
                        pst = con.prepareStatement(trsql);
                        rs = pst.executeQuery();
                        System.out.println("trigger disabled!");

                    } catch (SQLException e) {
                        e.printStackTrace();
                    }

                    view.setOnMouseClicked((MouseEvent e) -> {
                        playerPage(Integer.valueOf(aid), "Admin");
                    });

                    edit.setOnMouseClicked((MouseEvent e) -> {
                        DateTimeFormatter format = DateTimeFormatter.ofPattern("dd-MMM-yyyy");
                        String tempdob = f_dob.getValue().format(format), tempcon = f_contacttill.getValue().format(format);

                        String upsql = "UPDATE PLAYERS SET PLAYER_NAME='" + f_name.getText() + "', DATE_OF_BIRTH='" + tempdob + "', NATIONALITY='" + f_nat.getText() + "', POSITION='" + f_pos.getText() + "', HEIGHT=" + f_height.getText() + " , WEIGHT=" + f_weight.getText() + " , CONTACT_NO=" + f_contactno.getText() + ", WAGE=" + f_wage.getText() + ", CONTACT_TILL='" + tempcon + "', MARKET_VALUE=" + f_value.getText() + ", BUY_OUT_CLAUSE=" + f_buy.getText() + ", AGENT_NAME='" + f_agname.getText() + "' WHERE PLAYER_ID=" + aid;
                        // System.out.println(upsql);
                        try {
                            pst = con.prepareStatement(upsql);
                            rs = pst.executeQuery();
                            superAdminMsgFlag = 2;
                            superAdminPage(id, "Admin");
                        } catch (SQLException e1) {
                            e1.printStackTrace();
                            errorAlert("Invalid Input", "Invalid Input", null);
                        }

                    });

                    delete.setOnMouseClicked((MouseEvent e) -> {

                        String dsql = "DELETE FROM PLAYERS WHERE PLAYER_ID=" + aid;
                        try {
                            pst = con.prepareStatement(dsql);
                            rs = pst.executeQuery();
                            superAdminMsgFlag = 2;
                            superAdminPage(id, "Admin");
                        } catch (SQLException e1) {
                            e1.printStackTrace();
                        }

                    });


                }
        );

        if (superAdminMsgFlag == 2) {
            selectionModel.select(exploreTab);
            selectionModelExplore.select(playerTab);
        }

        playerPane.getChildren().addAll(playerText, playerBasicTable, search, add, view, edit, delete, f_name, f_dob, f_nat, f_pos, f_height, f_weight, f_contactno, f_wage, f_contacttill, f_buy, f_value, f_agname);


        playerTab.setContent(playerPane);

        Tab teamTab = new Tab();
        teamTab.setText("Teams");
        AnchorPane teamPane = new AnchorPane();
        teamPane.setPrefHeight(180);
        teamPane.setPrefWidth(200);
        teamPane.setStyle("-fx-background-color: #ccfbff");
        Text teams = textCreator("Teams", 100, 60, "System Bold", 30);
        table = new TableView<Team>();
        table.setLayoutX(100);
        table.setLayoutY(100);
        table.setPrefHeight(380);
        table.setPrefWidth(300);
        TableColumn<Team, Integer> c14 = new TableColumn("Id");
        TableColumn<Team, String> c24 = new TableColumn("Team Name");
        TableColumn<Team, String> c34 = new TableColumn("Manager Id");
        TableColumn<Team, String> c44 = new TableColumn("Scout Id");
        TableColumn<Team, String> c54 = new TableColumn("Medic Id");
        TableColumn<Team, String> c64 = new TableColumn("Captain");

        c14.setCellValueFactory(new PropertyValueFactory<>("team_id"));
        c24.setCellValueFactory(new PropertyValueFactory<>("team_name"));
        c34.setCellValueFactory(new PropertyValueFactory<>("manager_id"));
        c44.setCellValueFactory(new PropertyValueFactory<>("scout_id"));
        c54.setCellValueFactory(new PropertyValueFactory<>("medic_id"));
        c64.setCellValueFactory(new PropertyValueFactory<>("captain"));


        c14.setMinWidth(150);
        c24.setMinWidth(150);
        c34.setMinWidth(200);
        c44.setMinWidth(200);
        c54.setMinWidth(150);
        c64.setMinWidth(150);

        updateTable();
        table.getColumns().addAll(c14, c24);
        //Text managerName=textCreator(" ", 800, 200, 25);

        table.setOnMouseClicked((MouseEvent e) -> {
            //Team tObject = table.getSelectionModel().getSelectedItem();
            int tableIndex = table.getSelectionModel().getSelectedIndex();
            tableIndex++;
            System.out.println(tableIndex);

            String managerName = showManager(tableIndex);
            String scoutName = showScout(tableIndex);
            String medicName = showMedic(tableIndex);
            String capName = "";

            String Capsql = "SELECT CAPTAIN FROM TEAMS WHERE TEAM_ID=" + tableIndex;
            try {
                pst = con.prepareStatement(Capsql);
                rs = pst.executeQuery();
                while (rs.next()) {
                    capName = rs.getString(1);
                }
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
            System.out.println(capName);

            Text managerNameText = textCreator(managerName, 800, 150, 25);
            Text scoutNameText = textCreator(scoutName, 800, 190, 25);
            Text medicNameText = textCreator(medicName, 800, 230, 25);
            Text capNameText = textCreator(capName, 800, 270, 25);

            Text manager = textCreator("Manager      :", 600, 150, "System Bold", 25);
            Text scout = textCreator("Scout        :", 600, 190, "System Bold", 25);
            Text medic = textCreator("Medical Chief:", 600, 230, "System Bold", 25);
            Text captain = textCreator("Captain      :", 600, 270, "System Bold", 25);


            teamPane.getChildren().clear();
            teamPane.getChildren().addAll(table, teams, managerNameText, scoutNameText, medicNameText, capNameText, manager, scout, medic, captain);


        });

        teamPane.getChildren().addAll(table, teams);

        teamTab.setContent(teamPane);

        Tab managerTab = new Tab("Manager");
        AnchorPane managerPane = new AnchorPane();
        managerPane.setPrefHeight(180);
        managerPane.setPrefWidth(200);
        managerPane.setStyle("-fx-background-color: #ccfbff");
        Text managers = textCreator("Managers", 100, 60, "System Bold", 30);

        boardStaffTable = new TableView<BoardStaff>();
        boardStaffTable.setLayoutX(100);
        boardStaffTable.setLayoutY(100);
        boardStaffTable.setPrefHeight(300);
        //boardStaffTable.setPrefWidth(955);
        boardStaffTable.setEditable(false);
        TableColumn<BoardStaff, Integer> c16 = new TableColumn("Staff ID");
        TableColumn<BoardStaff, String> c26 = new TableColumn("Staff Name");
        TableColumn<BoardStaff, String> c36 = new TableColumn("Staff Address");
        TableColumn<BoardStaff, Integer> c46 = new TableColumn("Contact No");
        //TableColumn<BoardStaff, String> c5 = new TableColumn("Type");
        TableColumn<BoardStaff, Integer> c66 = new TableColumn("Salary");
        c16.setCellValueFactory(new PropertyValueFactory<>("staff_id"));
        c26.setCellValueFactory(new PropertyValueFactory<>("staff_name"));
        c36.setCellValueFactory(new PropertyValueFactory<>("staff_address"));
        c46.setCellValueFactory(new PropertyValueFactory<>("contact_no"));
        //c5.setCellValueFactory(new PropertyValueFactory<>("type"));
        c66.setCellValueFactory(new PropertyValueFactory<>("salary"));
        c16.setMinWidth(150);
        c26.setMinWidth(150);
        c36.setMinWidth(200);
        c46.setMinWidth(150);
        //c5.setMinWidth(150);
        c66.setMinWidth(150);
        boardStaffTable.getColumns().addAll(c16, c26, c36, c46, c66);
        int managerIndex = 0;
        String managerSql = "SELECT STAFF_ID, STAFF_NAME, STAFF_ADDRESS, CONTACT_NO, SALARY FROM STAFFS WHERE TYPE='Manager' ";
        try {
            pst = con.prepareStatement(managerSql);
            rs = pst.executeQuery();
            System.out.println("rs passed");
            bsdata.clear();
            while (rs.next()) {
                managerIndex++;
                bsdata.add(new BoardStaff(new SimpleIntegerProperty(rs.getInt(1)), new SimpleStringProperty(rs.getString(2)), new SimpleStringProperty(rs.getString(3)), new SimpleIntegerProperty(rs.getInt(4)), new SimpleIntegerProperty(rs.getInt(5))));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        boardStaffTable.setItems(bsdata);

        TextField m_name = textfieldCreatorNew("Name", 100, 420, 15, 100);
        TextField m_address = textfieldCreatorNew("Address", 220, 420, 15, 100);
        TextField m_con = textfieldCreatorNew("Contact No", 340, 420, 15, 100);
        TextField m_sal = textfieldCreatorNew("Salary", 460, 420, 15, 60);

        /*add.setOnMouseClicked((MouseEvent event) -> {
                    int acontact, asalary;
                    String aname, aaddress;
                    aname = f_name.getText();
                    aaddress = f_nat.getText();
                    acontact = Integer.valueOf(f_contactno.getText());
                    asalary = Integer.valueOf(f_wage.getText());
                    String isql="INSERT INTO PLAYERS (PLAYER_ID, PLAYER_NAME, DATE_OF_BIRTH, NATIONALITY, POSITION, HEIGHT, WEIGHT, CONTACT_NO, WAGE, CONTACT_TILL, MARKET_VALUE, BUY_OUT_CLAUSE, AGENT_NAME) VALUES ("+index+", '"+aname+"', '"+adob+"' , '"+anat+"', '"+apos+"', '"+aheight+"', '"+aweight+"', '"+acontact+"', '"+awage+"', '"+acontacttill+"', '"+avalue+"', '"+abuy+"', '"+aagname+"')";
                    try{
                        pst=con.prepareStatement(isql);
                        rs=pst.executeQuery();
                        superAdminMsgFlag=2;
                        superAdminPage(id);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
        );*/
        Button m_edit = new Button("Edit");
        m_edit.setLayoutX(750);
        m_edit.setLayoutY(420);
        m_edit.setVisible(false);
        Button m_delete = new Button("Delete");
        m_delete.setLayoutX(820);
        m_delete.setLayoutY(420);
        m_delete.setVisible(false);


        boardStaffTable.setOnMouseClicked((MouseEvent event) -> {
                    BoardStaff pObject = boardStaffTable.getSelectionModel().getSelectedItem();
                    //String newSal=sObject.rSal.get(), newName=sObject.name.get();
                    //System.out.println(newName+" "+newSal+" ");
                    //SimpleDateFormat formatter1=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                    m_edit.setVisible(true);
                    m_delete.setVisible(true);
                    //add.setVisible(false);
                    int aid, acontact, asalary;
                    String aname, aaddress;
                    aid = pObject.getStaff_id();
                    aname = pObject.getStaff_name();
                    aaddress = pObject.getStaff_address();
                    acontact = pObject.getContact_no();
                    asalary = pObject.getSalary();

                    m_name.setText(aname);

                    m_address.setText(aaddress);
                    m_con.setText(String.valueOf(acontact));
                    m_sal.setText(String.valueOf(asalary));

                    m_edit.setOnMouseClicked((MouseEvent e) -> {

                        String upsql = "UPDATE STAFFS SET STAFF_NAME='" + m_name.getText() + " ', STAFF_ADDRESS='" + m_address.getText() + " ', CONTACT_NO=" + m_con.getText() + " , SALARY=" + m_sal.getText() + " WHERE STAFF_ID=" + aid;
                        try {
                            pst = con.prepareStatement(upsql);
                            rs = pst.executeQuery();
                            superAdminMsgFlag = 3;
                            superAdminPage(id, "Admin");
                        } catch (SQLException e1) {
                            e1.printStackTrace();
                        }

                    });

                    m_delete.setOnMouseClicked((MouseEvent e) -> {

                        String dsql = "DELETE FROM STAFFS WHERE STAFF_ID=" + aid;
                        try {
                            pst = con.prepareStatement(dsql);
                            rs = pst.executeQuery();
                            superAdminMsgFlag = 3;
                            superAdminPage(id, "Admin");
                        } catch (SQLException e1) {
                            e1.printStackTrace();
                        }

                    });


                }
        );

        if (superAdminMsgFlag == 3) {
            selectionModel.select(exploreTab);
            selectionModelExplore.select(managerTab);
        }


        managerPane.getChildren().addAll(managers, boardStaffTable, m_name, m_address, m_con, m_sal, m_edit, m_delete);
        managerTab.setContent(managerPane);


        tablePane.getTabs().addAll(playerTab, teamTab, managerTab);
        explorePane.getChildren().add(tablePane);
        exploreTab.setContent(explorePane);

        if (detector == "Admin") {
            tabs.getTabs().addAll(homeTab, profileTab, editTab, msgTab, exploreTab);
        } else if (detector == "NotAdmin") {
            tabs.getTabs().addAll(homeTab, profileTab, editTab);
        }

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

    public static TextField textfieldCreatorNew(String name, double x, double y, double h, double w) {
        TextField temp = new TextField();
        temp.setPromptText(name);
        temp.setLayoutX(x);
        temp.setLayoutY(y);
        temp.setPrefHeight(h);
        temp.setPrefWidth(w);
        return temp;
    }

    public static String isStillPresident(int id) {
        String res = "";
        String sql = "begin ? := stillPresident(?); end;";
        CallableStatement cstmt;
        try {
            cstmt = con.prepareCall(sql);
            cstmt.registerOutParameter(1, Types.VARCHAR);
            cstmt.setInt(2, id);
            cstmt.execute();
            res = cstmt.getString(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return res;
    }


    public static String showManager(int tid) {
        String res = "";
        //String sql="{? = CALL stillPresident(?)}";
        CallableStatement cstmt;
        String sql = "begin ? := showManager(?); end;";
        try {
            cstmt = con.prepareCall(sql);
            //rs = cstmt.executeQuery();
            cstmt.registerOutParameter(1, Types.VARCHAR);
            cstmt.setInt(2, tid);
            cstmt.execute();
            res = cstmt.getString(1);

            System.out.println("rs passed");
            System.out.println(res);

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return res;

    }

    public static String showScout(int tid) {
        String res = "";
        //String sql="{? = CALL stillPresident(?)}";
        CallableStatement cstmt;
        String sql = "begin ? := showScout(?); end;";
        try {
            cstmt = con.prepareCall(sql);
            //rs = cstmt.executeQuery();
            cstmt.registerOutParameter(1, Types.VARCHAR);
            cstmt.setInt(2, tid);
            cstmt.execute();
            res = cstmt.getString(1);

            System.out.println("rs passed");
            System.out.println(res);

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return res;

    }

    public static String showMedic(int tid) {
        String res = "";
        //String sql="{? = CALL stillPresident(?)}";
        CallableStatement cstmt;
        String sql = "begin ? := showMedic(?); end;";
        try {
            cstmt = con.prepareCall(sql);
            //rs = cstmt.executeQuery();
            cstmt.registerOutParameter(1, Types.VARCHAR);
            cstmt.setInt(2, tid);
            cstmt.execute();
            res = cstmt.getString(1);

            System.out.println("rs passed");
            System.out.println(res);

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return res;
    }

    public static DatePicker datePickerCreator(double x, double y, double h, double w) {
        DatePicker temp = new DatePicker();
        temp.setLayoutX(x);
        temp.setLayoutY(y);
        temp.setPrefHeight(h);
        temp.setPrefWidth(w);
        return temp;
    }
}


//player id 3      jelani
//manager id 1     kasper
//medical id 1      madonna
//scout id 1        scarlett
//board member 1    solomon
//president 28       brian