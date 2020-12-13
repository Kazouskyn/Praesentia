package sample;

import API.DuoAPI;
import com.google.gson.JsonObject;
import javafx.application.Application;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.*;
import javafx.scene.*;
import javafx.scene.layout.*;
import javafx.scene.control.*;
import org.json.simple.JSONObject;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.DoubleUnaryOperator;

import static java.lang.Thread.sleep;

public class Main extends Application {
    // Buttons
    Button settings = new Button("Settings");
    Button goBack = new Button("Go Back");

    // Scenes
    Scene home;
    Scene classesManagement;
    Scene classManagement;
    Scene addRemoveStudent;
    Scene addRemoveClass;


    // Set primary Stage
    Stage window;

    @Override
    public void start(Stage primaryStage) throws Exception{

        //DataBase.ExcelWork workbook = new DataBase.ExcelWork();
//        DuoAPI.preauth();
//        DuoAPI.auth();
//        DuoAPI.enroll();
        // Set window as primary stage
        window = primaryStage;

        // Call home
        home();


        window.show();

    }

    public void home(){

        //Buttons
        Button currentClasses = new Button("Current Classes");
        Button previousClasses = new Button("Previous Classes");

        //Labels
        Label serversOnline = new Label("DUO AUTH Server Status: ");
        Label authrorization = new Label("DUO Authorization Status: ");
        try {
            String online = API.DuoAPI.ping();
            String auth = API.DuoAPI.check();
            serversOnline.setText(serversOnline.getText() + online);
            authrorization.setText(authrorization.getText() + auth);
        }catch(MalformedURLException e){
            e.printStackTrace();
        }
        // Basic layout
        BorderPane border = new BorderPane();

        // Top panel order
        HBox topPanel = new HBox();
        topPanel.setStyle("-fx-background-color: blueviolet");
        topPanel.getChildren().add(settings);
        border.setTop(topPanel);

        // Central panel
        VBox central = new VBox();
        central.getChildren().addAll(currentClasses, previousClasses, serversOnline, authrorization);
        border.setCenter(central);



        home = new Scene(border, 700, 500);
        //home.getStylesheets().add(getClass().getResource("../Styles/Style.css").toExternalForm());

        // Button Functions
        currentClasses.setOnAction(e->classesManagement());

        window.setScene(home);
    }

    public void classesManagement(){

        ArrayList<String> classes = DataBase.ExcelWork.getClasses();

        // Base layout
        BorderPane border = new BorderPane();

        // Buttons Classes management
        Button addRemove = new Button("Add/Remove");




        // Top Layout
        HBox topPanel = new HBox();
        topPanel.getChildren().addAll(goBack, settings);
        border.setTop(topPanel);

        // Central Layout
        FlowPane central = new FlowPane();
        central.getChildren().add(addRemove);
        // FUNCTION TO RETRIEVE THE NUMBER OF CLASSES
        for(int i = 0; i < classes.size(); i++){
            Button newClass = new Button(classes.get(i));
            central.getChildren().add(newClass);
            final String s = classes.get(i);
            newClass.setOnAction(e-> classManagement(s));
        }


        // ADD AS MANY BUTTONS AS NECESSARY
        border.setCenter(central);

        classesManagement = new Scene(border, 700, 500);
        classesManagement.getStylesheets().add(getClass().getResource("../Styles/Style.css").toExternalForm());

        //Button methods
        goBack.setOnAction(e-> window.setScene(home));
        addRemove.setOnAction(e->addRemoveClass());

        window.setScene(classesManagement);

    }

    public void classManagement(String s){
        // Buttons
        Button addRemoveStudent = new Button("Add/Remove Student");
        Button callAttendance = new Button("Call Attendance");
        Button exportAttendance = new Button("Export Attendance");

        // Layout
        BorderPane border = new BorderPane();

        // Top Layout
        HBox topPanel = new HBox();
        topPanel.getChildren().addAll(goBack, settings);
        border.setTop(topPanel);

        // Center Layout
        FlowPane center = new FlowPane();
        center.getChildren().addAll(addRemoveStudent, callAttendance, exportAttendance);
        border.setCenter(center);

        // Scene creation
        classManagement = new Scene(border, 700, 500);
        classManagement.getStylesheets().add(getClass().getResource("../Styles/Style.css").toExternalForm());

        // Button Methods
        goBack.setOnAction(e-> classesManagement());
        addRemoveStudent.setOnAction(e->addRemoveStudent(s));

        callAttendance.setOnAction(e->{
            DataBase.ExcelWork.datePresent(s);
            HashMap<String, Boolean> map = new HashMap<>();
            ArrayList<String> username = DataBase.ExcelWork.getUserNames(s);
            for(String u : username){
                try{
                    JSONObject obj = API.DuoAPI.auth(u);
                    JSONObject objRes = (JSONObject) obj.get("response");
                    if(objRes.get("result").toString().equals("allow")){
                        map.put(u, true);
                    }else{
                        map.put(u, false);
                    }
                }catch(Exception ex){
                    ex.printStackTrace();
                }

            }
            DataBase.ExcelWork.handleAuth(s, map);
        });

        exportAttendance.setOnAction(e->{
            DataBase.ExcelWork.exportAttendence(window, s);
        });
        window.setScene(classManagement);
    }

    public void addRemoveStudent(String s){
        // Layout
        BorderPane border = new BorderPane();

        // Top Layout
        HBox topPanel = new HBox();
        topPanel.getChildren().addAll(goBack, settings);
        border.setTop(topPanel);

        // Center
        FlowPane center = new FlowPane();


        Label studentName = new Label("Student First Name: ");
        TextField studentNameInput = new TextField();
        Label studentLName = new Label("Student Last Name: ");
        TextField studentLNameInput = new TextField();
        Button confirm = new Button("Confirm");


        //Remove Class
        ChoiceBox<String> studentNames = new ChoiceBox<>();
        ArrayList<String> names = DataBase.ExcelWork.getNames(s);
        //FIND ALL THE CLASSES AND ADD THEM TO THE CHOICE BOX WITH .GETiTEMS().ADD("CLASS NAME")
        addClasses(names, studentNames);
        Button confirmCB = new Button("Confirm");
        center.getChildren().addAll(studentName, studentNameInput, studentLName, studentLNameInput, confirm, studentNames, confirmCB);
        border.setCenter(center);

        addRemoveStudent = new Scene(border, 700, 500);

        // Button functions
        goBack.setOnAction(e->classManagement(s));

        ImageView duoQR = new ImageView();
        confirm.setOnAction(e->{

            try{

                JSONObject response = API.DuoAPI.enroll();
                JSONObject resres = (JSONObject) response.get("response");
                //InputStream stream = new FileInputStream(resres.get("activation_barcode").toString());
                Image image = new Image(resres.get("activation_barcode").toString());
                duoQR.setImage(image);
                duoQR.setX(150);
                duoQR.setY(150);
                center.getChildren().add(duoQR);
                DataBase.ExcelWork.createstudent(s, studentNameInput.getText(), studentLNameInput.getText(), resres.get("username").toString());

            }catch(Exception ex){
                ex.printStackTrace();
            }

        });


        confirmCB.setOnAction(e->DataBase.ExcelWork.deleteStudent(s, studentNames.getValue()));

        window.setScene(addRemoveStudent);
    }

    public void addRemoveClass(){

        // Layout
        BorderPane border = new BorderPane();

        // Top Layout
        HBox topPanel = new HBox();
        topPanel.getChildren().addAll(goBack, settings);
        border.setTop(topPanel);

        //center
        FlowPane center = new FlowPane();

        // Add class
        // Class Name
        Label className = new Label("Class Name: ");
        TextField classNameInput = new TextField();
        Button confirm = new Button("Confirm");

        //Remove Class
        ChoiceBox<String> classesNames = new ChoiceBox<>();
        //FIND ALL THE CLASSES AND ADD THEM TO THE CHOICE BOX WITH .GETiTEMS().ADD("CLASS NAME")
        addClasses(DataBase.ExcelWork.getClasses() , classesNames);
        Button confirmCB = new Button("Confirm");
        center.getChildren().addAll(className, classNameInput, confirm, classesNames, confirmCB);
        border.setCenter(center);

        // Scene
        addRemoveClass = new Scene(border, 700, 500);

        //Button functions
        goBack.setOnAction(e->classesManagement());
        // PASS FUNCTION WITH CLASSNAMEINPUT.GETTEXT() AS ATTRIBUTE TO SAVE THE CLASS



        confirm.setOnAction(e-> {
           DataBase.ExcelWork.addClass(classNameInput.getText());
        });
        
        
        // thEN WHEN SELECTED FIND CLASS AND DELETE FROM DATABASE
        confirmCB.setOnAction(e->{
            DataBase.ExcelWork.deleteClass(classesNames.getValue());
        });

        window.setScene(addRemoveClass);
    }

    public void addClasses(ArrayList<String> Classes, ChoiceBox<String> choice){
        for(String c : Classes){
            choice.getItems().add(c);
        }
    }


//    public ImageView getQR(){
//        JSONObject obj = new JSONObject();
//        try {
//            obj = DuoAPI.enroll();
//        } catch (MalformedURLException e) {
//            e.printStackTrace();
//        }
//        return obj.get("response").get("activation_barcode");
//    }

    public static void main(String[] args) {
        //DataBase.ExcelWork.createstudent("ninsi","nick","Kacousky","testing");
        launch(args);
    }
}
