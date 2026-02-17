package core;

import java.sql.SQLException;
import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {
        DatabaseManager db = DatabaseManager.getInstance();
        ArrayList<Student> list;
        QueryTester test = new QueryTester();
        String sql = "SELECT id, fName, lName FROM students";
        
        try {
            list = db.executeQuery(sql, test);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        for (Student student : list) {
            System.out.println(student.getId()+" "+student.getfName()+" "+student.getlName());
        }
    }
}
