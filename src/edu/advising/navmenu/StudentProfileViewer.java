package edu.advising.navmenu;

import edu.advising.users.Student;

import java.sql.SQLException;
import java.util.List;

public class StudentProfileViewer implements ProfilePanel{

    private Student student;

    public StudentProfileViewer(Student student){
        this.student = student;
    }

    @Override
    public List<ProfileField> getFields() throws SQLException {
            return List.of(
                    new ProfileField("Name", student.getFullName(), false),
                    new ProfileField("Id", student.getStudentId(), false),
                    new ProfileField("Major", student.getMajor(), false),
                    new ProfileField("GPA", student.getGpa().toString(), false),
                    new ProfileField("Total Credits", String.valueOf(student.getCreditsEarned()), false),
                    new ProfileField("Advisor", student.getAdvisor().getFullName(), false)
            );
    }

    @Override
    public void render() {

    }
}
