package edu.advising.navmenu;

import edu.advising.users.Student;

public class StudentDashboardWidget implements DashboardWidget{

    private Student student;

    public StudentDashboardWidget(Student student){
        this.student = student;
    }

    @Override
    public void render() {

    }

    @Override
    public void refresh() {

    }
}
