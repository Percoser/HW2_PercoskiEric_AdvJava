package sample;
import java.util.UUID;

public class Student {
    public UUID studentID;
    public String fName;
    public String lName;
    public int age;
    public String major;
    public double gpa;

    @Override
    public String toString(){
        return (this.fName + " "
                + this.lName+ ", "+this.age+ ", "+this.major+ ": "+ this.gpa);
    }


}
