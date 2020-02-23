package sample;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXTabPane;
import com.jfoenix.controls.JFXTextField;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;
import java.util.UUID;


public class Controller implements Initializable{

    @FXML
    public JFXTextField idText;
    @FXML
    public JFXTextField fNameText;
    @FXML
    public JFXTextField lNameText;
    @FXML
    public JFXTextField ageText;
    @FXML
    public JFXTextField majorText;
    @FXML
    public JFXTextField gpaText;
    @FXML
    JFXListView studentListView;
    @FXML
    JFXButton createDBButton;
    @FXML
    JFXButton deleteTableButton;
    @FXML
    JFXButton loadDataButton;

    final String AWS_URL = "jdbc:jtds:sqlserver://sandbox.cbuhg6kujbbi.us-east-1.rds.amazonaws.com:1433/StudentDB";
    final String username = "admin";
    final String pass = "password";


    private void createTable(String url, String user, String pass) {
        try{
            try {
                Class.forName("net.sourceforge.jtds.jdbc.Driver"); //Used jTDS to connect a SQL Server DB hosted by AWS
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                System.out.println("Connection Error");
            }

            Connection conn = DriverManager.getConnection(url, user ,pass);
            Statement stmt = conn.createStatement();
            try{
                stmt.execute("CREATE TABLE Student (" +
                        "id UNIQUEIDENTIFIER NOT NULL, " +
                        "fName VARCHAR(30), " +
                        "lName VARCHAR(30), " +
                        "age INT, " +
                        "major VARCHAR(30), " +
                        "gpa DECIMAL" +
                        ");"
                );
                System.out.println("TABLE CREATED SUCCESSFULLY");
            }
            catch (Exception ex){
                System.out.println("TABLE ALREADY EXISTS, NOT CREATED");
                try{
                    for(int i=0; i<10; i++){
                        UUID newID = UUID.randomUUID();
                        Student studNew = new Student();
                        studNew.studentID = newID;
                        studNew.fName = "New";
                        studNew.lName = "Student " +i;
                        studNew.age = 25 +i;
                        studNew.major = "CIS";
                        studNew.gpa = 3.79;
                        stmt.executeUpdate("INSERT INTO Student VALUES ('"
                        +studNew.studentID+"', '" +studNew.fName+ "', '" +studNew.lName+
                        "', '" + studNew.age+ "', '" +studNew.major+ "', '" +studNew.gpa+ "');"
                        );

                    }
                }catch (Exception e){
                    System.out.println("DEFAULT INFO ALREADY EXISTS");
                }
            }

            stmt.close();
            conn.close();

        }
        catch (Exception ex){
            String msg = ex.getMessage();
            System.out.println(msg);
        }
    }

    private void deleteTable (String url, String user, String pass){
        try{
            try {
                Class.forName("net.sourceforge.jtds.jdbc.Driver"); //Used jTDS to connect a SQL Server DB hosted by AWS
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                System.out.println("Connection Error");
            }
            Connection conn = DriverManager.getConnection(url, user ,pass);
            Statement stmt = conn.createStatement();
            stmt.execute("DROP TABLE Student");
            stmt.close();
            conn.close();
            System.out.println("TABLE DROPPED SUCCESSFULLY");
        }
        catch (Exception ex){
            String message = ex.getMessage();
            System.out.println("TABLE DROP FAILED");
            System.out.println(message);
        }
    }

    private void loadData(String url, String user, String pass){
        try{
            try {
                Class.forName("net.sourceforge.jtds.jdbc.Driver"); //Used jTDS to connect a SQL Server DB hosted by AWS
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                System.out.println("Connection Error");
            }
            Connection conn = DriverManager.getConnection(url, user ,pass);
            Statement stmt = conn.createStatement();
            String script="SELECT id, fName, lName, age, major, gpa FROM Student;";
            ResultSet result = stmt.executeQuery(script);
            ObservableList<Student> dbStudentList = FXCollections.observableArrayList();
            while (result.next()) {
                Student stud0 = new Student();
                stud0.studentID = UUID.fromString(result.getString("id"));
                stud0.fName = result.getString("fName");
                stud0.lName = result.getString("lName");
                stud0.age = result.getInt("age");
                stud0.gpa = result.getDouble("gpa");

                dbStudentList.add(stud0);
            }
            studentListView.isExpanded();
            studentListView.setExpanded(true);
            studentListView.setItems(dbStudentList);
            studentListView.getItems();

            Student selectedItem=new Student();
            selectedItem= (Student) studentListView.getSelectionModel().getSelectedItem();

            if(selectedItem==null){
                studentListView.getSelectionModel().selectFirst();
            }
            idText.setText(String.valueOf(selectedItem.studentID));
            fNameText.setText(selectedItem.fName);
            lNameText.setText(selectedItem.lName);
            ageText.setText(String.valueOf(selectedItem.age));
            majorText.setText(selectedItem.major);
            gpaText.setText(String.valueOf(selectedItem.gpa));


            System.out.println("DATA LOADED SUCCESSFULLY");
            stmt.close();
            conn.close();
        }
        catch (Exception ex){
            String msg = ex.getMessage();
            System.out.println("DATA NOT LOADED");
            System.out.println(msg);
        }
    }

        @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {



//                try {
//                    Class.forName("net.sourceforge.jtds.jdbc.Driver"); //Used jTDS to connect a SQL Server DB hosted by AWS
//                } catch (ClassNotFoundException e) {
//                    e.printStackTrace();
//                    System.out.println("Error");
//                }
//                try {
//                    Connection conn = DriverManager.getConnection("jdbc:jtds:sqlserver://sandbox.cbuhg6kujbbi.us-east-1.rds.amazonaws.com:1433/sandboxDB"
//                            , "admin", "password");
//                    Statement stmt = conn.createStatement();
//                    try{
//                        stmt.execute("CREATE TABLE Student (" +
//                                "id UNIQUEIDENTIFIER NOT NULL DEFAULT NEWID(), " +
//                                "fName VARCHAR(30), " +
//                                "lName VARCHAR(30), " +
//                                "age INT, " +
//                                "major VARCHAR(30), " +
//                                "gpa DECIMAL" +
//                                ");"
//                        );
//
//                        System.out.println("TABLE CREATED SUCCESSFULLY");
//
//                    }catch (Exception ex){
//                        System.out.println("TABLE ALREADY EXISTS, NOT CREATED");
//                    }
//                } catch (SQLException e) {
//                    e.printStackTrace();
//                }
        createDBButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                createTable(AWS_URL, username, pass);
            }
        });

        loadDataButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                loadData(AWS_URL, username, pass);
            }
        });

        deleteTableButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                deleteTable(AWS_URL, username, pass);
            }
        });




    }
}
