package sample;
import com.jfoenix.controls.*;
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
import java.util.Random;
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
    public JFXButton clearButton;
    public JFXButton addNewButton;
    public JFXToggleButton editToggle;
    public JFXCheckBox filterAgeCheck;
    public JFXTextField ageTextFilter;
    public JFXCheckBox filterMajorCheck;
    public JFXCheckBox filterCheckGPA;
    public JFXTextField gpaTextFilter;
    public JFXComboBox majorDrop;
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
                        "gpa DECIMAL(3,2)" +
                        ");"
                );

                System.out.println("TABLE CREATED SUCCESSFULLY");
            }
            catch (Exception ex) {
                System.out.println("TABLE ALREADY EXISTS, NOT CREATED");
            }
            try{
                for(int i=0; i<10; i++){
                    UUID newID = UUID.randomUUID();
                    Random r = new Random();
                    int index = r.nextInt(2 + 1 - 0);
                    majorDrop.getSelectionModel().select(index);
                    Student studNew = new Student();
                    studNew.studentID = newID;
                    studNew.fName = "New";
                    studNew.lName = "Student " +i;
                    studNew.age = new Random().nextInt(65 + 1 - 17) + 17;
                    studNew.major = (String) majorDrop.getSelectionModel().getSelectedItem();
                    studNew.gpa = 2.0 + (2.0) * r.nextDouble();
                    stmt.executeUpdate("INSERT INTO Student VALUES ('"
                    +studNew.studentID+"', '" +studNew.fName+ "', '" +studNew.lName+
                    "', '" + studNew.age+ "', '" +studNew.major+ "', '" +studNew.gpa+ "');"
                    );
                }
                }catch (Exception e){
                    System.out.println("DEFAULT INFO ALREADY EXISTS");
                }
            loadData(AWS_URL, this.username, this.pass);
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
            ObservableList<Student> dbStudentList = FXCollections.observableArrayList();
            studentListView.setItems(dbStudentList);
            studentListView.getItems();
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
                stud0.major = result.getString("major");
                stud0.gpa = result.getDouble("gpa");

                dbStudentList.add(stud0);
            }
            studentListView.isExpanded();
            studentListView.setExpanded(true);
            studentListView.setItems(dbStudentList);
            studentListView.getItems();
            int selectedRow = studentListView.getSelectionModel().getSelectedIndex();
            if(selectedRow!=0){
                studentListView.getSelectionModel().selectFirst();
            }
            filterAgeCheck.setSelected(false);
            filterMajorCheck.setSelected(false);
            filterCheckGPA.setSelected(false);
            System.out.println("DATA LOADED SUCCESSFULLY");
            stmt.close();
            conn.close();
        }
        catch (Exception ex){
            String msg = ex.getMessage();
            System.out.println("DATA NOT LOADED");
            System.out.println(msg);
        }

        studentListView.getSelectionModel().selectedItemProperty().addListener(((observableValue, o, t1) -> {
            Student selectedItem = (Student) studentListView.getSelectionModel().getSelectedItem();
            idText.setText(String.valueOf(selectedItem.studentID));
            fNameText.setText(selectedItem.fName);
            lNameText.setText(selectedItem.lName);
            ageText.setText(String.valueOf(selectedItem.age));
            majorText.setText(selectedItem.major);
            gpaText.setText(String.valueOf(selectedItem.gpa));



        }));
        idText.labelFloatProperty().set(true);
        fNameText.labelFloatProperty().set(true);
        lNameText.labelFloatProperty().set(true);

    }

    public void filterData(String url, String user, String pass, String condition){
        try{
            try {
                Class.forName("net.sourceforge.jtds.jdbc.Driver"); //Used jTDS to connect a SQL Server DB hosted by AWS
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                System.out.println("Connection Error");
            }
            Connection conn = DriverManager.getConnection(url, user ,pass);
            Statement stmt = conn.createStatement();

            //SQL Query that receives WHERE Clauses from the checkbox control
            String script="SELECT * FROM Student WHERE "+
                    condition + ";"
                    ;

            ResultSet result = stmt.executeQuery(script);
            ObservableList<Student> dbStudentList = FXCollections.observableArrayList();
            while (result.next()) {
                Student stud0 = new Student();
                stud0.studentID = UUID.fromString(result.getString("id"));
                stud0.fName = result.getString("fName");
                stud0.lName = result.getString("lName");
                stud0.age = result.getInt("age");
                stud0.major = result.getString("major");
                stud0.gpa = result.getDouble("gpa");

                dbStudentList.add(stud0);
            }
            studentListView.isExpanded();
            studentListView.setExpanded(true);
            studentListView.setItems(dbStudentList);
            studentListView.getItems();
            int selectedRow = studentListView.getSelectionModel().getSelectedIndex();
            if(selectedRow!=0){
                studentListView.getSelectionModel().selectFirst();
            }

            System.out.println("DATA LOADED SUCCESSFULLY");
            stmt.close();
            conn.close();
        }
        catch (Exception ex){
            String msg = ex.getMessage();
            System.out.println("DATA NOT LOADED");
            System.out.println(msg);
        }

        studentListView.getSelectionModel().selectedItemProperty().addListener(((observableValue, o, t1) -> {
            Student selectedItem = (Student) studentListView.getSelectionModel().getSelectedItem();
            idText.setText(String.valueOf(selectedItem.studentID));
            fNameText.setText(selectedItem.fName);
            lNameText.setText(selectedItem.lName);
            ageText.setText(String.valueOf(selectedItem.age));
            majorText.setText(selectedItem.major);
            gpaText.setText(String.valueOf(selectedItem.gpa));



        }));
        idText.labelFloatProperty().set(true);
        fNameText.labelFloatProperty().set(true);
        lNameText.labelFloatProperty().set(true);

    }


    public void addNewRecord(String url, String user, String pass, String sqlString){
        try{
            try {
                Class.forName("net.sourceforge.jtds.jdbc.Driver"); //Used jTDS to connect a SQL Server DB hosted by AWS
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                System.out.println("Connection Error");
            }
            Connection conn = DriverManager.getConnection(url, user ,pass);
            Statement stmt = conn.createStatement();

            stmt.executeUpdate(sqlString);
            loadData(AWS_URL, username, pass);
        }catch(Exception e){
            System.out.println("ERROR: THE RECORD MAY ALREADY EXIST.");
        }
    }

    public void clearFields(){
        idText.clear();
        fNameText.clear();
        lNameText.clear();
        ageText.clear();
        majorText.clear();
        gpaText.clear();
    }


        @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

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

        addNewButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {

                UUID newID = UUID.randomUUID();
                Student studNew = new Student();
                studNew.studentID = newID;
                studNew.fName = fNameText.getText();
                studNew.lName = lNameText.getText();
                studNew.age = Integer.parseInt(ageText.getText()) ;
                studNew.major = majorText.getText();
                studNew.gpa = Double.parseDouble(gpaText.getText());

                String sqlString="INSERT INTO Student VALUES ('"
                        +studNew.studentID+"', '" +studNew.fName+ "', '" +studNew.lName+
                        "', '" + studNew.age+ "', '" +studNew.major+ "', '" +studNew.gpa+ "');";

                addNewRecord(AWS_URL, username, pass, sqlString);
            }
        });

        clearButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                clearFields();
            }
        });

        editToggle.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                if (!fNameText.isEditable()) {
                    fNameText.setEditable(true);
                }else fNameText.setEditable(false);
                if (!lNameText.isEditable()) {
                    lNameText.setEditable(true);
                }else lNameText.setEditable(false);
                if (!ageText.isEditable()) {
                    ageText.setEditable(true);
                }else ageText.setEditable(false);
                if (!majorText.isEditable()) {
                    majorText.setEditable(true);
                }else majorText.setEditable(false);
                if (!gpaText.isEditable()) {
                    gpaText.setEditable(true);
                }else gpaText.setEditable(false);
                idText.clear();
            }

        });

        filterAgeCheck.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                String ageVariable = "";
                String majorVariable = "";
                String gpaVariable = "";
                if(filterAgeCheck.isSelected()) {
                    ageVariable = "age > "+ageTextFilter.getText();

                    if(filterMajorCheck.isSelected()) {
                        majorVariable = "AND major= '"+(String) majorDrop.getSelectionModel().getSelectedItem() + "'";

                        if(filterCheckGPA.isSelected()){
                            gpaVariable = "AND gpa > " +gpaTextFilter.getText();

                        }
                    }else if(filterCheckGPA.isSelected()){
                        gpaVariable = " gpa > " +gpaTextFilter.getText();
                    }
                }else if(filterMajorCheck.isSelected()) {
                    majorVariable = " major= '"+(String) majorDrop.getSelectionModel().getSelectedItem() + "'";

                    if(filterCheckGPA.isSelected()){
                        gpaVariable = "AND gpa > " +gpaTextFilter.getText();

                    }
                }else if(filterCheckGPA.isSelected()){
                    gpaVariable = " gpa > " +gpaTextFilter.getText();
                }

                String filter= ageVariable + majorVariable + gpaVariable;
                filterData(AWS_URL, username, pass, filter);

            }
        });

        filterCheckGPA.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                String ageVariable = "";
                String majorVariable = "";
                String gpaVariable = "";
                if(filterCheckGPA.isSelected()){
                    gpaVariable = "gpa > " +gpaTextFilter.getText();
                    if(filterAgeCheck.isSelected()) {
                        ageVariable = " AND age > "+ageTextFilter.getText();
                        if(filterMajorCheck.isSelected()) {
                            majorVariable = " AND major= '"+(String) majorDrop.getSelectionModel().getSelectedItem() + "'";
                        }
                    }else if(filterMajorCheck.isSelected()) {
                        majorVariable = " major= '"+(String) majorDrop.getSelectionModel().getSelectedItem() + "'";
                    }
                }else if(filterAgeCheck.isSelected()) {
                    ageVariable = " age > "+ageTextFilter.getText();
                    if(filterMajorCheck.isSelected()) {
                        majorVariable = " AND major= '"+(String) majorDrop.getSelectionModel().getSelectedItem() + "'";
                    }
                }else if(filterMajorCheck.isSelected()) {
                    majorVariable = " major= '"+(String) majorDrop.getSelectionModel().getSelectedItem() + "'";
                }

                String filter= gpaVariable + ageVariable + majorVariable  ;
                filterData(AWS_URL, username, pass, filter);
            }
        });

        majorDrop.getItems().add("CIS");
        majorDrop.getItems().add("ENG");
        majorDrop.getItems().add("BIO");
        majorDrop.getSelectionModel().select(0);
        filterMajorCheck.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                String ageVariable = "";
                String majorVariable = "";
                String gpaVariable = "";
                if(filterMajorCheck.isSelected()) {
                    majorVariable = "major= '"+(String) majorDrop.getSelectionModel().getSelectedItem() + "'";
                    if (filterCheckGPA.isSelected()) {
                        gpaVariable = " AND gpa > " +gpaTextFilter.getText();
                        if (filterAgeCheck.isSelected()) {
                            ageVariable = " AND age > "+ageTextFilter.getText();
                        }
                    } else if (filterAgeCheck.isSelected()) {
                        ageVariable = " age > "+ageTextFilter.getText();
                    }
                }else if (filterCheckGPA.isSelected()) {
                    gpaVariable = " gpa > " +gpaTextFilter.getText();
                    if (filterAgeCheck.isSelected()) {
                        ageVariable = " AND age > "+ageTextFilter.getText();
                    }
                } else if (filterAgeCheck.isSelected()) {
                    ageVariable = " age > "+ageTextFilter.getText();
                }

                String filter= majorVariable + gpaVariable + ageVariable;
                filterData(AWS_URL, username, pass, filter);
            }
        });

    }
}
