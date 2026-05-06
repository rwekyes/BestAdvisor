package edu.advising.navmenu;

import edu.advising.users.Student;

import java.sql.SQLException;
import java.util.List;

public class StudentProfilePanel implements ProfilePanel{

    private Student student;

    public StudentProfilePanel(Student student){
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
                    new ProfileField("Advisor", student.getAdvisor()
                            != null ? student.getAdvisor().getFullName() : "Unassigned", false),
                    new ProfileField("Phone Number", student.getPhone(), true),
                    new ProfileField("Email", student.getEmail(), true)
            );
    }

    @Override
    public void render() {

    }
}
