package core;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

// Testing out the DatabaseManager.executeQuery method with a full table pull from the sample db

public class QueryTester implements QueryHandler<ArrayList>{


    @Override
    public ArrayList<Student> handle(ResultSet rs) throws SQLException {
        ArrayList<Student> list = new ArrayList<>();

        while(rs.next()) {
            Student student = new Student();
            student.setId(rs.getInt("id"));
            student.setfName(rs.getString("fname"));
            student.setlName(rs.getString("lname"));

            list.add(student);
        }
        return list;
    }
}
